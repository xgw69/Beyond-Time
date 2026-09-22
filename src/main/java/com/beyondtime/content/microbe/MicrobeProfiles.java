package com.beyondtime.content.microbe;

import static com.beyondtime.content.microbe.Microbes.ANCIENT_WATER;
import static com.beyondtime.content.microbe.Microbes.BIZARRE;
import static com.beyondtime.content.microbe.Microbes.COPPER;
import static com.beyondtime.content.microbe.Microbes.CRIMSON_MOLD;
import static com.beyondtime.content.microbe.Microbes.ENDER;
import static com.beyondtime.content.microbe.Microbes.FAE;
import static com.beyondtime.content.microbe.Microbes.FALSE_ANCIENT_WATER;
import static com.beyondtime.content.microbe.Microbes.FIRE;
import static com.beyondtime.content.microbe.Microbes.GLIMMER;
import static com.beyondtime.content.microbe.Microbes.STONE;
import static com.beyondtime.content.microbe.Microbes.WOOD_MOLD;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.BlockItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WeatheringCopperCollection;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

/**
 * What lives where: the swab table.
 *
 * <p>Two kinds of entry, both written as {@code key -> mix of microbes with a weight}:
 *
 * <ul>
 *   <li>{@link #block} / {@link #tag} - what a petri dish finds when it is swabbed on that block,
 *   <li>{@link #air} - what it finds when it is swabbed at nothing, i.e. the air of a dimension.
 * </ul>
 *
 * <p>Adding a block is one line. A {@code tag} entry is consulted only when no {@code block} entry
 * matched, so {@code #minecraft:logs} can cover every log while
 * {@code Blocks.CRIMSON_STEM} still gets its own nether mix. Weights are relative, not absolute:
 * {@link #collect} turns them into a number of microbes with a little randomness, so two dishes
 * taken from the same block never read exactly the same.
 *
 * <p>The numbers live here so they are easy to tune; {@code docs/MICROBE_PROFILES.md} prints the
 * same tables in a form that is easier to read and to edit.
 */
public final class MicrobeProfiles {
    /**
     * How many microbes one unit of weight turns into, before jitter.
     *
     * <p>A total weight of about 90 therefore reads as roughly a thousand microbes, which keeps the
     * reported numbers in a range that looks like a plate count.
     */
    private static final int PER_WEIGHT = 12;

    /** How far each microbe's amount is allowed to wander from its weight, as a fraction. */
    private static final double JITTER = 0.12;

    private static final Map<Block, Map<Microbe, Integer>> BY_BLOCK = buildBlocks();
    private static final Map<TagKey<Block>, Map<Microbe, Integer>> BY_TAG = buildTags();
    private static final Map<Identifier, Map<Microbe, Integer>> BY_DIMENSION = buildDimensions();

    private MicrobeProfiles() {}

    // Lookup

    /** {@return the swab table for this block, or {@code null} when the block is not on the list} */
    public static @Nullable Map<Microbe, Integer> forBlock(BlockState state) {
        Map<Microbe, Integer> exact = BY_BLOCK.get(state.getBlock());
        if (exact != null) {
            return exact;
        }

        for (Map.Entry<TagKey<Block>, Map<Microbe, Integer>> entry : BY_TAG.entrySet()) {
            if (state.is(entry.getKey())) {
                return entry.getValue();
            }
        }

        return null;
    }

    /** {@return the swab table for a dimension's air, or {@code null} in an unknown dimension} */
    public static @Nullable Map<Microbe, Integer> forAir(Level level) {
        return BY_DIMENSION.get(level.dimension().identifier());
    }

    /** {@return whether holding a dish up in this dimension would collect anything} */
    public static boolean hasAir(Level level) {
        return forAir(level) != null;
    }

    /** {@return how many blocks and tags are on the list, for a command or a debug line} */
    public static int blockCount() {
        return BY_BLOCK.size() + BY_TAG.size();
    }

