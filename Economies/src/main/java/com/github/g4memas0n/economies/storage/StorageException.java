package com.github.g4memas0n.economies.storage;

import org.jetbrains.annotations.NotNull;

public class StorageException extends Exception {

    public StorageException(@NotNull final String message) {
        super(message);
    }

    public StorageException(@NotNull final String message, @NotNull final Throwable throwable) {
        super(message, throwable);
    }

}
