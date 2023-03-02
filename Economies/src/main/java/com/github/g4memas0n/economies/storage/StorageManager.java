package com.github.g4memas0n.economies.storage;

import org.jetbrains.annotations.NotNull;
import java.util.UUID;

public interface StorageManager {

    @NotNull AccountStorage getAccount(@NotNull final UUID uniqueId) throws StorageException;

    boolean createAccount(@NotNull final UUID uniqueId, @NotNull final String name) throws StorageException;

    boolean deleteAccount(@NotNull final UUID uniqueId) throws StorageException;

    boolean hasAccount(@NotNull final UUID uniqueId)  throws StorageException;

}