    /** {@return how many dimensions have an air table} */
    public static int dimensionCount() {
        return BY_DIMENSION.size();
    }

    // Collecting

    /** {@return the list of dimensions that have an air table, for reference and for tests} */
    public static List<Identifier> dimensions() {
        return List.copyOf(BY_DIMENSION.keySet());
    }

    /**
     * {@return a fresh sample taken from a table}
     *
     * <p>Every microbe's amount is its weight scaled by {@link #PER_WEIGHT} and then nudged by up to
     * {@link #JITTER}, so the proportions stay close to the table while no two dishes are identical.
     */
    public static MicrobeSample collect(
            MicrobeSample.Origin origin, Map<Microbe, Integer> profile, RandomSource random) {
        List<MicrobeSample.Reading> readings = new java.util.ArrayList<>(profile.size());
        int total = 0;
        for (Map.Entry<Microbe, Integer> entry : profile.entrySet()) {
            int weight = entry.getValue();
            if (weight <= 0) {
                continue;
            }

            double jitter = 1.0 + (random.nextDouble() * 2.0 - 1.0) * JITTER;
            int amount = Math.max(1, (int) Math.round(weight * PER_WEIGHT * jitter));
            readings.add(new MicrobeSample.Reading(entry.getKey().id(), amount));
            total += amount;
        }

        readings.sort(java.util.Comparator.comparingInt(MicrobeSample.Reading::amount).reversed());
        return new MicrobeSample(origin, List.copyOf(readings), total);
    }

    // Tables

