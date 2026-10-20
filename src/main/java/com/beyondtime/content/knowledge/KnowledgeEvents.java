package com.beyondtime.content.knowledge;

import com.beyondtime.registry.BTAttachments;

import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

/**
 * The single door through which knowledge is unlocked.
 *
 * <p>Two verbs, and everything that grants knowledge uses one of them:
 *
 * <ul>
 *   <li>{@link #observe} - the player worked it out themselves, e.g. saw a microbe under the
 *       microscope or read a tissue report;
 *   <li>{@link #grant} - the story hands it to the player, e.g. the journal reveals 伪古水菌. This
 *       exists because a disguised microbe can never be "observed" as itself.
 * </ul>
 *
 * <p>Both are server-authoritative. On the client they do nothing and return {@code false}; the
 * client learns the result through the attachment's sync handler.
 */
public final class KnowledgeEvents {
    private KnowledgeEvents() {}

    /** {@return whether the player already has this node; safe on both sides} */
    public static boolean has(Player player, Identifier node) {
        return player.getData(BTAttachments.KNOWLEDGE).has(node);
    }

    /**
     * Records a node the player found out about by themselves.
     *
     * @return {@code true} when this was the first time, {@code false} when it was already known or
     *     the call happened on the client
     */
    public static boolean observe(Player player, Identifier node) {
        return add(player, node);
    }

    /**
     * Records a node the story grants rather than one the player could observe.
     *
     * @return {@code true} when this was the first time
     */
    public static boolean grant(Player player, Identifier node) {
        return add(player, node);
    }

    private static boolean add(Player player, Identifier node) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return false;
        }

        Knowledge knowledge = serverPlayer.getData(BTAttachments.KNOWLEDGE);
        boolean firstTime = !knowledge.has(node);
        serverPlayer.setData(BTAttachments.KNOWLEDGE, knowledge.observed(node));
        serverPlayer.syncData(BTAttachments.KNOWLEDGE);

        if (firstTime) {
            KnowledgeListeners.onNewNode(serverPlayer, node);
        }

        return firstTime;
    }
}
