package com.github.g4memas0n.economies.config;

import com.github.g4memas0n.cores.bukkit.config.Configuration;
import com.github.g4memas0n.economies.Economies;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;

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
    private BigDecimal minimum;
    private BigDecimal maximum;

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
            Economies.warning("Unable to load default configuration '" + this.config.getFilename() + "': " + ex.getMessage());
        }

        this.initial = loadInitialBalance();
        this.minimum = loadMinimumBalance();
        this.maximum = loadMaximumBalance();

        this.debug = loadDebug();
    }

    public void save() {
        /*
         * Disabled, because it is not intended to save the config file, as this eventually breaks the comments.
         */
    }


    private @NotNull BigDecimal loadInitialBalance() {
        long amount = this.config.getLong("balance.initial");

        if (amount < 0) {
            Economies.warning("Found invalid initial balance: Value must be greater than or equal to zero");
            amount = this.defaults.getLong("balance.initial");
        }

        return BigDecimal.valueOf(amount);
    }

    public @NotNull BigDecimal getInitialBalance() {
        return this.initial;
    }

    private @NotNull BigDecimal loadMinimumBalance() {
        long amount = this.config.getLong("balance.minimum");

        if (amount > 0) {
            Economies.warning("Found invalid minimum balance: Value must be smaller than or equal to zero");
            amount = this.defaults.getLong("balance.minimum");
        }

        return BigDecimal.valueOf(amount);
    }

    public @NotNull BigDecimal getMinimumBalance() {
        return this.minimum;
    }

    public boolean hasMinimumBalance() {
        return this.minimum.compareTo(BigDecimal.ZERO) < 0;
    }

    private @NotNull BigDecimal loadMaximumBalance() {
        long amount = this.config.getLong("balance.maximum");

        if (amount <= 0 && amount != -1) {
            Economies.warning("Found invalid maximum balance: Value must be greater than zero or negative one");
            amount = this.defaults.getLong("balance.maximum");
        }

        return BigDecimal.valueOf(amount);
    }

    public @NotNull BigDecimal getMaximumBalance() {
        return this.maximum;
    }

    private boolean loadDebug() {
        return this.config.getBoolean("debug");
    }

    public boolean isDebug() {
        return this.debug;
    }
}
