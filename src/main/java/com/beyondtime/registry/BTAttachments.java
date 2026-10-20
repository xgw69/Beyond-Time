package com.beyondtime.registry;

import com.beyondtime.content.knowledge.Knowledge;

import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;

/**
 * Data attachments added by Beyond-Time.
 *
 * <p>Attachments are the modern NeoForge way to hang extra per-object data on a vanilla object
 * without a mixin or a capability. {@link #KNOWLEDGE} is the first one: one per player, saved with
 * the player, copied on death and synced to that player's client so a future journal screen can read
 * it without a request packet.
 */
public final class BTAttachments {
    /** Everything the player has worked out so far. See {@link Knowledge}. */
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Knowledge>> KNOWLEDGE =
            BTRegistries.ATTACHMENT_TYPES.register(
                    "knowledge",
                    () -> AttachmentType.builder(() -> new Knowledge())
                            .serialize(Knowledge.CODEC)
                            .copyOnDeath()
                            .sync(Knowledge.STREAM_CODEC)
                            .build());

    private BTAttachments() {}

    /** Forces this class to initialise so that the attachments above are declared in time. */
    public static void bootstrap() {}
}
