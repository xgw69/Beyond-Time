package com.beyondtime.client.screen;

import com.beyondtime.BeyondTime;
import com.beyondtime.content.menu.MicroscopeMenu;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

/**
 * The microscope observation screen.
 *
 * <p>The sample report area on the right is a placeholder: it is laid out and drawn, but always says
 * that there is nothing to report, until the microbial catalogue exists.
 */
public class MicroscopeScreen extends AbstractContainerScreen<MicroscopeMenu> {
    private static final Identifier BACKGROUND =
            Identifier.fromNamespaceAndPath(BeyondTime.MODID, "textures/gui/microscope.png");

    private static final int REPORT_X = 48;
    private static final int REPORT_Y = 18;
    private static final int REPORT_WIDTH = 120;
    private static final int REPORT_HEIGHT = 48;

    public MicroscopeScreen(MicroscopeMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, 176, 166);
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        int xo = (this.width - this.imageWidth) / 2;
        int yo = (this.height - this.imageHeight) / 2;
        graphics.blit(
                RenderPipelines.GUI_TEXTURED,
                BACKGROUND,
                xo,
                yo,
                0.0F,
                0.0F,
                this.imageWidth,
                this.imageHeight,
                MicroscopeMenu.BACKGROUND_TEXTURE_SIZE,
                MicroscopeMenu.BACKGROUND_TEXTURE_SIZE);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractLabels(graphics, mouseX, mouseY);

        int xo = this.leftPos + REPORT_X;
        int yo = this.topPos + REPORT_Y;
        graphics.outline(xo, yo, REPORT_WIDTH, REPORT_HEIGHT, 0xFF8B8B8B);
        graphics.text(
                this.font,
                Component.translatable("gui.beyondtime.microscope.report_title"),
                xo + 4,
                yo + 4,
                0xFF404040,
                false);
        graphics.text(
                this.font,
                Component.translatable("gui.beyondtime.microscope.report_empty"),
                xo + 4,
                yo + 18,
                0xFF808080,
                false);
    }
}
