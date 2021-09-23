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

package me.fisher2911.killtracker.listeners;

import me.fisher2911.killtracker.KillTracker;
import me.fisher2911.killtracker.config.EntityGroup;
import me.fisher2911.killtracker.reward.Rewards;
import me.fisher2911.killtracker.config.Settings;
import me.fisher2911.killtracker.user.KillInfo;
import me.fisher2911.killtracker.user.User;
import me.fisher2911.killtracker.user.UserManager;
import me.fisher2911.killtracker.util.MobUtil;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import java.time.Duration;
import java.time.LocalDateTime;
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

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityDeath(final EntityDeathEvent event) {
        final LivingEntity entity = event.getEntity();
        final Player player = entity.getKiller();
        if (player == null) {
            return;
        }
        final UUID uuid = player.getUniqueId();
        final Optional<User> optionalUser = userManager.getUser(uuid);
        optionalUser.ifPresent(user -> {
            if (entity instanceof final Player killedPlayer) {
                final KillInfo killInfo = user.getPlayerKillInfo(killedPlayer.getUniqueId());
                final int killDelaySeconds = settings.getDelaySamePlayerKills();
                final LocalDateTime lastKilled = killInfo.getLastKilled();
                if (lastKilled != null &&
                        Duration.between(lastKilled,
                                        LocalDateTime.now()).
                                getSeconds() < killDelaySeconds) {
                    return;
                }
                user.addPlayerKill(killedPlayer.getUniqueId());
                checkPlayerRewards(killedPlayer, user);
                return;
            }
            final Optional<String> entityTypeOptional = MobUtil.getMobType(entity);
            if (entityTypeOptional.isEmpty()) {
                return;
            }
            final String entityType = entityTypeOptional.get();
            user.addEntityKill(entityTypeOptional.get());
            checkEntityRewards(entityType, user, entity);
        });
    }

    private void checkEntityRewards(final String entityType, final User killer, final Entity killed) {
        plugin.debug("Checking entity rewards");
        int amount = killer.getEntityKillAmount(entityType);
        final Optional<Rewards> optionalRewards = settings.getEntityRewards(entityType);
        Rewards rewards = null;
        boolean acceptMoreRewards = true;
        if (optionalRewards.isPresent()) {
            plugin.debug("Rewards present");
             rewards = optionalRewards.get();
             acceptMoreRewards = settings.useAllTieredRewards();
        }
        if (acceptMoreRewards) {
            plugin.debug("Accepting more rewards");
            String entityGroup = EntityGroup.NEUTRAL.toString();
            if (killed instanceof Monster) {
                rewards = settings.getHostileMobsRewards();
                entityGroup = EntityGroup.HOSTILE.toString();
                plugin.debug("Mob is hostile");
            } else if (killed instanceof Animals) {
                rewards = settings.getPassiveMobsRewards();
                entityGroup = EntityGroup.PASSIVE.toString();
                plugin.debug("Mob is passive");
            } else {
                rewards = settings.getNeutralMobsRewards();
                plugin.debug("Mob is neutral");
            }
            plugin.debug("amount is " + amount);
            killer.addEntityKill(entityGroup);
            amount = killer.getEntityKillAmount(entityGroup);
        }
        if (rewards == null) {
            return;
        }
        rewards.applyRewards(killer.getOfflinePlayer(), killed, amount);
    }

    private void checkPlayerRewards(final Player killed, final User killer) {
        final KillInfo killInfo = killer.getPlayerKillInfo(killed.getUniqueId());
        killInfo.setLastKilled(LocalDateTime.now());
        final int amount = killer.getTotalPlayerKills();
        final Rewards optionalRewards = settings.getPlayerRewards();
        optionalRewards.applyRewards(killer.getOfflinePlayer(), killed, amount);
    }
}
