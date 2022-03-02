package com.github.g4memas0n.economies.economy.storage;

import org.jetbrains.annotations.NotNull;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface AccountStorage {

    @NotNull CompletableFuture<UUID> getUniqueId();

    @NotNull CompletableFuture<Boolean> setUniqueId(@NotNull final UUID uniqueId);

    @NotNull CompletableFuture<String> getName();

    @NotNull CompletableFuture<Boolean> setName(@NotNull final String name);

    @NotNull CompletableFuture<BigDecimal> getBalance();

    @NotNull CompletableFuture<Boolean> setBalance(@NotNull final BigDecimal balance);

    @NotNull CompletableFuture<Boolean> addBalance(@NotNull final BigDecimal amount);

    @NotNull CompletableFuture<Boolean> subtractBalance(@NotNull final BigDecimal amount);

    @NotNull CompletableFuture<Boolean> getInfinite();

    @NotNull CompletableFuture<Boolean> setInfinite(final boolean infinite);
}
