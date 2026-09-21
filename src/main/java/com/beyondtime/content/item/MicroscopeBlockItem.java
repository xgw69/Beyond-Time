package com.beyondtime.content.item;

import java.util.function.Consumer;

import com.beyondtime.registry.BTDataComponents;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;

/**
 * The microscope as an item.
 *
 * <p>A microscope that holds a petri dish does not stack, an empty one stacks to 64. The stack size
 * is decided per stack, so a single item can be both a 64-stack and a 1-stack depending on what is
 * inside it.
 */
public class MicroscopeBlockItem extends BlockItem {
    /** Stack size of a microscope with nothing inside. */
    public static final int EMPTY_STACK_SIZE = 64;

    /** Stack size of a microscope that holds a petri dish. */
    public static final int LOADED_STACK_SIZE = 1;

    public MicroscopeBlockItem(Block block, Item.Properties properties) {
        super(block, properties);
    }

    /** {@return the dish carried by this stack, or an empty stack if there is none} */
    public static ItemStack containedDish(ItemStack stack) {
        return stack.getOrDefault(BTDataComponents.CONTAINED_DISH.get(), ItemStack.EMPTY);
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return containedDish(stack).isEmpty() ? EMPTY_STACK_SIZE : LOADED_STACK_SIZE;
    }

    @Override
    public void appendHoverText(
            ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
        ItemStack dish = containedDish(stack);
        if (!dish.isEmpty()) {
            tooltip.accept(Component.translatable("item.beyondtime.microscope.contains", dish.getHoverName())
                    .withStyle(ChatFormatting.GRAY));
        }
    }
}
