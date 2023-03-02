package com.github.g4memas0n.economies.storage;

import org.jetbrains.annotations.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

public interface AccountStorage {

    @NotNull UUID getUniqueId() throws StorageException;

    @NotNull String getName() throws StorageException;

    boolean setName(@NotNull final String name) throws StorageException;

    @NotNull BigDecimal getBalance() throws StorageException;

    boolean setBalance(@NotNull final BigDecimal balance) throws StorageException;

    boolean setBalance(@NotNull final BigDecimal balance, @NotNull final Transaction transaction)  throws StorageException;

    boolean addBalance(@NotNull final BigDecimal amount) throws StorageException;

    boolean addBalance(@NotNull final BigDecimal amount, @NotNull final Transaction transaction) throws StorageException;

    boolean removeBalance(@NotNull final BigDecimal amount) throws StorageException;

    boolean removeBalance(@NotNull final BigDecimal amount, @NotNull final Transaction transaction) throws StorageException;

}
