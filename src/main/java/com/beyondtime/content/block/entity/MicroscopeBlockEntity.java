package com.beyondtime.content.block.entity;

import com.beyondtime.content.menu.MicroscopeMenu;
import com.beyondtime.registry.BTBlockEntities;
import com.beyondtime.registry.BTDataComponents;
import com.beyondtime.registry.BTItems;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/**
 * The single petri dish slot of a microscope.
 *
 * <p>The dish is stored in two places on purpose:
 *
 * <ul>
 *   <li>the block entity saves it so it survives a reload,
 *   <li>it is exposed as {@link BTDataComponents#CONTAINED_DISH} so that breaking the block drops a
 *       microscope item that still carries the dish, and so that placing that item back restores it.
 * </ul>
 */
public class MicroscopeBlockEntity extends BlockEntity implements Container, MenuProvider {
    public static final int DISH_SLOT = 0;
    public static final int SLOT_COUNT = 1;

    private static final String DISH_KEY = "dish";

    private ItemStack dish = ItemStack.EMPTY;

    public MicroscopeBlockEntity(BlockPos pos, BlockState state) {
        super(BTBlockEntities.MICROSCOPE.get(), pos, state);
    }

    public ItemStack getDish() {
        return this.dish;
    }

    public void setDish(ItemStack stack) {
        this.dish = stack;
        this.setChanged();
    }

    // Container

    @Override
    public int getContainerSize() {
        return SLOT_COUNT;
    }

    @Override
    public boolean isEmpty() {
        return this.dish.isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        return this.dish;
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack removed = this.dish.split(amount);
        if (this.dish.isEmpty()) {
            this.dish = ItemStack.EMPTY;
        }

        this.setChanged();
        return removed;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        ItemStack removed = this.dish;
        this.dish = ItemStack.EMPTY;
        return removed;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        this.dish = stack;
        this.setChanged();
    }

    /** A microscope holds exactly one dish, whatever the dish stacks to in an inventory. */
    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return stack.is(BTItems.PETRI_DISH.get());
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public void clearContent() {
        this.dish = ItemStack.EMPTY;
        this.setChanged();
    }

    // MenuProvider

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new MicroscopeMenu(containerId, inventory, this);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.beyondtime.microscope");
    }

    // Persistence

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.dish = input.read(DISH_KEY, ItemStack.CODEC).orElse(ItemStack.EMPTY);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        if (!this.dish.isEmpty()) {
            output.store(DISH_KEY, ItemStack.CODEC, this.dish);
        }
    }

    // Dropping and placing through item components

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        if (!this.dish.isEmpty()) {
            components.set(BTDataComponents.CONTAINED_DISH.get(), this.dish);
        }
    }

    @Override
    protected void applyImplicitComponents(DataComponentGetter components) {
        super.applyImplicitComponents(components);
        this.dish = components.getOrDefault(BTDataComponents.CONTAINED_DISH.get(), ItemStack.EMPTY);
    }
}
