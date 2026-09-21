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

    public BTModelProvider(PackOutput output) {
        super(output, BeyondTime.MODID);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        // Four block states (one per facing) all pointing at the same model, rotated by the game.
        blockModels.blockStateOutput.accept(MultiVariantGenerator
                .dispatch(BTBlocks.MICROSCOPE.get(), BlockModelGenerators.plainVariant(MICROSCOPE_MODEL))
                .with(BlockModelGenerators.ROTATION_HORIZONTAL_FACING));

        itemModels.generateFlatItem(BTItems.PETRI_DISH.get(), ModelTemplates.FLAT_ITEM);
    }
}
