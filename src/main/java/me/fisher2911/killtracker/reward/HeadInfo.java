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

package me.fisher2911.killtracker.reward;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.builder.item.SkullBuilder;
import me.fisher2911.killtracker.KillTracker;
import me.fisher2911.killtracker.placeholder.Placeholder;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HeadInfo {

    private final String texture;
    private final String itemName;
    private final List<String> lore;

    public HeadInfo(final String texture, final String itemName, final List<String> lore) {
        this.texture = texture;
        this.itemName = itemName;
        this.lore = lore;
    }

    public String getTexture() {
        return texture;
    }

    public String getItemName() {
        return itemName;
    }

    public List<String> getLore() {
        return lore;
    }

    public ItemStack getHeadFromEntity(final Entity entity) {
        final SkullBuilder skullBuilder = ItemBuilder.skull();
        final List<Component> placeholderLore = new ArrayList<>();
        final String cleanName = cleanupEntityName(entity);
        final String itemName = Placeholder.addNamePlaceholders(this.itemName, cleanName);
        lore.forEach(line -> placeholderLore.add(
                Component.text(Placeholder.addNamePlaceholders(line, cleanName))));
        return skullBuilder.
                texture(this.texture).
                lore(placeholderLore).
                name(Component.text(itemName)).
                build();
    }

    public ItemStack getHeadFromPlayer(final Player player) {
        final String playerName = player.getName();
        final String placeholderName = Placeholder.addNamePlaceholders(itemName, playerName);
        final SkullBuilder builder = ItemBuilder.skull();
        final List<Component> placeholderLore = new ArrayList<>();
        lore.forEach(line -> placeholderLore.add(
                Component.text(Placeholder.addNamePlaceholders(line, playerName))));
        return builder.owner(player).
                name(Component.text(placeholderName)).
                lore(placeholderLore).build();
    }

    private String cleanupEntityName(final Entity entity) {
        final String[] parts = entity.getType().toString().toLowerCase().split("_");
        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            final String part = parts[i];
            final String capitalize = String.valueOf(part.charAt(0)).toUpperCase() +
                    part.substring(1);
            builder.append(capitalize);
            if (i < parts.length -1) {
                builder.append(" ");
            }
        }
        return builder.toString();
    }
}
