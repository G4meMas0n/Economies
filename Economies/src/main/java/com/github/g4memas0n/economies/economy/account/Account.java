package com.github.g4memas0n.economies.economy.account;

import com.github.g4memas0n.economies.util.Response;
import org.jetbrains.annotations.NotNull;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.Future;
import java.util.function.Consumer;

public interface Account {

    /**
     * Requests the unique-id of this {@code Account}.
     * The response contains the requested unique-id if the action was successful. Otherwise, it will contain the
     * throwable of the failure. //TODO List throwable
     * @return a {@link Future} containing the response of this action.
     */
    @NotNull Future<Response<UUID>> getUniqueId();

    /**
     * Requests the unique-id of this {@code Account}.
     * @param consumer a {@link Consumer} accepting the response of the action.
     * @return a {@link Future} that completes when the consumer completes.
     * @see #getUniqueId()
     */
    @NotNull Future<Void> getUniqueId(@NotNull final Consumer<Response<UUID>> consumer);

    /**
     * Requests the name of this {@code Account}.
     * The response contains the requested name if the action was successful. Otherwise, it will contain the
     * throwable of the failure. //TODO List throwable
     * @return a {@link Future} containing the response of this action.
     */
    @NotNull Future<Response<String>> getName();

    /**
     * Requests the name of this {@code Account}.
     * @param consumer a {@link Consumer} accepting the response of the action.
     * @return a {@link Future} that completes when the consumer completes.
     * @see #getName()
     */
    @NotNull Future<Void> getName(@NotNull final Consumer<Response<String>> consumer);

    /**
     * Request the change of the name for this {@code Account}.
     * The response contains whether the request was successful. Otherwise, it will contain the
     * throwable of the failure. //TODO List throwable
     * @param name the new name for this {@code Account}.
     * @return a {@link Future} containing the response of this action.
     */
    @NotNull Future<Response<Boolean>> setName(@NotNull final String name);

    /**
     * Request the change of the name for this {@code Account}.
     * @param name the new name for this {@code Account}.
     * @param consumer a {@link Consumer} accepting the response of this action.
     * @return a {@link Future} that completes when the consumer completes.
     * @see #setName(String)
     */
    @NotNull Future<Void> setName(@NotNull final String name, @NotNull final Consumer<Response<Boolean>> consumer);

    /**
     * Requests the current balance of this {@code Account}.
     * The response contains the current balance if the action was successful. Otherwise, it will contains the
     * throwable of the failure. //TODO List throwable
     * @return a {@link Future} containing the response of this action.
     */
    @NotNull Future<Response<BigDecimal>> getBalance();

    /**
     * Requests the current balance of this {@code Account}.
     * @param consumer a {@link Consumer} accepting the response of this action.
     * @return a {@link Future} that completes when the consumer completes.
     * @see #getBalance()
     */
    @NotNull Future<Void> getBalance(@NotNull final Consumer<Response<BigDecimal>> consumer);

    /**
     * Requests a balance check for this {@code Account}
     * The result of the response will be {@code true} if this {@code Account} has at least the specified amount.
     * Otherwise, it will be {@code false}. If the check fails, the response contains the throwable of the failure.
     * //TODO List throwable
     * @param amount the amount that the balance must meet or exceed.
     * @return a {@link Future} containing the response for this check.
     */
    @NotNull Future<Response<Boolean>> hasBalance(@NotNull final BigDecimal amount);

    /**
     * Requests a balance check for this {@code Account}
     * @param amount the amount that the balance must meet or exceed.
     * @param consumer a {@link Consumer} accepting the response of this check.
     * @return a {@link Future} that completes when the consumer completes.
     * @see #hasBalance(BigDecimal)
     */
    @NotNull Future<Void> hasBalance(@NotNull final BigDecimal amount,
                                     @NotNull final Consumer<Response<Boolean>> consumer);

    /**
     * Requests a deposit of the specified amount onto this {@code Account}.
     * The response contains whether the request was successful. Otherwise, it will contain the
     * throwable of the failure. //TODO List throwable
     * @param amount the amount that should be deposited.
     * @return a {@link Future} containing the response of this action.
     */
    @NotNull Future<Response<Boolean>> depositBalance(@NotNull final BigDecimal amount);

