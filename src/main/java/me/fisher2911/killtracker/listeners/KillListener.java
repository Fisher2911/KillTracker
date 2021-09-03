package me.fisher2911.killtracker.listeners;

import me.fisher2911.killtracker.KillTracker;
import me.fisher2911.killtracker.config.Rewards;
import me.fisher2911.killtracker.config.Settings;
import me.fisher2911.killtracker.user.User;
import me.fisher2911.killtracker.user.UserManager;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.Optional;
import java.util.UUID;

public class KillListener implements Listener {

    private final KillTracker plugin;
    private final Settings settings;
    private final UserManager userManager;

    public KillListener(final KillTracker plugin) {
        this.plugin = plugin;
        this.settings = plugin.getSettings();
        this.userManager = plugin.getUserManager();
    }

    @EventHandler
    public void onEntityDeath(final EntityDeathEvent event) {
        final LivingEntity entity = event.getEntity();
        final Player player = entity.getKiller();
        if (player == null) {
            return;
        }
        final UUID uuid = player.getUniqueId();
        final Optional<User> optionalUser = userManager.getUser(uuid);
        optionalUser.ifPresent(user -> {
            plugin.debug("User is present: " + uuid);
            if (entity instanceof final Player killedPlayer) {
                user.addPlayerKill(killedPlayer.getUniqueId());
                checkPlayerRewards(killedPlayer, user);
                return;
            }
            user.addEntityKill(entity.getType().toString().toUpperCase());
            checkEntityRewards(entity, user);
        });
    }

    private void checkEntityRewards(final Entity killed, final User killer) {
        plugin.debug("Checking entity rewards");
        final String entityType = killed.getType().toString();
        final int amount = killer.getEntityKillAmount(entityType);
        final Optional<Rewards> optionalRewards = settings.getEntityRewards(entityType);
        Rewards rewards;
        if (optionalRewards.isPresent()) {
            plugin.debug("Rewards present");
             rewards = optionalRewards.get();
        } else if (killed instanceof Monster) {
            rewards = settings.getHostileMobsRewards();
        } else if (killed instanceof Animals) {
            rewards = settings.getPassiveMobsRewards();
        } else {
            rewards = settings.getNeutralMobsRewards();
        }
        rewards.applyRewards(killer.getOfflinePlayer(), amount);
    }

    private void checkPlayerRewards(final Player killed, final User killer) {
        final int amount = killer.getPlayerKillAmount(killed.getUniqueId());
        final Rewards optionalRewards = settings.getPlayerRewards();
        optionalRewards.applyRewards(killer.getOfflinePlayer(), amount);
    }
}
