package me.fisher2911.killtracker.user;

import me.fisher2911.killtracker.KillTracker;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class UserManager {

    private final KillTracker plugin;
    private final Map<UUID, User> userMap = new HashMap<>();

    public UserManager(final KillTracker plugin) {
        this.plugin = plugin;
    }

    public void addUser(final User user) {
        this.userMap.put(user.getUuid(), user);
    }

    public void removeUser(final User user) {
        this.userMap.remove(user.getUuid());
    }

    public Optional<User> getUser(final UUID uuid) {
        return Optional.ofNullable(this.userMap.get(uuid));
    }
}
