package com.github.g4memas0n.economies.economy.account;

import com.github.g4memas0n.economies.economy.Response;
import com.github.g4memas0n.economies.storage.AccountStorage;
import com.github.g4memas0n.economies.storage.StorageException;
import org.jetbrains.annotations.NotNull;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.function.Consumer;

public abstract class BasicAccount implements Account {

    protected final AccountStorage storage;

    BasicAccount(@NotNull final AccountStorage storage) {
        this.storage = storage;
    }

    @Override
    public @NotNull Future<Response<UUID>> getUniqueId() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return Response.of(this.storage.getUniqueId());
            } catch (StorageException ex) {
                return Response.of(ex);
            }
        });
    }

    @Override
    public @NotNull Future<Void> getUniqueId(@NotNull final Consumer<Response<UUID>> consumer) {
        return cast(getUniqueId()).thenAccept(consumer);
    }

    @Override
    public @NotNull Future<Response<String>> getName() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return Response.of(this.storage.getName());
            } catch (StorageException ex) {
                return Response.of(ex);
            }
        });
    }

    @Override
    public @NotNull Future<Void> getName(@NotNull final Consumer<Response<String>> consumer) {
        return cast(getName()).thenAccept(consumer);
    }

    @Override
    public @NotNull Future<Response<Boolean>> setName(@NotNull final String name) {
        //TODO Check for illegal names

        return CompletableFuture.supplyAsync(() -> {
            try {
                return Response.of(this.storage.setName(name));
            } catch (StorageException ex) {
                return Response.of(false, ex);
            }
        });
    }

    @Override
    public @NotNull Future<Void> setName(@NotNull final String name,
                                         @NotNull final Consumer<Response<Boolean>> consumer) {
        return cast(setName(name)).thenAccept(consumer);
    }

    @Override
    public @NotNull Future<Response<BigDecimal>> getBalance() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return Response.of(this.storage.getBalance());
            } catch (StorageException ex) {
                return Response.of(ex);
            }
        });
    }

    @Override
    public @NotNull Future<Void> getBalance(@NotNull final Consumer<Response<BigDecimal>> consumer) {
        return cast(getBalance()).thenAccept(consumer);
    }

    @Override
    public @NotNull Future<Response<Boolean>> hasBalance(@NotNull final BigDecimal amount) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                final BigDecimal balance = this.storage.getBalance();
                return Response.of(balance.compareTo(amount) >= 0);
            } catch (StorageException ex) {
                return Response.of(false, ex);
            }
        });
    }

    @Override
    public @NotNull Future<Void> hasBalance(@NotNull final BigDecimal amount,
                                            @NotNull final Consumer<Response<Boolean>> consumer) {
        return cast(hasBalance(amount)).thenAccept(consumer);
    }

    @Override
    public @NotNull Future<Void> depositBalance(@NotNull final BigDecimal amount,
                                                @NotNull final Consumer<Response<Boolean>> consumer) {
        return cast(depositBalance(amount)).thenAccept(consumer);
    }

    @Override
    public @NotNull Future<Void> transferBalance(@NotNull final Account account, @NotNull final BigDecimal amount,
                                                 @NotNull final Consumer<Response<Boolean>> consumer) {
        return cast(transferBalance(account, amount)).thenAccept(consumer);
    }

    @Override
    public @NotNull Future<Void> withdrawBalance(@NotNull final BigDecimal amount,
                                                 @NotNull final Consumer<Response<Boolean>> consumer) {
        return cast(withdrawBalance(amount)).thenAccept(consumer);
    }

    @Override
    public @NotNull Future<Void> isCreditworthy(@NotNull final Consumer<Response<Boolean>> consumer) {
        return cast(isCreditworthy()).thenAccept(consumer);
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
