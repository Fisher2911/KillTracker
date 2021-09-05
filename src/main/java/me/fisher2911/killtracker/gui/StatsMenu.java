package me.fisher2911.killtracker.gui;

import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import me.fisher2911.killtracker.user.User;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Map;

public class StatsMenu {

    private final GuiInfo guiInfo;

    public StatsMenu(final GuiInfo guiInfo) {
        this.guiInfo = guiInfo;
    }

    public void open(final User user) {
        final int rows = this.guiInfo.getRows();
        final PaginatedGui gui = Gui.
                paginated().
                title(Component.text(this.guiInfo.getTitle())).
                rows(rows).
                create();
        gui.setDefaultClickAction(event -> event.setCancelled(true));
        final int inventorySize = rows * 9;
        final int previousPageSlot = inventorySize - 9;
        final int nextPageSlot = inventorySize - 1;
        gui.setItem(previousPageSlot, guiInfo.getPreviousPageItem());
        gui.setItem(nextPageSlot, guiInfo.getNextPageItem());
        gui.addSlotAction(previousPageSlot, event -> gui.previous());
        gui.addSlotAction(nextPageSlot, event -> gui.next());
        // todo - set items in actual inventory slot
        for (final Map.Entry<Integer, GuiItem> entry : this.guiInfo.getGuiItemMap().entrySet()) {
            final int slot = entry.getKey();
            final GuiItem guiItem = entry.getValue();
            if (guiItem instanceof final StatGuiItem statGuiItem) {
                final String entityType = statGuiItem.getEntityType();
                final int kills = user.getEntityKillAmount(entityType);
//                gui.setItem(slot, replaceLore(guiItem, kills));
                gui.addItem(replaceLore(guiItem, kills));
            }
        }
        final Player player = Bukkit.getPlayer(user.getUuid());
        if (player == null) {
            return;
        }
        gui.open(player);
    }

    private GuiItem replaceLore(final GuiItem guiItem, final int kills) {
        final ItemStack itemStack = guiItem.getItemStack().clone();
        final ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) {
            return guiItem;
        }
        List<String> lore = itemMeta.getLore();
        if (lore == null) {
            return guiItem;
        }
        for (int i = 0; i < lore.size(); i++) {
            final String line = lore.get(i);
            lore.set(i, line.replace("%kills%", String.valueOf(kills)));
        }
        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
        return new GuiItem(itemStack);
    }
}
