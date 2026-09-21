package com.beyondtime;

import com.beyondtime.client.screen.MicroscopeScreen;
import com.beyondtime.registry.BTMenus;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

/**
 * Client-only entry point.
 *
 * <p>Entity renderers, custom models, block entity renderers and screens are registered from
 * here, because none of that code may load on a dedicated server.
 */
@Mod(value = BeyondTime.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = BeyondTime.MODID, value = Dist.CLIENT)
public class BeyondTimeClient {
    public BeyondTimeClient(ModContainer container) {
        // Screens are registered through RegisterMenuScreensEvent below.
    }

    @SubscribeEvent
    static void onRegisterMenuScreens(RegisterMenuScreensEvent event) {
        event.register(BTMenus.MICROSCOPE.get(), MicroscopeScreen::new);
    }
}
