package com.beyondtime.registry;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.util.Unit;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;

/**
 * Data components added by Beyond-Time.
 *
 * <p>All content that a block entity must keep when it is broken is stored as a data component on
 * the dropped item, the same way shulker boxes and decorated pots keep their contents.
 */
public final class BTDataComponents {
    /**
     * Present on a petri dish that has picked something up but has not been cleaned yet.
     *
     * <p>The payload is deliberately empty for now: what was collected is not designed yet, so this
     * only records the "used once" state. It becomes a real sample record once the microbial
     * catalogue exists.
     */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Unit>> SAMPLE =
            BTRegistries.DATA_COMPONENTS.registerComponentType(
                    "sample",
                    builder -> builder.persistent(Unit.CODEC).networkSynchronized(Unit.STREAM_CODEC));

    /** The petri dish currently sitting inside a microscope. */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ItemStack>> CONTAINED_DISH =
            BTRegistries.DATA_COMPONENTS.registerComponentType(
                    "contained_dish",
                    builder -> builder.persistent(ItemStack.CODEC).networkSynchronized(ItemStack.STREAM_CODEC));

    private BTDataComponents() {}

    /** Forces this class to initialise so that the components above are declared in time. */
    public static void bootstrap() {}
}
