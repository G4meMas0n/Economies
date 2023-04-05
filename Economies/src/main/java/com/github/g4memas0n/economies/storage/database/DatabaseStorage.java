package com.github.g4memas0n.economies.storage.database;

import com.github.g4memas0n.cores.database.DatabaseManager;
import com.github.g4memas0n.cores.database.query.BatchLoader;
import com.github.g4memas0n.economies.Economies;
import com.github.g4memas0n.economies.storage.AccountStorage;
import com.github.g4memas0n.economies.storage.StorageException;
import com.github.g4memas0n.economies.storage.StorageManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;
import java.util.logging.Level;

public class DatabaseStorage extends DatabaseManager implements StorageManager {

    private static final int MAJOR = 1;
    private static final int MINOR = 0;

    protected final Economies plugin;

    public DatabaseStorage(@NotNull final Economies plugin) {
        super("resources/queries/queries");
        this.plugin = plugin;
    }

    @Override
    public void initialize() throws SQLException {
        Statement statement;
        ResultSet result;

        try (Connection connection = fetch()) {
            statement = connection.createStatement();

            // create tables
            BatchLoader.getBatch("resources/batches/tables-create.sql", statement);
            for (final int value : statement.executeBatch()) {
                if (value == Statement.EXECUTE_FAILED) {
                    throw new SQLException("could not create tables");
                }
            }
            statement.clearBatch();

            // check schema version
            result = statement.executeQuery(query("version.get"));
            if (result.next()) {
                int major = result.getInt("version_major");
                int minor = result.getInt("version_minor");

                if (major > MAJOR || minor > MINOR) {
                    throw new SQLException("unknown schema version");
                }
            }  else {
                throw new SQLException("could not fetch schema version");
            }

            // initialize tables
            BatchLoader.getBatch("resources/batches/tables-initialize.sql", statement);
            for (final int value : statement.executeBatch()) {
                if (value == Statement.EXECUTE_FAILED) {
                    throw new SQLException("could not initialize tables");
                }
            }
            statement.clearBatch();
        } catch (IOException ex) {

            throw new SQLException("could not read batches", ex);
        }
    }

    /*
     * Methods implementing the StorageManager interface:
     */

    public @NotNull AccountStorage getGlobal() throws StorageException {
        PreparedStatement statement;
        ResultSet result;
        int count;

        try (Connection connection = fetch()) {
            statement = prepare(connection, "account.balance.get");
            statement.setInt(1, 1);
            result = statement.executeQuery();

            if (!result.next()) {
                close(statement);
                Economies.log(Level.SEVERE, "Failed to query balance for global account");
                throw new StorageException("failed to find global account");
            }

            // initialize balance of global bank account if not already set
            if (result.getBigDecimal("balance") == null) {
                close(statement);

                statement = prepare(connection, "account.balance.set");
                statement.setBigDecimal(1, this.plugin.getSettings().getBankBalance());
                statement.setInt(2, 1);
                count = statement.executeUpdate();

                if (count == 0) {
                    close(statement);
                    Economies.log(Level.SEVERE, "Failed to update balance for global account");
                    throw new StorageException("failed to find global account");
                }
            }

            close(statement);
        } catch (SQLException ex) {
            Economies.warn("Could not query global account: %s", ex.getMessage());
            throw new StorageException("could not query global account", ex);
        }

        return new DatabaseAccount(this, 1);
    }

    @Override
    public @Nullable AccountStorage getAccount(@NotNull final String name) throws StorageException {
        PreparedStatement statement;
        ResultSet result;
        int id = 0;

        try (Connection connection = fetch()) {
            statement = prepare(connection, "account.search");
            statement.setString(1, name);
            result = statement.executeQuery();

            while (result.next()) {
                if (id > 0) {
                    id = 0;
                    Economies.debug("Queried multiple accounts for name %s", name);
                    break;
                }

                id = result.getInt("id");
            }

            close(statement);
        } catch (SQLException ex) {
            Economies.warn("Could not query account for name %s: %s", name, ex.getMessage());
            throw new StorageException("could not query account", ex);
        }

        if (id > 0) {
            Economies.debug("Queried account (%d) for name %s", id, name);
            return new DatabaseAccount(this, id);
        }

        return null;
    }

