package com.beyondtime.content.item;

import java.util.function.Consumer;
import java.util.Map;

import com.beyondtime.content.microbe.Microbe;
import com.beyondtime.content.microbe.MicrobeProfiles;
import com.beyondtime.content.microbe.MicrobeSample;
import com.beyondtime.registry.BTBlocks;
import com.beyondtime.registry.BTDataComponents;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jspecify.annotations.Nullable;

/**
 * The petri dish (P-02).
 *
 * <p>A dish is either clean or used. A clean dish takes exactly one swab and then has to be washed in
 * a heated cauldron before it can be used again.
 *
 * <p>What a swab finds is decided by {@link MicrobeProfiles}: if the block that was clicked is on the
 * list it collects that block, and otherwise it collects the air of the current dimension. That makes
 * the dish take priority over the block it is pointed at, which is deliberate - showing a chest a
 * dish means "swab the air here", not "open the chest". The microscope is the one exception, because
 * showing a microscope a dish means "put this in".
 *
 * <p>Water is the one block the game's own pick cannot report, because it has no outline to hit, so
 * aiming at water would otherwise swab whatever sits behind it. {@link #aimedAtWater} traces a second
 * time with fluids enabled and lets water win whenever that is what the player is really looking at.
 *
 * <p>A dish never produces a resource. The only thing a sample is good for is being read on a
 * microscope screen.
 */
public class PetriDishItem extends Item {
    /** Stack size of a dish that has not taken a sample yet. */
    public static final int CLEAN_STACK_SIZE = 64;

    /** Stack size of a dish that already holds a sample. */
    public static final int USED_STACK_SIZE = 1;

    public PetriDishItem(Item.Properties properties) {
        super(properties);
    }

    /** {@return whether this dish is already carrying a sample} */
    public static boolean hasSample(ItemStack stack) {
        return stack.has(BTDataComponents.SAMPLE.get());
    }

    /** {@return the sample on this dish, or {@code null} when it is clean} */
    public static @Nullable MicrobeSample sample(ItemStack stack) {
        return stack.get(BTDataComponents.SAMPLE.get());
    }

    /**
     * The stack size depends on the individual stack rather than on the item, so a stack of clean
     * dishes stays at 64 while the dish that has just been swabbed leaves as a single item.
     */
    @Override
    public int getMaxStackSize(ItemStack stack) {
        return hasSample(stack) ? USED_STACK_SIZE : CLEAN_STACK_SIZE;
    }

