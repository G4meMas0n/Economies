package com.github.g4memas0n.economies.economy;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.NoSuchElementException;

public final class Response<T> {

    public final static Response<Boolean> FALSE = Response.of(false);
    public final static Response<Boolean> TRUE = Response.of(true);

    private final T result;
    private final Throwable failure;

    private Response(@Nullable final T result, @Nullable final Throwable failure) {
        this.result = result;
        this.failure = failure;
    }

    public static <T> @NotNull Response<T> of(@NotNull final T result) {
        return new Response<>(result, null);
    }

    public static <T> @NotNull Response<T> of(@NotNull final T result, @NotNull final Throwable throwable) {
        return new Response<>(result, throwable);
    }

    public static <T> @NotNull Response<T> of(@NotNull final Throwable throwable) {
        return new Response<>(null, throwable);
    }

    public boolean isFailure() {
        return this.failure != null;
    }

    public boolean isSuccess() {
        return this.result != null && this.failure == null;
    }

    public @NotNull Throwable getFailure() {
        if (this.failure == null) {
            throw new NoSuchElementException();
        }

        return this.failure;
    }

    public @NotNull T getResult() {
        if (this.result == null) {
            throw new NoSuchElementException();
        }

        return this.result;
    }

    public @NotNull T getOrThrow() throws Exception {
        if (this.failure != null) {
            throw (Exception) this.failure;
        }

        return getResult();
    }
}
