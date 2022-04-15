package com.github.g4memas0n.economies.economy.account;

import com.github.g4memas0n.economies.storage.AccountStorage;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class PlayerAccount extends Account {

    private final OfflinePlayer base;

    public PlayerAccount(@NotNull final AccountProvider provider, @NotNull final AccountStorage storage,
                         @NotNull final OfflinePlayer player) {
        super(provider, storage);

        this.base = player;
    }

    @Override
    public @NotNull Future<UUID> getUniqueId() {
        return CompletableFuture.completedFuture(this.base.getUniqueId());
    }

    @Override
    public @NotNull Future<String> getName() {
        final String name = this.base.getName();

        if (name != null) {
            if (this.base.isOnline() && this.base.getPlayer() != null) {
                super.setName(this.base.getPlayer().getName());
            }

            return CompletableFuture.completedFuture(name);
        }

        return super.getName();
    }

    @Override
    public @NotNull Future<Boolean> setName(@NotNull final String name) {
        return CompletableFuture.completedFuture(false);
    }

    @Override
    public @NotNull Future<Boolean> isCreditworthy() {
        if (this.base.isOnline() && this.base.getPlayer() != null) {
            final boolean creditworthy = this.base.getPlayer().hasPermission("economies.creditworthy");

            super.setCreditworthy(creditworthy);
            return CompletableFuture.completedFuture(creditworthy);
        }

        return super.isCreditworthy();
    }

    @Override
    public @NotNull Future<Boolean> setCreditworthy(final boolean creditworthy) {
        return CompletableFuture.completedFuture(false);
    }
}
