package com.github.g4memas0n.economies.economy.account;

import com.github.g4memas0n.economies.storage.AccountStorage;
import com.github.g4memas0n.economies.util.Response;
import org.jetbrains.annotations.NotNull;
import java.util.UUID;
import java.util.concurrent.Future;

public class BankAccount extends BasicAccount {

    public BankAccount(@NotNull final AccountStorage storage) {
        super(storage);
    }

    @Override
    public @NotNull Future<Response<UUID>> getUniqueId() {
        return Response.future(UUID.randomUUID());
    }

    @Override
    public @NotNull Future<Response<String>> getName() {
        return Response.future("Bank"); //TODO replace bank name
    }

    @Override
    public @NotNull Future<Response<Boolean>> setName(@NotNull final String name) {
        //TODO replace exception
        return Response.future(false, new IllegalArgumentException("not possible"));
    }

    @Override
    public @NotNull Future<Response<Boolean>> isCreditworthy() {
        return Response.future(true);
    }

    @Override
    public @NotNull Future<Response<Boolean>> setCreditworthy(final boolean creditworthy) {
        return Response.future(false);
    }
}
