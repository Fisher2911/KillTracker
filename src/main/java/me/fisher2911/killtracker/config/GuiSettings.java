/*
 * KillTracker
 * Copyright (C) 2021 fisher2911
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package me.fisher2911.killtracker.config;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import me.fisher2911.killtracker.KillTracker;
import me.fisher2911.killtracker.gui.GuiInfo;
import me.fisher2911.killtracker.gui.ItemFormat;
import me.fisher2911.killtracker.gui.StatGuiItem;
import me.fisher2911.killtracker.gui.StatsMenu;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.*;

public class GuiSettings {

    private final KillTracker plugin;
    private final ItemLoader itemLoader;

    public GuiSettings(final KillTracker plugin) {
        this.plugin = plugin;
        this.itemLoader = new ItemLoader(plugin);
    }

    private StatsMenu statsMenu;

    public void load() {
        final File file = getFile("menu.yml");
        plugin.debug(String.valueOf(file.exists()));
        final ConfigurationSection config = YamlConfiguration.loadConfiguration(file);
        final ConfigurationSection mainGuiInfoSection = config.getConfigurationSection("main-menu");
        if (mainGuiInfoSection == null) {
            plugin.sendError("Main Gui section was not found");
            return;
        }
        final ConfigurationSection entityGuiInfoSection = config.getConfigurationSection("entity-menu");
        if (entityGuiInfoSection == null) {
            plugin.sendError("Entity Gui section was not found");
            return;
        }
        final ConfigurationSection playerGuiInfoSection = config.getConfigurationSection("player-menu");
        if (playerGuiInfoSection == null) {
            plugin.sendError("Player Gui section was not found");
            return;
        }
        final GuiInfo mainGuiInfo = loadGuiInfo(mainGuiInfoSection, false);
        final GuiInfo entityGuiInfo = loadGuiInfo(entityGuiInfoSection, false);
        final GuiInfo playerGuiInfo = loadGuiInfo(playerGuiInfoSection, true);
        this.statsMenu = new StatsMenu(mainGuiInfo, entityGuiInfo, playerGuiInfo);
    }

    private GuiInfo loadGuiInfo(final ConfigurationSection config, boolean ignoreItems) {
        final int rows = config.getInt("rows");
        final ConfigurationSection section = config.getConfigurationSection("items");
        if (section == null && !ignoreItems) {
            plugin.sendError("Section \"item's\" not found in menu.yml in section " +
                    config.getCurrentPath() +  ", " +
                    "the stats menu will not work.");
            plugin.debug(String.valueOf(config.getKeys(true)));
            plugin.debug("rows - " + rows);
            return null;
        }
        final String title = config.getString("title");
        final ConfigurationSection buttonsSection = config.getConfigurationSection("buttons");
        final GuiItem nextPageItem;
        final GuiItem previousPageItem;
        if (buttonsSection == null) {
            plugin.debug("Could not find buttons section in menu.yml");
            nextPageItem = new GuiItem(Material.STONE);
            previousPageItem = new GuiItem(Material.STONE);
        } else {
            nextPageItem = itemLoader.loadItem(buttonsSection, "next-page-item");
            previousPageItem = itemLoader.loadItem(buttonsSection, "previous-page-item");

        }
        final Map<Integer, GuiItem> itemMap = new HashMap<>();
        if (section != null) {
            for (final String key : section.getKeys(false)) {
                try {
                    final int slot = Integer.parseInt(key);
                    final ConfigurationSection itemSection = section.getConfigurationSection(key);
                    if (itemSection == null) {
                        plugin.sendError("No valid item found at slot " + slot);
                        continue;
                    }
                    final GuiItem guiItem = itemLoader.loadItem(itemSection);
                    itemMap.put(slot, guiItem);
                } catch (final NumberFormatException exception) {
                    plugin.sendError(key + " is not a valid slot in menu.yml");
                }
            }
        }
        final List<GuiItem> borderItems = new ArrayList<>();
        final ConfigurationSection borderSection = config.getConfigurationSection("border-items");
        if (borderSection != null) {
            for (final String key : borderSection.getKeys(false)) {
                final ConfigurationSection borderItemSection =
                        borderSection.getConfigurationSection(key);
                if (borderItemSection == null) {
                    continue;
                }
                borderItems.add(itemLoader.loadItem(borderItemSection));
            }
        }
        final ConfigurationSection previousMenuSection = config.getConfigurationSection("previous-menu-item");
        GuiItem previousMenuItem = null;
        if (previousMenuSection != null) {
            previousMenuItem = itemLoader.loadItem(previousMenuSection);
        }
        final ConfigurationSection itemFormatSection = config.getConfigurationSection("item-format");
        ItemFormat itemFormat = null;
        if (itemFormatSection != null) {
            itemFormat = new ItemFormat(
                    itemFormatSection.getString("name"),
                    itemFormatSection.getStringList("lore"));
        }
        return new GuiInfo(title, itemMap, borderItems, rows,
                previousPageItem, nextPageItem, previousMenuItem, itemFormat);
    }
    
    private File getFile(final String name) {
        final File file = new File(plugin.getDataFolder(), name);
        if (!file.exists()) {
            plugin.saveResource(name, false);
        }
        return file;
    }

    public StatsMenu getStatsMenu() {
        return this.statsMenu;
    }
}
