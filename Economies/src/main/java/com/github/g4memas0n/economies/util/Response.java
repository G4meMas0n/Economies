package com.github.g4memas0n.economies.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public final class Response<T> {

    private final static Response<?> FAILED = new Response<>(null, new IllegalStateException());

    private final T result;
    private final Throwable failure;

    private Response(@Nullable final T result, @Nullable final Throwable failure) {
        this.result = result;
        this.failure = failure;
    }

    public static <T> @NotNull Response<T> failed() {
        @SuppressWarnings("unchecked")
        final Response<T> response = (Response<T>) FAILED;
        return response;
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

    public static <T> @NotNull Future<Response<T>> future(@NotNull final T result) {
        return CompletableFuture.completedFuture(Response.of(result));
    }

    public static <T> @NotNull Future<Response<T>> future(@NotNull final T result, @NotNull final Throwable throwable) {
        return CompletableFuture.completedFuture(Response.of(result, throwable));
    }

    public static <T> @NotNull Future<Response<T>> future(@NotNull final Throwable throwable) {
        return CompletableFuture.completedFuture(Response.of(throwable));
    }

    public boolean isFailure() {
        return failure != null;
    }

    public boolean isSuccess() {
        return result != null && failure == null;
    }

    public @NotNull Throwable getFailure() {
        if (failure == null) {
            throw new NoSuchElementException();
        }

        return failure;
    }

    public @NotNull T getResult() {
        if (result == null) {
            throw new NoSuchElementException();
        }

        return result;
    }
}
