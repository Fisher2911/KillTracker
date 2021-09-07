package me.fisher2911.killtracker.gui;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.builder.item.SkullBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import me.fisher2911.killtracker.KillTracker;
import me.fisher2911.killtracker.user.User;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class StatsMenu {

    private final GuiInfo mainGuiInfo;
    private final GuiInfo entityKillsInfo;
    private final GuiInfo playerKillsInfo;

    public StatsMenu(final GuiInfo mainGuiInfo,
                     final GuiInfo entityKillsInfo,
                     final GuiInfo playerKillsInfo) {
        this.mainGuiInfo = mainGuiInfo;
        this.entityKillsInfo = entityKillsInfo;
        this.playerKillsInfo = playerKillsInfo;
    }

    public void openEntityMenu(final User user) {
        final PaginatedGui gui = getGui(entityKillsInfo, user);

        // todo - set items in actual inventory slot
        for (final Map.Entry<Integer, GuiItem> entry :
                this.entityKillsInfo.getGuiItemMap().entrySet()) {
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

    public void openPlayerKillsMenu(final User user) {
        final PaginatedGui gui = getGui(playerKillsInfo, user);
        final Map<UUID, Integer> playerKills = user.getPlayerKills();
        KillTracker.
                newChain().
                syncFirst(() -> {
                    final Map<OfflinePlayer, Integer> offlinePlayerKills = new HashMap<>();
                    for (final Map.Entry<UUID, Integer> entry : playerKills.entrySet()) {
                        final UUID uuid = entry.getKey();
                        final int kills = entry.getValue();
                        final OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
                        offlinePlayerKills.put(player, kills);
                    }
                    return offlinePlayerKills;
                }).syncLast(map -> map.forEach((player, kills) -> {
                    final String name = ChatColor.
                            translateAlternateColorCodes('&',
                                    player.getName() + "'s kills");
                    final String lore = ChatColor.
                            translateAlternateColorCodes('&',
                                    String.valueOf(kills));
                    final SkullBuilder builder =
                            ItemBuilder.
                                    skull().
                                    owner(player).
                                    name(Component.text(name)).
                                    lore(Component.text(""),
                                            Component.text(lore));
                    gui.addItem(builder.asGuiItem());
                }));
        final Player player = Bukkit.getPlayer(user.getUuid());
        if (player == null) {
            return;
        }
        gui.open(player);
    }

    public void openMenu(final User user) {
        final Gui gui = Gui.
                gui().
                title(Component.text(this.mainGuiInfo.getTitle())).
                rows(this.mainGuiInfo.getRows()).
                create();
        final List<GuiItem> borderItems = this.mainGuiInfo.getBorderItems();
        if (!borderItems.isEmpty()) {
            gui.getFiller().fillBorder(borderItems);
        }
        for (final Map.Entry<Integer, GuiItem> entry :
                this.mainGuiInfo.getGuiItemMap().entrySet()) {
            gui.setItem(entry.getKey(), entry.getValue());
        }
        final Player player = Bukkit.getPlayer(user.getUuid());
        if (player == null) {
            return;
        }
        gui.open(player);
    }

    private PaginatedGui getGui(final GuiInfo info, final User user) {
        final int rows = info.getRows();
        final PaginatedGui gui = Gui.
                paginated().
                title(Component.text(info.getTitle())).
                rows(rows).
                create();
        final List<GuiItem> borderItems = info.getBorderItems();
        if (!borderItems.isEmpty()) {
            gui.getFiller().fillBorder(borderItems);
        }
        final int inventorySize = rows * 9;
        final int previousPageSlot = inventorySize - 9;
        final int nextPageSlot = inventorySize - 1;
        gui.setDefaultClickAction(event -> event.setCancelled(true));
        gui.setItem(previousPageSlot, info.getPreviousPageItem());
        gui.setItem(nextPageSlot, info.getNextPageItem());
        final GuiItem previousMenuItem = info.getPreviousMenuItem();
        if (previousMenuItem != null) {
            System.out.println("Previous Menu Item: " +
                    previousMenuItem.getItemStack());
            final int previousMenuSlot = inventorySize - 5;
            gui.setItem(previousMenuSlot, previousMenuItem);
            previousMenuItem.setAction(event -> openMenu(user));
        }
        gui.addSlotAction(previousPageSlot, event -> gui.previous());
        gui.addSlotAction(nextPageSlot, event -> gui.next());
        return gui;
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
