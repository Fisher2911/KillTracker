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

package me.fisher2911.killtracker.config;

import me.fisher2911.killtracker.KillTracker;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.*;

public class Settings {

    private final KillTracker plugin;
    private final File dataFolder;

    public Settings(final KillTracker plugin) {
        this.plugin = plugin;
        this.dataFolder = plugin.getDataFolder();
    }

    private final Map<String, Rewards> entityRewards = new HashMap<>();

    private Rewards playerRewards;
    private Rewards hostileMobsRewards;
    private Rewards passiveMobsRewards;
    private Rewards neutralMobsRewards;
    private boolean useAllTieredRewards;
    private int delaySamePlayerKills;
    private boolean sendDebugMessages;
    private int saveInterval;

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

    private static final String KILLS_SECTION = "kills";
    private static final String MILESTONE_SECTION = "milestones";

    public void load() {
        loadAllRewards();
        loadSettings();
    }

    private void loadAllRewards() {
        loadPlayerRewards();
        loadHostileMobRewards();
        loadPassiveMobRewards();
        loadNeutralMobsRewards();
        loadMobsRewards();
    }

    private void loadSettings() {
        plugin.saveDefaultConfig();
        final ConfigurationSection config = plugin.getConfig();
        this.useAllTieredRewards = config.getBoolean("use-all-tiered-rewards");
        this.delaySamePlayerKills = config.getInt("delay-same-player-kills");
        this.sendDebugMessages = config.getBoolean("send-debug-messages");
        this.saveInterval = config.getInt("save-interval");
    }

    private void loadMobsRewards() {
        final File folder = new File(dataFolder, "mobs");
        if (!folder.isDirectory()) {
            folder.mkdirs();
        }
        final File[] files = folder.listFiles();
        if (files == null) {
            return;
        }
        for (final File file : files) {
            if (file.getName().toLowerCase(Locale.ROOT).
                    contains(".ds_store")) {
                continue;
            }
            final String name = file.
                    getName().
                    replace(".yml", "").
                    toUpperCase();
            final Rewards rewards = loadRewards(file);
            this.entityRewards.put(name, rewards);
        }
    }

    private void loadPlayerRewards() {
        final String fileName = "PlayerRewards.yml";
        final File file = getFile(fileName);
        if (!file.exists()) {
            return;
        }
        this.playerRewards = loadRewards(file);
    }

    private void loadHostileMobRewards() {
        final String fileName = "HostileMobsRewards.yml";
        final File file = getFile(fileName);
        if (!file.exists()) {
            return;
        }
        this.hostileMobsRewards = loadRewards(file);
    }

    private void loadPassiveMobRewards() {
        final String fileName = "PassiveMobsRewards.yml";
        final File file = getFile(fileName);
        if (!file.exists()) {
            return;
        }
        this.passiveMobsRewards = loadRewards(file);
    }

    private void loadNeutralMobsRewards() {
        final String fileName = "NeutralMobsRewards.yml";
        final File file = getFile(fileName);
        if (!file.exists()) {
            return;
        }
        this.neutralMobsRewards = loadRewards(file);
    }

    private File getFile(final String name) {
        final File file = new File(dataFolder, name);
        if (!file.exists()) {
            plugin.saveResource(name, false);
        }
        return file;
    }

    private Rewards loadRewards(final File file) {
        final Rewards rewards = new Rewards(plugin);
        if (!file.exists()) {
            plugin.debug(file.getName() + " does not exist", false);
            return rewards;
        }
        final String fileName = file.getName();
        final YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        final ConfigurationSection killsSection = config.getConfigurationSection(KILLS_SECTION);
        if (killsSection != null) {
            plugin.debug("kills section not null", false);
            addRewards(rewards, loadRewards(killsSection, fileName));
        } else {
            plugin.debug("Kills section null", false);
        }
        final ConfigurationSection milestoneSection = config.getConfigurationSection(MILESTONE_SECTION);
        if (milestoneSection != null) {
            plugin.debug("milestone section not null", false);
            addRewardsMilestones(rewards, loadRewards(milestoneSection, fileName));
        } else {
            plugin.debug("milestone section null", false);
        }
        return rewards;
    }

    private void addRewards(final Rewards rewards, final Map<Integer, List<Reward>> rewardMap) {
        rewardMap.forEach(rewards::addRewards);
    }

    private void addRewardsMilestones(final Rewards rewards, final Map<Integer, List<Reward>> rewardMap) {
        rewardMap.forEach(rewards::addRewardsAtMilestone);
    }