    private static Map<Block, Map<Microbe, Integer>> buildBlocks() {
        Table table = new Table();

        // --- wet places: 古水菌 country, and the only place 伪古水菌 lives ------------------------
        table.block(Blocks.WATER, table.mix()
                .add(ANCIENT_WATER, 40).add(FALSE_ANCIENT_WATER, 12).add(BIZARRE, 8)
                .add(COPPER, 5).add(STONE, 4).add(ENDER, 3).add(WOOD_MOLD, 2).add(FAE, 1));
        table.block(Blocks.MUD, table.mix()
                .add(ANCIENT_WATER, 20).add(STONE, 10).add(COPPER, 6).add(BIZARRE, 8).add(WOOD_MOLD, 4));
        table.block(Blocks.CLAY, table.mix()
                .add(ANCIENT_WATER, 14).add(STONE, 14).add(BIZARRE, 6));
        table.block(Blocks.MOSS_BLOCK, table.mix()
                .add(ANCIENT_WATER, 16).add(STONE, 8).add(BIZARRE, 8).add(WOOD_MOLD, 4).add(FAE, 3));
        table.block(Blocks.WET_SPONGE, table.mix()
                .add(ANCIENT_WATER, 26).add(BIZARRE, 6).add(COPPER, 4));
        table.block(Blocks.DRIPSTONE_BLOCK, table.mix()
                .add(ANCIENT_WATER, 10).add(STONE, 12).add(BIZARRE, 4));
        table.block(Blocks.ICE, table.mix()
                .add(ANCIENT_WATER, 8).add(STONE, 6).add(BIZARRE, 4));

        // --- soil -----------------------------------------------------------------------------
        table.block(Blocks.DIRT, table.mix()
                .add(STONE, 16).add(ANCIENT_WATER, 10).add(BIZARRE, 10).add(WOOD_MOLD, 6).add(COPPER, 3));
        table.block(Blocks.GRASS_BLOCK, table.mix()
                .add(STONE, 14).add(ANCIENT_WATER, 10).add(BIZARRE, 10).add(WOOD_MOLD, 8).add(FAE, 2).add(COPPER, 3));
        table.block(Blocks.SAND, table.mix()
                .add(STONE, 20).add(BIZARRE, 8).add(ANCIENT_WATER, 4));
        table.block(Blocks.GRAVEL, table.mix()
                .add(STONE, 22).add(BIZARRE, 7).add(ANCIENT_WATER, 4));
        table.block(Blocks.FARMLAND, table.mix()
                .add(ANCIENT_WATER, 14).add(STONE, 12).add(BIZARRE, 8).add(COPPER, 4));

        // --- copper: 铜菌, plus the oxidation stages it is supposed to prevent -------------------
        table.block(Blocks.RAW_COPPER_BLOCK, table.mix().add(COPPER, 34).add(STONE, 8).add(BIZARRE, 3));

        // A copper block is a WeatheringCopperCollection of four stages, so the four stages get four
        // different mixes: the greener the metal, the fewer 铜菌 are left on it.
        WeatheringCopperCollection.ByState<Block> copperBlock = Blocks.COPPER_BLOCK.weathering();
        table.block(copperBlock.unaffected(), table.mix().add(COPPER, 28).add(STONE, 4).add(GLIMMER, 1));
        table.block(copperBlock.exposed(), table.mix().add(COPPER, 18).add(STONE, 6).add(BIZARRE, 3));
        table.block(copperBlock.weathered(), table.mix().add(COPPER, 10).add(STONE, 8).add(BIZARRE, 4));
        table.block(copperBlock.oxidized(), table.mix().add(COPPER, 4).add(STONE, 10).add(BIZARRE, 5));

        table.block(Blocks.CUT_COPPER.weathering().unaffected(), table.mix().add(COPPER, 26).add(STONE, 4).add(GLIMMER, 1));
        table.block(Blocks.COPPER_BULB.weathering().unaffected(), table.mix().add(COPPER, 26).add(GLIMMER, 2).add(STONE, 3));
        table.block(Blocks.LIGHTNING_ROD.weathering().unaffected(), table.mix().add(COPPER, 30).add(GLIMMER, 1).add(STONE, 2));

        // --- magic: 仙灵 ------------------------------------------------------------------------
        table.block(Blocks.ENCHANTING_TABLE, table.mix()
                .add(FAE, 30).add(ENDER, 6).add(ANCIENT_WATER, 4).add(COPPER, 4));
        table.block(Blocks.AMETHYST_BLOCK, table.mix().add(FAE, 22).add(GLIMMER, 4).add(BIZARRE, 3));
        table.block(Blocks.BUDDING_AMETHYST, table.mix().add(FAE, 26).add(GLIMMER, 6).add(BIZARRE, 3));
        table.block(Blocks.ENDER_CHEST, table.mix().add(ENDER, 16).add(FAE, 12).add(BIZARRE, 4));
        table.block(Blocks.BEACON, table.mix().add(FAE, 20).add(GLIMMER, 4).add(ENDER, 3));
        table.block(Blocks.CONDUIT, table.mix().add(FAE, 18).add(ANCIENT_WATER, 6).add(GLIMMER, 3));
        table.block(Blocks.RESPAWN_ANCHOR, table.mix().add(FIRE, 12).add(FAE, 10).add(ENDER, 6));
        table.block(Blocks.SCULK, table.mix().add(BIZARRE, 8).add(FAE, 8).add(ENDER, 6));
        table.block(Blocks.SCULK_CATALYST, table.mix().add(FAE, 10).add(ENDER, 8).add(BIZARRE, 6));

        // --- the blocks that glow at light level 1: 闪亮菌 --------------------------------------
        table.block(Blocks.BREWING_STAND, table.mix()
                .add(GLIMMER, 14).add(FAE, 8).add(ANCIENT_WATER, 6).add(COPPER, 3));
        table.block(Blocks.BROWN_MUSHROOM, table.mix().add(GLIMMER, 12).add(BIZARRE, 8).add(WOOD_MOLD, 6));
        table.block(Blocks.SMALL_AMETHYST_BUD, table.mix().add(GLIMMER, 14).add(FAE, 12).add(BIZARRE, 3));
        table.block(Blocks.SCULK_SENSOR, table.mix().add(GLIMMER, 10).add(FAE, 8).add(ENDER, 6));
        table.block(Blocks.DRAGON_EGG, table.mix().add(ENDER, 30).add(FAE, 10).add(GLIMMER, 6));
        table.block(Blocks.END_PORTAL_FRAME, table.mix().add(ENDER, 24).add(FAE, 12).add(GLIMMER, 4));

        // --- the End ---------------------------------------------------------------------------
        table.block(Blocks.END_STONE, table.mix().add(ENDER, 26).add(STONE, 12).add(BIZARRE, 6));
        table.block(Blocks.PURPUR_BLOCK, table.mix().add(ENDER, 18).add(BIZARRE, 6).add(FAE, 5));
        table.block(Blocks.CHORUS_FLOWER, table.mix().add(ENDER, 20).add(BIZARRE, 8).add(FAE, 4));
        table.block(Blocks.OBSIDIAN, table.mix().add(STONE, 12).add(ENDER, 10).add(FAE, 4));
        table.block(Blocks.CRYING_OBSIDIAN, table.mix().add(ENDER, 18).add(STONE, 10).add(FAE, 6));

        // --- the Nether: 火元素 country, where 古水菌 cannot live --------------------------------
        table.block(Blocks.NETHERRACK, table.mix().add(FIRE, 22).add(STONE, 20).add(BIZARRE, 5));
        table.block(Blocks.MAGMA_BLOCK, table.mix().add(FIRE, 30).add(STONE, 10).add(BIZARRE, 3));
        table.block(Blocks.SOUL_SAND, table.mix().add(FIRE, 18).add(STONE, 10).add(ENDER, 4));
        table.block(Blocks.SOUL_SOIL, table.mix().add(FIRE, 18).add(STONE, 10).add(ENDER, 4));
        table.block(Blocks.CALCITE, table.mix().add(STONE, 26).add(GLIMMER, 2).add(BIZARRE, 4));

        // --- 绯红霉, which lives with nether wart ------------------------------------------------
        table.block(Blocks.NETHER_WART, table.mix()
                .add(CRIMSON_MOLD, 30).add(FIRE, 10).add(BIZARRE, 6));
        table.block(Blocks.NETHER_WART_BLOCK, table.mix()
                .add(CRIMSON_MOLD, 34).add(FIRE, 6).add(BIZARRE, 4));
        table.block(Blocks.WARPED_WART_BLOCK, table.mix()
                .add(CRIMSON_MOLD, 26).add(BIZARRE, 8).add(FAE, 2));
        // The stems themselves are covered by the #crimson_stems / #warped_stems tags, which are
        // consulted before #logs so that the Nether wood never reports 古水菌.
        table.block(Blocks.CRIMSON_PLANKS, table.mix()
                .add(CRIMSON_MOLD, 16).add(WOOD_MOLD, 12).add(FIRE, 4));
        table.block(Blocks.WARPED_PLANKS, table.mix()
                .add(CRIMSON_MOLD, 10).add(WOOD_MOLD, 14).add(BIZARRE, 6));
        table.block(Blocks.CRIMSON_NYLIUM, table.mix()
                .add(CRIMSON_MOLD, 18).add(FIRE, 8).add(STONE, 6));
        table.block(Blocks.WARPED_NYLIUM, table.mix()
                .add(CRIMSON_MOLD, 12).add(FIRE, 6).add(BIZARRE, 8));
        table.block(Blocks.CRIMSON_FUNGUS, table.mix()
                .add(CRIMSON_MOLD, 20).add(BIZARRE, 5).add(FAE, 3));
        table.block(Blocks.WARPED_FUNGUS, table.mix()
                .add(CRIMSON_MOLD, 14).add(BIZARRE, 8).add(FAE, 5));

        return table.blocks;
    }

