package com.deeptime;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;

/**
 * Client-only entry point.
 *
 * <p>Entity renderers, custom models, screens and block entity renderers are registered from
 * here, because none of that code may load on a dedicated server.
 */
@Mod(value = DeepTime.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = DeepTime.MODID, value = Dist.CLIENT)
public class DeepTimeClient {
    public DeepTimeClient(ModContainer container) {
        // Nothing client side yet: the first content (items, blocks) needs no renderer.
    }
}
