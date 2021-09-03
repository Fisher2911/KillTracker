package me.fisher2911.killtracker;

import me.fisher2911.killtracker.config.Settings;
import me.fisher2911.killtracker.database.Database;
import me.fisher2911.killtracker.database.SQLiteDatabase;
import me.fisher2911.killtracker.listeners.KillListener;
import me.fisher2911.killtracker.listeners.PlayerJoinListener;
import me.fisher2911.killtracker.user.UserManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class KillTracker extends JavaPlugin {

    private Settings settings;
    private UserManager userManager;
    private Database database;
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
        this.userManager = new UserManager(this);
        this.database = new SQLiteDatabase(this);
        registerListeners();
        settings.loadAllRewards();
    }

    private void registerListeners() {
        List.of(new KillListener(this),
                        new PlayerJoinListener(this)).
                forEach(listener -> getServer().
                        getPluginManager().
                        registerEvents(listener, this));
    }

    public void sendError(final String error) {
        this.getLogger().severe(error);
    }

    public Settings getSettings() {
        return settings;
    }

    public UserManager getUserManager() {
        return userManager;
    }

    public Database getDatabase() {
        return database;
    }

    public void debug(final String message) {
        if (debug) {
            this.getLogger().warning("[DEBUG]: " + message);
        }
    }
}
