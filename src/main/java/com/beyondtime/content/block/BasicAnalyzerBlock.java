package com.beyondtime.content.block;

import com.beyondtime.content.block.entity.BasicAnalyzerBlockEntity;
import com.beyondtime.content.item.TissueSampleItem;
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
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.Nullable;

/**
 * The basic bio-analyzer (M-01).
 *
 * <p>A full cube with a facing, so its front panel can point at the player. It holds one sealed
 * tissue sample at a time. Showing it a sample inserts the sample; any other right click opens the
 * screen, where the sample is consumed immediately and a partial report appears.
 *
 * <p>The report is <em>not</em> stored: the screen owns it, and closing the screen loses it. That is
 * the basic analyzer's whole drawback compared with the advanced one.
 */
public class BasicAnalyzerBlock extends BaseEntityBlock {
    public static final MapCodec<BasicAnalyzerBlock> CODEC = simpleCodec(BasicAnalyzerBlock::new);
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;

    public BasicAnalyzerBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BasicAnalyzerBlockEntity(pos, state);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
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
    protected InteractionResult useWithoutItem(
            BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!(level.getBlockEntity(pos) instanceof BasicAnalyzerBlockEntity analyzer)) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide()) {
            player.openMenu(analyzer);
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
        if (!(level.getBlockEntity(pos) instanceof BasicAnalyzerBlockEntity analyzer)
                || !stack.is(BTItems.SEALED_TISSUE_SAMPLE.get())
                || !analyzer.getSample().isEmpty()) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }

        if (!level.isClientSide()) {
            analyzer.setSample(stack.split(1));
            // Open the screen right away so the player sees the report the sample produced.
            player.openMenu(analyzer);
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    protected @Nullable MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
        return level.getBlockEntity(pos) instanceof BasicAnalyzerBlockEntity analyzer ? analyzer : null;
    }

    /** {@return whether this stack can go into the analyzer, for the block and the menu} */
    public static boolean isSample(ItemStack stack) {
        return stack.is(BTItems.SEALED_TISSUE_SAMPLE.get()) && TissueSampleItem.sample(stack) != null;
    }
}
