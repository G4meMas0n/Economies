package com.github.g4memas0n.economies.economy.account;

import com.github.g4memas0n.economies.Economies;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Consumer;

public class AccountProvider {

    private final Map<UUID, Account> cache;
    private final Economies instance;

    private BankAccount bank; //TODO Get bank account.

    public AccountProvider(@NotNull final Economies instance) {
        this.cache = new ConcurrentHashMap<>();
        this.instance = instance;
    }

    public @NotNull Future<Boolean> createAccount(@NotNull final OfflinePlayer player) {
        if (this.cache.containsKey(player.getUniqueId()) || player.getName() == null) {
            return CompletableFuture.completedFuture(false);
        }

        final BigDecimal initial = this.instance.getSettings().getInitialBalance();

        if (!this.instance.getSettings().isBankInfinite()) {
            return this.instance.getStorage().createAccount(player.getUniqueId(), player.getName(), BigDecimal.ZERO).thenApply(success -> {
                if (success) {
                    try {
                        if (!this.bank.transferBalance(this.getAccount(player).get(), initial).get()) {
                            //TODO Log failed initial balance transfer.
                        }
                    } catch (ExecutionException | InterruptedException ex) {
                        //TODO Log unexpected error during initial balance transfer.
                    }
                }

                return success;
            });
        }

        return this.instance.getStorage().createAccount(player.getUniqueId(), player.getName(), initial);
    }

    public @NotNull Future<Void> createAccount(@NotNull final OfflinePlayer player, @NotNull final Consumer<Boolean> consumer) {
        return ((CompletableFuture<Boolean>) this.createAccount(player)).thenAccept(consumer);
    }

    public @NotNull Future<Account> getAccount(@NotNull final OfflinePlayer player) {
        final Account cached = this.cache.get(player.getUniqueId());

        if (cached == null) {
            return this.instance.getStorage().getAccount(player.getUniqueId()).thenApply(storage -> {
                Account account = this.cache.get(player.getUniqueId());

                if (account == null && storage != null) {
                    account = new PlayerAccount(storage, player);

                    this.cache.put(player.getUniqueId(), account);
                }

                return account;
            });
        }

        return CompletableFuture.completedFuture(cached);
    }

    public @NotNull Future<Void> getAccount(@NotNull final OfflinePlayer player, @NotNull final Consumer<Account> consumer) {
        return ((CompletableFuture<Account>) this.getAccount(player)).thenAccept(consumer);
    }
}
