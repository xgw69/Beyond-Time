package com.beyondtime.content.tissue;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

/**
 * The tissue library.
 *
 * <p>Creatures that matter to the main story get a hand-written profile: the enderman's bad reaction
 * to water, the piglin's reaction to a wet world, the warden's sculk body, the elder guardian's ancient
 * ocean. Everything else on the samplable list falls back to a profile for its {@link MobCategory},
 * so a newly added mob still produces a sensible, if generic, report.
 *
 * <p>The partial report shows only two traits and hides the last gene fragments, which is why two
 * samples from the same creature can read differently: the analyzer chooses by the sample's seed.
 */
public final class TissueProfiles {
    // Groups
    private static final String ENDER = "tissue.beyondtime.group.ender";
    private static final String NETHER = "tissue.beyondtime.group.nether";
    private static final String UNDEAD = "tissue.beyondtime.group.undead";
    private static final String ILLAGER = "tissue.beyondtime.group.illager";
    private static final String VILLAGER = "tissue.beyondtime.group.villager";
    private static final String AQUATIC = "tissue.beyondtime.group.aquatic";
    private static final String BEAST = "tissue.beyondtime.group.beast";
    private static final String CONSTRUCT = "tissue.beyondtime.group.construct";
    private static final String SCULK = "tissue.beyondtime.group.sculk";
    private static final String HOSTILE = "tissue.beyondtime.group.hostile";
    private static final String AMBIENT = "tissue.beyondtime.group.ambient";
    private static final String UNKNOWN = "tissue.beyondtime.group.unknown";

    // Generic fallbacks, one per MobCategory family.
    private static final TissueProfile GENERIC_HOSTILE = profile(HOSTILE, List.of("?"),
            "tissue.beyondtime.trait.hostile.0", "tissue.beyondtime.trait.hostile.1",
            "tissue.beyondtime.trait.hostile.2");
    private static final TissueProfile GENERIC_BEAST = profile(BEAST, List.of("?"),
            "tissue.beyondtime.trait.beast.0", "tissue.beyondtime.trait.beast.1",
            "tissue.beyondtime.trait.beast.2");
    private static final TissueProfile GENERIC_AQUATIC = profile(AQUATIC, List.of("?"),
            "tissue.beyondtime.trait.aquatic.0", "tissue.beyondtime.trait.aquatic.1",
            "tissue.beyondtime.trait.aquatic.2");
    private static final TissueProfile GENERIC_AMBIENT = profile(AMBIENT, List.of("?"),
            "tissue.beyondtime.trait.ambient.0", "tissue.beyondtime.trait.ambient.1",
            "tissue.beyondtime.trait.ambient.2");
    private static final TissueProfile GENERIC_UNKNOWN = profile(UNKNOWN, List.of("?"),
            "tissue.beyondtime.trait.unknown.0", "tissue.beyondtime.trait.unknown.1",
            "tissue.beyondtime.trait.unknown.2");

    private static final Map<Identifier, TissueProfile> OVERRIDES = buildOverrides();

    private TissueProfiles() {}

    /** {@return the profile for a creature, never {@code null}} */
    public static TissueProfile of(Identifier entityId) {
        TissueProfile override = OVERRIDES.get(entityId);
        if (override != null) {
            return override;
        }

        EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.getValue(entityId);
        if (type == null) {
            return GENERIC_UNKNOWN;
        }

        return switch (type.getCategory()) {
            case MONSTER -> GENERIC_HOSTILE;
            case CREATURE -> GENERIC_BEAST;
            case AMBIENT -> GENERIC_AMBIENT;
            case AXOLOTLS, UNDERGROUND_WATER_CREATURE, WATER_CREATURE, WATER_AMBIENT -> GENERIC_AQUATIC;
            case MISC -> GENERIC_UNKNOWN;
        };
    }

    /** {@return how many creatures have a hand-written profile, for the docs and for tests} */
    public static int overrideCount() {
        return OVERRIDES.size();
    }

