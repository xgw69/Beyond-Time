package com.deeptime;

import org.slf4j.Logger;

import com.deeptime.registry.DTRegistries;
import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

/**
 * Entry point of Deep Time.
 *
 * <p>Deep Time is an archaeology mod about the history of the world: it starts with a
 * microscope and microbial samples, moves on to sequencing and cloning the ancestral forms of
 * living creatures, and ends with machines that travel to the past of each dimension.
 */
@Mod(DeepTime.MODID)
public class DeepTime {
    /** The mod id. Must match the {@code mod_id} property and {@code neoforge.mods.toml}. */
    public static final String MODID = "deeptime";

    public static final Logger LOGGER = LogUtils.getLogger();

    public DeepTime(IEventBus modEventBus, ModContainer modContainer) {
        DTRegistries.register(modEventBus);

        modEventBus.addListener(this::onCommonSetup);

        // Game events (as opposed to mod loading events) are fired on the NeoForge bus.
        NeoForge.EVENT_BUS.register(this);
    }

    private void onCommonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("Deep Time finished common setup.");
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("Deep Time is present on the server side.");
    }
}
