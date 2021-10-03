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

package me.fisher2911.killtracker.util;

import me.fisher2911.killtracker.KillTracker;
import me.fisher2911.killtracker.config.entitygroup.EntityGroup;
import me.fisher2911.killtracker.config.Settings;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;

import java.util.Optional;
import java.util.Set;

public class MobUtil {

    private static final KillTracker plugin;

    static {
        plugin = KillTracker.getPlugin(KillTracker.class);
    }

    public static Optional<String> getMobType(final Entity entity) {
        if (!plugin.isMythicMobsEnabled()) {
            return Optional.of(entity.getType().toString());
        }
        return MythicMobs.getMythicMobName(entity);
    }

    public static boolean isMythicMob(final Entity entity) {
        if (!plugin.isMythicMobsEnabled()) {
            return false;
        }
        return MythicMobs.isMythicMob(entity);
    }

    public static Set<EntityGroup> getEntityGroups(final String entity) {
        final Settings settings = plugin.getSettings();
        return settings.getEntityGroups(entity);
    }

    public static boolean isPassive(final Entity entity) {
        return entity instanceof Animals;
    }

    public static boolean isHostile(final Entity entity) {
        return entity instanceof Monster;
    }
}
