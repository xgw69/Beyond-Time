package com.beyondtime.registry;

import com.beyondtime.content.block.entity.MicroscopeBlockEntity;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;

/** Block entities added by Beyond-Time. */
public final class BTBlockEntities {
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MicroscopeBlockEntity>> MICROSCOPE =
            BTRegistries.BLOCK_ENTITY_TYPES.register(
                    "microscope",
                    () -> new BlockEntityType<>(MicroscopeBlockEntity::new, BTBlocks.MICROSCOPE.get()));

    private BTBlockEntities() {}

    /** Forces this class to initialise so that the block entities above are declared in time. */
    public static void bootstrap() {}
}
