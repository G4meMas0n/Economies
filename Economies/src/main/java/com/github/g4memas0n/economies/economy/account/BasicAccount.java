package com.github.g4memas0n.economies.economy.account;

import com.github.g4memas0n.economies.util.Response;
import com.github.g4memas0n.economies.storage.AccountStorage;
import org.jetbrains.annotations.NotNull;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.function.Consumer;

public abstract class BasicAccount implements Account {

    private final AccountStorage storage;

    BasicAccount(@NotNull final AccountStorage storage) {
        this.storage = storage;
    }

    @Override
    public @NotNull Future<Response<UUID>> getUniqueId() {
        return CompletableFuture.supplyAsync(storage::getUniqueId);
    }

    @Override
    public @NotNull Future<Void> getUniqueId(@NotNull final Consumer<Response<UUID>> consumer) {
        return cast(getUniqueId()).thenAccept(consumer);
    }

    @Override
    public @NotNull Future<Response<String>> getName() {
        return CompletableFuture.supplyAsync(storage::getName);
    }

    @Override
    public @NotNull Future<Void> getName(@NotNull final Consumer<Response<String>> consumer) {
        return cast(getName()).thenAccept(consumer);
    }

    @Override
    public @NotNull Future<Response<Boolean>> setName(@NotNull final String name) {
        //TODO Check for illegal names
        return CompletableFuture.supplyAsync(() -> storage.setName(name));
    }

    @Override
    public @NotNull Future<Void> setName(@NotNull final String name,
                                         @NotNull final Consumer<Response<Boolean>> consumer) {
        return cast(setName(name)).thenAccept(consumer);
    }

    @Override
    public @NotNull Future<Response<BigDecimal>> getBalance() {
        return CompletableFuture.supplyAsync(storage::getBalance);
    }

    @Override
    public @NotNull Future<Void> getBalance(@NotNull final Consumer<Response<BigDecimal>> consumer) {
        return cast(getBalance()).thenAccept(consumer);
    }

    @Override
    public @NotNull Future<Response<Boolean>> hasBalance(@NotNull final BigDecimal amount) {
        return CompletableFuture.supplyAsync(() -> {
            final Response<BigDecimal> retrieval = storage.getBalance();

            if (retrieval.isSuccess()) {
                return Response.of(amount.compareTo(retrieval.getResult()) >= 0);
            }

            //TODO Replace exception
            return Response.of(new IllegalArgumentException("Not enough money"));
        });
    }

    @Override
    public @NotNull Future<Void> hasBalance(@NotNull final BigDecimal amount,
                                            @NotNull final Consumer<Response<Boolean>> consumer) {
        return cast(hasBalance(amount)).thenAccept(consumer);
    }

    @Override
    public @NotNull Future<Response<Boolean>> depositBalance(@NotNull final BigDecimal amount) {
        //TODO Maybe outsource to storage implementation
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return Response.future(new IllegalArgumentException("negative amount"));
        }

        return Response.future(false); //TODO Implement deposit
    }

    @Override
    public @NotNull Future<Void> depositBalance(@NotNull final BigDecimal amount,
                                                @NotNull final Consumer<Response<Boolean>> consumer) {
        return cast(depositBalance(amount)).thenAccept(consumer);
    }

    @Override
    public @NotNull Future<Response<Boolean>> transferBalance(@NotNull final Account account,
                                                              @NotNull final BigDecimal amount) {
        if (account instanceof BasicAccount receiver) {
            //TODO Maybe outsource to storage implementation
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                return Response.future(new IllegalArgumentException("negative amount"));
            }

            return Response.future(false); //TODO Implement transfer
        }

        return Response.future(new IllegalArgumentException("custom account implementation"));
    }

    @Override
    public @NotNull Future<Void> transferBalance(@NotNull final Account account, @NotNull final BigDecimal amount,
                                                 @NotNull final Consumer<Response<Boolean>> consumer) {
        return cast(transferBalance(account, amount)).thenAccept(consumer);
    }

    @Override
    public @NotNull Future<Response<Boolean>> withdrawBalance(@NotNull final BigDecimal amount) {
        //TODO Maybe outsource to storage implementation
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return Response.future(new IllegalArgumentException("negative amount"));
        }

        return Response.future(false); //TODO Implement withdraw
    }

    @Override
    public @NotNull Future<Void> withdrawBalance(@NotNull final BigDecimal amount,
                                                 @NotNull final Consumer<Response<Boolean>> consumer) {
        return cast(withdrawBalance(amount)).thenAccept(consumer);
    }

    @Override
    public @NotNull Future<Response<Boolean>> isCreditworthy() {
        return CompletableFuture.supplyAsync(storage::getCreditworthy);
    }

    @Override
    public @NotNull Future<Void> isCreditworthy(@NotNull final Consumer<Response<Boolean>> consumer) {
        return cast(isCreditworthy()).thenAccept(consumer);
    }

    @Override
    public @NotNull Future<Response<Boolean>> setCreditworthy(final boolean creditworthy) {
        return CompletableFuture.supplyAsync(() -> storage.setCreditworthy(creditworthy));
    }

    @Override
    public @NotNull Future<Void> setCreditworthy(final boolean creditworthy,
                                                 @NotNull final Consumer<Response<Boolean>> consumer) {
        return cast(setCreditworthy(creditworthy)).thenAccept(consumer);
    }

    @Override
    public @NotNull Future<Response<Boolean>> isInfinite() {
        return Response.future(false);
    }

    @Override
    public @NotNull Future<Void> isInfinite(@NotNull final Consumer<Response<Boolean>> consumer) {
        return cast(isInfinite()).thenAccept(consumer);
    }

    //TODO Find other solution
    private static <T> @NotNull CompletableFuture<Response<T>> cast(@NotNull final Future<Response<T>> future) {
        return (CompletableFuture<Response<T>>) future;
    }
}
