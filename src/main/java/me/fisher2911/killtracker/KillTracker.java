package me.fisher2911.killtracker;

import me.fisher2911.killtracker.command.KillTrackerCommand;
import me.fisher2911.killtracker.config.GuiSettings;
import me.fisher2911.killtracker.config.Settings;
import me.fisher2911.killtracker.database.Database;
import me.fisher2911.killtracker.database.SQLiteDatabase;
import me.fisher2911.killtracker.gui.StatsMenu;
import me.fisher2911.killtracker.listeners.KillListener;
import me.fisher2911.killtracker.listeners.PlayerJoinListener;
import me.fisher2911.killtracker.user.UserManager;
import me.mattstudios.mf.base.CommandManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class KillTracker extends JavaPlugin {

    private Settings settings;
    private GuiSettings guiSettings;
    private UserManager userManager;
    private Database database;
    private StatsMenu statsMenu;
    private CommandManager commandManager;
    private final boolean debug = true;

    @Override
    public void onEnable() {
        load();
    }

    @Override
    public void onDisable() {
        
    }

    private void load() {
        this.settings = new Settings(this);
        this.guiSettings = new GuiSettings(this);
        this.userManager = new UserManager(this);
        this.database = new SQLiteDatabase(this);
        this.settings.loadAllRewards();
        this.guiSettings.load();
        this.statsMenu = new StatsMenu(guiSettings.getGuiInfo());
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
        return statsMenu;
    }

    public void debug(final String message) {
        if (this.debug) {
            this.getLogger().warning("[DEBUG]: " + message);
        }
    }
}
