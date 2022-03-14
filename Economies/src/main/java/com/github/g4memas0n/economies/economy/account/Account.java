package com.github.g4memas0n.economies.economy.account;

import com.github.g4memas0n.economies.storage.AccountStorage;
import org.jetbrains.annotations.NotNull;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.function.Consumer;

public abstract class Account {

    protected final AccountStorage storage;

    protected Account(@NotNull final AccountStorage storage) {
        this.storage = storage;
    }

    /**
     * Requests the unique-id of this {@code Account}.
     * The result of the {@link Future} may be null if it fails to retrieve the unique-id.
     * @return a {@link Future} containing the unique-id after the retrieval.
     */
    public @NotNull Future<UUID> getUniqueId() {
        final CompletableFuture<UUID> future = this.storage.getUniqueId();

        return future.exceptionally(ex -> {
            //TODO Log exception to console and inform player.
            return null;
        });
    }

    /**
     * Requests the unique-id of this {@code Account}.
     * The input for the given {@link Consumer} may be null if it fails to retrieve the unique-id.
     * @param consumer a {@link Consumer} accepting the retrieved unique-id.
     * @return a {@link Future} that completes when the retrieval is completed.
     */
    public @NotNull Future<Void> getUniqueId(@NotNull final Consumer<UUID> consumer) {
        return ((CompletableFuture<UUID>) this.getUniqueId()).thenAccept(consumer);
    }

    /**
     * Requests the name of this {@code Account}.
     * The result of the {@link Future} may be null if it fails to retrieve the name.
     * @return a {@link Future} containing the name after the retrieval.
     */
    public @NotNull Future<String> getName() {
        final CompletableFuture<String> future = this.storage.getName();

        return future.exceptionally(ex -> {
            //TODO Log exception to console and inform player.
            return null;
        });
    }

    /**
     * Requests the name of this {@code Account}.
     * The input for the given {@link Consumer} may be null if it fails to retrieve the name.
     * @param consumer a {@link Consumer} accepting the retrieved name.
     * @return a {@link Future} that completes when the retrieval is completed.
     */
    public @NotNull Future<Void> getName(@NotNull final Consumer<String> consumer) {
        return ((CompletableFuture<String>) this.getName()).thenAccept(consumer);
    }

    /**
     * Sets the name of this {@code Account}.
     * If this update was successful, then the result of the future will be {@code true},
     * otherwise it will be {@code false}.
     * @param name the new name that this {@code Account} should have.
     * @return a {@link Future} containing the success of this update.
     */
    public @NotNull Future<Boolean> setName(@NotNull final String name) {
        final CompletableFuture<Boolean> future = this.storage.setName(name);

        return future.exceptionally(ex -> {
            //TODO Log exception to console and inform player.
            return false;
        });
    }

    /**
     * Sets the name of this {@code Account}.
     * If this update was successful, then the input for the consumer will be {@code true},
     * otherwise it will be {@code false}.
     * @param name the new name that this {@code Account} should have.
     * @param consumer a {@link Consumer} accepting the success of this update.
     * @return a {@link Future} that completes when the update is completed.
     */
    public @NotNull Future<Void> setName(@NotNull final String name, @NotNull final Consumer<Boolean> consumer) {
        return ((CompletableFuture<Boolean>) this.setName(name)).thenAccept(consumer);
    }

    /**
     * Requests the current balance of this {@code Account}.
     * The result of the {@link Future} may be null if it fails to retrieve the balance.
     * @return a {@link Future} containing the balance after the retrieval.
     */
    public @NotNull Future<BigDecimal> getBalance() {
        final CompletableFuture<BigDecimal> future = this.storage.getBalance();

        return future.exceptionally(ex -> {
            //TODO Log exception to console and inform player.
            return null;
        });
    }

    /**
     * Requests the current balance of this {@code Account}.
     * The input for the given {@link Consumer} may be null if it fails to retrieve the balance.
     * @param consumer a {@link Consumer} accepting the retrieved balance.
     * @return a {@link Future} that completes when the retrieval is completed.
     */
    public @NotNull Future<Void> getBalance(@NotNull final Consumer<BigDecimal> consumer) {
        return ((CompletableFuture<BigDecimal>) this.getBalance()).thenAccept(consumer);
    }

    /**
     * Checks whether this {@code Account} has at least a balance of the given amount.
     * If the balance of this {@code Account} meets or exceeds the specified amount, then the result of the future
     * will be {@code true}, otherwise it will be {@code false}.
     * @param amount the amount that the balance must meet or exceed.
     * @return a {@link Future} containing the result of this check.
     */
    public @NotNull Future<Boolean> hasBalance(@NotNull final BigDecimal amount) {
        return this.storage.getBalance().thenApply(balance -> balance.compareTo(amount) >= 0);
    }

    /**
     * Checks whether this {@code Account} has at least a balance of the given amount.
     * If the balance of this {@code Account} meets or exceeds the specified amount, then the input for the consumer
     * will be {@code true}, otherwise it will be {@code false}.
     * @param amount the amount that the balance must meet or exceed.
     * @param consumer a {@link Consumer} accepting the result of this check.
     * @return a {@link Future} that completes when the check is completed.
     */
    public @NotNull Future<Void> hasBalance(@NotNull final BigDecimal amount, @NotNull final Consumer<Boolean> consumer) {
        return ((CompletableFuture<Boolean>) this.hasBalance(amount)).thenAccept(consumer);
    }

