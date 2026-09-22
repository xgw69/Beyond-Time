package com.beyondtime.content.menu;

import com.beyondtime.content.block.entity.MicroscopeBlockEntity;
import com.beyondtime.registry.BTItems;
import com.beyondtime.registry.BTMenus;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/**
 * The microscope screen: one petri dish slot plus the player inventory.
 *
 * <p>The dish slot is limited to the dish item and to a single item, on both sides, so that the client
 * and the server agree on what fits.
 */
public class MicroscopeMenu extends AbstractContainerMenu {
    public static final int DISH_SLOT = MicroscopeBlockEntity.DISH_SLOT;

    /**
     * The panel is wider and taller than a chest, because it has to hold the whole microbe report.
     *
     * <p>It is still no larger than {@link #BACKGROUND_TEXTURE_SIZE} on either axis, so the
     * background PNG can stay a plain 256x256 with the panel in its top left corner.
     */
    public static final int PANEL_WIDTH = 256;
    public static final int PANEL_HEIGHT = 232;

    /** The dish slot, on the "stage" in the top left of the panel. */
    public static final int DISH_SLOT_X = 10;
    public static final int DISH_SLOT_Y = 22;

    /** The player inventory, centred under the report area. */
    public static final int INVENTORY_X = (PANEL_WIDTH - 9 * 18) / 2;
    public static final int INVENTORY_Y = 150;

    /**
     * Size of the background texture drawn by the screen.
     *
     * <p>Every container background, ours included, is a 256x256 PNG with the actual 176x166 panel in
     * its top left corner. Handing the game a smaller number here squeezes the panel and everything
     * drawn on it stops lining up with the slots.
     */
    public static final int BACKGROUND_TEXTURE_SIZE = 256;

    private final Container microscope;

    public MicroscopeMenu(int containerId, Inventory playerInventory, Container microscope) {
        super(BTMenus.MICROSCOPE.get(), containerId);
        this.microscope = microscope;
        this.addSlot(new Slot(microscope, DISH_SLOT, DISH_SLOT_X, DISH_SLOT_Y) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.is(BTItems.PETRI_DISH.get());
            }

            @Override
            public int getMaxStackSize() {
                return 1;
            }
        });
        this.addStandardInventorySlots(playerInventory, INVENTORY_X, INVENTORY_Y);
    }

    /** Factory used by the client, which only needs a placeholder container. */
    public static MicroscopeMenu createClient(int containerId, Inventory playerInventory) {
        return new MicroscopeMenu(containerId, playerInventory, new SimpleContainer(MicroscopeBlockEntity.SLOT_COUNT));
    }

    /** {@return the dish currently on the microscope stage, client and server side alike} */
    public ItemStack getDish() {
        return this.microscope.getItem(DISH_SLOT);
    }

    @Override
    public boolean stillValid(Player player) {
        return this.microscope.stillValid(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = this.slots.get(index);
        if (slot == null || !slot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack stack = slot.getItem();
        ItemStack result = stack.copy();
        if (index == DISH_SLOT) {
            if (!this.moveItemStackTo(stack, DISH_SLOT + 1, this.slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else if (!this.moveItemStackTo(stack, DISH_SLOT, DISH_SLOT + 1, false)) {
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
