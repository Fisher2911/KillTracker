/*
 * KillTracker
 * Copyright (C) 2021 fisher2911
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

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
    private ItemFormat itemFormat;

    public GuiInfo(final String title,
                   final Map<Integer, GuiItem> guiItemMap,
                   final List<GuiItem> borderItems, final int rows,
                   final GuiItem previousPageItem, final GuiItem nextPageItem,
                   final GuiItem previousMenuItem, final ItemFormat itemFormat) {
        this.title = title;
        this.guiItemMap = guiItemMap;
        this.borderItems = borderItems;
        this.rows = rows;
        this.previousPageItem = previousPageItem;
        this.nextPageItem = nextPageItem;
        this.previousMenuItem = previousMenuItem;
        this.itemFormat = itemFormat;
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

    public ItemFormat getItemFormat() {
        return itemFormat;
    }
}
