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
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import me.fisher2911.killtracker.KillTracker;
import me.fisher2911.killtracker.gui.StatGuiItem;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ItemLoader {

    private final KillTracker plugin;

    private static final GuiItem AIR = new GuiItem(new ItemStack(Material.AIR));

    public ItemLoader(final KillTracker plugin) {
        this.plugin = plugin;
    }

    public GuiItem loadItem(final ConfigurationSection section, final String path) {
        final ConfigurationSection otherSection = section.getConfigurationSection(path);
        if (otherSection == null) {
            return AIR;
        }
        return loadItem(otherSection);
    }

    public GuiItem loadItem(final ConfigurationSection section) {
        final String name = section.getString("name");
        final int amount = Math.max(1, section.getInt("amount"));
        final String materialString = section.getString("material");
        if (materialString == null) {
            plugin.sendError("Material not found in section " +
                    section.getCurrentPath());
            return AIR;
        }
        final Material material = Material.matchMaterial(materialString);
        if (material == null) {
            plugin.sendError(materialString + " is not a valid material");
            return AIR;
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
                plugin.sendError(flag + " is not a valid item flag");
            }
        });
        ItemBuilder builder;

        if (material == Material.PLAYER_HEAD) {
            final String texture = section.getString("texture");
            final String playerName = section.getString("player-name");
            if (texture != null) {
                builder = ItemBuilder.from(ItemBuilder.
                        skull().
                        texture(texture).build());
            } else if (playerName != null) {
                builder = ItemBuilder.from(ItemBuilder.
                        skull().
                        owner(Bukkit.getOfflinePlayer(playerName)).
                        build());
            } else {
                builder = ItemBuilder.from(material);
            }

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
}
