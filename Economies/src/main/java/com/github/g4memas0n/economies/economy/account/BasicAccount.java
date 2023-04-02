package com.github.g4memas0n.economies.economy.account;

import com.github.g4memas0n.economies.economy.EconomyException;
import com.github.g4memas0n.economies.economy.NotEnoughMoneyException;
import com.github.g4memas0n.economies.economy.Response;
import com.github.g4memas0n.economies.storage.AccountStorage;
import com.github.g4memas0n.economies.storage.StorageException;
import com.google.common.base.Preconditions;
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
                this.storage.setName(name);

                return Response.of(true);
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
                return Response.of(this.storage.getBalance().compareTo(amount) >= 0);
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
    public @NotNull Future<Response<Boolean>> setBalance(@NotNull final BigDecimal balance) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                this.storage.setBalance(balance);
                return Response.of(true);
            } catch (StorageException ex) {
                return Response.of(false, ex);
            }
        });
    }

    @Override
    public @NotNull Future<Void> setBalance(@NotNull final BigDecimal balance,
                                            @NotNull final Consumer<Response<Boolean>> consumer) {
        return cast(setBalance(balance)).thenAccept(consumer);
    }

    @Override
    public @NotNull Future<Response<Boolean>> depositBalance(@NotNull final BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("amount must be greater than zero");
        }

        return cast(BankAccount.get().isCreditworthy()).thenApplyAsync(creditworthy -> {
            try {
                // check if the bank can overdraft its balance
                if (creditworthy.isFailure() || !creditworthy.getResult()) {
                    BigDecimal balance = BankAccount.get().storage.getBalance().subtract(amount);

                    if (balance.compareTo(BigDecimal.ZERO) < 0) {
                        // throw exception as the bank would overdraft its balance
                        throw new NotEnoughMoneyException(balance.negate());
                    }
                }

                this.storage.depositBalance(amount, creditworthy.getResult());
                return Response.of(true);
            } catch (EconomyException | StorageException ex) {
                return Response.of(false, ex);
            }
        });
    }

    @Override
    public @NotNull Future<Void> depositBalance(@NotNull final BigDecimal amount,
                                                @NotNull final Consumer<Response<Boolean>> consumer) {
        return cast(depositBalance(amount)).thenAccept(consumer);
    }

    @Override
    public @NotNull Future<Response<Boolean>> withdrawBalance(@NotNull final BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("amount must be greater than zero");
        }

        return cast(BankAccount.get().isCreditworthy()).thenApplyAsync(creditworthy -> {
            try {
                // check if the account can overdraft its balance
                if (creditworthy.isFailure() || !creditworthy.getResult()) {
                    BigDecimal balance = this.storage.getBalance().subtract(amount);

                    if (balance.compareTo(BigDecimal.ZERO) < 0) {
                        // throw exception as the new balance is negative
                        throw new NotEnoughMoneyException(balance.negate());
                    }
                }

                this.storage.withdrawBalance(amount, creditworthy.getResult());
                return Response.of(true);
            } catch (EconomyException | StorageException ex) {
                return Response.of(false, ex);
            }
        });
    }

    @Override
    public @NotNull Future<Void> withdrawBalance(@NotNull final BigDecimal amount,
                                                 @NotNull final Consumer<Response<Boolean>> consumer) {
        return cast(withdrawBalance(amount)).thenAccept(consumer);
    }

    @Override
    public @NotNull Future<Response<Boolean>> transferBalance(@NotNull final Account account, @NotNull final BigDecimal amount) {
        Preconditions.checkArgument(account instanceof BasicAccount, "unknown account implementation");
        Preconditions.checkState(account != this, "account must be another account as itself");

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("amount must be greater than zero");
        }

        return cast(isCreditworthy()).thenApplyAsync(creditworthy -> {
            try {
                // check if the account can overdraft its balance
                if (creditworthy.isFailure() || !creditworthy.getResult()) {
                    BigDecimal balance = this.storage.getBalance().subtract(amount);

                    if (balance.compareTo(BigDecimal.ZERO) < 0) {
                        // throw exception as the new balance is negative
                        throw new NotEnoughMoneyException(balance.negate());
                    }
                }

                this.storage.transferBalance(((BasicAccount) account).storage, amount, creditworthy.getResult());
                return Response.of(true);
            } catch (EconomyException | StorageException ex) {
                return Response.of(false, ex);
            }
        });
    }

    @Override
    public @NotNull Future<Void> transferBalance(@NotNull final Account account, @NotNull final BigDecimal amount,
                                                 @NotNull final Consumer<Response<Boolean>> consumer) {
        return cast(transferBalance(account, amount)).thenAccept(consumer);
    }

    @Override
    public @NotNull Future<Void> isCreditworthy(@NotNull final Consumer<Response<Boolean>> consumer) {
        return cast(isCreditworthy()).thenAccept(consumer);
    }

    //TODO Find other solution
    private static <T> @NotNull CompletableFuture<Response<T>> cast(@NotNull final Future<Response<T>> future) {
        return (CompletableFuture<Response<T>>) future;
    }
}
