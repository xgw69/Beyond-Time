package com.beyondtime.content.block;

import com.beyondtime.content.block.entity.MicroscopeBlockEntity;
import com.beyondtime.registry.BTItems;
import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.Nullable;

/**
 * The microscope (X-01).
 *
 * <p>Placed like any other block, broken bare handed, and it keeps its petri dish: the dish is stored
 * as a data component on the dropped item rather than dropped separately.
 *
 * <ul>
 *   <li>right click with a clean or used petri dish, empty microscope: the dish goes in
 *   <li>right click in any other case: the observation screen opens
 * </ul>
 */
public class MicroscopeBlock extends BaseEntityBlock {
    public static final MapCodec<MicroscopeBlock> CODEC = simpleCodec(MicroscopeBlock::new);

    public MicroscopeBlock(Properties properties) {
        super(properties);
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
