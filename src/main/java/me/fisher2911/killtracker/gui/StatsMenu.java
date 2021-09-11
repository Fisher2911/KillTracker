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

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.builder.item.SkullBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import me.fisher2911.killtracker.KillTracker;
import me.fisher2911.killtracker.placeholder.Placeholder;
import me.fisher2911.killtracker.user.KillInfo;
import me.fisher2911.killtracker.user.User;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
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
        final Map<UUID, KillInfo> playerKills = user.getPlayerKills();
        KillTracker.
                newChain().
                asyncFirst(() -> {
                    final Map<OfflinePlayer, Integer> offlinePlayerKills = new HashMap<>();
                    for (final Map.Entry<UUID, KillInfo> entry : playerKills.entrySet()) {
                        final UUID uuid = entry.getKey();
                        final KillInfo killInfo = entry.getValue();
                        final OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
                        offlinePlayerKills.put(player, killInfo.getKills());
                    }
                    return offlinePlayerKills;
                }).syncLast(map -> {
                    final ItemFormat itemFormat = playerKillsInfo.getItemFormat();
                    if (itemFormat != null) {
                        map.forEach((player, kills) -> {
                            final String name =
                                    Placeholder.addPlayerAndKillsPlaceholders(
                                            addColor(itemFormat.getItemNameFormat()),
                                            player,
                                            kills);
                            final List<Component> lore = new ArrayList<>();
                            for (final String line : itemFormat.getItemLoreFormat()) {
                                lore.add(
                                        Component.text(
                                                Placeholder.addPlayerAndKillsPlaceholders(
                                                        addColor(line),
                                                        player,
                                                        kills)
                                        )
                                );
                            }
                            final SkullBuilder builder =
                                    ItemBuilder.
                                            skull().
                                            owner(player).
                                            name(Component.text(name)).
                                            lore(lore);
                            gui.addItem(builder.asGuiItem());
                        });
                    }
                    final Player userPlayer = Bukkit.getPlayer(user.getUuid());
                    if (userPlayer == null) {
                        return;
                    }
                    gui.open(userPlayer);
                }).execute();
    }

    public void openMenu(final User user) {
        final Gui gui = Gui.
                gui().
                title(Component.text(this.mainGuiInfo.getTitle())).
                rows(this.mainGuiInfo.getRows()).
                create();
        gui.setDefaultClickAction(event -> event.setCancelled(true));
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

    private String addColor(final String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }
}