    @Override
    public @Nullable DatabaseAccount getAccount(@NotNull final UUID uniqueId) throws StorageException {
        PreparedStatement statement;
        ResultSet result;
        int id = 0;

        try (Connection connection = fetch()) {
            statement = prepare(connection, "account.get");
            statement.setLong(1, uniqueId.getMostSignificantBits());
            statement.setLong(2, uniqueId.getLeastSignificantBits());
            result = statement.executeQuery();

            if (result.next()) {
                // an account for the unique-id has been found
                id = result.getInt("id");
                Economies.debug("Queried account (%d) for uuid %s", id, uniqueId);
            }

            close(statement);
        } catch (SQLException ex) {
            Economies.warn("Could not query account for uuid %s: %s", uniqueId, ex.getMessage());
            throw new StorageException("could not query account", ex);
        }

        return id > 0 ? new DatabaseAccount(this, id) : null;
    }

    @Override
    public boolean hasAccount(@NotNull final String name) throws StorageException {
        PreparedStatement statement;
        ResultSet result;
        int count = 0;

        try (Connection connection = fetch()) {
            statement = prepare(connection, "account.count");
            statement.setString(1, name);
            result = statement.executeQuery();

            if (!result.next()) {
                close(statement);
                Economies.log(Level.SEVERE, "Failed to check for account with name %s", name);
                throw new StorageException("failed to check for account");
            }

            count = result.getInt(1);
            close(statement);
        } catch (SQLException ex) {
            Economies.warn("Could not check for account with name %s: %s", name, ex.getMessage());
            throw new StorageException("could not check for account", ex);
        }

        return count == 1;
    }

    @Override
    public boolean hasAccount(@NotNull final UUID uniqueId) throws StorageException {
        PreparedStatement statement;
        ResultSet result;
        boolean exist;

        try (Connection connection = fetch()) {
            statement = prepare(connection, "account.exist");
            statement.setLong(1, uniqueId.getMostSignificantBits());
            statement.setLong(2, uniqueId.getLeastSignificantBits());
            result = statement.executeQuery();

            if (!result.next()) {
                close(statement);
                Economies.log(Level.SEVERE, "Failed to check for account with uuid %s", uniqueId);
                throw new StorageException("failed to check for account");
            }

            exist = result.getBoolean(1);
            close(statement);
        } catch (SQLException ex) {
            Economies.warn("Could not check for account with uuid %s: %s", uniqueId, ex.getMessage());
            throw new StorageException("could not check for account", ex);
        }

        return exist;
    }

    @Override
    public boolean createAccount(@NotNull final UUID uniqueId, @NotNull final String name) throws StorageException {
        PreparedStatement statement;
        ResultSet result;
        int created;

        try (Connection connection = fetch()) {
            statement = prepare(connection, "account.exist");
            statement.setLong(1, uniqueId.getMostSignificantBits());
            statement.setLong(2, uniqueId.getLeastSignificantBits());
            result = statement.executeQuery();

            if (!result.next()) {
                close(statement);
                Economies.log(Level.SEVERE, "Failed to check for account with uuid %s", uniqueId);
                throw new StorageException("failed to check for account");
            }

            if (!result.getBoolean(1)) {
                close(statement);
                statement = prepare(connection, "account.create");
                statement.setLong(1, uniqueId.getMostSignificantBits());
                statement.setLong(2, uniqueId.getLeastSignificantBits());
                statement.setString(3, name);
                created = statement.executeUpdate();
                close(statement);

                if (created == 0) {
                    Economies.log(Level.SEVERE, "Failed to insert account for uuid %s", uniqueId);
                    throw new StorageException("failed to insert account");
                }

                Economies.debug("Created account for uuid %s", uniqueId);
                return true;
            }

            close(statement);
        } catch (SQLException ex) {
            Economies.warn("Could not create account for uuid %s: %s", uniqueId, ex.getMessage());
            throw new StorageException("could not create account", ex);
        }

        return false;
    }

    @Override
    public boolean deleteAccount(@NotNull final UUID uniqueId) throws StorageException {
        PreparedStatement statement;
        int result;

        try (Connection connection = fetch()) {
            statement = prepare(connection, "account.delete");
            statement.setLong(1, uniqueId.getMostSignificantBits());
            statement.setLong(2, uniqueId.getLeastSignificantBits());
            result = statement.executeUpdate();
            close(statement);

            if (result > 0) {
                Economies.debug("Deleted account with uuid %s", uniqueId);
                return true;
            }
        } catch (SQLException ex) {
            Economies.warn("Could not delete account with uuid %s: %s", uniqueId, ex.getMessage());
            throw new StorageException("could not delete account", ex);
        }

        return false;
    }
}
