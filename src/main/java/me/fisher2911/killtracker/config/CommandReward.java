package me.fisher2911.killtracker.config;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class CommandReward implements Reward {

    private final String command;

    public CommandReward(final String command) {
        this.command = command;
    }

    @Override
    public void apply(final OfflinePlayer player) {
        final String playerName = player.getName();
        if (playerName == null) {
            return;
        }
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", playerName));
    }
}
