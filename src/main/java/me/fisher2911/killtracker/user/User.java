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

package me.fisher2911.killtracker.user;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.checkerframework.checker.units.qual.K;

import java.util.Map;
import java.util.UUID;

public class User {

    private final UUID uuid;
    private final Map<String, Integer> entityKills;
    private final Map<UUID, KillInfo> playerKills;

    public User(final UUID uuid, final Map<String, Integer> entityKills, final Map<UUID, KillInfo> playerKills) {
        this.uuid = uuid;
        this.entityKills = entityKills;
        this.playerKills = playerKills;
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
    }

    public int getEntityKillAmount(final String entity) {
        return entityKills.getOrDefault(entity, 0);
    }

    public int getPlayerKillAmount(final UUID player) {
        return getPlayerKillInfo(player).getKills();
    }

    public KillInfo getPlayerKillInfo(final UUID player) {
        return playerKills.
                getOrDefault(player,
                        new KillInfo(0, null));
    }

    public int getTotalPlayerKills() {
        int total = 0;
        for (final KillInfo killInfo : playerKills.values()) {
            total += killInfo.getKills();
        }
        return total;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Map<String, Integer> getEntityKills() {
        return entityKills;
    }

    public Map<UUID, KillInfo> getPlayerKills() {
        return playerKills;
    }
}
