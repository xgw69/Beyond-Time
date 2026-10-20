package com.beyondtime.content.tissue;

import java.util.Locale;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;

/**
 * How much of a sealed tissue sample has been read.
 *
 * <p>A fresh sample is {@link #NONE}. The basic bio-analyzer turns it into a {@link #PARTIAL} reading
 * that is deliberately incomplete and is not stored anywhere; the advanced analyzer later produces a
 * {@link #FULL} reading and keeps it in its own archive.
 */
public enum TissueAnalysis implements StringRepresentable {
    /** Freshly taken, nothing read yet. */
    NONE("none"),
    /** Read by the basic analyzer: some traits, some missing signal, nothing archived. */
    PARTIAL("partial"),
    /** Read by the advanced analyzer: the whole profile, archived by the machine. */
    FULL("full");

    public static final Codec<TissueAnalysis> CODEC = StringRepresentable.fromEnum(TissueAnalysis::values);
    public static final StreamCodec<ByteBuf, TissueAnalysis> STREAM_CODEC =
            ByteBufCodecs.VAR_INT.map(index -> values()[index], TissueAnalysis::ordinal);

    private final String name;

    TissueAnalysis(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    /** {@return the translation key of the short label shown in item tooltips and reports} */
    public String translationKey() {
        return "tissue.beyondtime.analysis." + this.name;
    }

    /** {@return the same enum value for a saved or network name, or {@link #NONE} when unknown} */
    public static TissueAnalysis byName(String name) {
        for (TissueAnalysis analysis : values()) {
            if (analysis.name.equalsIgnoreCase(name)) {
                return analysis;
            }
        }

        return NONE;
    }

    /** {@return the lower-case name, for logs and data files} */
    public String lowerCaseName() {
        return this.name.toLowerCase(Locale.ROOT);
    }
}
