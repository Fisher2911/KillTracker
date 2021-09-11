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
                    section.getCurrentPath() + " in menu.yml");
            return AIR;
        }
        final Material material = Material.matchMaterial(materialString);
        if (material == null) {
            plugin.sendError(materialString + " is not a valid material in menu.yml");
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
                plugin.sendError(flag + " is not a valid item flag in menu.yml");
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
