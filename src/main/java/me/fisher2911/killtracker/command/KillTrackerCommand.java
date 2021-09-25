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

package me.fisher2911.killtracker.command;


import me.clip.placeholderapi.PlaceholderAPI;
import me.fisher2911.killtracker.KillTracker;
import me.fisher2911.killtracker.gui.StatsMenu;
import me.fisher2911.killtracker.user.User;
import me.fisher2911.killtracker.user.UserManager;
import me.mattstudios.mf.annotations.*;
import me.mattstudios.mf.base.CommandBase;
import org.bukkit.entity.Player;

import java.util.Optional;

@Command("killtracker")
public class KillTrackerCommand extends CommandBase {

    private final KillTracker plugin;
    private final UserManager userManager;

    public KillTrackerCommand(final KillTracker plugin) {
        this.plugin = plugin;
        this.userManager = plugin.getUserManager();
    }

    @Default
    @Permission("killtracker.menu")
    public void onDefaultCommand(final Player player) {
        final Optional<User> optionalUser = this.userManager.getUser(player.getUniqueId());
        optionalUser.ifPresent(user -> {
            final StatsMenu menu = plugin.getStatsMenu();
            menu.openMenu(user);
        });
    }

    @SubCommand("players")
    @Permission("killtracker.menu")
    public void onPlayerKillsCommand(final Player player) {
        final Optional<User> optionalUser = this.userManager.getUser(player.getUniqueId());
        optionalUser.ifPresent(user -> {
            final StatsMenu menu = plugin.getStatsMenu();
            menu.openPlayerKillsMenu(user);
        });
    }

    @SubCommand("mobs")
    @Permission("killtracker.menu")
    public void onEntityKillsCommand(final Player player) {
        final Optional<User> optionalUser = this.userManager.getUser(player.getUniqueId());
        optionalUser.ifPresent(user -> {
            final StatsMenu menu = plugin.getStatsMenu();
            menu.openEntityMenu(user);
        });
    }
}
