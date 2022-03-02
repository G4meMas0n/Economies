package com.github.g4memas0n.economies.economy.account;

import com.github.g4memas0n.economies.economy.storage.AccountStorage;
import org.jetbrains.annotations.NotNull;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.function.Consumer;

public abstract class Account {

    private final AccountStorage storage;

    protected Account(@NotNull final AccountStorage storage) {
        this.storage = storage;
    }

    protected @NotNull AccountStorage getStorage() {
        return this.storage;
    }

    public @NotNull Future<UUID> getUniqueId() {
        final CompletableFuture<UUID> future = this.storage.getUniqueId();

        return future.exceptionally(ex -> {
            //TODO Log exception to console and inform player.
            return UUID.randomUUID();
        });
    }

    public @NotNull Future<Void> getUniqueId(@NotNull final Consumer<UUID> consumer) {
        return ((CompletableFuture<UUID>) this.getUniqueId()).thenAccept(consumer);
    }

    public @NotNull Future<String> getName() {
        final CompletableFuture<String> future = this.storage.getName();

        return future.exceptionally(ex -> {
            //TODO Log exception to console and inform player.
            return "";
        });
    }

    public @NotNull Future<Void> getName(@NotNull final Consumer<String> consumer) {
        return ((CompletableFuture<String>) this.getName()).thenAccept(consumer);
    }

    public @NotNull Future<Boolean> setName(@NotNull final String name) {
        final CompletableFuture<Boolean> future = this.storage.setName(name);

        return future.exceptionally(ex -> {
            //TODO Log exception to console and inform player.
            return false;
        });
    }

    public @NotNull Future<Void> setName(@NotNull final String name, @NotNull final Consumer<Boolean> consumer) {
        return ((CompletableFuture<Boolean>) this.setName(name)).thenAccept(consumer);
    }

    public @NotNull Future<BigDecimal> getBalance() {
        final CompletableFuture<BigDecimal> future = this.storage.getBalance();

        return future.exceptionally(ex -> {
            //TODO Log exception to console and inform player.
            return BigDecimal.ZERO;
        });
    }

    public @NotNull Future<Void> getBalance(@NotNull final Consumer<BigDecimal> consumer) {
        return ((CompletableFuture<BigDecimal>) this.getBalance()).thenAccept(consumer);
    }

    public @NotNull Future<Boolean> hasBalance(@NotNull final BigDecimal amount) {
        //TODO Implement has balance handling.
        return CompletableFuture.completedFuture(false);
    }

    public @NotNull Future<Void> hasBalance(@NotNull final BigDecimal amount, @NotNull final Consumer<Boolean> consumer) {
        return ((CompletableFuture<Boolean>) this.hasBalance(amount)).thenAccept(consumer);
    }

    public @NotNull Future<Boolean> depositBalance(@NotNull final BigDecimal amount) {
        //TODO Implement deposit balance handling.
        return CompletableFuture.completedFuture(false);
    }

    public @NotNull Future<Void> depositBalance(@NotNull final BigDecimal amount, @NotNull final Consumer<Boolean> consumer) {
        return ((CompletableFuture<Boolean>) this.depositBalance(amount)).thenAccept(consumer);
    }

    public @NotNull Future<Boolean> transferBalance(@NotNull final Account account, @NotNull final BigDecimal amount) {
        return CompletableFuture.supplyAsync(() -> {
            //TODO Implement transfer balance handling.
            return false;
        });
    }

    public @NotNull Future<Void> transferBalance(@NotNull final Account account, @NotNull final BigDecimal amount,
                                                 @NotNull final Consumer<Boolean> consumer) {
        return ((CompletableFuture<Boolean>) this.transferBalance(account, amount)).thenAccept(consumer);
    }

    public @NotNull Future<Boolean> withdrawBalance(@NotNull final BigDecimal amount) {
        //TODO Implement withdraw balance handling.
        return CompletableFuture.completedFuture(false);
    }

    public @NotNull Future<Void> withdrawBalance(@NotNull final BigDecimal amount, @NotNull final Consumer<Boolean> consumer) {
        return ((CompletableFuture<Boolean>) this.withdrawBalance(amount)).thenAccept(consumer);
    }

    public @NotNull Future<Boolean> isInfinite() {
        final CompletableFuture<Boolean> future = this.storage.getInfinite();

        return future.exceptionally(ex -> {
            //TODO Log exception to console and inform player.
            return false;
        });
    }

    public @NotNull Future<Void> isInfinite(@NotNull final Consumer<Boolean> consumer) {
        return ((CompletableFuture<Boolean>) this.isInfinite()).thenAccept(consumer);
    }

    public @NotNull Future<Boolean> setInfinite(final boolean infinite) {
        return this.storage.setInfinite(infinite);
    }

    public @NotNull Future<Void> setInfinite(final boolean infinite, @NotNull final Consumer<Boolean> consumer) {
        return this.storage.setInfinite(infinite).thenAccept(consumer);
    }
}
