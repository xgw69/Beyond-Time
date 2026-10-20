package com.beyondtime.content.item;

import java.util.function.Consumer;

import com.beyondtime.content.tissue.TissueAnalysis;
import com.beyondtime.content.tissue.TissueSample;
import com.beyondtime.registry.BTDataComponents;
import com.beyondtime.registry.BTItems;

import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import org.jspecify.annotations.Nullable;

/**
 * A sealed tissue sample (T-02).
 *
 * <p>One item for every creature, carrying a {@link TissueSample} component. It never stacks, because
 * two samples from two creatures are different data and merging them would silently lose one. It is
 * also not food, fuel or a crafting ingredient: it exists to be read.
 */
public class TissueSampleItem extends Item {
    public TissueSampleItem(Item.Properties properties) {
        super(properties);
    }

    /** {@return the sample on this stack, or {@code null} when the component is missing} */
    public static @Nullable TissueSample sample(ItemStack stack) {
        return stack.get(BTDataComponents.TISSUE.get());
    }

    /** {@return a stack holding the given sample} */
    public static ItemStack of(TissueSample sample) {
        ItemStack stack = new ItemStack(BTItems.SEALED_TISSUE_SAMPLE.get());
        stack.set(BTDataComponents.TISSUE.get(), sample);
        return stack;
    }

    /** {@return the creature's name, or the raw id when its mod is no longer installed} */
    public static Component entityName(Identifier entityId) {
        EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.getValue(entityId);
        return type == null ? Component.literal(entityId.toString()) : type.getDescription();
    }

    /** {@return the dimension's name} */
    public static Component dimensionName(Identifier dimensionId) {
        return Component.translatable("dimension." + dimensionId.getNamespace() + "." + dimensionId.getPath());
    }

    @Override
    public void appendHoverText(
            ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
        TissueSample sample = sample(stack);
        if (sample == null) {
            tooltip.accept(Component.translatable("item.beyondtime.sealed_tissue_sample.empty").withStyle(ChatFormatting.GRAY));
            return;
        }

        tooltip.accept(Component.translatable(
                        "item.beyondtime.sealed_tissue_sample.source",
                        entityName(sample.entity()),
                        dimensionName(sample.dimension()))
                .withStyle(ChatFormatting.GRAY));
        tooltip.accept(Component.translatable(
                        "item.beyondtime.sealed_tissue_sample.analysis",
                        Component.translatable(sample.analysis().translationKey()))
                .withStyle(sample.analysis() == TissueAnalysis.NONE ? ChatFormatting.DARK_GRAY : ChatFormatting.GREEN));
        tooltip.accept(Component.translatable("item.beyondtime.sealed_tissue_sample.hint").withStyle(ChatFormatting.DARK_GRAY));
    }
}
