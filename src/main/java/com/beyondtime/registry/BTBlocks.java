package com.beyondtime.registry;

import com.beyondtime.content.block.MicroscopeBlock;

import net.neoforged.neoforge.registries.DeferredBlock;

/** Blocks added by Beyond-Time. */
public final class BTBlocks {
    /**
     * The microscope (X-01). A placeretained container block: it keeps one petri dish, and the dish
     * travels with the dropped item when the block is broken.
     */
    public static final DeferredBlock<MicroscopeBlock> MICROSCOPE =
            BTRegistries.BLOCKS.registerBlock("microscope", MicroscopeBlock::new, properties -> properties.strength(2.0F));

    private BTBlocks() {}

    /** Forces this class to initialise so that the blocks above are declared in time. */
    public static void bootstrap() {}
}
