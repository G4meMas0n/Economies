package com.github.g4memas0n.economies.economy;

import org.jetbrains.annotations.NotNull;

public class EconomyException extends Exception {

    public EconomyException() {

    }

    public EconomyException(@NotNull final String message) {
        super(message);
    }

    public EconomyException(@NotNull final String message, @NotNull final Throwable cause) {
        super(message, cause);
    }

}
