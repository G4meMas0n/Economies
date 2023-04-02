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

    private boolean overdrafts;
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

        this.overdrafts = loadOverdraftEnabled();
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

    private boolean loadOverdraftEnabled() {
        return this.config.getBoolean("balance.overdrafts", true);
    }

    public boolean isOverdraftEnabled() {
        return this.overdrafts;
    }

    /*
     * Bank settings:
     */

    private boolean loadBankInfinite() {
        return this.config.getBoolean("bank.infinite", true);
    }

    public boolean isBankInfinite() {
        return this.infinite;
    }

    public @NotNull BigDecimal loadGlobalBalance() {
        long amount = this.config.getLong("bank.global");

        if (amount < 0) {
            Economies.warn("Could not load global balance in %s: illegal value", this.config.getFilename());
            amount = this.defaults.getLong("bank.global");
        }

        return BigDecimal.valueOf(amount);
    }

    public @NotNull BigDecimal getGlobalBalance() {
        return loadGlobalBalance();
    }



    /*
     *
     */

    private boolean loadDebug() {
        return this.config.getBoolean("debug");
    }

    public boolean isDebug() {
        return this.debug;
    }
}
