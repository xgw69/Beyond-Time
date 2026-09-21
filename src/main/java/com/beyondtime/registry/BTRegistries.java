package com.beyondtime.registry;

import com.beyondtime.BeyondTime;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Every {@link DeferredRegister} of the mod, in one place.
 *
 * <p>Registries for world generation (biomes, dimension types, level stems, structures,
 * features) are added by the world generation step, because they are registered differently
 * from ordinary content.
 */
public final class BTRegistries {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(BeyondTime.MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(BeyondTime.MODID);
    public static final DeferredRegister.Entities ENTITY_TYPES = DeferredRegister.createEntities(BeyondTime.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, BeyondTime.MODID);
    public static final DeferredRegister<MenuType<?>> MENU_TYPES =
            DeferredRegister.create(Registries.MENU, BeyondTime.MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, BeyondTime.MODID);
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
            DeferredRegister.create(Registries.PARTICLE_TYPE, BeyondTime.MODID);
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(Registries.SOUND_EVENT, BeyondTime.MODID);
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, BeyondTime.MODID);
    public static final DeferredRegister.DataComponents DATA_COMPONENTS =
            DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, BeyondTime.MODID);

    private BTRegistries() {}

    /** Attaches every registry of the mod to the mod event bus. */
    public static void register(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        ENTITY_TYPES.register(modEventBus);
        BLOCK_ENTITY_TYPES.register(modEventBus);
        MENU_TYPES.register(modEventBus);
        CREATIVE_TABS.register(modEventBus);
        PARTICLE_TYPES.register(modEventBus);
        SOUND_EVENTS.register(modEventBus);
        RECIPE_TYPES.register(modEventBus);
        DATA_COMPONENTS.register(modEventBus);

        // A class is only initialised the first time it is touched, and the deferred entries of a
        // content class are declared in its static initialiser. Touching every content class here,
        // while the mod is still being constructed, is therefore what actually fills the registers
        // above; without it the entries would first be created from a data generator or from a
        // recipe, long after the registry events have already fired.
        BTDataComponents.bootstrap();
        BTBlocks.bootstrap();
        BTBlockEntities.bootstrap();
        BTItems.bootstrap();
        BTMenus.bootstrap();
        BTCreativeTab.bootstrap();
    }
}
