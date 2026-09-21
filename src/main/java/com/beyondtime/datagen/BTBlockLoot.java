package com.beyondtime.datagen;

import java.util.List;
import java.util.Set;

import com.beyondtime.registry.BTBlocks;
import com.beyondtime.registry.BTDataComponents;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

/** Block loot tables of Beyond-Time. */
public class BTBlockLoot extends BlockLootSubProvider {
    public BTBlockLoot(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        this.add(BTBlocks.MICROSCOPE.get(), this::createMicroscopeDrop);
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return List.of(BTBlocks.MICROSCOPE.get());
    }

    /**
     * Breaking a microscope drops the microscope itself, carrying whatever petri dish was inside it.
     * The dish is never dropped as a separate item.
     */
    private LootTable.Builder createMicroscopeDrop(Block microscope) {
        return LootTable.lootTable()
                .withPool(
                        this.applyExplosionCondition(
                                microscope,
                                LootPool.lootPool()
                                        .setRolls(ConstantValue.exactly(1.0F))
                                        .add(
                                                LootItem.lootTableItem(microscope)
                                                        .apply(
                                                                CopyComponentsFunction.copyComponentsFromBlockEntity(
                                                                                LootContextParams.BLOCK_ENTITY)
                                                                        .include(DataComponents.CUSTOM_NAME)
                                                                        .include(BTDataComponents.CONTAINED_DISH.get())))));
    }
}
