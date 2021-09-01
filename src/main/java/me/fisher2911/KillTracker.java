package me.fisher2911;

import me.fisher2911.config.Settings;
import me.fisher2911.database.Database;
import me.fisher2911.database.SQLiteDatabase;
import me.fisher2911.listeners.KillListener;
import me.fisher2911.listeners.PlayerJoinListener;
import me.fisher2911.user.UserManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class KillTracker extends JavaPlugin {

    private Settings settings;
    private UserManager userManager;
    private Database database;

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
    }

    private void registerListeners() {
        List.of(new KillListener(this),
                        new PlayerJoinListener(this)).
                forEach(listener -> getServer().
                        getPluginManager().
                        registerEvents(listener, this));
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
}
