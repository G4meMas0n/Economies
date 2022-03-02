package com.github.g4memas0n.economies.economy.account;

import com.github.g4memas0n.economies.economy.storage.AccountStorage;
import org.jetbrains.annotations.NotNull;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class BankAccount extends Account {

    public static final UUID UNIQUE_ID = new UUID(0L, 0L);

    public BankAccount(@NotNull final AccountStorage storage) {
        super(storage);
    }

    @Override
    public @NotNull Future<UUID> getUniqueId() {
        return CompletableFuture.completedFuture(UNIQUE_ID);
    }

    @Override
    public @NotNull Future<String> getName() {
        return CompletableFuture.completedFuture("Bank");
    }

    @Override
    public @NotNull Future<Boolean> setName(@NotNull final String name) {
        return CompletableFuture.completedFuture(false);
    }

    @Override
    public @NotNull Future<Boolean> isInfinite() {
        return CompletableFuture.completedFuture(false);
    }

    @Override
    public @NotNull Future<Boolean> setInfinite(final boolean infinite) {
        return CompletableFuture.completedFuture(false);
    }
}
