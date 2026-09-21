package com.beyondtime.registry;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;

/** The single creative tab that holds every Beyond-Time item. */
public final class BTCreativeTab {
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TAB =
            BTRegistries.CREATIVE_TABS.register("beyondtime", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.beyondtime"))
                    .icon(() -> new ItemStack(BTItems.MICROSCOPE.get()))
                    .displayItems((parameters, output) -> {
                        output.accept(BTItems.MICROSCOPE.get());
                        output.accept(BTItems.PETRI_DISH.get());
                    })
                    .build());

    private BTCreativeTab() {}

    /** Forces this class to initialise so that the creative tab above is declared in time. */
    public static void bootstrap() {}
}
