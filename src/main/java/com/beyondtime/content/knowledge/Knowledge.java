package com.beyondtime.content.knowledge;

import java.util.HashMap;
import java.util.Map;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

/**
 * One player's accumulated knowledge.
 *
 * <p>Everything the player has worked out is a <em>node</em>: a microbe seen under the microscope, a
 * creature whose tissue has been analysed, a machine whose recipe has been worked out. The journal,
 * the recipe book and the machines all read the same set, so a new kind of unlock only has to add a
 * node rather than a new storage system.
 *
 * <p>This is a plain immutable value. {@link KnowledgeEvents} replaces the attachment with a new
 * instance after every change, which makes the attachment's sync and save handling simple.
 *
 * @param nodes how often each node has been observed; a node that is present is unlocked
 */
public record Knowledge(Map<Identifier, Integer> nodes) {
    /** The value a player starts with: nothing known. */
    public static final Knowledge EMPTY = new Knowledge(Map.of());

    /**
     * The saved form. Nodes are stored as a map from node id to the number of times it was observed,
     * so an old save that has an id the current mod no longer knows about still loads.
     */
    public static final MapCodec<Knowledge> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.unboundedMap(Identifier.CODEC, Codec.INT)
                    .optionalFieldOf("nodes", Map.of())
                    .forGetter(Knowledge::nodes)
    ).apply(instance, Knowledge::new));

    /** The network form, used by the {@code beyondtime:knowledge} data attachment. */
    public static final StreamCodec<RegistryFriendlyByteBuf, Knowledge> STREAM_CODEC =
            new StreamCodec<>() {
                @Override
                public Knowledge decode(RegistryFriendlyByteBuf buffer) {
                    int size = buffer.readVarInt();
                    Map<Identifier, Integer> nodes = new HashMap<>(Math.min(size, 256));
                    for (int i = 0; i < size; i++) {
                        nodes.put(Identifier.STREAM_CODEC.decode(buffer), buffer.readVarInt());
                    }

                    return new Knowledge(nodes);
                }

                @Override
                public void encode(RegistryFriendlyByteBuf buffer, Knowledge knowledge) {
                    buffer.writeVarInt(knowledge.nodes().size());
                    knowledge.nodes().forEach((node, count) -> {
                        Identifier.STREAM_CODEC.encode(buffer, node);
                        buffer.writeVarInt(count);
                    });
                }
            };

    /** Default constructor used by the attachment's default-value supplier. */
    public Knowledge() {
        this(Map.of());
    }

    public Knowledge {
        nodes = Map.copyOf(nodes);
    }

    /** {@return whether this node is unlocked} */
    public boolean has(Identifier node) {
        return this.nodes.containsKey(node);
    }

    /** {@return how many times this node has been observed, or {@code 0} when it is not unlocked} */
    public int count(Identifier node) {
        return this.nodes.getOrDefault(node, 0);
    }

    /** {@return how many nodes are unlocked} */
    public int size() {
        return this.nodes.size();
    }

    /**
     * {@return a copy with this node counted one more time}
     *
     * <p>Counting even when the node is already known lets the journal say "seen again" without
     * inventing a second storage field. Use {@link #has(Identifier)} before the call when only the
     * first observation matters.
     */
    public Knowledge observed(Identifier node) {
        Map<Identifier, Integer> next = new HashMap<>(this.nodes);
        next.merge(node, 1, Integer::sum);
        return new Knowledge(next);
    }
}
