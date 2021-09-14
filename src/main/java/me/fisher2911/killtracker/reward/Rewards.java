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

package me.fisher2911.killtracker.reward;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import me.fisher2911.killtracker.KillTracker;
import me.fisher2911.killtracker.config.Settings;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;

import java.util.Collection;
import java.util.List;

public class Rewards {

    private final KillTracker plugin;
    private final Settings settings;
    private final Multimap<Integer, Reward> rewards = ArrayListMultimap.create();
    private final Multimap<Integer, Reward> rewardAtMilestone = ArrayListMultimap.create();

    public Rewards(final KillTracker plugin) {
        this.plugin = plugin;
        this.settings = plugin.getSettings();
    }

    public void addReward(final int amount, final Reward reward) {
        this.rewards.put(amount, reward);
    }

    public void addRewards(final int milestone, final List<Reward> rewards) {
        for (final Reward reward : rewards) {
            this.addReward(milestone, reward);
        }
    }

    public void addReward(final int milestone, final Reward... rewards) {
        for (final Reward reward : rewards) {
            this.addReward(milestone, reward);
        }
    }

    public void addRewardAtMilestone(final int milestone, final Reward reward) {
        this.rewardAtMilestone.put(milestone, reward);
    }

    public void addRewardsAtMilestone(final int milestone, final List<Reward> rewards) {
        for (final Reward reward : rewards) {
            this.addRewardAtMilestone(milestone, reward);
        }
    }

    public void addRewardAtMilestone(final int milestone, final Reward... rewards) {
        for (final Reward reward : rewards) {
            this.addRewardAtMilestone(milestone, reward);
        }
    }

    public void applyRewards(final OfflinePlayer player, final Entity killedEntity, final int amount) {
        final Collection<Reward> rewardList = this.rewards.get(amount);
        plugin.debug("Rewards Total: " + rewardList);
        plugin.debug("Amount: " + amount);
        if (rewardList != null) {
            rewardList.forEach(reward -> reward.apply(player, killedEntity));
        }
        rewardAtMilestone.forEach((milestone, reward) -> {
            if (amount % milestone == 0) {
                reward.apply(player, killedEntity);
            }
        });
    }

}
