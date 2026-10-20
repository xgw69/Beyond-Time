package com.beyondtime.registry;

import com.beyondtime.BeyondTime;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

/**
 * Entity type tags added by Beyond-Time.
 *
 * <p>The samplable list is a tag rather than a Java set on purpose: the list is content, and content
 * belongs in JSON. Editing {@code data/beyondtime/tags/entity_type/samplable.json} changes both the
 * sampler and the cloning pod later, without a recompile, and a datapack can extend or trim it.
 */
public final class BTEntityTags {
    /**
     * Creatures whose death with a sampler yields a sealed tissue sample.
     *
     * <p>This is the draft list from {@code docs/MACHINES.md} section 1. The author can delete or add
     * entries in the JSON file; nothing in the code names an individual creature.
     */
    public static final TagKey<EntityType<?>> SAMPLABLE =
            TagKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(BeyondTime.MODID, "samplable"));

    private BTEntityTags() {}
}