    /**
     * Requests a deposit of the specified amount onto this {@code Account}.
     * @param amount the amount that should be deposited.
     * @param consumer a {@link Consumer} accepting the response of this action.
     * @return a {@link Future} that completes when the consumer completes.
     * @see #depositBalance(BigDecimal)
     */
    @NotNull Future<Void> depositBalance(@NotNull final BigDecimal amount,
                                         @NotNull final Consumer<Response<Boolean>> consumer);

    /**
     * Requests a transfer of the specified amount from this {@code Account} onto the specified {@code Account}.
     * The response contains whether the request was successful. Otherwise, it will contain the
     * throwable of the failure. //TODO List throwable
     * @param account the account that should receive the amount.
     * @param amount the amount that should be transferred.
     * @return a {@link Future} containing the result of this transfer.
     */
    @NotNull Future<Response<Boolean>> transferBalance(@NotNull final Account account,
                                                       @NotNull final BigDecimal amount);

    /**
     * Requests a transfer of the specified amount from this {@code Account} onto the specified {@code Account}.
     * @param account the account that should receive the amount.
     * @param amount the amount that should be transferred.
     * @param consumer a {@link Consumer} accepting the response of this action.
     * @return a {@link Future} that completes when the consumer completes.
     * @see #transferBalance(Account, BigDecimal)
     */
    @NotNull Future<Void> transferBalance(@NotNull final Account account, @NotNull final BigDecimal amount,
                                          @NotNull final Consumer<Response<Boolean>> consumer);

    /**
     * Requests a withdrawal of the specified amount from this {@code Account}.
     * The response contains whether the request was successful. Otherwise, it will contain the
     * throwable of the failure. //TODO List throwable
     * @param amount the amount that should be withdrawn.
     * @return a {@link Future} containing the result of this withdrawal.
     */
    @NotNull Future<Response<Boolean>> withdrawBalance(@NotNull final BigDecimal amount);

    /**
     * Requests a withdrawal of the specified amount from this {@code Account}.
     * @param amount the amount that should be withdrawn.
     * @param consumer a {@link Consumer} accepting the response of this action.
     * @return a {@link Future} that completes when the consumer completes.
     * @see #withdrawBalance(BigDecimal)
     */
    @NotNull Future<Void> withdrawBalance(@NotNull final BigDecimal amount,
                                          @NotNull final Consumer<Response<Boolean>> consumer);

    /**
     * Requests a check of the creditworthy for this {@code Account}.
     * The result of the response will be {@code true} if this {@code Account} is creditworthy. Otherwise, it will
     * be {@code false}. If the check fails, the response contains the throwable of the failure.
     * //TODO List throwable
     * @return a {@link Future} containing the response for this check.
     */
    @NotNull Future<Response<Boolean>> isCreditworthy();

    /**
     * Requests a check of the creditworthy for this {@code Account}.
     * @param consumer a {@link Consumer} accepting the response of this check.
     * @return a {@link Future} that completes when the consumer completes.
     * @see #isCreditworthy()
     */
    @NotNull Future<Void> isCreditworthy(@NotNull final Consumer<Response<Boolean>> consumer);

    /**
     * Requests a change of the creditworthy for this {@code Account}.
     * The response contains whether the request was successful. Otherwise, it will contain the
     * throwable of the failure. //TODO List throwable
     * @param creditworthy whether this account is creditworthy or not.
     * @return a {@link Future} containing the response of this action.
     */
    @NotNull Future<Response<Boolean>> setCreditworthy(final boolean creditworthy);

    /**
     * Requests a change of the creditworthy for this {@code Account}.
     * @param creditworthy whether this account is creditworthy or not.
     * @param consumer a {@link Consumer} accepting the response of this action.
     * @return a {@link Future} that completes when the consumer completes.
     * @see #setCreditworthy(boolean)
     */
    @NotNull Future<Void> setCreditworthy(final boolean creditworthy,
                                          @NotNull final Consumer<Response<Boolean>> consumer);

    /**
     * Requests an infinity check for this {@code Account}.
     * The result of the response will be {@code true} if this {@code Account} has an infinite balance. Otherwise, it
     * will be {@code false}. If the check fails, the response contains the throwable of the failure.
     * //TODO List throwable
     * @return a {@link Future} containing the response of this check.
     */
    @NotNull Future<Response<Boolean>> isInfinite();

    /**
     * Requests an infinity check for this {@code Account}.
     * @param consumer a {@link Consumer} accepting the response of this check.
     * @return a {@link Future} that completes when the consumer completes.
     * @see #isInfinite()
     */
    @NotNull Future<Void> isInfinite(@NotNull final Consumer<Response<Boolean>> consumer);

}
