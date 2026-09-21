package com.beyondtime.registry;

import com.beyondtime.content.item.MicroscopeBlockItem;
import com.beyondtime.content.item.PetriDishItem;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;

/** Items added by Beyond-Time. */
public final class BTItems {
    /** The petri dish (P-02). Stacks to 64 whether it is clean or already used. */
    public static final DeferredItem<Item> PETRI_DISH =
            BTRegistries.ITEMS.registerItem("petri_dish", PetriDishItem::new, properties -> properties.stacksTo(64));

    /**
     * The microscope as an item.
     *
     * <p>Stack size depends on the individual stack: an empty microscope stacks to 64, one that holds
     * a dish does not stack at all.
     */
    public static final DeferredItem<MicroscopeBlockItem> MICROSCOPE = BTRegistries.ITEMS.registerItem(
            "microscope",
            properties -> new MicroscopeBlockItem(BTBlocks.MICROSCOPE.get(), properties),
            Item.Properties::useBlockDescriptionPrefix);

    private BTItems() {}

    /** Forces this class to initialise so that the items above are declared in time. */
    public static void bootstrap() {}
}
