package com.beyondtime.datagen;

import com.beyondtime.registry.BTBlocks;
import com.beyondtime.registry.BTItems;
import com.beyondtime.BeyondTime;

import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.data.PackOutput;

/**
 * Block states, block models, item models and item definitions of Beyond-Time.
 *
 * <p>Every model points at a 32x32 texture in {@code assets/beyondtime/textures/} that the project
 * owner draws. The textures currently in the repository are placeholders.
 */
public class BTModelProvider extends ModelProvider {
    public BTModelProvider(PackOutput output) {
        super(output, BeyondTime.MODID);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        // A plain full cube for now; a bespoke model replaces it once the texture exists.
        blockModels.createTrivialCube(BTBlocks.MICROSCOPE.get());
        itemModels.generateFlatItem(BTItems.PETRI_DISH.get(), ModelTemplates.FLAT_ITEM);
    }
}
