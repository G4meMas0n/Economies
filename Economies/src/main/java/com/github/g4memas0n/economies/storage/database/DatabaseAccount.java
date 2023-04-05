package com.github.g4memas0n.economies.storage.database;

import com.github.g4memas0n.economies.Economies;
import com.github.g4memas0n.economies.storage.AccountStorage;
import com.github.g4memas0n.economies.storage.StorageException;
import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Level;

public class DatabaseAccount implements AccountStorage {

    private final DatabaseStorage database;
    private final int id;

    DatabaseAccount(@NotNull final DatabaseStorage database, final int id) {
        this.database = database;
        this.id = id;
    }

    @Override
    public @NotNull UUID getUniqueId() throws StorageException {
        PreparedStatement statement;
        ResultSet result;
        UUID uniqueId;

        try (Connection connection = this.database.fetch()) {
            statement = this.database.prepare(connection, "account.uuid.get");
            statement.setInt(1, this.id);
            result = statement.executeQuery();

            if (result.next()) {
                long most = result.getLong("uuid_most");
                long least = result.getLong("uuid_least");

                uniqueId = new UUID(most, least);
            } else {
                Economies.log(Level.SEVERE, "Failed to query uuid for non-existing account (%d)", this.id);
                throw new StorageException("failed to find account");
            }

            DatabaseStorage.close(statement);
        } catch (SQLException ex) {
            Economies.warn("Could not query uuid for account (%d): %s", this.id, ex.getMessage());
            throw new StorageException("could not query account uuid", ex);
        }

        return uniqueId;
    }

    @Override
    public void setUniqueId(@NotNull final UUID uniqueId) throws StorageException {
        PreparedStatement statement;
        int result;

        try (Connection connection = this.database.fetch()) {
            statement = this.database.prepare(connection, "account.uuid.set");
            statement.setLong(1, uniqueId.getMostSignificantBits());
            statement.setLong(2, uniqueId.getLeastSignificantBits());
            statement.setInt(3, this.id);
            result = statement.executeUpdate();
            DatabaseStorage.close(statement);

            if (result == 0) {
                Economies.log(Level.SEVERE, "Failed to update uuid for non-existing account (%d)", this.id);
                throw new StorageException("failed to find account");
            }
        } catch (SQLException ex) {
            Economies.warn("Could not update uuid for account (%d): %s", this.id, ex.getMessage());
            throw new StorageException("could not update account uuid", ex);
        }
    }

    @Override
    public @NotNull String getName() throws StorageException {
        PreparedStatement statement;
        ResultSet result;
        String name;

        try (Connection connection = this.database.fetch()) {
            statement = this.database.prepare(connection, "account.name.get");
            statement.setInt(1, this.id);
            result = statement.executeQuery();

            if (!result.next()) {
                Economies.log(Level.SEVERE, "Failed to query name for non-existing account (%d)", this.id);
                throw new StorageException("failed to find account");
            }

            name = result.getString("name");
            DatabaseStorage.close(statement);
        } catch (SQLException ex) {
            Economies.warn("Could not query name for account (%d): %s", this.id, ex.getMessage());
            throw new StorageException("could not query account name", ex);
        }

        return name;
    }

    @Override
    public void setName(@NotNull final String name) throws StorageException {
        PreparedStatement statement;
        int result;

        try (Connection connection = this.database.fetch()) {
            statement = this.database.prepare(connection, "account.name.set");
            statement.setString(1, name);
            statement.setInt(2, this.id);
            result = statement.executeUpdate();
            DatabaseStorage.close(statement);

            if (result == 0) {
                Economies.log(Level.SEVERE, "Failed to update name for non-existing account (%d)", this.id);
                throw new StorageException("failed to find account");
            }
        } catch (SQLException ex) {
            Economies.warn("Could not update name for account (%d): %s", this.id, ex.getMessage());
            throw new StorageException("could not update account name", ex);
        }
    }

    @Override
    public @NotNull BigDecimal getBalance() throws StorageException {
        try (Connection connection = this.database.fetch()) {
            return queryBalance(connection, this.id);
        } catch (SQLException ex) {
            Economies.warn("Could not get balance for account (%d): %s", this.id, ex.getMessage());
            throw new StorageException("could not get account balance", ex);
        }
    }

