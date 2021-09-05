package me.fisher2911.killtracker.gui;

import dev.triumphteam.gui.guis.GuiItem;

import java.util.Map;

public class GuiInfo {

    private final String title;
    private final Map<Integer, GuiItem> guiItemMap;
    // Key is entity type, value is lore line to be displayed in the gui
    private final int rows;

    public GuiInfo(final String title,
                   final Map<Integer, GuiItem> guiItemMap,
                   final int rows) {
        this.title = title;
        this.guiItemMap = guiItemMap;
        this.rows = rows;
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
}