    private static Map<Identifier, TissueProfile> buildOverrides() {
        Map<Identifier, TissueProfile> profiles = new LinkedHashMap<>();

        // --- the End -------------------------------------------------------------------------
        profiles.put(id("enderman"), profile(ENDER, List.of("E-01", "E-04", "E-09"),
                "tissue.beyondtime.trait.enderman.water",
                "tissue.beyondtime.trait.enderman.teleport",
                "tissue.beyondtime.trait.enderman.microbe"));
        profiles.put(id("endermite"), profile(ENDER, List.of("E-21", "E-22"),
                "tissue.beyondtime.trait.endermite.sculk",
                "tissue.beyondtime.trait.endermite.pearl",
                "tissue.beyondtime.trait.endermite.distant"));
        profiles.put(id("shulker"), profile(ENDER, List.of("E-31", "E-34", "E-35"),
                "tissue.beyondtime.trait.shulker.stone",
                "tissue.beyondtime.trait.shulker.fold",
                "tissue.beyondtime.trait.shulker.ender"));
        profiles.put(id("ender_dragon"), profile(ENDER, List.of("E-00", "E-01", "E-40"),
                "tissue.beyondtime.trait.dragon.reflect",
                "tissue.beyondtime.trait.dragon.ender",
                "tissue.beyondtime.trait.dragon.heart"));

        // --- villages and their ancient relatives --------------------------------------------
        profiles.put(id("villager"), profile(VILLAGER, List.of("V-01", "V-03", "V-07"),
                "tissue.beyondtime.trait.villager.gem",
                "tissue.beyondtime.trait.villager.voice",
                "tissue.beyondtime.trait.villager.ancient"));
        profiles.put(id("wandering_trader"), profile(VILLAGER, List.of("V-11", "V-12"),
                "tissue.beyondtime.trait.trader.far",
                "tissue.beyondtime.trait.trader.potion",
                "tissue.beyondtime.trait.trader.villager"));
        profiles.put(id("pillager"), profile(ILLAGER, List.of("V-01", "I-02", "I-05"),
                "tissue.beyondtime.trait.illager.strong",
                "tissue.beyondtime.trait.illager.dye",
                "tissue.beyondtime.trait.illager.ancient"));
        profiles.put(id("vindicator"), profile(ILLAGER, List.of("V-01", "I-02", "I-06"),
                "tissue.beyondtime.trait.vindicator.arm",
                "tissue.beyondtime.trait.illager.strong",
                "tissue.beyondtime.trait.illager.ancient"));
        profiles.put(id("evoker"), profile(ILLAGER, List.of("V-01", "I-02", "I-09"),
                "tissue.beyondtime.trait.evoker.finger",
                "tissue.beyondtime.trait.evoker.vex",
                "tissue.beyondtime.trait.illager.ancient"));
        profiles.put(id("illusioner"), profile(ILLAGER, List.of("V-01", "I-02", "I-12"),
                "tissue.beyondtime.trait.illusioner.eye",
                "tissue.beyondtime.trait.illusioner.mirror",
                "tissue.beyondtime.trait.illager.ancient"));
        profiles.put(id("witch"), profile(VILLAGER, List.of("V-01", "V-20", "W-03"),
                "tissue.beyondtime.trait.witch.potion",
                "tissue.beyondtime.trait.witch.resist",
                "tissue.beyondtime.trait.witch.villager"));

        // --- the deep dark --------------------------------------------------------------------
        profiles.put(id("warden"), profile(SCULK, List.of("S-01", "S-02", "S-13"),
                "tissue.beyondtime.trait.warden.eyes",
                "tissue.beyondtime.trait.warden.organ",
                "tissue.beyondtime.trait.warden.light"));

        // --- the ancient ocean ----------------------------------------------------------------
        profiles.put(id("elder_guardian"), profile(AQUATIC, List.of("A-01", "A-02", "A-50"),
                "tissue.beyondtime.trait.elder.copper",
                "tissue.beyondtime.trait.elder.sediment",
                "tissue.beyondtime.trait.elder.pulse"));
        profiles.put(id("guardian"), profile(AQUATIC, List.of("A-01", "A-02", "A-11"),
                "tissue.beyondtime.trait.guardian.copper",
                "tissue.beyondtime.trait.guardian.air",
                "tissue.beyondtime.trait.guardian.pulse"));
        profiles.put(id("dolphin"), profile(AQUATIC, List.of("A-20", "A-21"),
                "tissue.beyondtime.trait.dolphin.lung",
                "tissue.beyondtime.trait.dolphin.clean",
                "tissue.beyondtime.trait.dolphin.sonar"));
        profiles.put(id("turtle"), profile(AQUATIC, List.of("A-30", "A-31", "A-32"),
                "tissue.beyondtime.trait.turtle.shell",
                "tissue.beyondtime.trait.turtle.copper",
                "tissue.beyondtime.trait.turtle.toxin"));
        profiles.put(id("axolotl"), profile(AQUATIC, List.of("A-40", "A-41"),
                "tissue.beyondtime.trait.axolotl.regen",
                "tissue.beyondtime.trait.axolotl.microbe",
                "tissue.beyondtime.trait.axolotl.dry"));
        profiles.put(id("squid"), profile(AQUATIC, List.of("A-60", "A-61"),
                "tissue.beyondtime.trait.squid.water",
                "tissue.beyondtime.trait.squid.ink",
                "tissue.beyondtime.trait.squid.dry"));
        profiles.put(id("glow_squid"), profile(AQUATIC, List.of("A-60", "A-62", "A-63"),
                "tissue.beyondtime.trait.glow_squid.light",
                "tissue.beyondtime.trait.glow_squid.clear",
                "tissue.beyondtime.trait.glow_squid.squid"));

        // --- the Nether, and what a wet world does to it --------------------------------------
        profiles.put(id("piglin"), profile(NETHER, List.of("N-01", "N-02", "N-10"),
                "tissue.beyondtime.trait.piglin.wet",
                "tissue.beyondtime.trait.piglin.ancient_water",
                "tissue.beyondtime.trait.piglin.fire"));
        profiles.put(id("piglin_brute"), profile(NETHER, List.of("N-01", "N-02", "N-11"),
                "tissue.beyondtime.trait.piglin.wet",
                "tissue.beyondtime.trait.brute.muscle",
                "tissue.beyondtime.trait.piglin.fire"));
        profiles.put(id("hoglin"), profile(NETHER, List.of("N-01", "N-20", "N-21"),
                "tissue.beyondtime.trait.hoglin.nose",
                "tissue.beyondtime.trait.hoglin.wet",
                "tissue.beyondtime.trait.hoglin.piglin"));
        profiles.put(id("zoglin"), profile(NETHER, List.of("N-01", "N-20", "N-22"),
                "tissue.beyondtime.trait.zoglin.necrotic",
                "tissue.beyondtime.trait.zoglin.nose",
                "tissue.beyondtime.trait.zoglin.rage"));
        profiles.put(id("zombified_piglin"), profile(NETHER, List.of("N-01", "N-02", "N-30"),
                "tissue.beyondtime.trait.zombified_piglin.dead",
                "tissue.beyondtime.trait.zombified_piglin.toxin",
                "tissue.beyondtime.trait.zombified_piglin.dry"));
        profiles.put(id("blaze"), profile(NETHER, List.of("N-40", "N-41"),
                "tissue.beyondtime.trait.blaze.fire",
                "tissue.beyondtime.trait.blaze.cells",
                "tissue.beyondtime.trait.blaze.dry"));
        profiles.put(id("ghast"), profile(NETHER, List.of("N-50", "N-51", "N-52"),
                "tissue.beyondtime.trait.ghast.water",
                "tissue.beyondtime.trait.ghast.soul",
                "tissue.beyondtime.trait.ghast.tear"));
        profiles.put(id("magma_cube"), profile(NETHER, List.of("N-60", "N-61"),
                "tissue.beyondtime.trait.magma.shell",
                "tissue.beyondtime.trait.magma.split",
                "tissue.beyondtime.trait.magma.fire"));
        profiles.put(id("wither_skeleton"), profile(NETHER, List.of("N-70", "N-71"),
                "tissue.beyondtime.trait.wither_skeleton.bone",
                "tissue.beyondtime.trait.wither_skeleton.muscle",
                "tissue.beyondtime.trait.wither_skeleton.wither"));
        profiles.put(id("wither"), profile(NETHER, List.of("N-70", "N-80", "N-81"),
                "tissue.beyondtime.trait.wither.heads",
                "tissue.beyondtime.trait.wither.drain",
                "tissue.beyondtime.trait.wither.deposit"));

        // --- the dead, and the water that keeps them ------------------------------------------
        profiles.put(id("zombie"), profile(UNDEAD, List.of("U-01", "U-02"),
                "tissue.beyondtime.trait.zombie.dead",
                "tissue.beyondtime.trait.zombie.dry",
                "tissue.beyondtime.trait.zombie.ancient_water"));
        profiles.put(id("drowned"), profile(UNDEAD, List.of("U-01", "U-03", "U-04"),
                "tissue.beyondtime.trait.drowned.lung",
                "tissue.beyondtime.trait.drowned.toxin",
                "tissue.beyondtime.trait.drowned.water"));

        // --- constructs -----------------------------------------------------------------------
        profiles.put(id("iron_golem"), profile(CONSTRUCT, List.of("C-01", "C-02"),
                "tissue.beyondtime.trait.iron_golem.body",
                "tissue.beyondtime.trait.iron_golem.rune",
                "tissue.beyondtime.trait.iron_golem.microbe"));
        profiles.put(id("snow_golem"), profile(CONSTRUCT, List.of("C-10", "C-11"),
                "tissue.beyondtime.trait.snow_golem.body",
                "tissue.beyondtime.trait.snow_golem.fae",
                "tissue.beyondtime.trait.snow_golem.dry"));

        // --- the very old world ---------------------------------------------------------------
        profiles.put(id("sniffer"), profile(BEAST, List.of("B-00", "B-01", "B-02"),
                "tissue.beyondtime.trait.sniffer.old",
                "tissue.beyondtime.trait.sniffer.spore",
                "tissue.beyondtime.trait.sniffer.overworld"));

        return Map.copyOf(profiles);
    }

    private static Identifier id(String path) {
        return Identifier.withDefaultNamespace(path);
    }

    private static TissueProfile profile(String groupKey, List<String> genome, String... traits) {
        return new TissueProfile(groupKey, genome, List.of(traits));
    }
}
