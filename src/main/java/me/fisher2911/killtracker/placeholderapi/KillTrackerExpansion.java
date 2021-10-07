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

package me.fisher2911.killtracker.placeholderapi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.fisher2911.killtracker.KillTracker;
import me.fisher2911.killtracker.database.Database;
import me.fisher2911.killtracker.user.User;
import me.fisher2911.killtracker.user.UserManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

public class KillTrackerExpansion extends PlaceholderExpansion {

    private final KillTracker plugin;
    private final UserManager userManager;

    public KillTrackerExpansion(KillTracker plugin){
        this.plugin = plugin;
        this.userManager = plugin.getUserManager();
    }

    /**
     * Because this is an internal class,
     * you must override this method to let PlaceholderAPI know to not unregister your expansion class when
     * PlaceholderAPI is reloaded
     *
     * @return true to persist through reloads
     */
    @Override
    public boolean persist(){
        return true;
    }

    /**
     * Because this is a internal class, this check is not needed
     * and we can simply return {@code true}
     *
     * @return Always true since it's an internal class.
     */
    @Override
    public boolean canRegister(){
        return true;
    }

    /**
     * The name of the person who created this expansion should go here.
     * <br>For convenience do we return the author from the plugin.yml
     *
     * @return The name of the author as a String.
     */
    @Override
    public @NotNull String getAuthor(){
        return plugin.getDescription().getAuthors().toString();
    }

    /**
     * The placeholder identifier should go here.
     * <br>This is what tells PlaceholderAPI to call our onRequest
     * method to obtain a value if a placeholder starts with our
     * identifier.
     * <br>The identifier has to be lowercase and can't contain _ or %
     *
     * @return The identifier in {@code %<identifier>_<value>%} as String.
     */
    @Override
    public String getIdentifier(){
        return "killtracker";
    }

    /**
     * This is the version of the expansion.
     * <br>You don't have to use numbers, since it is set as a String.
     *
     * For convienience do we return the version from the plugin.yml
     *
     * @return The version as a String.
     */
    @Override
    public String getVersion(){
        return plugin.getDescription().getVersion();
    }

    /**
     * This is the method called when a placeholder with our identifier
     * is found and needs a value.
     * <br>We specify the value identifier in this method.
     * <br>Since version 2.9.1 can you use OfflinePlayers in your requests.
     *
     * @param  player
     *         A {@link org.bukkit.Player Player}.
     * @param  identifier
     *         A String containing the identifier/value.
     *
     * @return possibly-null String of the requested identifier.
     */
    @Override
    public String onPlaceholderRequest(Player player, @NotNull String identifier){
        final UserManager userManager = plugin.getUserManager();
        if (player == null) {
            return "";
        }
        final Optional<User> optional = userManager.getUser(player.getUniqueId());

        if (optional.isEmpty()) {
            return "";
        }
        final User user = optional.get();
        final String[] splitData = identifier.split("_");
        if (splitData.length < 2) {
            return "";
        }
        final String data = splitData[1];
        if (identifier.startsWith("mobkills")) {
            return String.valueOf(user.getEntityKillAmount(data));
        }

        if (identifier.startsWith("playerkills")) {
            final OfflinePlayer placeholderPlayer = Bukkit.getOfflinePlayer(data);
            return String.valueOf(user.getPlayerKillAmount(placeholderPlayer.getUniqueId()));
        }
        // We return null if an invalid placeholder (f.e. %someplugin_placeholder3%)
        // was provided
        return null;
    }

}
