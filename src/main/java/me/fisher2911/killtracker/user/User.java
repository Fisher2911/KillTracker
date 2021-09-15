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

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.Map;
import java.util.UUID;

public class User {

    private final UUID uuid;
    private final Map<String, Integer> entityKills;
    private final Map<UUID, KillInfo> playerKills;
    private int totalPlayerKills = 0;

    public User(final UUID uuid, final Map<String, Integer> entityKills, final Map<UUID, KillInfo> playerKills) {
        this.uuid = uuid;
        this.entityKills = entityKills;
        this.playerKills = playerKills;
        for (final KillInfo killInfo : this.playerKills.values()) {
            this.totalPlayerKills += killInfo.getKills();
        }
    }

    public OfflinePlayer getOfflinePlayer() {
        return Bukkit.getOfflinePlayer(uuid);
    }

    public void addEntityKill(final String entity) {
        final int kills = this.entityKills.computeIfAbsent(entity, v -> 0) + 1;
        this.entityKills.put(entity, kills);
    }

    public void addPlayerKill(final UUID player) {
        final KillInfo killInfo = this.playerKills.computeIfAbsent(player,
                v -> new KillInfo(0, null));
        killInfo.addKill();
        this.playerKills.put(player, killInfo);
        this.totalPlayerKills++;
    }

    public int getEntityKillAmount(final String entity) {
        return this.entityKills.getOrDefault(entity, 0);
    }

    public int getPlayerKillAmount(final UUID player) {
        return getPlayerKillInfo(player).getKills();
    }

    public KillInfo getPlayerKillInfo(final UUID player) {
        return this.playerKills.
                getOrDefault(player,
                        new KillInfo(0, null));
    }

    public int getTotalPlayerKills() {
        return this.totalPlayerKills;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public Map<String, Integer> getEntityKills() {
        return this.entityKills;
    }

    public Map<UUID, KillInfo> getPlayerKills() {
        return this.playerKills;
    }
}
