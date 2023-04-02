package com.github.g4memas0n.economies.economy.account;

import org.jetbrains.annotations.NotNull;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.Future;

public interface Account {

    /**
     * Gets the unique-id of this account.
     * The result of the future will always be not null. If any error occurs, the future will throw an
     * {@code ExecutionException}.
     *
     * @return a {@link Future} that returns the uuid of this account.
     */
    @NotNull Future<UUID> getUniqueId();

    /**
     * Gets the name of this account.<br>
     * The result of the future will always be not null. If any error occurs, the future will throw an
     * {@code ExecutionException}.
     *
     * @return a {@link Future} that returns the name of this account.
     */
    @NotNull Future<String> getName();

    /**
     * Sets the name for this account.<br>
     * If any error occurs, the future will throw an {@code ExecutionException}.
     *
     * @param name the new name for this account.
     * @return a {@link Future} that sets the name of this account.
     * @throws IllegalArgumentException if the new name is too long.
     */
    @NotNull Future<Void> setName(@NotNull String name);

    /**
     * Gets the current balance of this account.<br>
     * The result of the future will always be not null. If any error occurs, the future will throw an
     * {@code ExecutionException}.
     *
     * @return a {@link Future} that returns the balance of this account.
     */
    @NotNull Future<BigDecimal> getBalance();

    /**
     * Checks whether the current balance of this account meets or exceeds the specified {@code amount}.<br>
     * The result of the future will contain whether this account has enough money for the specified {@code amount}.
     * If any error occurs, the future will throw an {@code ExecutionException}.
     *
     * @param amount the amount that the balance must meet or exceed.
     * @return a {@link Future} that returns whether this account has enough money.
     * @throws IllegalArgumentException if the specified {@code amount} is negative.
     */
    @NotNull Future<Boolean> hasBalance(@NotNull BigDecimal amount);

    /**
     * Sets the balance for this account to the specified {@code balance}.<br>
     * If any error occurs, the future will throw an {@code ExecutionException}.
     * <p>
     *     Note: This method will forcibly set the specified {@code balance} for this account, ignoring the global
     *     balance of the bank. That means that the global balance of the bank could overdraft.
     * </p>
     *
     * @param balance the new balance for this account.
     * @return a {@link Future} that sets the balance for this account.
     */
    @NotNull Future<Void> setBalance(@NotNull BigDecimal balance);

    /**
     * Deposits the specified {@code amount} onto the balance of this account.<br>
     * If any error occurs, the future will throw an {@code ExecutionException} that may contain an
     * {@link com.github.g4memas0n.economies.economy.EconomyException}.
     * <p>
     *     Note: This method will softly deposit the specified {@code amount} onto the balance of this account,
     *     considering the global balance of the bank. That means that the deposit could fail if the global balance of
     *     the bank does not meet or exceed the specified {@code amount}. In that case, a
     *     {@link com.github.g4memas0n.economies.economy.NotEnoughMoneyException} will be thrown during the execution.
     * </p>
     *
     * @param amount the amount that should be deposited.
     * @return a {@link Future} that deposits an amount onto the balance of this account.
     * @throws IllegalArgumentException if the specified {@code amount} is negative or equal to zero.
     */
    @NotNull Future<Void> depositBalance(@NotNull BigDecimal amount);

    /**
     * Withdraws the specified {@code amount} from the balance of this account.<br>
     * If any error occurs, the future will throw an {@code ExecutionException} that may contain an
     * {@link com.github.g4memas0n.economies.economy.EconomyException}.
     * <p>
     *     Note: This method will softly withdraw the specified {@code amount} from the balance of this account,
     *     considering the current balance of this account. That means that the deposit could fail if the balance of
     *     this account does not meet or exceed the specified {@code amount}. In that case, a
     *     {@link com.github.g4memas0n.economies.economy.NotEnoughMoneyException} will be thrown during the execution.
     * </p>
     *
     * @param amount the amount that should be withdrawn.
     * @return a {@link Future} that withdraws an amount from the balance of this account.
     * @throws IllegalArgumentException if the specified {@code amount} is negative or equal to zero.
     */
    @NotNull Future<Void> withdrawBalance(@NotNull BigDecimal amount);

    /**
     * Transfer the specified {@code amount} from the balance of this account onto the balance of the specified
     * {@code account}.<br>
     * If any error occurs, the future will throw an {@code ExecutionException} that may contain an
     * {@link com.github.g4memas0n.economies.economy.EconomyException}.
     * <p>
     *     Note: This method will softly transfer the specified {@code amount} from the balance of this account to the
     *     balance of the specified {@code account}, considering the current balance of this account. That means that
     *     the transfer could fail if the balance of this account does not meet or exceed the specified {@code amount}.
     *     In that case, a {@link com.github.g4memas0n.economies.economy.NotEnoughMoneyException} will be thrown during
     *     the execution.
     * </p>
     *
     * @param account the account that should receive the amount.
     * @param amount the amount that should be transferred.
     * @return a {@link Future} that transfers an amount from this account to another account.
     * @throws IllegalArgumentException if the specified {@code account} is equal to this account.
     * @throws IllegalArgumentException if the specified {@code amount} is negative or equal to zero.
     */
    @NotNull Future<Void> transferBalance(@NotNull Account account, @NotNull BigDecimal amount);

    /**
     * Checks whether this account is creditworthy.<br>
     * The result of the future will contain whether this account is allowed to overdraft its balance to a negative
     * value. If any error occurs, the future will throw an {@code ExecutionException}.
     *
     * @return a {@link Future} that returns whether this account is creditworthy.
     */
    @NotNull Future<Boolean> isCreditworthy();

}
