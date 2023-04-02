package com.github.g4memas0n.economies.economy.account;

import com.github.g4memas0n.economies.economy.Response;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import java.util.concurrent.Future;
import java.util.function.Consumer;

public interface AccountProvider {

    @NotNull Future<Response<Account>> getAccount(@NotNull OfflinePlayer player);

    @NotNull Future<Void> getAccount(@NotNull OfflinePlayer player, @NotNull Consumer<Response<Account>> consumer);

    @NotNull Future<Response<Boolean>> hasAccount(@NotNull OfflinePlayer player);

    @NotNull Future<Void> hasAccount(@NotNull OfflinePlayer player, @NotNull Consumer<Response<Boolean>> consumer);

    @NotNull Future<Response<Boolean>> createAccount(@NotNull OfflinePlayer player);

    @NotNull Future<Void> createAccount(@NotNull OfflinePlayer player, @NotNull Consumer<Response<Boolean>> consumer);

    @NotNull Future<Response<Boolean>> deleteAccount(@NotNull OfflinePlayer player);

    @NotNull Future<Void> deleteAccount(@NotNull OfflinePlayer player, @NotNull Consumer<Response<Boolean>> consumer);

}
