package com.github.g4memas0n.economies.storage;

import com.github.g4memas0n.economies.util.Response;
import org.jetbrains.annotations.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

public interface AccountStorage {

    @NotNull Response<UUID> getUniqueId();

    @NotNull Response<Boolean> setUniqueId(@NotNull final UUID uniqueId);

    @NotNull Response<String> getName();

    @NotNull Response<Boolean> setName(@NotNull final String name);

    @NotNull Response<BigDecimal> getBalance();

    @NotNull Response<Boolean> setBalance(@NotNull final BigDecimal balance);

    @NotNull Response<Boolean> setBalance(@NotNull final BigDecimal balance,
                                          @NotNull final Transaction transaction);

    @NotNull Response<Boolean> addBalance(@NotNull final BigDecimal amount);

    @NotNull Response<Boolean> addBalance(@NotNull final BigDecimal amount,
                                          @NotNull final Transaction transaction);

    @NotNull Response<Boolean> removeBalance(@NotNull final BigDecimal amount);

    @NotNull Response<Boolean> removeBalance(@NotNull final BigDecimal amount,
                                             @NotNull final Transaction transaction);

    @NotNull Response<Boolean> getCreditworthy();

    @NotNull Response<Boolean> setCreditworthy(final boolean creditworthy);

}
