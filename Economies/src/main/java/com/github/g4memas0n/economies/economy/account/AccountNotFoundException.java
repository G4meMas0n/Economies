package com.github.g4memas0n.economies.economy.account;

import com.github.g4memas0n.economies.economy.EconomyException;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import java.util.UUID;

public class AccountNotFoundException extends EconomyException {

    private final UUID uniqueId;

    public AccountNotFoundException(@NotNull final UUID uniqueId) {
        this.uniqueId = uniqueId;
    }

    public AccountNotFoundException(@NotNull final OfflinePlayer player) {
        this.uniqueId = player.getUniqueId();
    }

    public @NotNull UUID getUniqueId() {
        return this.uniqueId;
    }
}
