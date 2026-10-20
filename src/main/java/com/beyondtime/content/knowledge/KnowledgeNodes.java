package com.beyondtime.content.knowledge;

import com.beyondtime.BeyondTime;

import net.minecraft.resources.Identifier;

/**
 * How knowledge node ids are spelled.
 *
 * <p>Ids are deliberately readable and stable, because they end up in save files, in recipe gates and
 * in the journal. Every node lives in the {@code beyondtime} namespace even when it talks about a
 * vanilla microbe or entity.
 *
 * <ul>
 *   <li>{@code beyondtime:microbe/ancient_water} - seen under the microscope
 *   <li>{@code beyondtime:tissue/enderman} - analysed a sealed tissue sample
 *   <li>{@code beyondtime:machine/basic_analyzer} - worked out a machine or its recipe
 * </ul>
 */
public final class KnowledgeNodes {
    private KnowledgeNodes() {}

    /** {@return the node for having seen a microbe under the microscope} */
    public static Identifier microbe(Identifier microbeId) {
        return node("microbe/" + microbeId.getPath());
    }

    /** {@return the node for having analysed a creature's tissue} */
    public static Identifier tissue(Identifier entityId) {
        return node("tissue/" + entityId.getPath());
    }

    /** {@return the node for having unlocked a machine or one of its recipes} */
    public static Identifier machine(String machineId) {
        return node("machine/" + machineId);
    }

    /** {@return an arbitrary node below this mod's namespace} */
    public static Identifier node(String path) {
        return Identifier.fromNamespaceAndPath(BeyondTime.MODID, path);
    }
}
