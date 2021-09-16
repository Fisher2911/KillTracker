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

package me.fisher2911.killtracker.user;

import me.fisher2911.killtracker.KillTracker;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class UserManager {

    private final KillTracker plugin;
    private final Map<UUID, User> userMap = new ConcurrentHashMap<>();

    public UserManager(final KillTracker plugin) {
        this.plugin = plugin;
    }

    public void addUser(final User user) {
        this.userMap.put(user.getUuid(), user);
    }

    public void removeUser(final User user) {
        this.userMap.remove(user.getUuid());
    }

    public Optional<User> getUser(final UUID uuid) {
        return Optional.ofNullable(this.userMap.get(uuid));
    }

    public Map<UUID, User> getUserMap() {
        return userMap;
    }
}
