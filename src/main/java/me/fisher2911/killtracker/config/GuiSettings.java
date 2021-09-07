package me.fisher2911.killtracker.config;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import me.fisher2911.killtracker.KillTracker;
import me.fisher2911.killtracker.gui.GuiInfo;
import me.fisher2911.killtracker.gui.StatGuiItem;
import me.fisher2911.killtracker.gui.StatsMenu;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;

import java.io.File;
import java.util.*;

public class GuiSettings {

    private final KillTracker plugin;

    public GuiSettings(final KillTracker plugin) {
        this.plugin = plugin;
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
        final GuiInfo mainGuiInfo = loadGuiInfo(mainGuiInfoSection);
        final GuiInfo entityGuiInfo = loadGuiInfo(entityGuiInfoSection);
        final GuiInfo playerGuiInfo = loadGuiInfo(playerGuiInfoSection);
        this.statsMenu = new StatsMenu(mainGuiInfo, entityGuiInfo, playerGuiInfo);
    }

    private GuiInfo loadGuiInfo(final ConfigurationSection config) {
        final int rows = config.getInt("rows");
        final ConfigurationSection section = config.getConfigurationSection("items");
        if (section == null) {
            plugin.sendError("Section \"item's\" not found in menu.yml, " +
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
            nextPageItem = loadItem(buttonsSection, "next-page-item");
            previousPageItem = loadItem(buttonsSection, "previous-page-item");

        }
        final Map<Integer, GuiItem> itemMap = new HashMap<>();
        for (final String key : section.getKeys(false)) {
            try {
                final int slot = Integer.parseInt(key);
                final ConfigurationSection itemSection = section.getConfigurationSection(key);
                if (itemSection == null) {
                    plugin.sendError("No valid item found at slot " + slot);
                    continue;
                }
                final GuiItem guiItem = loadItem(itemSection);
                itemMap.put(slot, guiItem);
            } catch (final NumberFormatException exception) {
                plugin.sendError(key + " is not a valid slot in menu.yml");
            }
        }
        final List<GuiItem> borderItems = new ArrayList<>();
        final ConfigurationSection borderSection = config.getConfigurationSection("border");
        if (borderSection != null) {
            for (final String key : borderSection.getKeys(false)) {
                final ConfigurationSection borderItemSection =
                        borderSection.getConfigurationSection(key);
                if (borderItemSection == null) {
                    continue;
                }
                borderItems.add(loadItem(borderItemSection));
            }
        }
        final ConfigurationSection previousMenuSection = config.getConfigurationSection("previous-menu-item");
        GuiItem previousMenuItem = null;
        if (previousMenuSection != null) {
            previousMenuItem = loadItem(previousMenuSection);
        }
        return new GuiInfo(title, itemMap, borderItems, rows, previousPageItem, nextPageItem, previousMenuItem);
    }

    private GuiItem loadItem(final ConfigurationSection section, final String path) {
        final ConfigurationSection otherSection = section.getConfigurationSection(path);
        if (otherSection == null) {
            return new GuiItem(Material.AIR);
        }
        return loadItem(otherSection);
    }

    private GuiItem loadItem(final ConfigurationSection section) {
        final String name = section.getString("name");
        final int amount = Math.max(1, section.getInt("amount"));
        final String materialString = section.getString("material");
        if (materialString == null) {
            plugin.sendError("Material not found in section " +
                    section.getCurrentPath() + " in menu.yml");
            return new GuiItem(Material.AIR);
        }
        final Material material = Material.matchMaterial(materialString);
        if (material == null) {
            plugin.sendError(materialString + " is not a valid material in menu.yml");
            return new GuiItem(Material.AIR);
        }
        final boolean glowing = section.getBoolean("glowing");
        final String entityType = section.getString("type");
        final List<String> lore = section.getStringList("lore");
        final List<String> flagsAsStrings = section.getStringList("item-flags");
        final Set<ItemFlag> itemFlags = new HashSet<>();
        flagsAsStrings.forEach(flag -> {
            try {
                itemFlags.add(ItemFlag.valueOf(flag));
            } catch (final IllegalArgumentException exception) {
                plugin.sendError(flag + " is not a valid item flag in menu.yml");
            }
        });
        ItemBuilder builder;

        if (material == Material.PLAYER_HEAD) {
            String texture = section.getString("texture");
            if (texture == null) {
                texture = "";
            }
            builder = ItemBuilder.from(ItemBuilder.
                    skull().
                    texture(texture).build());
        } else {
            builder = ItemBuilder.from(material);
        }

        if (name != null) {
            builder.name(Component.text(ChatColor.
                    translateAlternateColorCodes('&', name)));
        }
        itemFlags.forEach(builder::flags);
        final List<Component> componentLore = new ArrayList<>();
        lore.forEach(line -> componentLore.add(
                Component.text(
                        ChatColor.
                                translateAlternateColorCodes('&',
                                        line))));
        builder.glow(glowing).lore(componentLore).amount(amount);
        final String command = section.getString("command");
        final GuiItem guiItem;
        if (entityType != null) {
            guiItem = new StatGuiItem(builder.asGuiItem(), entityType.toUpperCase());
        } else {
            guiItem = builder.asGuiItem();
        }
        if (command != null) {
            guiItem.setAction(event -> {
                final HumanEntity entity = event.getWhoClicked();
                if (entity instanceof final Player player) {
                    player.performCommand(command);
                }
            });
        }
        return guiItem;
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
