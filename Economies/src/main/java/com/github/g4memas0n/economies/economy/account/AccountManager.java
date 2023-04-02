package com.github.g4memas0n.economies.economy.account;

import com.github.g4memas0n.economies.Economies;
import com.github.g4memas0n.economies.economy.Response;
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
import java.util.function.Consumer;
import java.util.logging.Level;

public class AccountManager implements AccountProvider {

    private final Map<UUID, SoftReference<Account>> cache;
    private final StorageManager storage;

    public AccountManager(@NotNull final StorageManager storage) {
        this.cache = new ConcurrentHashMap<>();
        this.storage = storage;
    }

    @Override
    public @NotNull Future<Response<Account>> getAccount(@NotNull final OfflinePlayer player) {
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
                    return Response.of(ex);
                }

                if (storage == null) {
                    return Response.of(new AccountNotFoundException(player));
                }

                account = new PlayerAccount(storage, player);
                reference = new SoftReference<>(account);

                // put account into cache
                this.cache.put(player.getUniqueId(), reference);
            }

            return Response.of(account);
        });
    }

    @Override
    public @NotNull Future<Void> getAccount(@NotNull final OfflinePlayer player,
                                            @NotNull final Consumer<Response<Account>> consumer) {
        return cast(getAccount(player)).thenAccept(consumer);
    }

    @Override
    public @NotNull Future<Response<Boolean>> hasAccount(@NotNull final OfflinePlayer player) {
        Preconditions.checkArgument(player.getUniqueId().version() == 4, "illegal offline uuid");

        return CompletableFuture.supplyAsync(() -> {
            try {
                SoftReference<Account> reference = this.cache.get(player.getUniqueId());
                if (reference != null && reference.get() != null) {
                    return Response.TRUE;
                }

                return Response.of(this.storage.hasAccount(player.getUniqueId()));
            } catch (StorageException ex) {
                return Response.of(false, ex);
            }
        });
    }

    @Override
    public @NotNull Future<Void> hasAccount(@NotNull final OfflinePlayer player,
                                            @NotNull final Consumer<Response<Boolean>> consumer) {
        return cast(hasAccount(player)).thenAccept(consumer);
    }

    @Override
    public @NotNull Future<Response<Boolean>> createAccount(@NotNull final OfflinePlayer player) {
        Preconditions.checkArgument(player.getUniqueId().version() == 4, "illegal offline uuid");
        final String name = player.getName();

        if (name == null) {
            throw new IllegalArgumentException("player must have a username");
        }

        return CompletableFuture.supplyAsync(() -> {
            try {
                return Response.of(this.storage.createAccount(player.getUniqueId(), name));
            } catch (StorageException ex) {
                return Response.of(false, ex);
            }
        });
    }

    @Override
    public @NotNull Future<Void> createAccount(@NotNull final OfflinePlayer player,
                                               @NotNull final Consumer<Response<Boolean>> consumer) {
        return cast(createAccount(player)).thenAccept(consumer);
    }

    @Override
    public @NotNull Future<Response<Boolean>> deleteAccount(@NotNull final OfflinePlayer player) {
        Preconditions.checkArgument(player.getUniqueId().version() == 4, "illegal offline uuid");

        return CompletableFuture.supplyAsync(() -> {
            try {
                SoftReference<Account> reference = this.cache.remove(player.getUniqueId());
                if (reference != null && reference.get() != null) {
                    Economies.debug("Invalidated cached account for uuid: %s", player.getUniqueId());
                }

                return Response.of(this.storage.deleteAccount(player.getUniqueId()));
            } catch (StorageException ex) {
                return Response.of(false, ex);
            }
        });
    }

    @Override
    public @NotNull Future<Void> deleteAccount(@NotNull final OfflinePlayer player,
                                               @NotNull final Consumer<Response<Boolean>> consumer) {
        return cast(deleteAccount(player)).thenAccept(consumer);
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

    /*
     *
     */

    //TODO Find other solution
    private static <T> @NotNull CompletableFuture<Response<T>> cast(@NotNull final Future<Response<T>> future) {
        return (CompletableFuture<Response<T>>) future;
    }
}
