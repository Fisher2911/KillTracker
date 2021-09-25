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

package me.fisher2911.killtracker.placeholder;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Map;

public class Placeholder {

    public static String PLAYER_PLACEHOLDER = "%player%";
    public static String KILLS_PLACEHOLDER = "%kills%";
    public static final String NAME_PLACEHOLDER = "%name%";

    public static String addPlaceholders(final Map<String, String> placeholders, final String message) {
        String replace = message;
        for (final Map.Entry<String, String> entry : placeholders.entrySet()) {
            final String key = entry.getKey();
            final String value = entry.getValue();
            if (value == null || key == null) {
                continue;
            }
            replace = replace.replace(entry.getKey(), entry.getValue());
        }
        return replace;
    }

    public static String addNamePlaceholders(final String message, final String name) {
        return message.replace(NAME_PLACEHOLDER, name);
    }


    public static String addPlayerPlaceholders(final String message, final Player player) {
        return message.replace(PLAYER_PLACEHOLDER, player.getName());
    }

    public static String addPlayerAndKillsPlaceholders(final String message,
                                                       final OfflinePlayer player,
                                                       final int kills) {
        return addPlaceholders(Map.of(PLAYER_PLACEHOLDER, player.getName(),
                                    KILLS_PLACEHOLDER, String.valueOf(kills)), message);
    }

}
