package com.github.g4memas0n.economies.economy.account;

import com.github.g4memas0n.economies.economy.EconomyException;
import com.github.g4memas0n.economies.economy.NotEnoughMoneyException;
import com.github.g4memas0n.economies.storage.AccountStorage;
import com.github.g4memas0n.economies.storage.StorageException;
import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public abstract class BasicAccount implements Account {

    protected final AccountStorage storage;

    BasicAccount(@NotNull final AccountStorage storage) {
        this.storage = storage;
    }

    @Override
    public @NotNull Future<UUID> getUniqueId() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return this.storage.getUniqueId();
            } catch (StorageException ex) {
                throw new EconomyException("", ex);
            }
        });
    }

    @Override
    public @NotNull Future<String> getName() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return this.storage.getName();
            } catch (StorageException ex) {
                throw new EconomyException("", ex);
            }
        });
    }

    @Override
    public @NotNull Future<Void> setName(@NotNull final String name) {
        Preconditions.checkArgument(name.length() <= 16, "name is too long");

        return CompletableFuture.runAsync(() -> {
            try {
                this.storage.setName(name);
            } catch (StorageException ex) {
                throw new EconomyException("", ex);
            }
        });
    }

    @Override
    public @NotNull Future<BigDecimal> getBalance() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return this.storage.getBalance();
            } catch (StorageException ex) {
                throw new EconomyException("", ex);
            }
        });
    }

    @Override
    public @NotNull Future<Boolean> hasBalance(@NotNull final BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("amount must be greater than zero");
        }

        return CompletableFuture.supplyAsync(() -> {
            try {
                return this.storage.getBalance().compareTo(amount) >= 0;
            } catch (StorageException ex) {
                throw new EconomyException("", ex);
            }
        });
    }

    @Override
    public @NotNull Future<Void> setBalance(@NotNull final BigDecimal balance) {
        return CompletableFuture.runAsync(() -> {
            try {
                this.storage.setBalance(balance);
            } catch (StorageException ex) {
                throw new EconomyException("", ex);
            }
        });
    }

    @Override
    public @NotNull Future<Void> depositBalance(@NotNull final BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("amount must be greater than zero");
        }

        return ((CompletableFuture<Boolean>) BankAccount.get().isCreditworthy()).thenAcceptAsync(creditworthy -> {
            try {
                if (!creditworthy) {
                    BigDecimal balance = BankAccount.get().storage.getBalance().subtract(amount);

                    if (balance.compareTo(BigDecimal.ZERO) < 0) {
                        // throw exception as the new balance would be negative
                        throw new NotEnoughMoneyException(balance.negate());
                    }
                }

                this.storage.depositBalance(amount, creditworthy);
            } catch (StorageException ex) {
                throw new EconomyException("", ex);
            }
        });
    }

    @Override
    public @NotNull Future<Void> withdrawBalance(@NotNull final BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("amount must be greater than zero");
        }

        return ((CompletableFuture<Boolean>) isCreditworthy()).thenAcceptAsync(creditworthy -> {
            try {
                if (!creditworthy) {
                    BigDecimal balance = this.storage.getBalance().subtract(amount);

                    if (balance.compareTo(BigDecimal.ZERO) < 0) {
                        // throw exception as the new balance would be negative
                        throw new NotEnoughMoneyException(balance.negate());
                    }
                }

                this.storage.withdrawBalance(amount, creditworthy);
            } catch (StorageException ex) {
                throw new EconomyException("", ex);
            }
        });
    }

    @Override
    public @NotNull Future<Void> transferBalance(@NotNull final Account account, @NotNull final BigDecimal amount) {
        Preconditions.checkState(account instanceof BasicAccount, "unknown account implementation");
        Preconditions.checkArgument(account != this, "account can not be equal to itself");

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("amount must be greater than zero");
        }

        return ((CompletableFuture<Boolean>) isCreditworthy()).thenAcceptAsync(creditworthy -> {
            try {
                if (!creditworthy) {
                    BigDecimal balance = this.storage.getBalance().subtract(amount);

                    if (balance.compareTo(BigDecimal.ZERO) < 0) {
                        // throw exception as the new balance would be negative
                        throw new NotEnoughMoneyException(balance.negate());
                    }
                }

                this.storage.transferBalance(((BasicAccount) account).storage, amount, creditworthy);
            } catch (StorageException ex) {
                throw new EconomyException("", ex);
            }
        });
    }
}
