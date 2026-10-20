package com.beyondtime.content.item;

import java.util.function.Consumer;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

/**
 * The sampler (T-01): the first way to get data out of a living creature.
 *
 * <p>It is a deliberately poor weapon: three points of damage, no enchantability, and 64 durability.
 * Every hit costs one point through the vanilla {@code WEAPON} component, and a killing blow costs
 * sixteen more, so one sampler is worth exactly three samples before it breaks. That arithmetic is
 * intentional (see {@code docs/MACHINES.md} 2.1): the player has to go back to the bench instead of
 * farming a chest full of samples.
 *
 * <p>The actual drop is handled in {@link SamplerEvents}, because only the death event knows whether
 * the creature was on the samplable list.
 */
public class SamplerItem extends Item {
    /** Total durability of one sampler. */
    public static final int DURABILITY = 64;

    /** Extra durability a killing blow costs, on top of the one point every hit costs. */
    public static final int KILL_COST = 16;

    /** Total damage of the sampler, including the player's bare-hand point. */
    public static final int ATTACK_DAMAGE = 3;

    public SamplerItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(
            ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
        tooltip.accept(Component.translatable("item.beyondtime.sampler.how_to_use").withStyle(ChatFormatting.GRAY));
        tooltip.accept(Component.translatable("item.beyondtime.sampler.cost").withStyle(ChatFormatting.DARK_GRAY));
        tooltip.accept(Component.translatable("item.beyondtime.sampler.no_enchant").withStyle(ChatFormatting.DARK_GRAY));
    }
}
