package me.fisher2911.killtracker.config;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import me.fisher2911.killtracker.KillTracker;
import me.fisher2911.killtracker.gui.GuiInfo;
import me.fisher2911.killtracker.gui.StatGuiItem;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemFlag;

import java.io.File;
import java.util.*;

public class GuiSettings {

    private final KillTracker plugin;

    public GuiSettings(final KillTracker plugin) {
        this.plugin = plugin;
    }

    private GuiInfo guiInfo;

    public void load() {
        final File file = getFile("menu.yml");
        plugin.debug(String.valueOf(file.exists()));
        final ConfigurationSection config = YamlConfiguration.loadConfiguration(file);
        final int rows = config.getInt("rows");
        final ConfigurationSection section = config.getConfigurationSection("items");
        if (section == null) {
            plugin.sendError("Section \"item's\" not found in menu.yml, " +
                    "the stats menu will not work.");
            plugin.debug(String.valueOf(config.getKeys(true)));
            plugin.debug("rows - " + rows);
            return;
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
        this.guiInfo = new GuiInfo(title, itemMap, rows, previousPageItem, nextPageItem);
    }

    private GuiItem loadItem(final ConfigurationSection section, final String path) {
        final ConfigurationSection otherSection = section.getConfigurationSection(path);
        if (otherSection == null) {
            // todo set to air
            return new GuiItem(Material.STONE);
        }
        return loadItem(otherSection);
    }

    private GuiItem loadItem(final ConfigurationSection section) {
        final String name = section.getString("name");
        final int amount = section.getInt("amount");
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
        if (entityType != null) {
            return new StatGuiItem(builder.asGuiItem(), entityType.toUpperCase());
        }
        return builder.asGuiItem();
    }

    private File getFile(final String name) {
        final File file = new File(plugin.getDataFolder(), name);
        if (!file.exists()) {
            plugin.saveResource(name, false);
        }
        return file;
    }

    public GuiInfo getGuiInfo() {
        return guiInfo;
    }
}
