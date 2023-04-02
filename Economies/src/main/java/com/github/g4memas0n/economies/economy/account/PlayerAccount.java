package com.github.g4memas0n.economies.economy.account;

import com.github.g4memas0n.economies.storage.AccountStorage;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class PlayerAccount extends BasicAccount {

    protected final OfflinePlayer player;

    public PlayerAccount(@NotNull final AccountStorage storage, @NotNull final OfflinePlayer player) {
        super(storage);

        this.player = player;
    }

    @Override
    public @NotNull Future<UUID> getUniqueId() {
        return CompletableFuture.completedFuture(this.player.getUniqueId());
    }

    @Override
    public @NotNull Future<String> getName() {
        final String name = this.player.getName();

        return name != null ? CompletableFuture.completedFuture(name) : super.getName();
    }

    @Override
    public @NotNull Future<Void> setName(@NotNull final String name) {
        throw new UnsupportedOperationException("not possible on player bank account");
    }

    @Override
    public @NotNull Future<Boolean> isCreditworthy() {
        boolean creditworthy;

        if (this.player.isOnline() && this.player.getPlayer() != null) {
            creditworthy = this.player.getPlayer().hasPermission("economies.creditworthy");
        } else {
            creditworthy = false;
        }

        return CompletableFuture.completedFuture(creditworthy);
    }
}
