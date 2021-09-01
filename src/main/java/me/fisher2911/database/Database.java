package me.fisher2911.database;

import me.fisher2911.user.User;

import java.util.Optional;
import java.util.UUID;

public interface Database {

    Optional<User> loadUser(final UUID uuid);
    void saveUser(final User user);

}
