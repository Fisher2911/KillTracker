package me.fisher2911.config;

import me.fisher2911.KillTracker;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.yaml.snakeyaml.Yaml;

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

    public void loadAllRewards() {
        loadPlayerRewards();
        loadHostileMobRewards();
        loadPassiveMobRewards();
        loadNeutralMobsRewards();
        loadMobsRewards();
    }

    private void loadMobsRewards() {
        final File folder = getFile("mobs");
        if (!folder.isDirectory()) {
            plugin.sendError("Could not finds 'mobs' directory.");
            return;
        }
        final File[] files = folder.listFiles();
        if (files == null) {
            return;
        }
        for (final File file : files) {
            final String name = file.
                    getName().
                    replace(".yml", "").
                    toUpperCase();
            final Rewards rewards = loadRewards(file);
            this.entityRewards.put(name, rewards);
            // todo - remove debug
            System.out.println("Loading mobs rewards: " + name);
        }
    }

    private void loadPlayerRewards() {
        final File file = getFile("PlayerRewards.yml");
        if (!file.exists()) {
            return;
        }
        this.playerRewards = loadRewards(file);
    }

    private void loadHostileMobRewards() {
        final File file = getFile("HostileMobsRewards.yml");
        if (!file.exists()) {
            return;
        }
        this.hostileMobsRewards = loadRewards(file);
    }

    private void loadPassiveMobRewards() {
        final File file = getFile("PassiveMobsRewards.yml");
        if (!file.exists()) {
            return;
        }
        this.passiveMobsRewards = loadRewards(file);
    }

    private void loadNeutralMobsRewards() {
        final File file = getFile("NeutralMobsRewards");
            if (!file.exists()) {
                return;
            }
            this.neutralMobsRewards = loadRewards(file);
    }

    private File getFile(final String name) {
        final File file = new File(name);
        if (!file.exists()) {
            plugin.saveResource(name, false);
        }
        return file;
    }

    private Rewards loadRewards(final File file) {
        final Rewards rewards = new Rewards(plugin);
        final FileConfiguration configuration = YamlConfiguration.loadConfiguration(file);
        final String fileName = file.getName();
        for (final String key : configuration.getKeys(false)) {
            try {
                final int killsRequired = Integer.parseInt(key);
                final ConfigurationSection rewardConfiguration =
                        configuration.getConfigurationSection(key);
                if (rewardConfiguration == null) {
                    continue;
                }
                final String type = rewardConfiguration.getString("type");
                if (type == null) {
                    continue;
                }
                
                try {
                    final Reward reward = loadRewardFromType(type, fileName, rewardConfiguration);
                    if (reward == null) {
                        continue;
                    }
                    rewards.addReward(killsRequired, reward);
                } catch (final IllegalArgumentException exception) {
                    plugin.sendError(exception.getMessage());
                }

            } catch (final NumberFormatException exception) {
                plugin.sendError("Warning, " + key + " is not a valid number " +
                        "for number of kills in file: " + fileName);
            }
        }
        return rewards;
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
        return new CommandReward(message);
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
}
