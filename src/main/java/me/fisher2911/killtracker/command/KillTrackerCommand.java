package me.fisher2911.killtracker.command;


import me.fisher2911.killtracker.KillTracker;
import me.fisher2911.killtracker.gui.StatsMenu;
import me.fisher2911.killtracker.user.User;
import me.fisher2911.killtracker.user.UserManager;
import me.mattstudios.mf.annotations.Alias;
import me.mattstudios.mf.annotations.Command;
import me.mattstudios.mf.annotations.Default;
import me.mattstudios.mf.base.CommandBase;
import org.bukkit.entity.Player;

import java.util.Optional;

@Command("killtracker")
@Alias("stats")
public class KillTrackerCommand extends CommandBase {

    private final KillTracker plugin;
    private final UserManager userManager;

    public KillTrackerCommand(final KillTracker plugin) {
        this.plugin = plugin;
        this.userManager = plugin.getUserManager();
    }

    @Default
    public void onDefaultCommand(final Player player) {
        final Optional<User> optionalUser = this.userManager.getUser(player.getUniqueId());
        optionalUser.ifPresent(user -> {
            final StatsMenu menu = plugin.getStatsMenu();
            menu.open(user);
        });
    }
}
