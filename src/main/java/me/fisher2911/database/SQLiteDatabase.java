package me.fisher2911.database;

import me.fisher2911.KillTracker;
import me.fisher2911.user.User;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

public class SQLiteDatabase implements Database {

    private final KillTracker plugin;

    public SQLiteDatabase(final KillTracker plugin) {
        this.plugin = plugin;
    }

    // todo
    @Override
    public Optional<User> loadUser(final UUID uuid) {
        return Optional.of(new User(uuid, new HashMap<>(), new HashMap<>()));
    }

    // todo
    @Override
    public void saveUser(final User user) {

    }
}
