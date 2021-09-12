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

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.Set;

public class ItemsReward implements Reward {

    private final Set<ItemStack> items;

    public ItemsReward(final Set<ItemStack> items) {
        this.items = items;
    }

    @Override
    public void apply(final OfflinePlayer offlinePlayer) {
        if (!(offlinePlayer instanceof final Player player)) {
            return;
        }
        for (final ItemStack itemStack : items) {
            addItem(player, itemStack);
        }
    }

    private void addItem(final Player player, final ItemStack itemStack) {
        final ItemStack clone = itemStack.clone();

        final Map<Integer, ItemStack> leftoverItems = player.getInventory().addItem(clone);
        if (!leftoverItems.isEmpty()) {
            final World world = player.getWorld();
            final Location location = player.getLocation();
            leftoverItems.forEach((index, leftOverItemStack) ->
                    world.dropItem(location, leftOverItemStack));
        }
    }
}
