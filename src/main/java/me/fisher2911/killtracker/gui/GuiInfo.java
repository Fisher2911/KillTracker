package me.fisher2911.killtracker.gui;

import dev.triumphteam.gui.guis.GuiItem;

import java.util.List;
import java.util.Map;

public class GuiInfo {

    private final String title;
    private final Map<Integer, GuiItem> guiItemMap;
    private final List<GuiItem> borderItems;
    // Key is entity type, value is lore line to be displayed in the gui
    private final int rows;
    private final GuiItem previousPageItem;
    private final GuiItem nextPageItem;
    private final GuiItem previousMenuItem;

    public GuiInfo(final String title,
                   final Map<Integer, GuiItem> guiItemMap,
                   final List<GuiItem> borderItems, final int rows,
                   final GuiItem previousPageItem, final GuiItem nextPageItem,
                   final GuiItem previousMenuItem) {
        this.title = title;
        this.guiItemMap = guiItemMap;
        this.borderItems = borderItems;
        this.rows = rows;
        this.previousPageItem = previousPageItem;
        this.nextPageItem = nextPageItem;
        this.previousMenuItem = previousMenuItem;
    }

    public String getTitle() {
        return title;
    }

    public Map<Integer, GuiItem> getGuiItemMap() {
        return guiItemMap;
    }

    public List<GuiItem> getBorderItems() {
        return borderItems;
    }

    public int getRows() {
        return rows;
    }

    public GuiItem getPreviousPageItem() {
        return previousPageItem;
    }

    public GuiItem getNextPageItem() {
        return nextPageItem;
    }

    public GuiItem getPreviousMenuItem() {
        return previousMenuItem;
    }
}
