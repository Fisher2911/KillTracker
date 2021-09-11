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

package me.fisher2911.killtracker.command;


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

    @SubCommand("entities")
    @Permission("killtracker.menu")
    public void onEntityKillsCommand(final Player player) {
        final Optional<User> optionalUser = this.userManager.getUser(player.getUniqueId());
        optionalUser.ifPresent(user -> {
            final StatsMenu menu = plugin.getStatsMenu();
            menu.openEntityMenu(user);
        });
    }
}
