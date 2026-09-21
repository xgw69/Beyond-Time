package com.beyondtime;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;

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
        // No client side content yet.
    }
}
