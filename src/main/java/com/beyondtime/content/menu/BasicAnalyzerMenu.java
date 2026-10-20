package com.beyondtime.content.menu;

import com.beyondtime.content.block.BasicAnalyzerBlock;
import com.beyondtime.content.block.entity.BasicAnalyzerBlockEntity;
import com.beyondtime.content.item.TissueSampleItem;
import com.beyondtime.content.knowledge.KnowledgeEvents;
import com.beyondtime.content.knowledge.KnowledgeNodes;
import com.beyondtime.content.tissue.TissueAnalysis;
import com.beyondtime.content.tissue.TissueProfiles;
import com.beyondtime.content.tissue.TissueReport;
import com.beyondtime.content.tissue.TissueSample;
import com.beyondtime.registry.BTMenus;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.EntityType;
import org.jspecify.annotations.Nullable;

/**
 * The basic bio-analyzer screen: one sample slot, a written report, and the player inventory.
 *
 * <p>The report is kept in this menu, not in the block entity. The menu exists only while the screen
 * is open, so closing it throws the reading away - the basic analyzer cannot be used as an archive.
 * The reading itself is sent as four integers (state, entity registry id, two halves of the seed);
 * both sides rebuild the same text from those with {@link TissueReport}.
 */
public class BasicAnalyzerMenu extends AbstractContainerMenu {
    public static final int SAMPLE_SLOT = BasicAnalyzerBlockEntity.SAMPLE_SLOT;

    /** Wider than a chest because the report needs the room, still inside the 256x256 background. */
    public static final int PANEL_WIDTH = 200;
    public static final int PANEL_HEIGHT = 240;

    /** The sample well, at the top left under the title strip. */
    public static final int SAMPLE_SLOT_X = 8;
    public static final int SAMPLE_SLOT_Y = 20;

    /** Player inventory, centred under the report. */
    public static final int INVENTORY_X = (PANEL_WIDTH - 9 * 18) / 2;
    public static final int INVENTORY_Y = 156;

    public static final int BACKGROUND_TEXTURE_SIZE = 256;

    // Synced integers. The report text is rebuilt from these on the client, so no strings are sent.
    public static final int DATA_STATE = 0;
    public static final int DATA_ENTITY = 1;
    public static final int DATA_SEED_HIGH = 2;
    public static final int DATA_SEED_LOW = 3;
    public static final int DATA_NEW_KNOWLEDGE = 4;
    public static final int DATA_COUNT = 5;

    private final Container container;
    private final Player player;
    private final ContainerData data = new SimpleContainerData(DATA_COUNT);

    public BasicAnalyzerMenu(int containerId, Inventory inventory, Container container, Player player) {
        super(BTMenus.BASIC_ANALYZER.get(), containerId);
        this.container = container;
        this.player = player;

        this.addSlot(new Slot(container, SAMPLE_SLOT, SAMPLE_SLOT_X, SAMPLE_SLOT_Y) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return BasicAnalyzerBlock.isSample(stack);
            }

            @Override
            public int getMaxStackSize() {
                return 1;
            }
        });
        this.addStandardInventorySlots(inventory, INVENTORY_X, INVENTORY_Y);
        this.addDataSlots(this.data);
    }

    /** Factory used by the client, which only needs a placeholder container. */
    public static BasicAnalyzerMenu createClient(int containerId, Inventory inventory) {
        return new BasicAnalyzerMenu(
                containerId, inventory, new SimpleContainer(BasicAnalyzerBlockEntity.SLOT_COUNT), inventory.player);
    }

    /** {@return the sample currently in the slot} */
    public ItemStack getSample() {
        return this.container.getItem(SAMPLE_SLOT);
    }

    /**
     * Consumes the sample and updates the report, once per tick at most.
     *
     * <p>Doing this in {@code broadcastChanges} rather than in a slot callback matters because the
     * block entity is a plain {@link Container}: it does not notify the menu when its slot changes,
     * but the menu is ticked by the server every tick anyway.
     */
    @Override
    public void broadcastChanges() {
        if (!this.player.level().isClientSide()) {
            this.tryAnalyze();
        }

        super.broadcastChanges();
    }

    private void tryAnalyze() {
        ItemStack input = this.container.getItem(SAMPLE_SLOT);
        TissueSample sample = TissueSampleItem.sample(input);
        if (sample == null) {
            return;
        }

        // The sample is consumed. The report survives only in this menu.
        this.container.removeItem(SAMPLE_SLOT, 1);
        this.container.setChanged();

        EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.getValue(sample.entity());
        if (type == null) {
            this.data.set(DATA_STATE, 0);
            return;
        }

        long seed = sample.seed();
        Identifier entityId = sample.entity();
        this.data.set(DATA_STATE, 1);
        this.data.set(DATA_ENTITY, BuiltInRegistries.ENTITY_TYPE.getId(type));
        this.data.set(DATA_SEED_HIGH, (int) (seed >>> 32));
        this.data.set(DATA_SEED_LOW, (int) seed);

        boolean firstTissue = KnowledgeEvents.observe(this.player, KnowledgeNodes.tissue(entityId));
        boolean firstMachine = KnowledgeEvents.observe(this.player, KnowledgeNodes.machine("basic_analyzer"));
        this.data.set(DATA_NEW_KNOWLEDGE, firstTissue || firstMachine ? 1 : 0);
    }

    /** {@return the report to draw, or {@code null} when no sample has been analysed in this menu} */
    public @Nullable TissueReport report() {
        if (this.data.get(DATA_STATE) == 0) {
            return null;
        }

        EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.byId(this.data.get(DATA_ENTITY));
        if (type == null) {
            return null;
        }

        Identifier entityId = BuiltInRegistries.ENTITY_TYPE.getKey(type);
        if (entityId == null) {
            return null;
        }

        long seed = ((long) this.data.get(DATA_SEED_HIGH) << 32) | (this.data.get(DATA_SEED_LOW) & 0xFFFF_FFFFL);
        return new TissueReport(entityId, TissueProfiles.of(entityId), seed, TissueAnalysis.PARTIAL);
    }

    /** {@return whether the last analysis unlocked a knowledge node for the first time} */
    public boolean isNewKnowledge() {
        return this.data.get(DATA_NEW_KNOWLEDGE) != 0;
    }

    @Override
    public boolean stillValid(Player player) {
        return this.container.stillValid(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = this.slots.get(index);
        if (slot == null || !slot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack stack = slot.getItem();
        ItemStack result = stack.copy();
        if (index == SAMPLE_SLOT) {
            if (!this.moveItemStackTo(stack, SAMPLE_SLOT + 1, this.slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else if (BasicAnalyzerBlock.isSample(stack)) {
            if (!this.moveItemStackTo(stack, SAMPLE_SLOT, SAMPLE_SLOT + 1, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            return ItemStack.EMPTY;
        }

        if (stack.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        return result;
    }
}
