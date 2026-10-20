package com.beyondtime.content.tissue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;

/**
 * One analyser reading, ready to draw.
 *
 * <p>The report is a pure function of the sample's creature, its seed and the analysis level, so the
 * client can rebuild exactly the same text that the server saw without sending a single string. That
 * is what lets the basic analyzer keep its result only in the open menu: close the screen and the
 * reading is gone, exactly as {@code docs/MACHINES.md} asks, because the report was never stored.
 */
public record TissueReport(Identifier entity, TissueProfile profile, long seed, TissueAnalysis analysis) {
    /** A report shows this many traits when the sample is only partially read. */
    public static final int PARTIAL_TRAIT_COUNT = 2;

    /** {@return the report for a sample} */
    public static TissueReport of(TissueSample sample) {
        return new TissueReport(sample.entity(), TissueProfiles.of(sample.entity()), sample.seed(), sample.analysis());
    }

    /** {@return the creature's display name, or its id when the mod that added it is gone} */
    public Component entityName() {
        EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.getValue(this.entity);
        return type == null ? Component.literal(this.entity.toString()) : type.getDescription();
    }

    /** {@return the report lines, in drawing order} */
    public List<Line> lines() {
        boolean full = this.analysis == TissueAnalysis.FULL;
        List<Line> lines = new ArrayList<>();

        lines.add(new Line(Style.TITLE, this.entityName()));
        lines.add(new Line(Style.NORMAL, Component.translatable(
                "gui.beyondtime.basic_analyzer.analysis",
                Component.translatable(this.analysis.translationKey()))));
        lines.add(new Line(Style.NORMAL, Component.translatable(
                "gui.beyondtime.basic_analyzer.group", Component.translatable(this.profile.groupKey()))));
        lines.add(new Line(Style.NORMAL, Component.translatable(
                "gui.beyondtime.basic_analyzer.genome", Component.literal(this.genomeText(full)))));
        lines.add(new Line(Style.NORMAL, Component.translatable("gui.beyondtime.basic_analyzer.traits")));

        for (String trait : this.visibleTraits(full)) {
            lines.add(new Line(Style.TRAIT, Component.literal("· ")
                    .append(Component.translatable(trait))));
        }

        if (!full) {
            lines.add(new Line(Style.MISSING, Component.literal("· ")
                    .append(Component.translatable("gui.beyondtime.basic_analyzer.missing"))));
        }

        lines.add(new Line(Style.FOOTER, Component.translatable(full
                ? "gui.beyondtime.basic_analyzer.full_footer"
                : "gui.beyondtime.basic_analyzer.partial_footer")));
        return List.copyOf(lines);
    }

    private String genomeText(boolean full) {
        List<String> genome = this.profile.genome();
        if (full) {
            return String.join(" / ", genome);
        }

        int visible = Math.min(2, genome.size());
        return String.join(" / ", genome.subList(0, visible)) + " / ??";
    }

    /**
     * {@return the traits this reading can show}
     *
     * <p>A partial reading shows two, chosen by the sample's seed, so two samples from the same kind
     * of creature do not always reveal the same pair. A full reading shows all of them.
     */
    private List<String> visibleTraits(boolean full) {
        List<String> traits = new ArrayList<>(this.profile.traitKeys());
        if (full || traits.size() <= PARTIAL_TRAIT_COUNT) {
            return traits;
        }

        // Partial Fisher-Yates, seeded by the sample, so both sides pick the same pair.
        Random random = new Random(this.seed);
        for (int i = 0; i < PARTIAL_TRAIT_COUNT; i++) {
            int j = i + random.nextInt(traits.size() - i);
            Collections.swap(traits, i, j);
        }

        return List.copyOf(traits.subList(0, PARTIAL_TRAIT_COUNT));
    }

    /** How a line should be drawn. */
    public enum Style {
        /** The creature's name, at the top. */
        TITLE,
        /** A label/value line such as the group or the gene fragments. */
        NORMAL,
        /** One readable trait. */
        TRAIT,
        /** The line that says the rest of the signal is missing. */
        MISSING,
        /** The conclusion at the bottom. */
        FOOTER
    }

    /** One line of the report. */
    public record Line(Style style, Component text) {}
}
