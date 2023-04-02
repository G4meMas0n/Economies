package com.github.g4memas0n.economies.storage;

import org.jetbrains.annotations.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

public interface AccountStorage {

    @NotNull UUID getUniqueId() throws StorageException;

    void setUniqueId(@NotNull UUID uniqueId) throws StorageException;

    @NotNull String getName() throws StorageException;

    void setName(@NotNull String name) throws StorageException;

    @NotNull BigDecimal getBalance() throws StorageException;

    void setBalance(@NotNull BigDecimal balance) throws StorageException;

    void depositBalance(@NotNull BigDecimal amount, boolean negative) throws StorageException;

    void withdrawBalance(@NotNull BigDecimal amount, boolean negative) throws StorageException;

    void transferBalance(@NotNull AccountStorage account, @NotNull BigDecimal amount, boolean negative) throws StorageException;

}
