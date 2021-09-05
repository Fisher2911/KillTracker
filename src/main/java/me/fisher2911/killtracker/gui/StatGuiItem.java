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
