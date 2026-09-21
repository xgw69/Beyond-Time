package com.beyondtime.datagen;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

/** Wires {@link BTBlockLoot} into the data generator. */
public class BTLootTableProvider extends LootTableProvider {
    public BTLootTableProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(
                output,
                Set.of(),
                List.of(new LootTableProvider.SubProviderEntry(BTBlockLoot::new, LootContextParamSets.BLOCK)),
                registries);
    }
}
