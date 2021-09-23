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

package me.fisher2911.killtracker.util;

import io.lumine.xikage.mythicmobs.mobs.ActiveMob;
import io.lumine.xikage.mythicmobs.mobs.MobManager;
import org.bukkit.entity.Entity;

import java.util.Optional;

public class MythicMobs {

    private static final io.lumine.xikage.mythicmobs.MythicMobs mythicMobs;
    private static final MobManager mobManager;

    static {
        mythicMobs = io.lumine.xikage.mythicmobs.MythicMobs.inst();
        mobManager = mythicMobs.getMobManager();
    }

    public static Optional<String> getMythicMobName(final Entity entity) {
        if(!isMythicMob(entity)) {
            return Optional.of(entity.getType().toString());
        }
        final ActiveMob activeMob = mobManager.getMythicMobInstance(entity);
        return Optional.of(activeMob.getType().getInternalName());
    }

    public static boolean isMythicMob(final Entity entity) {
        return mobManager.isActiveMob(entity.getUniqueId());
    }

}