    /**
     * Runs before the block gets its turn, which is what lets a clean dish take priority over the
     * block it is pointed at.
     */
    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null || hasSample(stack)) {
            // A used dish only knows how to be washed, and that runs later, on a heated cauldron.
            return InteractionResult.PASS;
        }

        Level level = context.getLevel();
        BlockState state = level.getBlockState(context.getClickedPos());
        if (state.is(BTBlocks.MICROSCOPE.get())) {
            // Showing the microscope a dish means "put it in", never "swab the microscope".
            return InteractionResult.PASS;
        }

        BlockState water = aimedAtWater(level, player);
        if (water != null) {
            state = water;
        }

        return swab(level, player, context.getHand(), MicrobeProfiles.forBlock(state), MicrobeSample.Origin.ofBlock(state.getBlock()));
    }

    /**
     * Right click against nothing at all: the water in front of the player if there is any, and the
     * air of the dimension they are standing in otherwise.
     */
    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (hasSample(player.getItemInHand(hand))) {
            return InteractionResult.PASS;
        }

        BlockState water = aimedAtWater(level, player);
        if (water != null) {
            return swab(level, player, hand, MicrobeProfiles.forBlock(water), MicrobeSample.Origin.ofBlock(water.getBlock()));
        }

        return swab(level, player, hand, MicrobeProfiles.forAir(level), MicrobeSample.Origin.ofAir(level));
    }

    /**
     * Re-traces the player's line of sight while asking for water.
     *
     * <p>The hit result the game hands to {@link #onItemUseFirst} was traced with fluids turned off,
     * so aiming at a lake reports the sand behind it and the water itself can never be swabbed. This
     * second trace stops at the water surface instead. Anything that is not plain water is discarded,
     * so every other block, and every waterlogged one, keeps behaving exactly as before.
     *
     * @return the water the player is looking at, or {@code null} when it is not water
     */
    private static @Nullable BlockState aimedAtWater(Level level, Player player) {
        BlockHitResult hit = getPlayerPOVHitResult(level, player, ClipContext.Fluid.WATER);
        if (hit.getType() != HitResult.Type.BLOCK) {
            return null;
        }

        BlockState state = level.getBlockState(hit.getBlockPos());
        return state.is(Blocks.WATER) ? state : null;
    }

    /** Right click against a block that declined the click itself, i.e. a cauldron to wash in. */
    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null || !hasSample(player.getItemInHand(context.getHand()))) {
            return InteractionResult.PASS;
        }

        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        if (!isHeatedWashingStation(level, pos, state)) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide()) {
            LayeredCauldronBlock.lowerFillLevel(state, level, pos);
            swap(player, context.getHand(), null);
        }

        return InteractionResult.SUCCESS;
    }

    /**
     * Takes a sample, or does nothing when there is nothing to find.
     *
     * @param profile the table to roll on, or {@code null} when this block and dimension are both
     *     unknown, in which case the dish is left clean
     */
    private static InteractionResult swab(
            Level level, Player player, InteractionHand hand, @Nullable Map<Microbe, Integer> profile, MicrobeSample.Origin origin) {
        if (profile == null) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide()) {
            swap(player, hand, MicrobeProfiles.collect(origin, profile, level.getRandom()));
        }

        return InteractionResult.SUCCESS;
    }

    /**
     * Replaces the held dish with one in the opposite state.
     *
     * <p>The rest of the stack is left untouched, so a stack of 64 clean dishes becomes 63 clean
     * dishes plus one used dish, exactly like filling a bucket from a stack of buckets.
     *
     * @param sample what the new dish carries, or {@code null} for a washed, clean dish
     */
    private static void swap(Player player, InteractionHand hand, @Nullable MicrobeSample sample) {
        ItemStack held = player.getItemInHand(hand);
        ItemStack replacement = held.copyWithCount(1);
        if (sample == null) {
            replacement.remove(BTDataComponents.SAMPLE);
        } else {
            replacement.set(BTDataComponents.SAMPLE, sample);
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
     * <p>A water cauldron heated from below. Washing uses up one level of water, the way filling a
     * bottle does.
     */
    private static boolean isHeatedWashingStation(Level level, BlockPos pos, BlockState state) {
        if (!state.is(Blocks.WATER_CAULDRON) || state.getValue(LayeredCauldronBlock.LEVEL) <= 0) {
            return false;
        }

        return isHeatSource(level.getBlockState(pos.below()));
    }

    /**
     * {@return whether this block heats a cauldron sitting on top of it}
     *
     * <p>Campfire, soul campfire, magma block, or plain lava. {@code Blocks.LAVA} covers flowing lava
     * as well, because flowing and still lava are the same block with a different fluid state.
     */
    private static boolean isHeatSource(BlockState below) {
        return below.is(Blocks.CAMPFIRE)
                || below.is(Blocks.SOUL_CAMPFIRE)
                || below.is(Blocks.MAGMA_BLOCK)
                || below.is(Blocks.LAVA);
    }

    /**
     * The tooltip says what was swabbed and how much is on the plate, and nothing about which
     * microbes they are: reading the plate is what the microscope is for.
     */
    @Override
    public void appendHoverText(
            ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
        MicrobeSample sample = sample(stack);
        if (sample == null) {
            tooltip.accept(Component.translatable("item.beyondtime.petri_dish.clean").withStyle(ChatFormatting.DARK_GREEN));
            tooltip.accept(Component.translatable("item.beyondtime.petri_dish.how_to_swab").withStyle(ChatFormatting.DARK_GRAY));
            return;
        }

        tooltip.accept(Component.translatable("item.beyondtime.petri_dish.used").withStyle(ChatFormatting.GRAY));
        tooltip.accept(Component.translatable("item.beyondtime.petri_dish.origin", sample.origin().displayName())
                .withStyle(ChatFormatting.GRAY));
        tooltip.accept(Component.translatable("item.beyondtime.petri_dish.count", sample.total())
                .withStyle(ChatFormatting.GRAY));
        tooltip.accept(Component.translatable("item.beyondtime.petri_dish.read_on_microscope").withStyle(ChatFormatting.DARK_GRAY));
        tooltip.accept(Component.translatable("item.beyondtime.petri_dish.how_to_wash").withStyle(ChatFormatting.DARK_GRAY));
    }
}
