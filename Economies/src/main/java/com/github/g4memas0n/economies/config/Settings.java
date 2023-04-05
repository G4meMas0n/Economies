package com.github.g4memas0n.economies.config;

import com.github.g4memas0n.cores.bukkit.config.Configuration;
import com.github.g4memas0n.cores.database.DatabaseManager.Placeholder;
import com.github.g4memas0n.economies.Economies;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Objects;
import java.util.Properties;

/**
 * The Settings class that represent the configuration file of this plugin.
 *
 * @author G4meMason
 * @since Release 1.0.0
 */
public final class Settings {

    private final YamlConfiguration defaults;
    private final Configuration config;

    private BigDecimal initial;

    private boolean overdraft;
    private boolean infinite;

    private boolean debug;

    public Settings(@NotNull final Economies plugin) {
        this.config = new Configuration(plugin);
        this.defaults = new YamlConfiguration();
    }

    public void load() {
        this.config.load();

        try {
            final InputStream stream = this.config.getPlugin().getResource(this.config.getFilename());

            if (stream != null) {
                this.defaults.load(new InputStreamReader(stream));
                this.config.setDefaults(this.defaults);
            }
        } catch (InvalidConfigurationException | IOException ex) {
            Economies.warn("Could not load configuration %s: %s", this.config.getFilename(), ex.getMessage());
        }

        this.initial = loadInitialBalance();
        this.overdraft = loadOverdraftBalance();
        this.infinite = loadBankInfinite();



        this.debug = loadDebug();
    }

    public void save() {
        /*
         * Disabled, because it is not intended to save the config file, as this eventually breaks the comments.
         */
    }


    /*
     * Balance settings:
     */

    private @NotNull BigDecimal loadInitialBalance() {
        long amount = this.config.getLong("balance.initial");

        if (amount < 0) {
            Economies.warn("Could not load initial balance in %s: illegal value", this.config.getFilename());
            amount = this.defaults.getLong("balance.initial");
        }

        return BigDecimal.valueOf(amount);
    }

    public @NotNull BigDecimal getInitialBalance() {
        return this.initial;
    }

    private boolean loadOverdraftBalance() {
        return this.config.getBoolean("balance.overdrafts", this.defaults.getBoolean("balance.overdrafts"));
    }

    public boolean isOverdraftBalance() {
        return this.overdraft;
    }

    /*
     * Bank settings:
     */

    public @NotNull BigDecimal loadBankBalance() {
        long amount = this.config.getLong("bank.balance");

        if (amount < 0) {
            Economies.warn("Could not load bank balance in %s: illegal value", this.config.getFilename());
            amount = this.defaults.getLong("bank.balance");
        }

        return BigDecimal.valueOf(amount);
    }

    public @NotNull BigDecimal getBankBalance() {
        return loadBankBalance();
    }

    private boolean loadBankInfinite() {
        return this.config.getBoolean("bank.infinite", this.defaults.getBoolean("bank.infinite"));
    }

    public boolean isBankInfinite() {
        return this.infinite;
    }

    /*
     * currency settings:
     */

    private @NotNull DecimalFormat loadCurrencyFormat() {
        String pattern = this.config.getString("currency.format");
        DecimalFormat format = null;

        if (pattern != null && !pattern.isBlank()) {
            try {
                format = new DecimalFormat(pattern);
            } catch (IllegalArgumentException ex) {
                Economies.warn("Could not setup currency format in %s: %s", this.config.getFilename(), ex.getMessage());
            }
        } else {
            Economies.warn("Could not find currency format in %s", this.config.getFilename());
        }

        if (format == null) {
            format = new DecimalFormat(Objects.requireNonNull(this.defaults.getString("currency.format")));
        }

        format.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(loadCurrencyLocale()));
        return format;
    }

    private @NotNull Locale loadCurrencyLocale() {
        String locale = this.config.getString("currency.locale");

        if (locale == null || locale.isBlank()) {
            Economies.warn("Could not find currency locale in %s", this.config.getFilename());
            locale = this.defaults.getString("currency.locale");
        }

        return Locale.forLanguageTag(Objects.requireNonNull(locale));
    }

    public @NotNull DecimalFormat getCurrencyFormat() {
        return loadCurrencyFormat();
    }

    private @NotNull String loadCurrencySymbol() {
        String symbol = this.config.getString("currency.symbol");

        if (symbol == null || symbol.isBlank()) {
            Economies.warn("Could not find currency symbol in %s", this.config.getFilename());
            symbol = this.defaults.getString("currency.symbol");
        }

        return Objects.requireNonNull(symbol);
    }

    public @NotNull String getCurrencySymbol() {
        return loadCurrencySymbol();
    }

    private boolean loadCurrencySuffix() {
        return this.config.getBoolean("currency.suffix", this.defaults.getBoolean("currency.suffix"));
    }

    public boolean isCurrencySuffix() {
        return loadCurrencySuffix();
    }

    /*
     * database settings:
     */

    private @NotNull String loadDatabaseType() {
        String type = this.config.getString("database.type");

        if (type == null || type.isBlank()) {
            Economies.warn("Could not find database type in %s", this.config.getFilename());
            type = this.defaults.getString("database.type");
        }

        return Objects.requireNonNull(type);
    }

    public @NotNull String getDatabaseType() {
        return loadDatabaseType();
    }

    private @NotNull Properties loadDatabaseProperties() {
        ConfigurationSection section = this.config.getConfigurationSection("database.properties");

        if (section == null) {
            Economies.warn("Could not find database properties in %s", this.config.getFilename());
            section = Objects.requireNonNull(this.defaults.getConfigurationSection("database.properties"));
        }

        Properties properties = new Properties();
        String value;

        for (final Placeholder entry : Placeholder.values()) {
            value = section.getString(entry.getKey());

            if (value != null) {
                properties.setProperty(entry.getKey(), value);
            }
        }

        return properties;
    }

    public @NotNull Properties getDatabaseProperties() {
        return loadDatabaseProperties();
    }

    /*
     *
     */

    private boolean loadDebug() {
        return this.config.getBoolean("debug", false);
    }

    public boolean isDebug() {
        return this.debug;
    }
}
