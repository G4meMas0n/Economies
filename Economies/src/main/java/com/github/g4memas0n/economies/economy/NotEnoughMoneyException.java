package com.github.g4memas0n.economies.economy;

import org.jetbrains.annotations.NotNull;
import java.math.BigDecimal;

public class NotEnoughMoneyException extends EconomyException {

    private final BigDecimal amount;

    public NotEnoughMoneyException(@NotNull final BigDecimal amount) {
        this.amount = amount;
    }

    public @NotNull BigDecimal getAmount() {
        return this.amount;
    }
}
