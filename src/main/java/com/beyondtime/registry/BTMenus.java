package com.beyondtime.registry;

import com.beyondtime.content.menu.MicroscopeMenu;

import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.DeferredHolder;

/** Container menus added by Beyond-Time. */
public final class BTMenus {
    public static final DeferredHolder<MenuType<?>, MenuType<MicroscopeMenu>> MICROSCOPE = BTRegistries.MENU_TYPES.register(
            "microscope", () -> new MenuType<>(MicroscopeMenu::createClient, FeatureFlags.VANILLA_SET));

    private BTMenus() {}

    /** Forces this class to initialise so that the menus above are declared in time. */
    public static void bootstrap() {}
}
