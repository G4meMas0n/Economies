package com.github.g4memas0n.economies.storage;

import com.github.g4memas0n.economies.util.Response;
import org.jetbrains.annotations.NotNull;
import java.util.UUID;

public interface StorageManager {

    @NotNull Response<AccountStorage> getAccount(@NotNull final UUID uniqueId);

    @NotNull Response<Boolean> createAccount(@NotNull final UUID uniqueId, @NotNull final String name);

    @NotNull Response<Boolean> deleteAccount(@NotNull final UUID uniqueId);

    @NotNull Response<Boolean> hasAccount(@NotNull final UUID uniqueId);

}
