package com.github.g4memas0n.economies.economy.account;

import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import java.util.UUID;
import java.util.concurrent.Future;

public interface AccountProvider {

    /**
     * Gets the account for the specified {@code name}.<br>
     * The result of the future may be null, if no or too many accounts for the specified {@code name} could be found.
     * If any error occurs, the future will throw an {@code ExecutionException}.
     *
     * @param name the name of the account to get.
     * @return a {@link Future} that returns the account for the specified {@code name} if it exists.
     * @throws IllegalArgumentException if the name is empty or blank.
     */
    @NotNull Future<Account> getAccount(@NotNull String name);

    /**
     * Gets the account with the specified {@code uniqueId}.<br>
     * The result of the future may be null, if no account with the specified {@code uniqueId} could be found.
     * If any error occurs, the future will throw an {@code ExecutionException}.
     *
     * @param uniqueId the unique-id of the account to get.
     * @return a {@link Future} that returns the account with the specified {@code uniqueId} if it exists.
     * @throws IllegalArgumentException if the uuid version is illegal.
     */
    @NotNull Future<Account> getAccount(@NotNull UUID uniqueId);

    /**
     * Gets the account for the specified {@code player}.<br>
     * The result of the future may be null, if no account for the specified {@code player} could be found.
     * If any error occurs, the future will throw an {@code ExecutionException}.
     * <p>
     *     Note: This method will create an account for the specified {@code player} if no existing account could be
     *     found and if the specified {@code player} is currently online.
     * </p>
     *
     * @param player the player of the account to get.
     * @return a {@link Future} that returns the account for the specified {@code player} if it exists.
     * @throws IllegalArgumentException if the offline player object has an offline uuid.
     */
    @NotNull Future<Account> getAccount(@NotNull OfflinePlayer player);

    /**
     * Checks whether an account for specified {@code name} exists.<br>
     * The result of the future will contain whether an account for the specified {@code name} exists.
     * If any error occurs, the future will throw an {@code ExecutionException}.
     *
     * @param name the name of the account to check.
     * @return a {@link Future} that returns whether the account exists.
     * @throws IllegalArgumentException if the name is empty or blank.
     */
    @NotNull Future<Boolean> hasAccount(@NotNull String name);

    /**
     * Checks whether an account with specified {@code uniqueId} exists.<br>
     * The result of the future will contain whether an account with the specified {@code uniqueId} exists.
     * If any error occurs, the future will throw an {@code ExecutionException}.
     *
     * @param uniqueId the unique-id of the account to get.
     * @return a {@link Future} that returns whether the account exists.
     * @throws IllegalArgumentException if the uuid version is illegal.
     */
    @NotNull Future<Boolean> hasAccount(@NotNull UUID uniqueId);

    /**
     * Checks whether an account for specified {@code player} exists.<br>
     * The result of the future will contain whether an account for the specified {@code player} exists.
     * If any error occurs, the future will throw an {@code ExecutionException}.
     *
     * @param player the player of the account to check.
     * @return a {@link Future} that returns whether the account exists.
     * @throws IllegalArgumentException if the offline player object has an offline uuid.
     */
    @NotNull Future<Boolean> hasAccount(@NotNull OfflinePlayer player);

    /**
     * Creates a new account for the specified {@code player} if it not already exists.<br>
     * The result of the future will contain whether an account for the specified {@code player} was created. If any
     * error occurs, the future will throw an {@code ExecutionException}.
     *
     * @param player the player of the account to create.
     * @return a {@link Future} that returns whether the account was created.
     * @throws IllegalArgumentException if the offline player object has an offline uuid.
     * @throws IllegalArgumentException if the offline player object has no name.
     */
    @NotNull Future<Boolean> createAccount(@NotNull OfflinePlayer player);

    /**
     * Deletes the account for the specified {@code player} if it exists.<br>
     * The result of the future will contain whether the account for the specified {@code player} was deleted. If any
     * error occurs, the future will throw an {@code ExecutionException}.
     *
     * @param player the player of the account to delete.
     * @return a {@link Future} that returns whether the account was deleted.
     * @throws IllegalArgumentException if the offline player object has an offline uuid.
     */
    @NotNull Future<Boolean> deleteAccount(@NotNull OfflinePlayer player);

}
