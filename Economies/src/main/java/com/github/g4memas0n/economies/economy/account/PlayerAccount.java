package com.github.g4memas0n.economies.economy.account;

import com.github.g4memas0n.economies.economy.Response;
import com.github.g4memas0n.economies.storage.AccountStorage;
import com.github.g4memas0n.economies.storage.StorageException;
import com.google.common.base.Preconditions;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class PlayerAccount extends BasicAccount {

    private final OfflinePlayer player;

    public PlayerAccount(@NotNull final AccountStorage storage,
                         @NotNull final OfflinePlayer player) {
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
    public @NotNull Future<Response<Boolean>> depositBalance(@NotNull final BigDecimal amount) {
        Preconditions.checkArgument(amount.compareTo(BigDecimal.ZERO) > 0, "amount must be greater than zero");

        return CompletableFuture.completedFuture(Response.FALSE);
    }

    @Override
    public @NotNull Future<Response<Boolean>> transferBalance(@NotNull final Account account,
                                                              @NotNull final BigDecimal amount) {
        Preconditions.checkArgument(amount.compareTo(BigDecimal.ZERO) > 0, "amount must be greater than zero");

        if (account instanceof BasicAccount receiver) {
            return CompletableFuture.supplyAsync(() -> {
                try {
                    final BigDecimal balance = this.storage.getBalance();

                    //TODO Implement transfer

                    return Response.of(false);
                } catch (StorageException ex) {
                    return Response.of(false, ex);
                }
            });
        }

        throw new IllegalArgumentException("custom account implementation");
    }

    @Override
    public @NotNull Future<Response<Boolean>> withdrawBalance(@NotNull final BigDecimal amount) {
        Preconditions.checkArgument(amount.compareTo(BigDecimal.ZERO) > 0, "amount must be greater than zero");

        return CompletableFuture.completedFuture(Response.FALSE);
    }

    @Override
    public @NotNull Future<Response<Boolean>> isCreditworthy() {
        if (this.player.isOnline() && this.player.getPlayer() != null) {
            //TODO Check permission
        }

        return CompletableFuture.completedFuture(Response.FALSE);
    }

    @Override
    public @NotNull Future<Response<Boolean>> isInfinite() {
        return CompletableFuture.completedFuture(Response.FALSE);
    }
}
