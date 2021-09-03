package me.fisher2911.killtracker.config;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import me.fisher2911.killtracker.KillTracker;
import org.bukkit.OfflinePlayer;

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

    public void applyRewards(final OfflinePlayer player, final int amount) {
        final Collection<Reward> rewardList = this.rewards.get(amount);
        plugin.debug("Rewards Total: " + rewardList);
        plugin.debug("Amount: " + amount);
        if (rewardList != null) {
            rewardList.forEach(reward -> reward.apply(player));
        }
        rewardAtMilestone.forEach((milestone, reward) -> {
            if (amount % milestone == 0) {
                reward.apply(player);
            }
        });
    }

}
