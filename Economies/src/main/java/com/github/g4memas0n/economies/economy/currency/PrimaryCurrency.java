package com.github.g4memas0n.economies.economy.currency;

import com.github.g4memas0n.economies.Economies;
import org.jetbrains.annotations.NotNull;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Locale;

public class PrimaryCurrency implements Currency {

    private final Economies plugin;

    public PrimaryCurrency(@NotNull final Economies plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String format(final double number) {
        try {
            return format(BigDecimal.valueOf(number));
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("can not format number", ex);
        }
    }

    @Override
    public @NotNull String format(@NotNull final BigDecimal decimal) {
        final DecimalFormat formatter = this.plugin.getSettings().getCurrencyFormat();

        String string = formatter.format(decimal);
        if (string.endsWith(formatter.getDecimalFormatSymbols().getDecimalSeparator() + "00")) {
            string = string.substring(0, string.length() - 3);
        }

        return this.plugin.getSettings().isCurrencySuffix() ? string + symbol() : symbol() + string;
    }

    @Override
    public @NotNull BigDecimal parse(final double number) {
        try {
            BigDecimal decimal = BigDecimal.valueOf(number);

            if (decimal.scale() > 2) {
                decimal = decimal.setScale(2, RoundingMode.FLOOR);
            }

            return decimal;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("can not parse number", ex);
        }
    }

    @Override
    public @NotNull BigDecimal parse(@NotNull final String number) {
        final DecimalFormat formatter = this.plugin.getSettings().getCurrencyFormat();

        try {
            BigDecimal decimal = (BigDecimal) formatter.parse(number.toUpperCase(Locale.ROOT));

            if (decimal.scale() > 2) {
                decimal = decimal.setScale(2, RoundingMode.FLOOR);
            }

            return decimal;
        } catch (ParseException ex) {
            throw new IllegalArgumentException("can not parse number", ex);
        }
    }

    @Override
    public @NotNull String symbol() {
        return this.plugin.getSettings().getCurrencySymbol();
    }
}
