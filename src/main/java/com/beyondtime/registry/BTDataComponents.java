package com.beyondtime.registry;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;

import com.beyondtime.content.microbe.MicrobeSample;

/**
 * Data components added by Beyond-Time.
 *
 * <p>All content that a block entity must keep when it is broken is stored as a data component on
 * the dropped item, the same way shulker boxes and decorated pots keep their contents.
 */
public final class BTDataComponents {
    /**
     * The sample a petri dish is carrying.
     *
     * <p>Present means "used": the dish cannot pick anything else up until it has been washed, and it
     * does not stack. The payload is what the microscope screen reads, and it is purely
     * informational - a sample never turns into an item.
     */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<MicrobeSample>> SAMPLE =
            BTRegistries.DATA_COMPONENTS.registerComponentType(
                    "sample",
                    builder -> builder.persistent(MicrobeSample.CODEC).networkSynchronized(MicrobeSample.STREAM_CODEC));

    /** The petri dish currently sitting inside a microscope. */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ItemStack>> CONTAINED_DISH =
            BTRegistries.DATA_COMPONENTS.registerComponentType(
                    "contained_dish",
                    builder -> builder.persistent(ItemStack.CODEC).networkSynchronized(ItemStack.STREAM_CODEC));

    private BTDataComponents() {}

    /** Forces this class to initialise so that the components above are declared in time. */
    public static void bootstrap() {}
}
