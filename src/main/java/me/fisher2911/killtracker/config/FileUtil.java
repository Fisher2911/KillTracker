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

package me.fisher2911.killtracker.config;

import me.fisher2911.killtracker.KillTracker;

import java.io.File;

public class FileUtil {

    public static File getFile(final String name, final KillTracker plugin) {
        final File file = new File(plugin.getDataFolder(), name);
        if (!file.exists()) {
            plugin.saveResource(name, false);
            plugin.debug("File " + file.getPath() + " does not exist, trying to load it...");
            if (!file.exists()) {
                plugin.debug("File " + file.getPath() + " was still not able to be loaded");
            }
        }
        return file;
    }

}
