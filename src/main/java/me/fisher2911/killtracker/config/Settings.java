/*
 * KillTracker
 * Copyright (C) 2021 fisher2911
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package me.fisher2911.killtracker.config;

import me.fisher2911.killtracker.KillTracker;
import me.fisher2911.killtracker.reward.*;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.*;

public class Settings {

    private final KillTracker plugin;
    private final ItemLoader itemLoader;
    private final File dataFolder;

    public Settings(final KillTracker plugin) {
        this.plugin = plugin;
        this.itemLoader = new ItemLoader(plugin);
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
                    replace(".yml", "");
            final Rewards rewards = loadRewards(file);
            this.entityRewards.put(name, rewards);
        }
    }

    private void loadPlayerRewards() {
        final String fileName = "PlayerRewards.yml";
        final File file = FileUtil.getFile(fileName, plugin);
        if (!file.exists()) {
            return;
        }
        this.playerRewards = loadRewards(file);
    }

    private void loadHostileMobRewards() {
        final String fileName = "HostileMobsRewards.yml";
        final File file = FileUtil.getFile(fileName, plugin);
        if (!file.exists()) {
            return;
        }
        this.hostileMobsRewards = loadRewards(file);
    }

    private void loadPassiveMobRewards() {
        final String fileName = "PassiveMobsRewards.yml";
        final File file = FileUtil.getFile(fileName, plugin);
        if (!file.exists()) {
            return;
        }
        this.passiveMobsRewards = loadRewards(file);
    }

    private void loadNeutralMobsRewards() {
        final String fileName = "NeutralMobsRewards.yml";
        final File file = FileUtil.getFile(fileName, plugin);
        if (!file.exists()) {
            return;
        }
        this.neutralMobsRewards = loadRewards(file);
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
    private static final String HEAD_REWARD = "HEAD";

    private Reward loadRewardFromType(final String type, final String fileName,
                                      final ConfigurationSection section) throws IllegalArgumentException {
        return switch (type.toUpperCase()) {
            case COMMAND_REWARD -> loadCommandReward(fileName, section);
            case MESSAGE_REWARD -> loadMessageReward(fileName, section);
            case ITEMS_REWARD -> loadItemsReward(fileName, section);
            case HEAD_REWARD -> loadHeadReward(fileName, section);
            default -> throw new IllegalArgumentException(type + " is not a valid reward type!");
        };
    }

    private Reward loadCommandReward(final String fileName, final ConfigurationSection section) {
        final String command = section.getString("command");
        if (checkNull(command, "Command is null in file " +
                fileName +
                " at section " +
                section.getCurrentPath())) {
            return null;
        }
        return new CommandReward(command);
    }

    private Reward loadMessageReward(final String fileName, final ConfigurationSection section) {
        final String message = section.getString("message");
        if (checkNull(message, "Message is null in file " +
                fileName +
                " at section " +
                section.getCurrentPath())) {
            return null;
        }
        plugin.debug("Message is: " + message, false);
        return new MessageReward(ChatColor.translateAlternateColorCodes('&', message));
    }


    private Reward loadItemsReward(final String fileName, final ConfigurationSection section) {
        final ConfigurationSection itemsSection = section.getConfigurationSection("items");
        if (checkNull(itemsSection, "No items found in file " +
                fileName +
                " at section " +
                section.getCurrentPath())) {
            return null;
        }
        final Set<ItemStack> itemRewards = new HashSet<>();
        for (final String item : itemsSection.getKeys(false)) {
            final ConfigurationSection materialSection = itemsSection.getConfigurationSection(item);
            if (materialSection == null) {
                final Material material = Material.matchMaterial(item);
                if (material != null) {
                    itemRewards.add(new ItemStack(material));
                }
                continue;
            }
            final ConfigurationSection itemSection = materialSection.getConfigurationSection(item);
            if (itemSection == null) {
                continue;
            }
            itemRewards.add(itemLoader.loadItem(itemSection).getItemStack());
        }
        return new ItemsReward(itemRewards);
    }

    private Reward loadHeadReward(final String fileName, final ConfigurationSection section) {
        final String texture = returnOtherIfNull(section.getString("texture"), " ");
        final String itemName = returnOtherIfNull(section.getString("item-name"), " ");
        final List<String> lore = section.getStringList("lore");
        return new HeadReward(new HeadInfo(texture, itemName, lore));
    }

    private <T> T returnOtherIfNull(final T checked, T returnObject) {
        if (checked == null) {
            return returnObject;
        }
        return checked;
    }

    private boolean checkNull(final Object object, final String errorMessage) {
        if (object == null) {
            plugin.sendError(errorMessage);
            return true;
        }
        return false;
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
