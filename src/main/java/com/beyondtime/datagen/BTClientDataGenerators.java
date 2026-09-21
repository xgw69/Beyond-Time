package com.beyondtime.datagen;

import com.beyondtime.BeyondTime;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

/**
 * Data generation entry point.
 *
 * <p>In 26.2 a single {@code runData} pass produces both the server data and the client resources, and
 * it is driven by {@link GatherDataEvent.Client}. Everything that touches client only classes lives
 * here so that a dedicated server never loads them.
 */
@EventBusSubscriber(modid = BeyondTime.MODID, value = Dist.CLIENT)
public final class BTClientDataGenerators {
    private BTClientDataGenerators() {}

    @SubscribeEvent
    static void onGatherData(GatherDataEvent.Client event) {
        event.createProvider(BTRecipeProvider.Runner::new);
        event.createProvider(BTLootTableProvider::new);
        event.createProvider(BTModelProvider::new);
    }
}