    private static Map<TagKey<Block>, Map<Microbe, Integer>> buildTags() {
        Table table = new Table();

        table.tag(BlockTags.BASE_STONE_OVERWORLD, table.mix()
                .add(STONE, 30).add(BIZARRE, 6).add(COPPER, 4).add(GLIMMER, 1).add(ANCIENT_WATER, 2));
        table.tag(BlockTags.BASE_STONE_NETHER, table.mix()
                .add(STONE, 20).add(FIRE, 16).add(BIZARRE, 5));

        // Ahead of #logs on purpose: a stripped crimson stem is still Nether wood, and the Nether
        // has no 古水菌 at all - only 火元素 crowding it out.
        table.tag(BlockItemTags.CRIMSON_STEMS.block(), table.mix()
                .add(CRIMSON_MOLD, 22).add(WOOD_MOLD, 10).add(FIRE, 6));
        table.tag(BlockItemTags.WARPED_STEMS.block(), table.mix()
                .add(CRIMSON_MOLD, 14).add(WOOD_MOLD, 12).add(BIZARRE, 8));

        table.tag(BlockTags.LOGS, table.mix()
                .add(WOOD_MOLD, 34).add(ANCIENT_WATER, 6).add(BIZARRE, 5));
        table.tag(BlockTags.PLANKS, table.mix()
                .add(WOOD_MOLD, 18).add(BIZARRE, 5).add(ANCIENT_WATER, 3));
        table.tag(BlockTags.COPPER, table.mix()
                .add(COPPER, 24).add(STONE, 6).add(GLIMMER, 1));
        table.tag(BlockTags.COPPER_ORES, table.mix()
                .add(COPPER, 30).add(STONE, 18).add(BIZARRE, 4));

        return table.tags;
    }

