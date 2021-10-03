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

package me.fisher2911.killtracker.config.entitygroup;

import org.bukkit.entity.Animals;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;

public class DefaultEntityGroups extends EntityGroup {

    public static final EntityGroup PASSIVE = new DefaultEntityGroups("PASSIVE", Animals.class);
    public static final EntityGroup HOSTILE = new DefaultEntityGroups("HOSTILE", Monster.class);
    public static final EntityGroup NEUTRAL = new EntityGroup("NEUTRAL") {
        @Override
        public boolean isInGroup(final String group) {
            return !PASSIVE.isInGroup(group) && !HOSTILE.isInGroup(group);
        }
    };

    private final Class<?> clazz;

    public DefaultEntityGroups(final String id, final Class<?> clazz) {
        super(id);
        this.clazz = clazz;
    }

    @Override
    public boolean isInGroup(final String group) {
        try {
            final EntityType entityType = EntityType.valueOf(group);
            final Class<?> entityClass = entityType.getEntityClass();
            if (entityClass == null) {
                return false;
            }
            return clazz.isAssignableFrom(entityClass);
        } catch (final IllegalArgumentException exception) {
            return false;
        }
    }
}
