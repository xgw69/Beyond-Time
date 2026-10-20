package com.beyondtime.content.knowledge;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

/**
 * Things that react to a newly unlocked knowledge node.
 *
 * <p>The intelligence lives in the listeners, not in {@link KnowledgeEvents}: recipe gates, the
 * journal's "new entry" toast and advancements will all subscribe here. Keeping the list explicit
 * means a feature can be removed without touching the knowledge storage.
 */
public final class KnowledgeListeners {
    /** One reaction to a node becoming known. */
    @FunctionalInterface
    public interface Listener {
        void onNewNode(ServerPlayer player, Identifier node);
    }

    private static final List<Listener> LISTENERS = new CopyOnWriteArrayList<>();

    private KnowledgeListeners() {}

    /** Registers a listener. Called once during mod setup. */
    public static void register(Listener listener) {
        LISTENERS.add(listener);
    }

    static void onNewNode(ServerPlayer player, Identifier node) {
        for (Listener listener : LISTENERS) {
            listener.onNewNode(player, node);
        }
    }
}