    private Map<Integer, List<Reward>> loadRewards(final ConfigurationSection configuration, final String fileName) {
        final Map<Integer, List<Reward>> rewardMap = new HashMap<>();
        plugin.debug("Keys = " + configuration.getKeys(false), false);
        plugin.debug("Path = " + configuration.getCurrentPath(), false);
        for (final String key : configuration.getKeys(false)) {
            plugin.debug("Key is " + key, false);
            try {
                final int killsRequired = Integer.parseInt(key);
                plugin.debug("Kills required: " + killsRequired, false);
                final ConfigurationSection rewardsListConfiguration =
                        configuration.getConfigurationSection(key);
                if (rewardsListConfiguration == null) {
                    plugin.debug("RewardsListConfiguration null", false);
                    continue;
                }
                for (final String rewardKey : rewardsListConfiguration.getKeys(false)) {
                    final ConfigurationSection rewardConfiguration =
                            rewardsListConfiguration.getConfigurationSection(rewardKey);
                    if (rewardConfiguration == null) {
                        plugin.debug("RewardConfiguration null", false);
                        continue;
                    }
                    final String type = rewardConfiguration.getString("type");
                    plugin.debug("type is " + type, false);
                    if (type == null) {
                        continue;
                    }

                    try {
                        final Reward reward = loadRewardFromType(type, fileName, rewardConfiguration);
                        if (reward == null) {
                            plugin.debug("reward is null", false);
                            continue;
                        }
                        final List<Reward> rewardList = rewardMap.
                                computeIfAbsent(killsRequired, v -> new ArrayList<>());
                        rewardList.add(reward);
                        rewardMap.put(killsRequired, rewardList);
                        plugin.debug("Added reward: " + reward, false);
                    } catch (final IllegalArgumentException exception) {
                        plugin.sendError(exception.getMessage());
                    }
                }

            } catch (final NumberFormatException exception) {
                plugin.sendError("Warning, " + key + " is not a valid number " +
                        "for number of kills in file: " + fileName);
            }
        }
        plugin.debug("rewards = " + rewardMap, false);
        return rewardMap;
    }

    private static final String COMMAND_REWARD = "COMMAND";
    private static final String MESSAGE_REWARD = "MESSAGE";
    private static final String ITEMS_REWARD = "ITEMS";

    private Reward loadRewardFromType(final String type, final String fileName,
                                      final ConfigurationSection section) throws IllegalArgumentException {
        return switch (type.toUpperCase()) {
            case COMMAND_REWARD -> loadCommandReward(fileName, section);
            case MESSAGE_REWARD -> loadMessageReward(fileName, section);
            case ITEMS_REWARD -> loadItemsReward(fileName, section);
            default -> throw new IllegalArgumentException(type + " is not a valid reward type!");
        };
    }

    private Reward loadCommandReward(final String fileName, final ConfigurationSection section) {
        final String command = section.getString("command");
        if (command == null) {
            plugin.sendError("Command is null in file " +
                    fileName +
                    " at section " +
                    section.getCurrentPath());
            return null;
        }
        return new CommandReward(command);
    }

    private Reward loadMessageReward(final String fileName, final ConfigurationSection section) {
        final String message = section.getString("message");
        if (message == null) {
            plugin.sendError("Message is null in file " +
                    fileName +
                    " at section " +
                    section.getCurrentPath());
            return null;
        }
        plugin.debug("Message is: " + message, false);
        return new MessageReward(ChatColor.translateAlternateColorCodes('&', message));
    }

    private Reward loadItemsReward(final String fileName, final ConfigurationSection section) {
        final ConfigurationSection itemSection = section.getConfigurationSection("items");
        if (itemSection == null) {
            plugin.sendError("No items found in file " +
                    fileName +
                    " at section " +
                    section.getCurrentPath());
            return null;
        }
        final Map<ItemStack, Integer> itemRewards = new HashMap<>();
        for (final String item : itemSection.getKeys(false)) {
            final int amount = itemSection.getInt(item);
            try {
                final Material material = Material.matchMaterial(item);
                itemRewards.put(new ItemStack(material), amount);
            } catch (final IllegalArgumentException | NullPointerException exception) {
                plugin.sendError(item + " is not a valid material in file " +
                        fileName +
                        " at section " +
                        section.getCurrentPath());
            }
        }
        return new ItemsReward(itemRewards);
    }

    public boolean useAllTieredRewards() {
        return useAllTieredRewards;
    }

    public int getDelaySamePlayerKills() {
        return delaySamePlayerKills;
    }

    public boolean sendDebugMessages() {
        return this.sendDebugMessages;
    }

    public int getSaveInterval() {
        return saveInterval;
    }
}
