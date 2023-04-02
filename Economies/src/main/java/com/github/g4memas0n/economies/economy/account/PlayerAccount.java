package com.github.g4memas0n.economies.economy.account;

import com.github.g4memas0n.economies.economy.Response;
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
    public @NotNull Future<Response<UUID>> getUniqueId() {
        return CompletableFuture.completedFuture(Response.of(this.player.getUniqueId()));
    }

    @Override
    public @NotNull Future<Response<String>> getName() {
        final String name = this.player.getName();

        return name != null ? CompletableFuture.completedFuture(Response.of(name)) : super.getName();
    }

    @Override
    public @NotNull Future<Response<Boolean>> setName(@NotNull final String name) {
        throw new UnsupportedOperationException("not possible on player bank account");
    }

    @Override
    public @NotNull Future<Response<Boolean>> isCreditworthy() {
        Response<Boolean> response;

        if (this.player.isOnline() && this.player.getPlayer() != null) {
            response = Response.of(this.player.getPlayer().hasPermission("economies.creditworthy"));
        } else {
            response = Response.FALSE;
        }

        return CompletableFuture.completedFuture(response);
    }
}
