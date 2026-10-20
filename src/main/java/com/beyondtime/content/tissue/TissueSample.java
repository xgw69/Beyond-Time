package com.beyondtime.content.tissue;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

/**
 * The payload of a sealed tissue sample.
 *
 * <p>One component serves the sampler, both analyzers, the editor, the culture vat and the cloning
 * pod, exactly as {@code docs/MACHINES.md} section 0.2 asks. It records which creature the tissue
 * came from, how much has been read, a seed that makes the partial report reproducible on both
 * sides, and the dimension it was taken in.
 *
 * @param entity the creature's entity type id, e.g. {@code minecraft:enderman}
 * @param analysis how much has been read
 * @param seed the seed the analyzer uses to choose which fragments are legible
 * @param dimension the dimension the sample was taken in
 */
public record TissueSample(Identifier entity, TissueAnalysis analysis, long seed, Identifier dimension) {
    public static final Codec<TissueSample> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("entity").forGetter(TissueSample::entity),
            TissueAnalysis.CODEC.fieldOf("analysis").forGetter(TissueSample::analysis),
            Codec.LONG.fieldOf("seed").forGetter(TissueSample::seed),
            Identifier.CODEC.fieldOf("dimension").forGetter(TissueSample::dimension)
    ).apply(instance, TissueSample::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, TissueSample> STREAM_CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC, TissueSample::entity,
            TissueAnalysis.STREAM_CODEC, TissueSample::analysis,
            ByteBufCodecs.VAR_LONG, TissueSample::seed,
            Identifier.STREAM_CODEC, TissueSample::dimension,
            TissueSample::new);

    /** {@return a fresh, unread sample} */
    public static TissueSample fresh(Identifier entity, Identifier dimension, long seed) {
        return new TissueSample(entity, TissueAnalysis.NONE, seed, dimension);
    }

    /** {@return a copy with the analysis level changed} */
    public TissueSample withAnalysis(TissueAnalysis analysis) {
        return new TissueSample(this.entity, analysis, this.seed, this.dimension);
    }
}
