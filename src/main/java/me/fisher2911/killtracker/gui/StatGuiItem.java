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

import dev.triumphteam.gui.components.GuiAction;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class StatGuiItem extends GuiItem {

    private final String entityType;

    /**
     * Main constructor of the GuiItem
     *
     * @param itemStack The {@link ItemStack} to be used
     * @param action    The {@link GuiAction} to run when clicking on the Item
     */
    public StatGuiItem(final @org.jetbrains.annotations.NotNull ItemStack itemStack, final GuiAction<InventoryClickEvent> action, final String entityType) {
        super(itemStack, action);
        this.entityType = entityType;
    }

    /**
     * Secondary constructor with no action
     *
     * @param itemStack The ItemStack to be used
     */
    public StatGuiItem(final @org.jetbrains.annotations.NotNull ItemStack itemStack, final String entityType) {
        super(itemStack);
        this.entityType = entityType;
    }

    /**
     * Alternate constructor that takes {@link Material} instead of an {@link ItemStack} but without a {@link GuiAction}
     *
     * @param material The {@link Material} to be used when invoking class
     */
    public StatGuiItem(final @org.jetbrains.annotations.NotNull Material material, final String entityType) {
        super(material);
        this.entityType = entityType;
    }

    /**
     * Alternate constructor that takes {@link Material} instead of an {@link ItemStack}
     *
     * @param material The {@code Material} to be used when invoking class
     * @param action   The {@link GuiAction} should be passed on {@link InventoryClickEvent}
     */
    public StatGuiItem(final @org.jetbrains.annotations.NotNull Material material, final @org.jetbrains.annotations.Nullable GuiAction<InventoryClickEvent> action, final String entityType) {
        super(material, action);
        this.entityType = entityType;
    }

    public StatGuiItem(final GuiItem guiItem, final GuiAction<InventoryClickEvent> action, final String entityType) {
        this(guiItem.getItemStack(), action, entityType);
    }

    public StatGuiItem(final GuiItem guiItem, final String entityType) {
        this(guiItem.getItemStack(), entityType);
    }

    public String getEntityType() {
        return entityType;
    }
}
