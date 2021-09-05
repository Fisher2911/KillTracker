package me.fisher2911.killtracker.database;

import me.fisher2911.killtracker.user.User;

import java.util.Optional;
import java.util.UUID;

public interface Database {

    Optional<User> loadUser(final UUID uuid);
    void saveUser(final User user);
    void close();

}
