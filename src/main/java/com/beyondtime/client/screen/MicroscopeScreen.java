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
 *
 * <p>Coordinates here come in two flavours, and mixing them up is what makes a container GUI look
 * broken:
 *
 * <ul>
 *   <li>the background is drawn in screen coordinates, so it is offset by {@code leftPos/topPos};
 *   <li>{@link #extractLabels} runs inside a pose that is <em>already</em> translated by
 *       {@code leftPos/topPos}, exactly like {@link AbstractContainerScreen}'s own labels, so every
 *       coordinate in there is relative to the GUI's top left corner.
 * </ul>
 *
 * <p>The background texture is 256x256 - the size every vanilla container background uses - with the
 * visible 176x166 panel drawn in its top left corner. See {@code docs/MICROSCOPE.md}.
 */
public class MicroscopeScreen extends AbstractContainerScreen<MicroscopeMenu> {
    private static final Identifier BACKGROUND =
            Identifier.fromNamespaceAndPath(BeyondTime.MODID, "textures/gui/microscope.png");

    private static final int REPORT_X = 48;
    private static final int REPORT_Y = 18;
    private static final int REPORT_WIDTH = 120;
    private static final int REPORT_HEIGHT = 48;

    private static final int COLOR_OUTLINE = 0xFF8B8B8B;
    private static final int COLOR_TITLE = 0xFF404040;
    private static final int COLOR_EMPTY = 0xFF808080;

    public MicroscopeScreen(MicroscopeMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, 176, 166);
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        graphics.blit(
                RenderPipelines.GUI_TEXTURED,
                BACKGROUND,
                this.leftPos,
                this.topPos,
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

        // GUI-local coordinates: the pose already sits at the top left corner of the panel.
        graphics.outline(REPORT_X, REPORT_Y, REPORT_WIDTH, REPORT_HEIGHT, COLOR_OUTLINE);
        graphics.text(
                this.font,
                Component.translatable("gui.beyondtime.microscope.report_title"),
                REPORT_X + 4,
                REPORT_Y + 4,
                COLOR_TITLE,
                false);
        graphics.text(
                this.font,
                Component.translatable("gui.beyondtime.microscope.report_empty"),
                REPORT_X + 4,
                REPORT_Y + 18,
                COLOR_EMPTY,
                false);
    }
}
