/*
 * MIT License
 *
 * Copyright (c) fisher2911
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