    private static Map<Identifier, Map<Microbe, Integer>> buildDimensions() {
        Table table = new Table();

        // Temperate and wet: everything except the two that cannot live in air and the one that
        // only lives in water.
        table.air(Level.OVERWORLD, table.mix()
                .add(ANCIENT_WATER, 26).add(BIZARRE, 20).add(WOOD_MOLD, 15).add(STONE, 14)
                .add(COPPER, 12).add(ENDER, 4).add(FAE, 3).add(FIRE, 2));

        // Hot: 火元素 crowds 古水菌 out completely, which is the author's explanation for why the
        // Nether has none.
        table.air(Level.NETHER, table.mix()
                .add(FIRE, 34).add(STONE, 18).add(BIZARRE, 12).add(COPPER, 8)
                .add(CRIMSON_MOLD, 8).add(ENDER, 6).add(WOOD_MOLD, 6).add(FAE, 2));

        // The End, dry and nearly empty, with a trace of 古水菌 left over from when it still had
        // water.
        table.air(Level.END, table.mix()
                .add(ENDER, 30).add(BIZARRE, 18).add(STONE, 16).add(FAE, 6)
                .add(COPPER, 6).add(ANCIENT_WATER, 4).add(FIRE, 1));

        return table.dimensions;
    }

    /** A tiny builder, so a table entry is one readable line. */
    private static final class Mix {
        private final Map<Microbe, Integer> weights = new LinkedHashMap<>();

        Mix add(Microbe microbe, int weight) {
            this.weights.put(microbe, weight);
            return this;
        }

        Map<Microbe, Integer> build() {
            return Map.copyOf(this.weights);
        }
    }

    /** Collects the tables while they are being built. */
    private static final class Table {
        private final Map<Block, Map<Microbe, Integer>> blocks = new LinkedHashMap<>();
        private final Map<TagKey<Block>, Map<Microbe, Integer>> tags = new LinkedHashMap<>();
        private final Map<Identifier, Map<Microbe, Integer>> dimensions = new LinkedHashMap<>();

        Mix mix() {
            return new Mix();
        }

        void block(Block block, Mix mix) {
            this.blocks.put(block, mix.build());
        }

        void tag(TagKey<Block> tag, Mix mix) {
            this.tags.put(tag, mix.build());
        }

        void air(net.minecraft.resources.ResourceKey<Level> dimension, Mix mix) {
            this.air(dimension.identifier(), mix);
        }

        /** The same, for a dimension that is only known by its id, e.g. one of the seven past eras. */
        void air(Identifier dimension, Mix mix) {
            this.dimensions.put(dimension, mix.build());
        }
    }
}
