package com.github.g4memas0n.economies.storage;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.UUID;

public interface StorageManager {

    @Nullable AccountStorage getAccount(@NotNull UUID uniqueId) throws StorageException;

    boolean hasAccount(@NotNull UUID uniqueId) throws StorageException;

    boolean createAccount(@NotNull UUID uniqueId, @NotNull String name) throws StorageException;

    boolean deleteAccount(@NotNull UUID uniqueId) throws StorageException;

}
