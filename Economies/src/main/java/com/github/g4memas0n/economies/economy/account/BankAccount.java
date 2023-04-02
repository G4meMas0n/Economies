package com.github.g4memas0n.economies.economy.account;

import com.github.g4memas0n.economies.Economies;
import com.github.g4memas0n.economies.storage.AccountStorage;
import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;
import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class BankAccount extends BasicAccount {

    protected static BankAccount global;

    private final Economies plugin;

    protected BankAccount(@NotNull final AccountStorage storage, @NotNull final Economies plugin) {
        super(storage);

        this.plugin = plugin;
    }

    @Override
    public @NotNull Future<Void> setBalance(@NotNull final BigDecimal amount) {
        throw new UnsupportedOperationException("not possible on global bank account");
    }

    @Override
    public @NotNull Future<Void> depositBalance(@NotNull final BigDecimal amount) {
        throw new UnsupportedOperationException("not possible on global bank account");
    }

    @Override
    public @NotNull Future<Void> withdrawBalance(@NotNull final BigDecimal amount) {
        throw new UnsupportedOperationException("not possible on global bank account");
    }

    @Override
    public @NotNull Future<Void> transferBalance(@NotNull final Account account, @NotNull final BigDecimal amount) {
        throw new UnsupportedOperationException("not possible on global bank account");
    }

    @Override
    public @NotNull Future<Boolean> isCreditworthy() {
        return CompletableFuture.completedFuture(this.plugin.getSettings().isBankInfinite());
    }

    public static @NotNull BankAccount get() {
        Preconditions.checkState(global != null, "bank account not initialized");
        return global;
    }
}