    /**
     * Deposits the specified amount onto this {@code Account}.
     * If the deposit was successful, then the result of the future will be {@code true},
     * otherwise it will be {@code false}.
     * @param amount the amount that should be deposited.
     * @return a {@link Future} containing the result of this deposit.
     */
    public @NotNull Future<Boolean> depositBalance(@NotNull final BigDecimal amount) {
        //TODO Implement deposit balance handling.
        return CompletableFuture.completedFuture(false);
    }

    /**
     * Deposits the specified amount onto this {@code Account}.
     * If the deposit was successful, then the input for the consumer will be {@code true},
     * otherwise it will be {@code false}.
     * @param amount the amount that should be deposited.
     * @param consumer a {@link Consumer} accepting the result of this deposit.
     * @return a {@link Future} that completes when the deposit is completed.
     */
    public @NotNull Future<Void> depositBalance(@NotNull final BigDecimal amount, @NotNull final Consumer<Boolean> consumer) {
        return ((CompletableFuture<Boolean>) this.depositBalance(amount)).thenAccept(consumer);
    }

    /**
     * Transfers the specified amount from this {@code Account} onto the specified {@code Account}.
     * If the transfer was successful, then the result of the future will be {@code true},
     * otherwise it will be {@code false}.
     * @param amount the amount that should be transferred.
     * @return a {@link Future} containing the result of this transfer.
     */
    public @NotNull Future<Boolean> transferBalance(@NotNull final Account account, @NotNull final BigDecimal amount) {
        return CompletableFuture.supplyAsync(() -> {
            //TODO Implement transfer balance handling.
            return false;
        });
    }

    /**
     * Transfers the specified amount from this {@code Account} onto the specified {@code Account}.
     * If the transfer was successful, then the input for the consumer will be {@code true},
     * otherwise it will be {@code false}.
     * @param amount the amount that should be transferred.
     * @param consumer a {@link Consumer} accepting the result of this transfer.
     * @return a {@link Future} that completes when the transfer is completed.
     */
    public @NotNull Future<Void> transferBalance(@NotNull final Account account, @NotNull final BigDecimal amount,
                                                 @NotNull final Consumer<Boolean> consumer) {
        return ((CompletableFuture<Boolean>) this.transferBalance(account, amount)).thenAccept(consumer);
    }

    /**
     * Withdraws the specified amount from this {@code Account}.
     * If the withdrawal was successful, then the result of the future will be {@code true},
     * otherwise it will be {@code false}.
     * @param amount the amount that should be withdrawn.
     * @return a {@link Future} containing the result of this withdrawal.
     */
    public @NotNull Future<Boolean> withdrawBalance(@NotNull final BigDecimal amount) {
        //TODO Implement withdraw balance handling.
        return CompletableFuture.completedFuture(false);
    }

    /**
     * Withdraws the specified amount from this {@code Account}.
     * If the withdrawal was successful, then the input for the consumer will be {@code true},
     * otherwise it will be {@code false}.
     * @param amount the amount that should be withdrawn.
     * @param consumer a {@link Consumer} accepting the result of this withdrawal.
     * @return a {@link Future} that completes when the withdrawal is completed.
     */
    public @NotNull Future<Void> withdrawBalance(@NotNull final BigDecimal amount, @NotNull final Consumer<Boolean> consumer) {
        return ((CompletableFuture<Boolean>) this.withdrawBalance(amount)).thenAccept(consumer);
    }

    /**
     * Checks whether the balance of this {@code Account} is infinite.
     * If the balance of this {@code Account} is infinite, then the result of the future
     * will be {@code true}, otherwise it will be {@code false}.
     * @return a {@link Future} containing the result of this check.
     */
    public @NotNull Future<Boolean> isInfinite() {
        final CompletableFuture<Boolean> future = this.storage.getInfinite();

        return future.exceptionally(ex -> {
            //TODO Log exception to console and inform player.
            return false;
        });
    }

    /**
     * Checks whether the balance of this {@code Account} is infinite.
     * If the balance of this {@code Account} is infinite, then the input for the consumer
     * will be {@code true}, otherwise it will be {@code false}.
     * @param consumer a {@link Consumer} accepting the result of this check.
     * @return a {@link Future} that completes when the check is completed.
     */
    public @NotNull Future<Void> isInfinite(@NotNull final Consumer<Boolean> consumer) {
        return ((CompletableFuture<Boolean>) this.isInfinite()).thenAccept(consumer);
    }

    /**
     * Updates whether the balance of this {@code Account} is infinite.
     * If the update was successful, then the result of the future will be {@code true},
     * otherwise it will be {@code false}.
     * @param infinite whether the balance should be infinite.
     * @return a {@link Future} containing the success of this update.
     */
    public @NotNull Future<Boolean> setInfinite(final boolean infinite) {
        return this.storage.setInfinite(infinite);
    }

    /**
     * Updates whether the balance of this {@code Account} is infinite.
     * If the update was successful, then the input for the consumer will be {@code true},
     * otherwise it will be {@code false}.
     * @param infinite whether the balance should be infinite.
     * @param consumer a {@link Consumer} accepting the success of this update.
     * @return a {@link Future} that completes when the check is completed.
     */
    public @NotNull Future<Void> setInfinite(final boolean infinite, @NotNull final Consumer<Boolean> consumer) {
        return this.storage.setInfinite(infinite).thenAccept(consumer);
    }
}
