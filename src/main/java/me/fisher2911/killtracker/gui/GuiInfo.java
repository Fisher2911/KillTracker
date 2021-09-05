package me.fisher2911.killtracker.gui;

import dev.triumphteam.gui.guis.GuiItem;

import java.util.Map;

public class GuiInfo {

    private final String title;
    private final Map<Integer, GuiItem> guiItemMap;
    // Key is entity type, value is lore line to be displayed in the gui
    private final int rows;
    private GuiItem previousPageItem;
    private GuiItem nextPageItem;

    public GuiInfo(final String title,
                   final Map<Integer, GuiItem> guiItemMap,
                   final int rows,
                   final GuiItem previousPageItem,
                   final GuiItem nextPageItem) {
        this.title = title;
        this.guiItemMap = guiItemMap;
        this.rows = rows;
        this.previousPageItem = previousPageItem;
        this.nextPageItem = nextPageItem;
    }

    public String getTitle() {
        return title;
    }

    public Map<Integer, GuiItem> getGuiItemMap() {
        return guiItemMap;
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
}
