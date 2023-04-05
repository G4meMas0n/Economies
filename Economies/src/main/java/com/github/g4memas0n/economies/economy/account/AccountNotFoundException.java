package com.github.g4memas0n.economies.economy.account;

import com.github.g4memas0n.economies.economy.EconomyException;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.UUID;

public class AccountNotFoundException extends EconomyException {

    private final UUID uniqueId;
    private final String name;

    public AccountNotFoundException(@NotNull final String name) {
        this.uniqueId = null;
        this.name = name;
    }

    public AccountNotFoundException(@NotNull final UUID uniqueId) {
        this.uniqueId = uniqueId;
        this.name = null;
    }

    public AccountNotFoundException(@NotNull final OfflinePlayer player) {
        this.uniqueId = player.getUniqueId();
        this.name = null;
    }

    public @NotNull Object getAccount() {
        return this.uniqueId != null ? this.uniqueId : this.name;
    }

    public @Nullable UUID getUniqueId() {
        return this.uniqueId;
    }

    public @Nullable String getName() {
        return this.name;
    }
}
