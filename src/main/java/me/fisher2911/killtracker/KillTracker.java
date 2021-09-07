package me.fisher2911.killtracker;

import co.aikar.taskchain.BukkitTaskChainFactory;
import co.aikar.taskchain.TaskChain;
import co.aikar.taskchain.TaskChainFactory;
import me.fisher2911.killtracker.command.KillTrackerCommand;
import me.fisher2911.killtracker.config.GuiSettings;
import me.fisher2911.killtracker.config.Settings;
import me.fisher2911.killtracker.database.Database;
import me.fisher2911.killtracker.database.SQLiteDatabase;
import me.fisher2911.killtracker.gui.StatsMenu;
import me.fisher2911.killtracker.listeners.KillListener;
import me.fisher2911.killtracker.listeners.PlayerJoinListener;
import me.fisher2911.killtracker.user.User;
import me.fisher2911.killtracker.user.UserManager;
import me.mattstudios.mf.base.CommandManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Optional;

public class KillTracker extends JavaPlugin {

    private Settings settings;
    private GuiSettings guiSettings;
    private UserManager userManager;
    private Database database;
    private CommandManager commandManager;
    private final boolean debug = true;

    private static TaskChainFactory taskChainFactory;
    public static <T> TaskChain<T> newChain() {
        return taskChainFactory.newChain();
    }
    public static <T> TaskChain<T> newSharedChain(String name) {
        return taskChainFactory.newSharedChain(name);
    }

    @Override
    public void onEnable() {
        load();
        for (final Player player : Bukkit.getOnlinePlayers()) {
            database.loadUser(player.getUniqueId()).
                    ifPresent(userManager::addUser);
        }
    }

    @Override
    public void onDisable() {
        Bukkit.getOnlinePlayers().
                forEach(player -> {
                    final Optional<User> optionalUser = this.userManager.
                            getUser(player.getUniqueId());
                    optionalUser.ifPresent(database::saveUser);
                });
        database.close();
    }

    private void load() {
        taskChainFactory = BukkitTaskChainFactory.create(this);
        this.settings = new Settings(this);
        this.guiSettings = new GuiSettings(this);
        this.userManager = new UserManager(this);
        this.database = new SQLiteDatabase(this);
        this.settings.loadAllRewards();
        this.guiSettings.load();
        this.registerListeners();
        this.registerCommands();
    }

    private void registerCommands() {
        this.commandManager = new CommandManager(this);
        this.commandManager.register(new KillTrackerCommand(this));
    }

    private void registerListeners() {
        List.of(new KillListener(this),
                        new PlayerJoinListener(this)).
                forEach(listener -> this.getServer().
                        getPluginManager().
                        registerEvents(listener, this));
    }

    public void sendError(final String error) {
        this.getLogger().severe(error);
    }

    public Settings getSettings() {
        return this.settings;
    }

    public UserManager getUserManager() {
        return this.userManager;
    }

    public Database getDatabase() {
        return this.database;
    }

    public StatsMenu getStatsMenu() {
        return guiSettings.getStatsMenu();
    }

    public void debug(final String message) {
        debug(message, this.debug);
    }

    public void debug(final String message, final boolean send) {
        if (send && this.debug) {
            this.getLogger().warning("[DEBUG]: " + message);
        }
    }
}
