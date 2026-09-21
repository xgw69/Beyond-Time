package com.beyondtime;

import org.slf4j.Logger;

import com.beyondtime.registry.BTRegistries;
import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

/**
 * Entry point of Beyond-Time.
 *
 * <p>Beyond-Time is an archaeology mod about the history of the world. Gameplay, story and
 * content are specified by the project owner; see {@code docs/DESIGN.md} for the agreed
 * framework and {@code docs/LORE_INTAKE.md} for the story intake form.
 */
@Mod(BeyondTime.MODID)
public class BeyondTime {
    /** The mod id. Must match the {@code mod_id} property and {@code neoforge.mods.toml}. */
    public static final String MODID = "beyondtime";

    public static final Logger LOGGER = LogUtils.getLogger();

    public BeyondTime(IEventBus modEventBus, ModContainer modContainer) {
        BTRegistries.register(modEventBus);

        modEventBus.addListener(this::onCommonSetup);

        // Game events (as opposed to mod loading events) are fired on the NeoForge bus.
        NeoForge.EVENT_BUS.register(this);
    }

    private void onCommonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("Beyond-Time finished common setup.");
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("Beyond-Time is present on the server side.");
    }
}