    @Override
    public void setBalance(@NotNull final BigDecimal balance) throws StorageException {
        Preconditions.checkState(this.id != 1, "not allowed on global account");
        Connection connection = null;
        BigDecimal delta;

        try {
            connection = this.database.fetch();
            connection.setAutoCommit(false);
            delta = queryBalance(connection, this.id).subtract(balance);

            if (!delta.equals(BigDecimal.ZERO)) {
                updateBalance(connection, balance, this.id);

                if (delta.compareTo(BigDecimal.ZERO) > 0) {
                    // old balance is higher than new balance: increment global balance
                    incrementBalance(connection, delta, 1);
                    logTransaction(connection, delta, this.id, 1);
                } else {
                    // old balance is lower than new balance: decrement global balance
                    delta = delta.negate();
                    decrementBalance(connection, delta, true, 1);
                    logTransaction(connection, delta, 1, this.id);
                }

                connection.commit();
            } else {
                connection.rollback();
            }

            DatabaseStorage.close(connection);
        } catch (StorageException | SQLException ex) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ignored) {

                }
                DatabaseStorage.close(connection);
            }

            Economies.warn("Could not set balance for account (%d): %s", this.id, ex.getMessage());
            throw new StorageException("could not set account balance", ex);
        }
    }

    @Override
    public void depositBalance(@NotNull final BigDecimal amount, final boolean negative) throws StorageException {
        Connection connection = null;

        try {
            connection = this.database.fetch();
            connection.setAutoCommit(false);

            queryBalance(connection, this.id); // ensures that the balance is initialized
            decrementBalance(connection, amount, negative, 1);
            incrementBalance(connection, amount, this.id);
            logTransaction(connection, amount, 1, this.id);

            connection.commit();
            DatabaseStorage.close(connection);
        } catch (StorageException | SQLException ex) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ignored) {

                }
                DatabaseStorage.close(connection);
            }

            Economies.warn("Could not deposit onto account (%d): %s", this.id, ex.getMessage());
            throw new StorageException("could not deposit onto account", ex);
        }
    }

    @Override
    public void withdrawBalance(@NotNull final BigDecimal amount, final boolean negative) throws StorageException {
        Connection connection = null;

        try {
            connection = this.database.fetch();
            connection.setAutoCommit(false);

            //queryBalance(connection, this.id); // ensures that the balance is initialized
            decrementBalance(connection, amount, negative, this.id);
            incrementBalance(connection, amount, 1);
            logTransaction(connection, amount, this.id, 1);

            connection.commit();
            DatabaseStorage.close(connection);
        } catch (StorageException | SQLException ex) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ignored) {

                }
                DatabaseStorage.close(connection);
            }

            Economies.warn("Could not withdraw from account (%d): %s", this.id, ex.getMessage());
            throw new StorageException("could not withdraw from account", ex);
        }
    }

    @Override
    public void transferBalance(@NotNull final AccountStorage account, @NotNull final BigDecimal amount, final boolean negative) throws StorageException {
        Preconditions.checkArgument(account instanceof DatabaseAccount, "unknown storage implementation");
        DatabaseAccount receiver = (DatabaseAccount) account;
        Connection connection = null;

        try {
            connection = this.database.fetch();
            connection.setAutoCommit(false);

            queryBalance(connection, receiver.id); // ensures that the receiver balance is initialized
            decrementBalance(connection, amount, negative, this.id);
            incrementBalance(connection, amount, receiver.id);
            logTransaction(connection, amount, this.id, receiver.id);

            connection.commit();
            DatabaseStorage.close(connection);
        } catch (StorageException | SQLException ex) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ignored) {

                }
                DatabaseStorage.close(connection);
            }

            Economies.warn("Could not transfer from account (%d) to account (%d): %s", this.id, receiver.id, ex.getMessage());
            throw new StorageException("could not transfer to account", ex);
        }
    }

    /*
     * helper methods:
     */

    private @NotNull BigDecimal queryBalance(@NotNull final Connection connection, final int id) throws StorageException {
        PreparedStatement statement;
        ResultSet result;
        BigDecimal balance;

        try {
            statement = this.database.prepare(connection, "account.balance.get");
            statement.setInt(1, id);
            result = statement.executeQuery();

            if (!result.next()) {
                DatabaseStorage.close(statement);
                Economies.log(Level.SEVERE, "Failed to query balance for non-existing account (%d)", id);
                throw new StorageException("failed to find account");
            }

            balance = result.getBigDecimal("balance");
            DatabaseStorage.close(statement);

            if (balance == null) {
                // balance hasn't initialized yet, initialize it now
                balance = initBalance(connection, id);
            }
        } catch (SQLException ex) {
            Economies.warn("Could not query balance for account (%d): %s", id, ex.getMessage());
            throw new StorageException("could not query account balance", ex);
        }

        return balance;
    }

    private @NotNull BigDecimal initBalance(@NotNull final Connection connection, final int id) throws StorageException {
        Preconditions.checkState(id != 1, "calling init balance on global account");
        final BigDecimal initial = this.database.plugin.getSettings().getInitialBalance();
        boolean autocommit;

        try {
            autocommit = connection.getAutoCommit();
            connection.setAutoCommit(autocommit);
            updateBalance(connection, initial, id);

            if (initial.compareTo(BigDecimal.ZERO) > 0) {
                decrementBalance(connection, initial, true, 1);
                logTransaction(connection, initial, 1, id);
            }

            connection.commit();
            connection.setAutoCommit(autocommit);
        } catch (SQLException ex) {
            try {
                connection.rollback();
            } catch (SQLException ignored) {

            }

            Economies.warn("Could not initialize balance for account (%d): %s", id, ex.getMessage());
            throw new StorageException("could not initialize account balance", ex);
        }

        return initial;
    }

    private void updateBalance(@NotNull final Connection connection, @NotNull final BigDecimal balance, final int id) throws StorageException {
        PreparedStatement statement;
        int result;

        try {
            statement = this.database.prepare(connection, "account.balance.set");
            statement.setBigDecimal(1, balance);
            statement.setInt(2, id);
            result = statement.executeUpdate();
            DatabaseStorage.close(statement);

            if (result == 0) {
                Economies.log(Level.SEVERE, "Failed to update balance for non-existing account (%d)", id);
                throw new StorageException("failed to find account");
            }
        } catch (SQLException ex) {
            Economies.warn("Could not update balance for account (%d): %s", id, ex.getMessage());
            throw new StorageException("could not update account balance", ex);
        }
    }

    private void decrementBalance(@NotNull final Connection connection, @NotNull final BigDecimal amount,
                                  final boolean negative, final int id) throws StorageException {
        if (!negative) {
            BigDecimal balance = queryBalance(connection, id).subtract(amount);

            if (balance.compareTo(BigDecimal.ZERO) < 0) {
                Economies.log(Level.SEVERE, "Failed to ensure allowed balance for account (%d)", id);
                throw new StorageException("failed to ensure allowed balance");
            }

            updateBalance(connection, balance, id);
            return;
        }

        PreparedStatement statement;
        int result;

        try {
            statement = this.database.prepare(connection, "account.balance.decrement");
            statement.setBigDecimal(1, amount);
            statement.setInt(2, id);
            result = statement.executeUpdate();
            DatabaseStorage.close(statement);

            if (result == 0) {
                Economies.log(Level.SEVERE, "Failed to decrement balance for non-existing account (%d)", id);
                throw new StorageException("failed to find account");
            }
        } catch (SQLException ex) {
            Economies.warn("Could not decrement balance for account (%d): %s", id, ex.getMessage());
            throw new StorageException("could not update account balance", ex);
        }
    }

    private void incrementBalance(@NotNull final Connection connection, @NotNull final BigDecimal amount, final int id) throws StorageException {
        PreparedStatement statement;
        int result;

        try {
            // Increment balance to account with the given id
            statement = this.database.prepare(connection, "account.balance.increment");
            statement.setBigDecimal(1, amount);
            statement.setInt(2, id);
            result = statement.executeUpdate();
            DatabaseStorage.close(statement);

            if (result == 0) {
                Economies.log(Level.SEVERE, "Failed to increment balance for non-existing account (%d)", id);
                throw new StorageException("failed to find account");
            }
        } catch (SQLException ex) {
            Economies.warn("Could not increment balance for account (%d): %s", id, ex.getMessage());
            throw new StorageException("could not update account balance", ex);
        }
    }


    private void logTransaction(@NotNull final Connection connection, @NotNull final BigDecimal amount,
                                final int sender, final int receiver) throws StorageException {
        //TODO Implement transaction insert
    }

}
