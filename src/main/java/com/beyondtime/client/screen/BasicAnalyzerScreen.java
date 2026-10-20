package com.beyondtime.client.screen;

import java.util.List;

import com.beyondtime.BeyondTime;
import com.beyondtime.content.menu.BasicAnalyzerMenu;
import com.beyondtime.content.tissue.TissueReport;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

/**
 * The basic bio-analyzer screen.
 *
 * <p>A sample well at the top left, a written report on the parchment below, and the player inventory
 * at the bottom. The report is rebuilt every frame from the menu's synced integers
 * ({@link BasicAnalyzerMenu#report()}), so the screen never has to store text.
 *
 * <p>The report area is deliberately drawn like a page torn out of a notebook: dark ink for labels,
 * green for readable traits, faded red for the parts the machine could not read.
 */
public class BasicAnalyzerScreen extends AbstractContainerScreen<BasicAnalyzerMenu> {
    private static final Identifier BACKGROUND =
            Identifier.fromNamespaceAndPath(BeyondTime.MODID, "textures/gui/basic_analyzer.png");

    private static final int REPORT_X = 12;
    private static final int REPORT_Y = 48;
    private static final int REPORT_LINE_HEIGHT = 10;

    private static final int COLOR_TITLE = 0xFF3B2A1A;
    private static final int COLOR_NORMAL = 0xFF4A3A28;
    private static final int COLOR_TRAIT = 0xFF2F4A2A;
    private static final int COLOR_MISSING = 0xFF8A4A3A;
    private static final int COLOR_FOOTER = 0xFF6B5A3A;
    private static final int COLOR_HINT = 0xFF6B5A3A;
    private static final int COLOR_NEW = 0xFF2E6B34;

    public BasicAnalyzerScreen(BasicAnalyzerMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, BasicAnalyzerMenu.PANEL_WIDTH, BasicAnalyzerMenu.PANEL_HEIGHT);
        this.inventoryLabelX = BasicAnalyzerMenu.INVENTORY_X;
        this.inventoryLabelY = BasicAnalyzerMenu.INVENTORY_Y - 12;
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
                BasicAnalyzerMenu.BACKGROUND_TEXTURE_SIZE,
                BasicAnalyzerMenu.BACKGROUND_TEXTURE_SIZE);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractLabels(graphics, mouseX, mouseY);

        // GUI-local coordinates, like the microscope screen.
        graphics.text(
                this.font,
                Component.translatable("gui.beyondtime.basic_analyzer.sample"),
                BasicAnalyzerMenu.SAMPLE_SLOT_X + 24,
                BasicAnalyzerMenu.SAMPLE_SLOT_Y + 3,
                COLOR_NORMAL,
                false);
        graphics.text(
                this.font,
                Component.translatable("gui.beyondtime.basic_analyzer.consume_hint"),
                BasicAnalyzerMenu.SAMPLE_SLOT_X + 24,
                BasicAnalyzerMenu.SAMPLE_SLOT_Y + 15,
                COLOR_HINT,
                false);

        TissueReport report = this.menu.report();
        if (report == null) {
            extractEmptyReport(graphics);
            return;
        }

        extractReport(graphics, report);

        if (this.menu.isNewKnowledge()) {
            graphics.text(
                    this.font,
                    Component.translatable("gui.beyondtime.basic_analyzer.recorded"),
                    REPORT_X,
                    REPORT_Y + 9 * REPORT_LINE_HEIGHT,
                    COLOR_NEW,
                    false);
        }
    }

    private void extractEmptyReport(GuiGraphicsExtractor graphics) {
        graphics.text(
                this.font,
                Component.translatable("gui.beyondtime.basic_analyzer.empty_title"),
                REPORT_X,
                REPORT_Y,
                COLOR_NORMAL,
                false);
        graphics.text(
                this.font,
                Component.translatable("gui.beyondtime.basic_analyzer.empty_line_1"),
                REPORT_X,
                REPORT_Y + REPORT_LINE_HEIGHT,
                COLOR_HINT,
                false);
        graphics.text(
                this.font,
                Component.translatable("gui.beyondtime.basic_analyzer.empty_line_2"),
                REPORT_X,
                REPORT_Y + REPORT_LINE_HEIGHT * 2,
                COLOR_HINT,
                false);
    }

    private void extractReport(GuiGraphicsExtractor graphics, TissueReport report) {
        List<TissueReport.Line> lines = report.lines();
        for (int i = 0; i < lines.size(); i++) {
            TissueReport.Line line = lines.get(i);
            int color = switch (line.style()) {
                case TITLE -> COLOR_TITLE;
                case NORMAL -> COLOR_NORMAL;
                case TRAIT -> COLOR_TRAIT;
                case MISSING -> COLOR_MISSING;
                case FOOTER -> COLOR_FOOTER;
            };

            graphics.text(
                    this.font,
                    line.text(),
                    REPORT_X,
                    REPORT_Y + i * REPORT_LINE_HEIGHT,
                    color,
                    false);
        }
    }
}
