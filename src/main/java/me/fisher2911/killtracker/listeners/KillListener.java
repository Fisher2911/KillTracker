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

package me.fisher2911.killtracker.listeners;

import me.fisher2911.killtracker.KillTracker;
import me.fisher2911.killtracker.config.EntityGroup;
import me.fisher2911.killtracker.config.Rewards;
import me.fisher2911.killtracker.config.Settings;
import me.fisher2911.killtracker.user.KillInfo;
import me.fisher2911.killtracker.user.User;
import me.fisher2911.killtracker.user.UserManager;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
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
            user.addEntityKill(entity.getType().toString().toUpperCase());
            checkEntityRewards(entity, user);
        });
    }

    private void checkEntityRewards(final Entity killed, final User killer) {
        plugin.debug("Checking entity rewards");
        final String entityType = killed.getType().toString();
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
        rewards.applyRewards(killer.getOfflinePlayer(), amount);
    }

    private void checkPlayerRewards(final Player killed, final User killer) {
        final KillInfo killInfo = killer.getPlayerKillInfo(killed.getUniqueId());
        killInfo.setLastKilled(LocalDateTime.now());
        final int amount = killer.getTotalPlayerKills();
        final Rewards optionalRewards = settings.getPlayerRewards();
        optionalRewards.applyRewards(killer.getOfflinePlayer(), amount);
    }
}
