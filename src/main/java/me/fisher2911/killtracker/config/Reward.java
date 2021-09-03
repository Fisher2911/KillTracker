package me.fisher2911.killtracker.config;

import org.bukkit.OfflinePlayer;

import java.util.UUID;

public interface Reward {

    void apply(final OfflinePlayer player);

}
