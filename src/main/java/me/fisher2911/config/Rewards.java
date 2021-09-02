package me.fisher2911.config;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import me.fisher2911.KillTracker;
import me.fisher2911.user.User;
import org.bukkit.OfflinePlayer;

import java.awt.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public void addRewardAtMilestone(final int milestone, final Reward reward) {
        this.rewardAtMilestone.put(milestone, reward);
    }

    public void applyRewards(final OfflinePlayer player, final int amount) {
        final Collection<Reward> rewardList = this.rewards.get(amount);
        plugin.debug("Rewards Total: " + rewardList);
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
