package com.beyondtime.registry;

import com.beyondtime.content.block.BasicAnalyzerBlock;
import com.beyondtime.content.block.MicroscopeBlock;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;

/** Blocks added by Beyond-Time. */
public final class BTBlocks {
    /**
     * The microscope (X-01).
     *
     * <p>A container block that keeps one petri dish. It is <em>not</em> a full cube: it is a bench
     * instrument with its own collision shape and a facing, so it needs {@code noOcclusion} to stop
     * it from darkening its neighbours like a solid block would.
     */
    public static final DeferredBlock<MicroscopeBlock> MICROSCOPE =
            BTRegistries.BLOCKS.registerBlock(
                    "microscope",
                    MicroscopeBlock::new,
                    properties -> properties.strength(2.0F).sound(SoundType.METAL).mapColor(MapColor.METAL).noOcclusion());

    /**
     * The basic bio-analyzer (M-01).
     *
     * <p>A full cube with a front panel and a facing. It holds one sealed tissue sample and reads a
     * deliberately incomplete report from it; the report lives only in the open screen.
     */
    public static final DeferredBlock<BasicAnalyzerBlock> BASIC_ANALYZER =
            BTRegistries.BLOCKS.registerBlock(
                    "basic_analyzer",
                    BasicAnalyzerBlock::new,
                    properties -> properties
                            .strength(3.5F, 6.0F)
                            .sound(SoundType.METAL)
                            .mapColor(MapColor.METAL)
                            .requiresCorrectToolForDrops());

    private BTBlocks() {}

    /** Forces this class to initialise so that the blocks above are declared in time. */
    public static void bootstrap() {}
}
