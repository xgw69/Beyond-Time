package com.beyondtime.datagen;

import com.beyondtime.BeyondTime;
import com.beyondtime.registry.BTBlocks;
import com.beyondtime.registry.BTItems;

import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;

/**
 * Block states, block models, item models and item definitions of Beyond-Time.
 *
 * <p>Only the block <em>state</em> of the microscope is generated here. The model itself is written
 * by hand in {@code src/main/resources/assets/beyondtime/models/block/microscope.json}, because it is
 * a shape made of several boxes rather than a cube, and because it has to stay in step with the
 * matching collision shape in {@code MicroscopeBlock#NORTH_SHAPE}. {@code docs/MICROSCOPE.md}
 * documents both halves and the alternative shapes.
 *
 * <p>Every texture referred to by that model is a 32x32 PNG in
 * {@code assets/beyondtime/textures/} that the project owner draws. The textures currently in the
 * repository are placeholders.
 */
public class BTModelProvider extends ModelProvider {
    /** The hand-written microscope model, as a model id. */
    public static final Identifier MICROSCOPE_MODEL =
            Identifier.fromNamespaceAndPath(BeyondTime.MODID, "block/microscope");

    /** The hand-written six-face basic-analyzer model. */
    public static final Identifier BASIC_ANALYZER_MODEL =
            Identifier.fromNamespaceAndPath(BeyondTime.MODID, "block/basic_analyzer");

    public BTModelProvider(PackOutput output) {
        super(output, BeyondTime.MODID);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        // Four block states (one per facing) all pointing at the same model, rotated by the game.
        blockModels.blockStateOutput.accept(MultiVariantGenerator
                .dispatch(BTBlocks.MICROSCOPE.get(), BlockModelGenerators.plainVariant(MICROSCOPE_MODEL))
                .with(BlockModelGenerators.ROTATION_HORIZONTAL_FACING));
        blockModels.registerSimpleItemModel(BTBlocks.MICROSCOPE.get(), MICROSCOPE_MODEL);

        // The basic analyzer is a full cube: one hand-written model with six separate face textures,
        // rotated to face the player. No uv tricks, no single-texture fallback (rule R10).
        blockModels.blockStateOutput.accept(MultiVariantGenerator
                .dispatch(BTBlocks.BASIC_ANALYZER.get(), BlockModelGenerators.plainVariant(BASIC_ANALYZER_MODEL))
                .with(BlockModelGenerators.ROTATION_HORIZONTAL_FACING));
        blockModels.registerSimpleItemModel(BTBlocks.BASIC_ANALYZER.get(), BASIC_ANALYZER_MODEL);

        itemModels.generateFlatItem(BTItems.PETRI_DISH.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(BTItems.SAMPLER.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(BTItems.SEALED_TISSUE_SAMPLE.get(), ModelTemplates.FLAT_ITEM);
    }
}
