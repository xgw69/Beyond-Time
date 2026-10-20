package com.beyondtime.content.block.entity;

import com.beyondtime.content.block.BasicAnalyzerBlock;
import com.beyondtime.content.menu.BasicAnalyzerMenu;
import com.beyondtime.registry.BTBlockEntities;

import net.minecraft.core.BlockPos;
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
 * The single sample slot of a basic bio-analyzer.
 *
 * <p>Only the input is stored. The report deliberately is not: it lives in the open
 * {@link BasicAnalyzerMenu}, so closing the screen throws it away, which is exactly the basic
 * analyzer's limitation. Breaking the block drops the unread sample, but never a report.
 */
public class BasicAnalyzerBlockEntity extends BlockEntity implements Container, MenuProvider {
    public static final int SAMPLE_SLOT = 0;
    public static final int SLOT_COUNT = 1;
    private static final String SAMPLE_KEY = "sample";

    private ItemStack sample = ItemStack.EMPTY;

    public BasicAnalyzerBlockEntity(BlockPos pos, BlockState state) {
        super(BTBlockEntities.BASIC_ANALYZER.get(), pos, state);
    }

    public ItemStack getSample() {
        return this.sample;
    }

    public void setSample(ItemStack stack) {
        this.sample = stack;
        this.setChanged();
    }

    // Container

    @Override
    public int getContainerSize() {
        return SLOT_COUNT;
    }

    @Override
    public boolean isEmpty() {
        return this.sample.isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        return this.sample;
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack removed = this.sample.split(amount);
        if (this.sample.isEmpty()) {
            this.sample = ItemStack.EMPTY;
        }

        this.setChanged();
        return removed;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        ItemStack removed = this.sample;
        this.sample = ItemStack.EMPTY;
        return removed;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        this.sample = stack;
        this.setChanged();
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return BasicAnalyzerBlock.isSample(stack);
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public void clearContent() {
        this.sample = ItemStack.EMPTY;
        this.setChanged();
    }

    // MenuProvider

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new BasicAnalyzerMenu(containerId, inventory, this, player);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.beyondtime.basic_analyzer");
    }

    // Persistence

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.sample = input.read(SAMPLE_KEY, ItemStack.CODEC).orElse(ItemStack.EMPTY);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        if (!this.sample.isEmpty()) {
            output.store(SAMPLE_KEY, ItemStack.CODEC, this.sample);
        }
    }

    /** Breaking the block drops the sample instead of deleting it. */
    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        super.preRemoveSideEffects(pos, state);
        if (!this.sample.isEmpty() && this.level != null) {
            net.minecraft.world.Containers.dropItemStack(
                    this.level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, this.sample);
            this.sample = ItemStack.EMPTY;
        }
    }
}
