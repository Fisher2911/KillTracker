/*
 * KillTracker
 * Copyright (C) 2021 fisher2911
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package me.fisher2911.killtracker.reward;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.builder.item.SkullBuilder;
import me.fisher2911.killtracker.KillTracker;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public class HeadReward implements Reward {

    private final HeadInfo headInfo;

    public HeadReward(final HeadInfo headInfo) {
        this.headInfo = headInfo;
    }

    @Override
    public void apply(final OfflinePlayer player, final Entity killedEntity) {
        if (!(player instanceof final Player killer)) {
            return;
        }
        ItemStack headItem;
        if (killedEntity instanceof final Player killedPlayer) {
            headItem = this.headInfo.getHeadFromPlayer(killedPlayer);
        } else {
            headItem = this.headInfo.getHeadFromEntity(killedEntity);
        }
        final Map<Integer, ItemStack> heads = killer.getInventory().addItem(headItem);
        if (heads.isEmpty()) {
            return;
        }
        final Location location = killer.getLocation();
        final World world = location.getWorld();
        if (world == null) {
            return;

        }
        heads.forEach((slot, item) -> world.dropItem(location, item));
    }
}
