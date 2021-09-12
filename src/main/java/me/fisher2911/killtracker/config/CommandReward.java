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

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class CommandReward implements Reward {

    private final String command;

    public CommandReward(final String command) {
        this.command = command;
    }

    @Override
    public void apply(final OfflinePlayer player) {
        final String playerName = player.getName();
        if (playerName == null) {
            return;
        }
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", playerName));
    }
}
