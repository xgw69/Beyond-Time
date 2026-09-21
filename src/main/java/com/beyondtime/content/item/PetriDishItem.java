package com.beyondtime.content.item;

import java.util.function.Consumer;

import com.beyondtime.registry.BTBlocks;
import com.beyondtime.registry.BTDataComponents;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Unit;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * The petri dish (P-02).
 *
 * <p>A dish is either clean or used. A clean dish picks something up exactly once, on a right click
 * against the air or against a block; a used dish cannot pick anything up again until it has been
 * washed in a heated cauldron.
 *
 * <p>What a dish actually collects is not designed yet, so collecting currently only flips the
 * {@link BTDataComponents#SAMPLE} flag.
 */
public class PetriDishItem extends Item {
    public PetriDishItem(Item.Properties properties) {
        super(properties);
    }

    /** Whether this dish has already taken a sample and therefore needs washing. */
    public static boolean hasSample(ItemStack stack) {
        return stack.has(BTDataComponents.SAMPLE.get());
    }

    /** Right click against air. */
    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (hasSample(player.getItemInHand(hand))) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide()) {
            swap(player, hand, true);
        }

        return InteractionResult.SUCCESS;
    }

    /** Right click against a block. */
    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null) {
            return InteractionResult.PASS;
        }

        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);

        // In 26.2 the block gets the first say on a right click, so this only runs when that was
        // suppressed (sneaking) or declined. Never sample the microscope itself: the player showing it
        // a dish means "put this in", not "take a swab of the microscope".
        if (state.is(BTBlocks.MICROSCOPE.get())) {
            return InteractionResult.PASS;
        }

        if (hasSample(player.getItemInHand(context.getHand()))) {
            if (!isHeatedWashingStation(level, pos, state)) {
                return InteractionResult.PASS;
            }

            if (!level.isClientSide()) {
                if (state.is(Blocks.WATER_CAULDRON)) {
                    LayeredCauldronBlock.lowerFillLevel(state, level, pos);
                }

                swap(player, context.getHand(), false);
            }

            return InteractionResult.SUCCESS;
        }

        if (!level.isClientSide()) {
            swap(player, context.getHand(), true);
        }

        return InteractionResult.SUCCESS;
    }

    /**
     * Replaces the held dish with one in the opposite state.
     *
     * <p>The rest of the stack is left untouched, so a stack of 64 clean dishes becomes 63 clean
     * dishes plus one used dish, exactly like filling a bucket from a stack of buckets.
     *
     * @param collect {@code true} to add a sample, {@code false} to wash it off
     */
    private static void swap(Player player, InteractionHand hand, boolean collect) {
        ItemStack held = player.getItemInHand(hand);
        ItemStack replacement = held.copyWithCount(1);
        if (collect) {
            replacement.set(BTDataComponents.SAMPLE, Unit.INSTANCE);
        } else {
            replacement.remove(BTDataComponents.SAMPLE);
        }

        held.shrink(1);
        if (held.isEmpty()) {
            player.setItemInHand(hand, replacement);
        } else if (!player.getInventory().add(replacement)) {
            player.drop(replacement, false);
        }
    }

    /**
     * {@return whether the block at {@code pos} can wash a used dish}
     *
     * <p>Two shapes are accepted, because the exact wording of the rule was ambiguous:
     * a water cauldron or a lava cauldron, either of them heated from below.
     */
    private static boolean isHeatedWashingStation(Level level, BlockPos pos, BlockState state) {
        BlockState below = level.getBlockState(pos.below());
        boolean heated = below.is(Blocks.CAMPFIRE) || below.is(Blocks.SOUL_CAMPFIRE) || below.is(Blocks.MAGMA_BLOCK);
        if (!heated) {
            return false;
        }

        if (state.is(Blocks.LAVA_CAULDRON)) {
            return true;
        }

        return state.is(Blocks.WATER_CAULDRON) && state.getValue(LayeredCauldronBlock.LEVEL) > 0;
    }

    @Override
    public void appendHoverText(
            ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
        tooltip.accept(Component.translatable(hasSample(stack)
                        ? "item.beyondtime.petri_dish.used"
                        : "item.beyondtime.petri_dish.clean")
                .withStyle(hasSample(stack) ? ChatFormatting.GRAY : ChatFormatting.DARK_GREEN));
    }
}
