package com.beyondtime.content.microbe;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

/**
 * The microbes of Beyond-Time.
 *
 * <p>The eleven entries and their descriptions come from the author; the wording of each description
 * lives in {@code docs/LORE_MICROBES.md}. This class only says what exists in code.
 *
 * <p>Ordering matters in one place: {@link #CATALOG} is what the journal will list, and
 * {@link #FALSE_ANCIENT_WATER} is deliberately <em>not</em> in it. See {@link Microbe#appearance()}.
 */
public final class Microbes {
    /** 1 - 古水菌. Wet places, survives boiling, suspected magic structure. */
    public static final Microbe ANCIENT_WATER = Microbe.of("ancient_water");

    /** 2 - 岩菌. Slowly breaks rock down. Everywhere. */
    public static final Microbe STONE = Microbe.of("stone");

    /** 3 - 仙灵. No understandable physiology; gathers around magical objects. */
    public static final Microbe FAE = Microbe.of("fae");

    /** 4 - 火元素. Clearly engineered; common in the Nether; suppresses 古水菌. */
    public static final Microbe FIRE = Microbe.of("fire");

    /** 5 - 绯红霉. Spreads by spores, lives with nether wart. */
    public static final Microbe CRIMSON_MOLD = Microbe.of("crimson_mold");

    /** 6 - 末影菌. Tied to endermen; unstable, occasionally annihilates itself. */
    public static final Microbe ENDER = Microbe.of("ender");

    /** 7 - 铜菌. Eats biomass, stops copper from oxidising, dies off in unexplained waves. */
    public static final Microbe COPPER = Microbe.of("copper");

    /** 8 - 木质霉. Eats logs. */
    public static final Microbe WOOD_MOLD = Microbe.of("wood_mold");

    /** 9 - 闪亮菌. Faint light; only on blocks that glow at light level 1; never in air. */
    public static final Microbe GLIMMER = Microbe.of("glimmer");

    /** 10 - 奇异菌. Widespread, odd looking. */
    public static final Microbe BIZARRE = Microbe.of("bizarre");

    /**
     * 11 - 伪古水菌. Water only, poisonous to endermen, converts to and from 古水菌.
     *
     * <p>Reported as {@link #ANCIENT_WATER} until the journal reveals it, which is why it is missing
     * from {@link #CATALOG}.
     */
    public static final Microbe FALSE_ANCIENT_WATER = Microbe.of("false_ancient_water").appearsAs(ANCIENT_WATER);

    /** Every microbe, including the ones the player has not been told about yet. */
    public static final List<Microbe> ALL = List.of(
            ANCIENT_WATER, STONE, FAE, FIRE, CRIMSON_MOLD, ENDER, COPPER, WOOD_MOLD, GLIMMER, BIZARRE, FALSE_ANCIENT_WATER);

    /** The microbes the journal has an entry for. Missing {@link #FALSE_ANCIENT_WATER} on purpose. */
    public static final List<Microbe> CATALOG = List.of(
            ANCIENT_WATER, STONE, FAE, FIRE, CRIMSON_MOLD, ENDER, COPPER, WOOD_MOLD, GLIMMER, BIZARRE);

    private static final Map<Identifier, Microbe> BY_ID = buildIndex();

    private Microbes() {}

    private static Map<Identifier, Microbe> buildIndex() {
        Map<Identifier, Microbe> index = new LinkedHashMap<>();
        for (Microbe microbe : ALL) {
            index.put(microbe.id(), microbe);
        }

        return Map.copyOf(index);
    }

    /** {@return the microbe with this id, or {@code null} if a sample named one that no longer exists} */
    public static @Nullable Microbe byId(Identifier id) {
        return BY_ID.get(id);
    }
}
