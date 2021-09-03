package me.fisher2911.killtracker.user;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.EntityType;

import java.util.Map;
import java.util.UUID;

public class User {

    private final UUID uuid;
    private final Map<String, Integer> entityKills;
    private final Map<UUID, Integer> playerKills;

    public User(final UUID uuid, final Map<String, Integer> entityKills, final Map<UUID, Integer> playerKills) {
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
        final int kills = this.playerKills.computeIfAbsent(player, v -> 0) + 1;
        this.playerKills.put(player, kills);
    }

    public int getEntityKillAmount(final String entity) {
        return entityKills.getOrDefault(entity, 0);
    }

    public int getPlayerKillAmount(final UUID player) {
        return playerKills.get(player);
    }

    public UUID getUuid() {
        return uuid;
    }
}
