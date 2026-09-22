package com.beyondtime.content.microbe;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

/**
 * What a petri dish picked up.
 *
 * <p>This is the payload of {@code beyondtime:sample}: it is what a dish carries around, what the
 * microscope screen reads, and (later) what the journal is allowed to record. It is purely
 * informational - a sample never yields an item or a resource.
 *
 * @param origin where the dish was swabbed: a block, or the air of a dimension
 * @param readings how much of each microbe was found, unordered and unmerged
 * @param total the total number of microbes on the dish, i.e. what the screen calls 菌数
 */
public record MicrobeSample(Origin origin, List<Reading> readings, int total) {
    public static final Codec<MicrobeSample> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Origin.CODEC.fieldOf("origin").forGetter(MicrobeSample::origin),
            Reading.CODEC.listOf().fieldOf("readings").forGetter(MicrobeSample::readings),
            Codec.INT.fieldOf("total").forGetter(MicrobeSample::total)
    ).apply(instance, MicrobeSample::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, MicrobeSample> STREAM_CODEC = StreamCodec.composite(
            Origin.STREAM_CODEC, MicrobeSample::origin,
            Reading.STREAM_CODEC.apply(ByteBufCodecs.list()), MicrobeSample::readings,
            ByteBufCodecs.VAR_INT, MicrobeSample::total,
            MicrobeSample::new);

    /**
     * Where a sample came from.
     *
     * @param id the block or the dimension
     * @param air {@code true} when this is the air of {@code id}, {@code false} when {@code id} is a
     *     block that was swabbed
     */
    public record Origin(Identifier id, boolean air) {
        public static final Codec<Origin> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Identifier.CODEC.fieldOf("id").forGetter(Origin::id),
                Codec.BOOL.fieldOf("air").forGetter(Origin::air)
        ).apply(instance, Origin::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, Origin> STREAM_CODEC = StreamCodec.composite(
                Identifier.STREAM_CODEC, Origin::id,
                ByteBufCodecs.BOOL, Origin::air,
                Origin::new);

        public static Origin ofBlock(Block block) {
            return new Origin(BuiltInRegistries.BLOCK.getKey(block), false);
        }

        public static Origin ofAir(Level level) {
            return new Origin(level.dimension().identifier(), true);
        }

        /** {@return the name to show on the screen and in the tooltip} */
        public Component displayName() {
            if (!this.air) {
                Block block = BuiltInRegistries.BLOCK.getValue(this.id);
                return block.getName();
            }

            return Component.translatable("microbe.beyondtime.origin.air", dimensionName());
        }

        /** {@return the dimension's name, used inside {@link #displayName()}} */
        public Component dimensionName() {
            return Component.translatable("dimension." + this.id.getNamespace() + "." + this.id.getPath());
        }
    }

    /** One line of the report: how much of a single microbe was found. */
    public record Reading(Identifier microbe, int amount) {
        public static final Codec<Reading> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Identifier.CODEC.fieldOf("microbe").forGetter(Reading::microbe),
                Codec.INT.fieldOf("amount").forGetter(Reading::amount)
        ).apply(instance, Reading::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, Reading> STREAM_CODEC = StreamCodec.composite(
                Identifier.STREAM_CODEC, Reading::microbe,
                ByteBufCodecs.VAR_INT, Reading::amount,
                Reading::new);
    }

    /**
     * {@return the report as the microscope shows it: sorted by amount, with disguised microbes
     * folded into the microbe they look like}
     *
     * <p>This is why a water sample reports one big 古水菌 number: the 伪古水菌 in it is being counted
     * as 古水菌 until the journal says otherwise.
     */
    public List<Reading> report() {
        Map<Identifier, Integer> merged = new LinkedHashMap<>();
        for (Reading reading : this.readings) {
            Microbe microbe = Microbes.byId(reading.microbe());
            Identifier shown = microbe == null ? reading.microbe() : microbe.visibleAs().id();
            merged.merge(shown, reading.amount(), Integer::sum);
        }

        List<Reading> report = new ArrayList<>(merged.size());
        merged.forEach((microbe, amount) -> report.add(new Reading(microbe, amount)));
        report.sort(Comparator.comparingInt(Reading::amount).reversed());
        return List.copyOf(report);
    }

    /** {@return the share of this sample that the given amount makes up, as a percentage} */
    public double percent(int amount) {
        return this.total <= 0 ? 0.0 : 100.0 * amount / this.total;
    }

    /** {@return how many kinds of microbe the microscope can show for this sample} */
    public int speciesCount() {
        return report().size();
    }

}
