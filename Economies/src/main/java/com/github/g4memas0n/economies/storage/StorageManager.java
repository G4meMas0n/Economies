package com.github.g4memas0n.economies.storage;

import org.jetbrains.annotations.NotNull;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface StorageManager {

    @NotNull CompletableFuture<Boolean> createAccount(@NotNull final UUID uniqueId, @NotNull final String name,
                                                      @NotNull final BigDecimal balance);

    @NotNull CompletableFuture<Boolean> deleteAccount(@NotNull final UUID uniqueId);

    @NotNull CompletableFuture<AccountStorage> getAccount(@NotNull final UUID uniqueId);

}
