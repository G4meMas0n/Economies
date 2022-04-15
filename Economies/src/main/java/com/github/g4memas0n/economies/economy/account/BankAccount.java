package com.github.g4memas0n.economies.economy.account;

import com.github.g4memas0n.economies.Economies;
import com.github.g4memas0n.economies.storage.AccountStorage;
import org.jetbrains.annotations.NotNull;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class BankAccount extends Account {

    public static final UUID UNIQUE_ID = new UUID(0L, 0L);

    private final Economies instance;

    public BankAccount(@NotNull final AccountProvider provider, @NotNull final AccountStorage storage,
                       @NotNull final Economies instance) {
        super(provider, storage);

        this.instance = instance;
    }

    @Override
    public @NotNull Future<UUID> getUniqueId() {
        return CompletableFuture.completedFuture(UNIQUE_ID);
    }

    @Override
    public @NotNull Future<String> getName() {
        return CompletableFuture.completedFuture(this.instance.getSettings().getBankName());
    }

    @Override
    public @NotNull Future<Boolean> setName(@NotNull final String name) {
        return CompletableFuture.completedFuture(false);
    }

    public @NotNull Future<Boolean> isCreditworthy() {
        return CompletableFuture.completedFuture(true);
    }

    public @NotNull Future<Boolean> setCreditworthy(final boolean creditworthy) {
        return CompletableFuture.completedFuture(false);
    }

    @Override
    public @NotNull Future<Boolean> isInfinite() {
        return CompletableFuture.completedFuture(this.instance.getSettings().isBankInfinite());
    }
}
