package com.github.g4memas0n.economies.economy.currency;

import com.github.g4memas0n.economies.config.Settings;
import org.jetbrains.annotations.NotNull;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Locale;

public class BasicCurrency implements Currency {

    private final DecimalFormat format;
    private final String symbol;
    private final boolean suffix;

    public BasicCurrency(@NotNull final Settings settings) {
        this(settings.getCurrencyFormat(), settings.getCurrencySymbol(), settings.isCurrencySuffix());
    }

    public BasicCurrency(@NotNull final DecimalFormat format, @NotNull final String symbol, final boolean suffix) {
        this.format = format;
        this.format.setRoundingMode(RoundingMode.FLOOR);
        this.format.setMaximumFractionDigits(2);
        this.format.setParseBigDecimal(true);
        this.symbol = symbol;
        this.suffix = suffix;
    }

    public @NotNull String getSymbol() {
        return this.symbol;
    }

    @Override
    public @NotNull String format(final double number) {
        try {
            return format(BigDecimal.valueOf(number));
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("number cannot be formatted", ex);
        }
    }

    @Override
    public @NotNull String format(@NotNull final BigDecimal decimal) {
        String string = this.format.format(decimal);

        if (this.format.toPattern().contains(".00")) {
            char separator = this.format.getDecimalFormatSymbols().getDecimalSeparator();

            if (string.endsWith(separator + "00")) {
                string = string.substring(0, string.length() - 3);
            }
        }

        return this.suffix ? string + getSymbol() : getSymbol() + string;
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
            throw new IllegalArgumentException("number cannot be parsed", ex);
        }
    }

    @Override
    public @NotNull BigDecimal parse(@NotNull final String number) {
        try {
            BigDecimal decimal = (BigDecimal) this.format.parse(number.toUpperCase(Locale.ROOT));

            if (decimal.scale() > 2) {
                decimal = decimal.setScale(2, RoundingMode.FLOOR);
            }

            return decimal;
        } catch (ParseException ex) {
            throw new IllegalArgumentException("number cannot be parsed", ex);
        }
    }
}
