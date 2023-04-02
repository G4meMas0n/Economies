package com.github.g4memas0n.economies.economy.account;

import com.github.g4memas0n.economies.Economies;
import com.github.g4memas0n.economies.economy.EconomyException;
import com.github.g4memas0n.economies.storage.AccountStorage;
import com.github.g4memas0n.economies.storage.StorageException;
import com.github.g4memas0n.economies.storage.StorageManager;
import com.google.common.base.Preconditions;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import java.lang.ref.SoftReference;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.logging.Level;

public class AccountManager implements AccountProvider {

    private final Map<UUID, SoftReference<Account>> cache;
    private final StorageManager storage;

    public AccountManager(@NotNull final StorageManager storage) {
        this.cache = new ConcurrentHashMap<>();
        this.storage = storage;
    }

    @Override
    public @NotNull Future<Account> getAccount(@NotNull final OfflinePlayer player) {
        Preconditions.checkArgument(player.getUniqueId().version() == 4, "illegal offline uuid");

        return CompletableFuture.supplyAsync(() -> {
            SoftReference<Account> reference = this.cache.computeIfPresent(player.getUniqueId(), (uuid, ref) -> {
                if (ref.get() == null) {
                    Economies.debug("Invalidated cached account for uuid: %s", uuid);
                    return null;
                }

                return ref;
            });

            Account account = reference != null ? reference.get() : null;

            if (account == null) {
                final Player online = player.getPlayer();
                AccountStorage storage;

                try {
                    storage = this.storage.getAccount(player.getUniqueId());

                    if (storage == null && online != null) {
                        // create account if player is currently online and retry query
                        this.storage.createAccount(player.getUniqueId(), online.getName());
                        storage = this.storage.getAccount(player.getUniqueId());
                    }
                } catch (StorageException ex) {
                    throw new EconomyException("", ex);
                }

                if (storage == null) {
                    throw new AccountNotFoundException(player);
                }

                account = new PlayerAccount(storage, player);
                reference = new SoftReference<>(account);

                // put account into cache
                this.cache.put(player.getUniqueId(), reference);
            }

            return account;
        });
    }

    @Override
    public @NotNull Future<Boolean> hasAccount(@NotNull final OfflinePlayer player) {
        Preconditions.checkArgument(player.getUniqueId().version() == 4, "illegal offline uuid");

        return CompletableFuture.supplyAsync(() -> {
            try {
                SoftReference<Account> reference = this.cache.get(player.getUniqueId());
                if (reference != null && reference.get() != null) {
                    return true;
                }

                return this.storage.hasAccount(player.getUniqueId());
            } catch (StorageException ex) {
                throw new EconomyException("", ex);
            }
        });
    }

    @Override
    public @NotNull Future<Boolean> createAccount(@NotNull final OfflinePlayer player) {
        Preconditions.checkArgument(player.getUniqueId().version() == 4, "illegal offline uuid");
        final String name = player.getName();

        if (name == null) {
            throw new IllegalArgumentException("player must have a username");
        }

        return CompletableFuture.supplyAsync(() -> {
            try {
                return this.storage.createAccount(player.getUniqueId(), name);
            } catch (StorageException ex) {
                throw new EconomyException("", ex);
            }
        });
    }

    @Override
    public @NotNull Future<Boolean> deleteAccount(@NotNull final OfflinePlayer player) {
        Preconditions.checkArgument(player.getUniqueId().version() == 4, "illegal offline uuid");

        return CompletableFuture.supplyAsync(() -> {
            try {
                SoftReference<Account> reference = this.cache.remove(player.getUniqueId());
                if (reference != null && reference.get() != null) {
                    Economies.debug("Invalidated cached account for uuid: %s", player.getUniqueId());
                }

                return this.storage.deleteAccount(player.getUniqueId());
            } catch (StorageException ex) {
                throw new EconomyException("", ex);
            }
        });
    }

    /*
     * cache validation and invalidation
     */

    public @NotNull Future<Void> validate(@NotNull final Player player) {
        return CompletableFuture.runAsync(() -> {
            this.cache.computeIfPresent(player.getUniqueId(), (uuid, reference) -> {
                Account value;

                if ((value = reference.get()) != null) {
                    if (value instanceof PlayerAccount account) {
                        // only update the reference if it points to an invalid player instance
                        if (account.player != player) {
                            value = new PlayerAccount(account.storage, player);
                            reference = new SoftReference<>(value);
                        }

                        Economies.debug("Validated cached account for uuid: %s", player.getUniqueId());
                        return reference;
                    } else {
                        Economies.log(Level.SEVERE, "Invalidated unknown/illegal cached account for uuid: %s", player.getUniqueId());
                    }
                }

                return null;
            });
        });
    }

    public @NotNull Future<Void> invalidate(@NotNull final Player player) {
        return CompletableFuture.runAsync(() -> {
            SoftReference<Account> reference = this.cache.remove(player.getUniqueId());
            Account value;

            if (reference != null && (value = reference.get()) != null) {
                if (!(value instanceof PlayerAccount)) {
                    Economies.log(Level.SEVERE, "Invalidated unknown/illegal cached account for uuid: %s", player.getUniqueId());
                    return;
                }

                Economies.debug("Invalidated cached account for uuid: %s", player.getUniqueId());
            }
        });
    }
}
