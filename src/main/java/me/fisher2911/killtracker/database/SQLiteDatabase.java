/*
 * KillTracker
 * Copyright (C) 2021 fisher2911
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package me.fisher2911.killtracker.database;

import me.fisher2911.killtracker.KillTracker;
import me.fisher2911.killtracker.user.KillInfo;
import me.fisher2911.killtracker.user.User;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.units.qual.K;

import java.io.File;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class SQLiteDatabase implements Database {

    private final KillTracker plugin;
    private Connection conn;

    public SQLiteDatabase(final KillTracker plugin) {
        this.plugin = plugin;
        setupTables();
    }

    private Connection getConnection() {
        if (this.conn != null) {
            return this.conn;
        }
        try {
            this.conn = DriverManager.getConnection("jdbc:sqlite:" + new File(plugin.getDataFolder(), "users.db"));
            return conn;
        } catch (SQLException e) {
            plugin.getLogger().warning("Error accessing sqlite connection: " + e.getMessage());
            plugin.getLogger().warning("Shutting down...");
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(plugin);
        }
        return null;
    }

    private static final String USER_TABLE_NAME = "entity_kills";
    private static final String USER_TABLE_UUID_COLUMN = "uuid";
    private static final String USER_TABLE_ENTITY_COLUMN = "entity";
    private static final String USER_TABLE_KILL_AMOUNT_COLUMN = "kills";
    private static final String CREATE_USER_TABLE_STATEMENT =
            "CREATE TABLE IF NOT EXISTS " + USER_TABLE_NAME + " (" +
                    USER_TABLE_UUID_COLUMN + " CHAR(36), " +
                    USER_TABLE_ENTITY_COLUMN + " TEXT, " +
                    USER_TABLE_KILL_AMOUNT_COLUMN + " INTEGER, " +
                    "UNIQUE (" + USER_TABLE_UUID_COLUMN + ", " +
                    USER_TABLE_ENTITY_COLUMN + " ))";

    private static final String PLAYER_KILLS_TABLE_NAME = "player_kills";
    private static final String PLAYER_KILLS_UUID_COLUMN = "uuid";
    private static final String PLAYER_KILLS_KILLED_PLAYER_UUID_COLUMN = "killed_uuid";
    private static final String PLAYER_KILLS_AMOUNT_COLUMN = "kills";
    private static final String PLAYER_KILLS_LAST_KILLED_DATE_COLUMN = "last_killed_date";
    private static final String CREATE_PLAYER_KILLS_TABLE_STATEMENT =
            "CREATE TABLE IF NOT EXISTS " + PLAYER_KILLS_TABLE_NAME + " (" +
                    PLAYER_KILLS_UUID_COLUMN + " CHAR(36), " +
                    PLAYER_KILLS_KILLED_PLAYER_UUID_COLUMN + " CHAR(36), " +
                    PLAYER_KILLS_AMOUNT_COLUMN + " INTEGER, " +
                    PLAYER_KILLS_LAST_KILLED_DATE_COLUMN + " LOCALDATE, " +
                    "UNIQUE (" + PLAYER_KILLS_UUID_COLUMN + ", " +
                    PLAYER_KILLS_KILLED_PLAYER_UUID_COLUMN + "))";

    private static final String LOAD_USER_ENTITY_KILLS_STATEMENT = "SELECT " +
            USER_TABLE_ENTITY_COLUMN + ", " +
            USER_TABLE_KILL_AMOUNT_COLUMN +
            " FROM " +
            USER_TABLE_NAME + " " +
            "WHERE " + USER_TABLE_UUID_COLUMN + " " +
            "=?";

    private static final String LOAD_USER_PLAYER_KILLS_STATEMENT = "SELECT " +
            PLAYER_KILLS_KILLED_PLAYER_UUID_COLUMN + ", " +
            PLAYER_KILLS_AMOUNT_COLUMN + ", " +
            PLAYER_KILLS_LAST_KILLED_DATE_COLUMN +
            " FROM " +
            PLAYER_KILLS_TABLE_NAME + " " +
            "WHERE " + PLAYER_KILLS_UUID_COLUMN + " " +
            "=?";

    private static final String SAVE_USER_ENTITY_KILLS_STATEMENT =
            "INSERT INTO " +
                    USER_TABLE_NAME + " (" +
                    USER_TABLE_UUID_COLUMN + ", " +
                    USER_TABLE_ENTITY_COLUMN + ", " +
                    USER_TABLE_KILL_AMOUNT_COLUMN + ") " +
                    "VALUES (?,?,?) " +
                    "ON CONFLICT (" +
                    USER_TABLE_UUID_COLUMN + "," +
                    USER_TABLE_ENTITY_COLUMN + ") " +
                    "DO UPDATE SET " +
                    USER_TABLE_ENTITY_COLUMN + "=?, " +
                    USER_TABLE_KILL_AMOUNT_COLUMN + "=?";

    private static final String SAVE_USER_PLAYER_KILLS_STATEMENT =
            "INSERT INTO " +
                    PLAYER_KILLS_TABLE_NAME + " (" +
                    PLAYER_KILLS_UUID_COLUMN + ", " +
                    PLAYER_KILLS_KILLED_PLAYER_UUID_COLUMN + ", " +
                    PLAYER_KILLS_AMOUNT_COLUMN + ", " +
                    PLAYER_KILLS_LAST_KILLED_DATE_COLUMN + ") " +
                    "VALUES (?,?,?,?) " +
                    "ON CONFLICT (" +
                    PLAYER_KILLS_UUID_COLUMN + "," +
                    PLAYER_KILLS_KILLED_PLAYER_UUID_COLUMN + ") " +
                    "DO UPDATE SET " +
                    PLAYER_KILLS_KILLED_PLAYER_UUID_COLUMN + "=?, " +
                    PLAYER_KILLS_AMOUNT_COLUMN + "=?, " +
                    PLAYER_KILLS_LAST_KILLED_DATE_COLUMN + "=?";

    private void setupTables() {
        createTable(CREATE_USER_TABLE_STATEMENT);
        createTable(CREATE_PLAYER_KILLS_TABLE_STATEMENT);
    }

    private void createTable(final String statementString) {
        final Connection conn = getConnection();
        try (final PreparedStatement statement = conn.prepareStatement(statementString)) {
            statement.execute();
            plugin.debug("Created table: " + statementString);
        } catch (final Exception exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public Optional<User> loadUser(final UUID uuid) {
       final Map<String, Integer> entityKills = loadEntityKillsAmount(uuid);
       plugin.debug("Entity Kills: " + entityKills);
       final Map<UUID, KillInfo> playerKills = loadPlayerKillsAmount(uuid);
       plugin.debug("Player Kills: " + playerKills);
       return Optional.of(new User(uuid, entityKills, playerKills));
    }

    private Map<UUID, KillInfo> loadPlayerKillsAmount(final UUID uuid) {
        final Map<UUID, KillInfo> killsMap = new HashMap<>();
        ResultSet results = null;
        try (final PreparedStatement statement = getConnection().prepareStatement(
                LOAD_USER_PLAYER_KILLS_STATEMENT);
        ) {
            statement.setString(1, uuid.toString());
            results = statement.executeQuery();
            while (results.next()) {
                final String killedUuidString = results.getString(PLAYER_KILLS_KILLED_PLAYER_UUID_COLUMN);
                final int kills = results.getInt(PLAYER_KILLS_AMOUNT_COLUMN);
                final Timestamp lastKilledDate = results.getTimestamp(PLAYER_KILLS_LAST_KILLED_DATE_COLUMN);
                if (killedUuidString == null) {
                    continue;
                }
                final KillInfo killInfo = new KillInfo(kills, lastKilledDate.toLocalDateTime());
                killsMap.put(UUID.fromString(killedUuidString), killInfo);
            }
        } catch (final Exception exception) {
            exception.printStackTrace();
        } finally {
            try {
                if (results != null) {
                    results.close();
                }
            } catch (final SQLException exception) {
                exception.printStackTrace();
            }
        }
        return killsMap;
    }

    private Map<String, Integer> loadEntityKillsAmount(final UUID uuid) {
        final Map<String, Integer> killsMap = new HashMap<>();
        ResultSet results = null;
        try (final PreparedStatement statement = getConnection().prepareStatement(
                LOAD_USER_ENTITY_KILLS_STATEMENT);
            ) {
            statement.setString(1, uuid.toString());
            results = statement.executeQuery();
            while (results.next()) {
                final String entityType = results.getString(USER_TABLE_ENTITY_COLUMN);
                final int kills = results.getInt(USER_TABLE_KILL_AMOUNT_COLUMN);
                killsMap.put(entityType, kills);
            }
        } catch (final Exception exception) {
            exception.printStackTrace();
        } finally {
            try {
                if (results != null) {
                    results.close();
                }
            } catch (final SQLException exception) {
                exception.printStackTrace();
            }
        }
        return killsMap;
    }

    @Override
    public void saveUser(final User user) {
        saveUserEntityKills(user);
        saveUserPlayerKills(user);
    }

    private void saveUserEntityKills(final User user) {
        final Map<String, Integer> entityKills = user.getEntityKills();
        try (final PreparedStatement statement = getConnection().prepareStatement(
                SAVE_USER_ENTITY_KILLS_STATEMENT
        )) {
            final String uuid = user.getUuid().toString();
            for (final Map.Entry<String, Integer> entry : entityKills.entrySet()) {
                final String entityType = entry.getKey();
                final int kills = entry.getValue();
                statement.setString(1, uuid);
                statement.setString(2, entityType);
                statement.setInt(3, kills);
                statement.setString(4, entityType);
                statement.setInt(5, kills);
                statement.addBatch();
            }
            statement.executeBatch();
        } catch (final Exception exception) {
            exception.printStackTrace();
        }
    }

    private void saveUserPlayerKills(final User user) {
        final Map<UUID, KillInfo> playerKills = user.getPlayerKills();
        try (final PreparedStatement statement = getConnection().prepareStatement(
                SAVE_USER_PLAYER_KILLS_STATEMENT
        )) {
            final String uuid = user.getUuid().toString();
            for (final Map.Entry<UUID, KillInfo> entry : playerKills.entrySet()) {
                final String killedUuid = entry.getKey().toString();
                final KillInfo killInfo = entry.getValue();
                final int kills = killInfo.getKills();
                final Timestamp lastKilled = Timestamp.valueOf(killInfo.getLastKilled());
                statement.setString(1, uuid);
                statement.setString(2, killedUuid);
                statement.setInt(3, kills);
                statement.setObject(4, lastKilled);
                statement.setString(5, killedUuid);
                statement.setInt(6, kills);
                statement.setObject(7, lastKilled);
                statement.addBatch();
            }
            statement.executeBatch();
        } catch (final Exception exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void close() {
        try {
            conn.close();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }
}
