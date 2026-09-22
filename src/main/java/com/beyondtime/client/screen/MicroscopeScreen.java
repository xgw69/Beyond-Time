package com.beyondtime.client.screen;

import java.util.List;
import java.util.Locale;

import com.beyondtime.BeyondTime;
import com.beyondtime.content.item.PetriDishItem;
import com.beyondtime.content.menu.MicroscopeMenu;
import com.beyondtime.content.microbe.Microbe;
import com.beyondtime.content.microbe.MicrobeSample;
import com.beyondtime.content.microbe.Microbes;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

/**
 * The microscope observation screen.
 *
 * <p>Left of the top band is the stage with the dish in it; right of it the dish says where the
 * sample came from and how many microbes are on the plate. Under that is the report: one cell per
 * microbe, each an icon, a name and the share of the plate it makes up.
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
 * <p>The background texture is 256x256 with the visible {@link MicroscopeMenu#PANEL_WIDTH} x
 * {@link MicroscopeMenu#PANEL_HEIGHT} panel drawn in its top left corner. See
 * {@code docs/MICROSCOPE.md}.
 */
public class MicroscopeScreen extends AbstractContainerScreen<MicroscopeMenu> {
    private static final Identifier BACKGROUND =
            Identifier.fromNamespaceAndPath(BeyondTime.MODID, "textures/gui/microscope.png");

    /** Drawn for a reading whose microbe is not in {@link Microbes} any more, e.g. from an old save. */
    private static final Identifier UNKNOWN_ICON =
            Identifier.fromNamespaceAndPath(BeyondTime.MODID, "textures/microbe/unknown.png");

    // Source and plate count, next to the dish slot.
    private static final int INFO_X = MicroscopeMenu.DISH_SLOT_X + 24;
    private static final int INFO_Y = MicroscopeMenu.DISH_SLOT_Y + 2;
    private static final int INFO_LINE_HEIGHT = 12;

    // The report grid.
    private static final int LIST_TITLE_X = 10;
    private static final int LIST_TITLE_Y = 52;
    private static final int LIST_X = 10;
    private static final int LIST_Y = 66;
    private static final int LIST_COLUMNS = 3;
    private static final int LIST_ROWS = 4;
    private static final int LIST_COLUMN_WIDTH = 78;
    private static final int LIST_ROW_HEIGHT = 20;

    /** Microbes are drawn at half of their 32x32 texture so that six rows still fit the panel. */
    private static final int ICON_SIZE = 16;
    private static final int ICON_TEXTURE_SIZE = 32;

    private static final int COLOR_OUTLINE = 0xFF8B8B8B;
    private static final int COLOR_TEXT = 0xFF404040;
    private static final int COLOR_PERCENT = 0xFF1B5E20;
    private static final int COLOR_HINT = 0xFF808080;

    public MicroscopeScreen(MicroscopeMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, MicroscopeMenu.PANEL_WIDTH, MicroscopeMenu.PANEL_HEIGHT);
        this.inventoryLabelX = MicroscopeMenu.INVENTORY_X;
        this.inventoryLabelY = MicroscopeMenu.INVENTORY_Y - 12;
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
        ItemStack dish = this.menu.getDish();
        MicrobeSample sample = PetriDishItem.sample(dish);
        extractDishInfo(graphics, dish, sample);

        graphics.text(
                this.font,
                Component.translatable("gui.beyondtime.microscope.report_title"),
                LIST_TITLE_X,
                LIST_TITLE_Y,
                COLOR_TEXT,
                false);

        if (sample == null) {
            graphics.text(
                    this.font,
                    Component.translatable("gui.beyondtime.microscope.report_empty"),
                    LIST_X,
                    LIST_Y + 2,
                    COLOR_HINT,
                    false);
            return;
        }

        extractReport(graphics, sample);
    }

    /** The lines beside the dish slot: where it was swabbed, and how big the plate count is. */
    private void extractDishInfo(GuiGraphicsExtractor graphics, ItemStack dish, MicrobeSample sample) {
        if (sample != null) {
            graphics.text(
                    this.font,
                    Component.translatable("gui.beyondtime.microscope.origin", sample.origin().displayName()),
                    INFO_X,
                    INFO_Y,
                    COLOR_TEXT,
                    false);
            graphics.text(
                    this.font,
                    Component.translatable("gui.beyondtime.microscope.count", sample.total()),
                    INFO_X,
                    INFO_Y + INFO_LINE_HEIGHT,
                    COLOR_TEXT,
                    false);
            return;
        }

        graphics.text(
                this.font,
                Component.translatable(dish.isEmpty()
                        ? "gui.beyondtime.microscope.no_dish"
                        : "gui.beyondtime.microscope.clean_dish"),
                INFO_X,
                INFO_Y,
                COLOR_HINT,
                false);
    }

    /**
     * The report grid, filled column by column.
     *
     * <p>A sample usually has fewer readings than there are cells. When it has more - which needs a
     * block to be added to {@code MicrobeProfiles} with a very long list - the overflow is counted in
     * the last cell instead of being dropped silently.
     */
    private void extractReport(GuiGraphicsExtractor graphics, MicrobeSample sample) {
        List<MicrobeSample.Reading> report = sample.report();
        int cells = LIST_COLUMNS * LIST_ROWS;
        int shown = Math.min(report.size(), cells);

        for (int i = 0; i < shown; i++) {
            extractReading(graphics, sample, report.get(i), i);
        }

        if (report.size() > cells) {
            Component overflow = Component.translatable(
                    "gui.beyondtime.microscope.more", report.size() - cells);
            graphics.text(
                    this.font,
                    overflow,
                    LIST_X + (LIST_COLUMNS - 1) * LIST_COLUMN_WIDTH,
                    LIST_Y + (LIST_ROWS - 1) * LIST_ROW_HEIGHT + 6,
                    COLOR_HINT,
                    false);
        }
    }

    /** One cell: icon, name, and the share of the plate the microbe takes up. */
    private void extractReading(
            GuiGraphicsExtractor graphics, MicrobeSample sample, MicrobeSample.Reading reading, int index) {
        int x = LIST_X + (index / LIST_ROWS) * LIST_COLUMN_WIDTH;
        int y = LIST_Y + (index % LIST_ROWS) * LIST_ROW_HEIGHT;

        Microbe microbe = Microbes.byId(reading.microbe());
        Identifier icon = microbe == null ? UNKNOWN_ICON : microbe.icon();
        Component name = microbe == null
                ? Component.literal(reading.microbe().toString())
                : Component.translatable(microbe.nameKey());

        graphics.blit(
                RenderPipelines.GUI_TEXTURED,
                icon,
                x,
                y + 2,
                0.0F,
                0.0F,
                ICON_SIZE,
                ICON_SIZE,
                ICON_TEXTURE_SIZE,
                ICON_TEXTURE_SIZE,
                ICON_TEXTURE_SIZE,
                ICON_TEXTURE_SIZE);
        graphics.text(this.font, name, x + ICON_SIZE + 3, y + 6, COLOR_TEXT, false);

        Component percent = Component.literal(formatPercent(sample.percent(reading.amount())));
        graphics.text(
                this.font,
                percent,
                x + LIST_COLUMN_WIDTH - this.font.width(percent),
                y + 6,
                COLOR_PERCENT,
                false);
    }

    /** {@return an amount's share, e.g. {@code 32.1%}} */
    private static String formatPercent(double percent) {
        return String.format(Locale.ROOT, "%.1f%%", percent);
    }
}
