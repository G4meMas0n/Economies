package com.github.g4memas0n.economies.economy.account;

import com.github.g4memas0n.economies.storage.AccountStorage;
import com.github.g4memas0n.economies.util.Response;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import java.util.UUID;
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
        return Response.future(player.getUniqueId());
    }

    @Override
    public @NotNull Future<Response<String>> getName() {
        final String name = this.player.getName();

        return name != null ? Response.future(name) : super.getName();
    }

    @Override
    public @NotNull Future<Response<Boolean>> setName(@NotNull final String name) {
        //TODO replace exception
        return Response.future(false, new IllegalArgumentException("not possible"));
    }

    @Override
    public @NotNull Future<Response<Boolean>> isCreditworthy() {
        if (this.player.isOnline() && this.player.getPlayer() != null) {
            //TODO Check permission

            return Response.future(false);
        }

        return super.isCreditworthy();
    }

    @Override
    public @NotNull Future<Response<Boolean>> setCreditworthy(final boolean creditworthy) {
        //TODO replace exception
        return Response.future(false, new IllegalArgumentException("not possible"));
    }
}
