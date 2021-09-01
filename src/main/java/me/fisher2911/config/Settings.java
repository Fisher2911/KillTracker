package me.fisher2911.config;

import me.fisher2911.KillTracker;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Settings {

    private final KillTracker plugin;

    public Settings(final KillTracker plugin) {
        this.plugin = plugin;
    }

    private final Map<String, Rewards> entityRewards = new HashMap<>();

    private Rewards playerRewards;
    private Rewards hostileMobsRewards;
    private Rewards passiveMobsRewards;
    private Rewards neutralMobsRewards;

    public Optional<Rewards> getEntityRewards(final String entity) {
        return Optional.ofNullable(entityRewards.get(entity));
    }

    public Rewards getPlayerRewards() {
        return playerRewards;
    }

    public Rewards getHostileMobsRewards() {
        return hostileMobsRewards;
    }

    public Rewards getPassiveMobsRewards() {
        return passiveMobsRewards;
    }

    public Rewards getNeutralMobsRewards() {
        return neutralMobsRewards;
    }
}
