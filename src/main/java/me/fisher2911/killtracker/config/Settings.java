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

import com.google.common.collect.*;
import me.fisher2911.killtracker.KillTracker;
import me.fisher2911.killtracker.config.entitygroup.DefaultEntityGroups;
import me.fisher2911.killtracker.config.entitygroup.EntityGroup;
import me.fisher2911.killtracker.config.entitygroup.SetEntityGroup;
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
        this.dataFolder =this.plugin.getDataFolder();
    }

    private final Map<String, Rewards> entityRewards = new HashMap<>();
    // key is entity, not entity group id
    private final SetMultimap<String, EntityGroup> entityGroups = HashMultimap.create();
    private final Map<EntityGroup, Rewards> entityGroupRewards = new HashMap<>();
    private Rewards playerRewards;
    private Rewards passiveMobRewards;
    private Rewards hostileMobRewards;
    private Rewards neutralMobRewards;
    private boolean useAllTieredRewards;
    private int delaySamePlayerKills;
    private boolean sendDebugMessages;
    private int saveInterval;

    public Optional<Rewards> getEntityRewards(final String entity) {
        return Optional.ofNullable(entityRewards.get(entity));
    }

    public Set<EntityGroup> getEntityGroups(final String entity) {
        return this.entityGroups.get(entity);
    }

    public Rewards getPlayerRewards() {
        return playerRewards;
    }

    public Rewards getHostileMobsRewards() {
        return this.hostileMobRewards;

    }

    public Rewards getPassiveMobsRewards() {
        return this.passiveMobRewards;
    }

    public Rewards getNeutralMobsRewards() {
        return this.neutralMobRewards;
    }

    public Optional<Rewards> getEntityGroupRewards(final EntityGroup entityGroup) {
        return Optional.ofNullable(this.entityGroupRewards.get(entityGroup));
    }

    public Map<EntityGroup, Rewards> getAllEntityGroupRewards() {
        return this.entityGroupRewards;
    }

    private static final String KILLS_SECTION = "kills";
    private static final String MILESTONE_SECTION = "milestones";

    public void load() {
        loadSettings();
        loadAllRewards();
    }

    private void loadAllRewards() {
        loadEntityGroups();
        loadPlayerRewards();
        loadHostileMobRewards();
        loadPassiveMobRewards();
        loadNeutralMobsRewards();
        loadMobsRewards();
    }

    private void loadSettings() {
       this.plugin.saveDefaultConfig();
        final ConfigurationSection config =this.plugin.getConfig();
        this.useAllTieredRewards = config.getBoolean("use-all-tiered-rewards");
        this.delaySamePlayerKills = config.getInt("delay-same-player-kills");
        this.sendDebugMessages = config.getBoolean("send-debug-messages");
        this.saveInterval = config.getInt("save-interval");
    }

    private void loadMobsRewards() {
        final List<File> files = this.getFilesFromDir("mobs");
        for (final File file : files) {
            final String name = file.
                    getName().
                    replace(".yml", "");
            final Rewards rewards = loadRewards(file);
            this.entityRewards.put(name, rewards);
        }
    }
    
    private void loadEntityGroups() {
        final List<File> files = this.getFilesFromDir("mobgroups");
        this.plugin.debug("Loading entity groups");
        this.plugin.debug("Total Mob Groups is " + files.size());
        for (final File file : files) {
            this.loadEntityGroup(file);
        }
    }
    
    private List<File> getFilesFromDir(final String fileName) {
        final List<File> fileList = new ArrayList<>();
        final File folder = new File(dataFolder, fileName);
        if (!folder.isDirectory()) {
            folder.mkdirs();
        }
        final File[] files = folder.listFiles();
        if (files == null) {
            return fileList;
        }
        for (final File file : files) {
            if (file.getName().toLowerCase(Locale.ROOT).
                    contains(".ds_store")) {
                continue;
            }
            fileList.add(file);
        }
        return fileList;
    }

    private void loadPlayerRewards() {
        final String fileName = "PlayerRewards.yml";
        final File file = FileUtil.getFile(fileName,this.plugin);
        if (!file.exists()) {
            return;
        }
        this.playerRewards = loadRewards(file);
    }

    private void loadHostileMobRewards() {
        final String fileName = "HostileMobsRewards.yml";
        final File file = FileUtil.getFile(fileName,this.plugin);
        if (!file.exists()) {
            return;
        }
        this.hostileMobRewards = loadRewards(file);
    }

    private void loadPassiveMobRewards() {
        final String fileName = "PassiveMobsRewards.yml";
        final File file = FileUtil.getFile(fileName,this.plugin);
        if (!file.exists()) {
            return;
        }
        this.passiveMobRewards = loadRewards(file);
    }

    private void loadNeutralMobsRewards() {
        final String fileName = "NeutralMobsRewards.yml";
        final File file = FileUtil.getFile(fileName, this.plugin);
        if (!file.exists()) {
            return;
        }
        this.neutralMobRewards = loadRewards(file);
    }
    
    private void loadEntityGroup(final File file) {
        final YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        final Set<String> entities = new HashSet<>(
                config.getStringList("mobs")
        );
        this.plugin.debug("Entity groups: " + entities);
        final String groupName = file.getName().replace(".yml", "");
        final EntityGroup entityGroup = new SetEntityGroup(groupName, entities);
        entities.forEach(entity ->
                this.entityGroups.put(entity, entityGroup)
        );
        final Rewards rewards = loadRewards(file);
        plugin.debug("Rewards is: " + rewards);
        this.entityGroupRewards.put(entityGroup, rewards);
    }

    private Rewards loadRewards(final File file) {
        final Rewards rewards = new Rewards(plugin);
        if (!file.exists()) {
           this.plugin.debug(file.getName() + " does not exist", false);
            return rewards;
        }
        final String fileName = file.getName();
        final YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        final ConfigurationSection killsSection = config.getConfigurationSection(KILLS_SECTION);
        if (killsSection != null) {
           this.plugin.debug("kills section not null", false);
            addRewards(rewards, loadRewards(killsSection, fileName));
        } else {
           this.plugin.debug("Kills section null", false);
        }
        final ConfigurationSection milestoneSection = config.getConfigurationSection(MILESTONE_SECTION);
        if (milestoneSection != null) {
           this.plugin.debug("milestone section not null", false);
            addRewardsMilestones(rewards, loadRewards(milestoneSection, fileName));
        } else {
           this.plugin.debug("milestone section null", false);
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
       this.plugin.debug("Keys = " + configuration.getKeys(false), false);
       this.plugin.debug("Path = " + configuration.getCurrentPath(), false);
        for (final String key : configuration.getKeys(false)) {
           this.plugin.debug("Key is " + key, false);
            try {
                final int killsRequired = Integer.parseInt(key);
               this.plugin.debug("Kills required: " + killsRequired, false);
                final ConfigurationSection rewardsListConfiguration =
                        configuration.getConfigurationSection(key);
                if (rewardsListConfiguration == null) {
                   this.plugin.debug("RewardsListConfiguration null", false);
                    continue;
                }
                for (final String rewardKey : rewardsListConfiguration.getKeys(false)) {
                    final ConfigurationSection rewardConfiguration =
                            rewardsListConfiguration.getConfigurationSection(rewardKey);
                    if (rewardConfiguration == null) {
                       this.plugin.debug("RewardConfiguration null", false);
                        continue;
                    }
                    final String type = rewardConfiguration.getString("type");
                   this.plugin.debug("type is " + type, false);
                    if (type == null) {
                        continue;
                    }

                    try {
                        final Reward reward = loadRewardFromType(type, fileName, rewardConfiguration);
                        if (reward == null) {
                           this.plugin.debug("reward is null", false);
                            continue;
                        }
                        final List<Reward> rewardList = rewardMap.
                                computeIfAbsent(killsRequired, v -> new ArrayList<>());
                        rewardList.add(reward);
                        rewardMap.put(killsRequired, rewardList);
                       this.plugin.debug("Added reward: " + reward, false);
                    } catch (final IllegalArgumentException exception) {
                       this.plugin.sendError(exception.getMessage());
                    }
                }

            } catch (final NumberFormatException exception) {
               this.plugin.sendError("Warning, " + key + " is not a valid number " +
                        "for number of kills in file: " + fileName);
            }
        }
       this.plugin.debug("rewards = " + rewardMap, false);
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
       this.plugin.debug("Message is: " + message, false);
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
           this.plugin.sendError(errorMessage);
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
