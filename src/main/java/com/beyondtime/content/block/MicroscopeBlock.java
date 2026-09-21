package com.beyondtime.content.block;

import java.util.Map;

import com.beyondtime.content.block.entity.MicroscopeBlockEntity;
import com.beyondtime.registry.BTItems;
import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

/**
 * The microscope (X-01).
 *
 * <p>Not a full cube: it is a bench-top instrument made of a base plate, a stage, a column, an arm
 * and a tube. It can be placed in four orientations; {@code FACING} points from the block towards the
 * player, so the front of the model (its north side) always faces whoever placed it, the way the
 * stonecutter and the lectern behave.
 *
 * <p>The shape below is the collision and outline shape and is meant to match the block model
 * exactly. Both live in {@code docs/MICROSCOPE.md}: change one and you must change the other.
 *
 * <ul>
 *   <li>right click with a clean or used petri dish, empty microscope: the dish goes in
 *   <li>right click in any other case: the observation screen opens
 * </ul>
 */
public class MicroscopeBlock extends BaseEntityBlock {
    public static final MapCodec<MicroscopeBlock> CODEC = simpleCodec(MicroscopeBlock::new);

    /** The direction the front of the instrument points in. */
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;

    /**
     * Shape variant A, as it looks when the block faces north. The numbers are block model pixels,
     * so they can be copied straight into {@code models/block/microscope.json}.
     */
    public static final VoxelShape NORTH_SHAPE = Shapes.or(
            Block.box(1.0, 0.0, 1.0, 15.0, 3.0, 15.0),   // base plate
            Block.box(3.0, 5.0, 3.0, 11.0, 6.0, 13.0),   // stage
            Block.box(10.0, 3.0, 11.0, 14.0, 16.0, 15.0), // column
            Block.box(5.0, 13.0, 5.0, 14.0, 15.0, 15.0),  // arm
            Block.box(6.0, 9.0, 6.0, 8.0, 16.0, 8.0));    // tube

    /** {@link #NORTH_SHAPE} turned into the shape for each of the four placements. */
    private static final Map<Direction, VoxelShape> SHAPES = Shapes.rotateHorizontal(NORTH_SHAPE);

    public MicroscopeBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MicroscopeBlockEntity(pos, state);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPES.get(state.getValue(FACING));
    }

    @Override
    protected boolean useShapeForLightOcclusion(BlockState state) {
        return true;
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType type) {
        return false;
    }

    @Override
    protected InteractionResult useWithoutItem(
            BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!(level.getBlockEntity(pos) instanceof MicroscopeBlockEntity microscope)) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide()) {
            player.openMenu(microscope);
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    protected InteractionResult useItemOn(
            ItemStack stack,
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hitResult) {
        if (!(level.getBlockEntity(pos) instanceof MicroscopeBlockEntity microscope)
                || !stack.is(BTItems.PETRI_DISH.get())
                || !microscope.getDish().isEmpty()) {
            // Anything else falls through to "use with an empty hand", which opens the screen.
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }

        if (!level.isClientSide()) {
            microscope.setDish(stack.split(1));
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    protected @Nullable MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
        return level.getBlockEntity(pos) instanceof MicroscopeBlockEntity microscope ? microscope : null;
    }
}
