package me.fisher2911.config;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class MessageReward implements Reward {

    private final String message;

    public MessageReward(final String message) {
        this.message = message;
    }

    @Override
    public void apply(final OfflinePlayer offlinePlayer) {
        if (offlinePlayer instanceof final Player player) {
            player.sendMessage(message);
        }
    }
}
