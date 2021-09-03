package me.fisher2911.killtracker.listeners;

import me.fisher2911.killtracker.KillTracker;
import me.fisher2911.killtracker.config.Settings;
import me.fisher2911.killtracker.database.Database;
import me.fisher2911.killtracker.user.User;
import me.fisher2911.killtracker.user.UserManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Optional;
import java.util.UUID;

public class PlayerJoinListener implements Listener {

    private final KillTracker plugin;
    private final Settings settings;
    private final UserManager userManager;
    private final Database database;

    public PlayerJoinListener(final KillTracker plugin) {
        this.plugin = plugin;
        this.settings = plugin.getSettings();
        this.userManager = plugin.getUserManager();
        this.database = plugin.getDatabase();
    }

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final UUID uuid = player.getUniqueId();
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            final Optional<User> optionalUser = database.loadUser(uuid);
            optionalUser.ifPresent(userManager::addUser);
        });
    }

    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        final UUID uuid = player.getUniqueId();
        final Optional<User> optionalUser = userManager.getUser(uuid);
        optionalUser.ifPresent(user -> {
            userManager.removeUser(user);
            Bukkit.
                    getScheduler().
                    runTaskAsynchronously(plugin,
                            () -> database.saveUser(user));
        });
    }
}
