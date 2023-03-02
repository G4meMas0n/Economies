package com.github.g4memas0n.economies.economy.account;

import com.github.g4memas0n.economies.economy.Response;
import com.github.g4memas0n.economies.storage.AccountStorage;
import org.jetbrains.annotations.NotNull;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class BankAccount extends BasicAccount {

    public BankAccount(@NotNull final AccountStorage storage) {
        super(storage);
    }

    @Override
    public @NotNull Future<Response<UUID>> getUniqueId() {
        return CompletableFuture.completedFuture(Response.of(UUID.randomUUID())); //TODO replace bank uuid
    }

    @Override
    public @NotNull Future<Response<String>> getName() {
        return CompletableFuture.completedFuture(Response.of("Bank")); //TODO replace bank name
    }

    @Override
    public @NotNull Future<Response<Boolean>> setName(@NotNull final String name) {
        throw new UnsupportedOperationException("not possible on global bank account");
    }

    @Override
    public @NotNull Future<Response<Boolean>> depositBalance(@NotNull final BigDecimal amount) {
        throw new UnsupportedOperationException("not possible on global bank account");
    }

    @Override
    public @NotNull Future<Response<Boolean>> transferBalance(@NotNull final Account account,
                                                              @NotNull final BigDecimal amount) {
        throw new UnsupportedOperationException("not possible on global bank account");
    }

    @Override
    public @NotNull Future<Response<Boolean>> withdrawBalance(@NotNull final BigDecimal amount) {
        throw new UnsupportedOperationException("not possible on global bank account");
    }

    @Override
    public @NotNull Future<Response<Boolean>> isCreditworthy() {
        return CompletableFuture.completedFuture(Response.of(true));
    }

    @Override
    public @NotNull Future<Response<Boolean>> isInfinite() {
        return CompletableFuture.completedFuture(Response.FALSE); //TODO implement infinite check
    }
}
