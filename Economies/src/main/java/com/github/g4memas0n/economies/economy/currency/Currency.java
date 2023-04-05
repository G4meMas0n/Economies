package com.github.g4memas0n.economies.economy.currency;

import org.jetbrains.annotations.NotNull;
import java.math.BigDecimal;

public interface Currency {

    /**
     * Returns the currency symbol that will appear at the start or at the end of a formatted number string.
     * @return the currency symbol of this currency.
     */
    @NotNull String getSymbol();

    /**
     * Formats the given {@code number} into a human-readable representation.
     *
     * @param number the double number to format.
     * @return the formatted decimal.
     * @throws IllegalArgumentException if the number could not be formatted.
     */
    @NotNull String format(double number);

    /**
     * Formats the given {@code decimal} into a human-readable representation.
     *
     * @param number the decimal number to format.
     * @return the formatted decimal.
     * @throws IllegalArgumentException if the number could not be formatted.
     */
    @NotNull String format(@NotNull BigDecimal number);

    /**
     * Parses the specified {@code double} into a {@link BigDecimal}.
     * <p>
     *     Note: The parsed number will be rounded down to the scale/precision of two if the scale/precision of the
     *     specified {@code number} is higher than two.
     * </p>
     *
     * @param number the double number to parse.
     * @return the parsed decimal.
     * @throws IllegalArgumentException if the number could not be parsed.
     */
    @NotNull BigDecimal parse(double number);

    /**
     * Parses the specified {@code number} string into a {@link BigDecimal}.
     * <p>
     *     Note: The parsed number will be rounded down to the scale/precision of two if the scale/precision of the
     *     specified {@code number} is higher than two.
     * </p>
     *
     * @param number the string representation of the number to parse.
     * @return the parsed decimal.
     * @throws IllegalArgumentException if the number could not be parsed.
     */
    @NotNull BigDecimal parse(@NotNull String number);

}
