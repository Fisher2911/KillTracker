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

package me.fisher2911.killtracker.message;

import org.bukkit.ChatColor;

public class Message {

    public static final String PREFIX = ChatColor.AQUA + "KillTracker >> ";
    public static final String NO_UPDATE_MESSAGE = PREFIX + ChatColor.GREEN + "No update found.";
    public static final String YES_UPDATE_MESSAGE = PREFIX + ChatColor.RED +
            "Version %version% found, please update as soon as possible";

}
