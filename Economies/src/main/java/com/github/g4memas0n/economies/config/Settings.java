package com.github.g4memas0n.economies.config;

import com.github.g4memas0n.economies.Economies;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;

/**
 * The Settings class that represent the configuration file of this plugin.
 *
 * @author G4meMason
 * @since Release 1.0.0
 */
public final class Settings {

    private final Economies instance;
    private final YamlConfiguration storage;

    private BigDecimal initial;

    private String bankName;
    private BigDecimal bankInitial;
    private boolean bankInfinite;

    private boolean debug;

    public Settings(@NotNull final Economies instance) {
        this.instance = instance;
        this.storage = new YamlConfiguration();
    }

    public void load() {
        final File config = new File(this.instance.getDataFolder(), "config.yml");

        try {
            this.storage.load(config);

            this.instance.getLogger().info("Loaded configuration file: " + config.getName());
        } catch (FileNotFoundException ex) {
            this.instance.getLogger().warning("Unable to find configuration file: " + config.getName() + " (Saving default configuration...)");
            this.instance.saveResource(config.getName(), true);
            this.instance.getLogger().info("Saved default configuration from template: " + config.getName());

            this.load();
            return;
        } catch (InvalidConfigurationException ex) {
            this.instance.getLogger().warning("Unable to load broken configuration file: " + config.getName() + " (Renaming it and saving default configuration...)");

            final File broken = new File(config.getParent(), config.getName().replaceAll("(?i)(yml)$", "broken.$1"));

            if (broken.exists() && broken.delete()) {
                this.instance.getLogger().info("Deleted old broken configuration file: " + broken.getName());
            }

            if (config.renameTo(broken)) {
                this.instance.getLogger().info("Renamed broken configuration file to: " + broken.getName());
            }

            this.instance.saveResource(config.getName(), true);
            this.instance.getLogger().info("Saved default configuration from template: " + config.getName());

            this.load();
            return;
        } catch (IOException ex) {
            this.instance.getLogger().warning("Unable to load configuration file: " + config.getName() + " (Loading default configuration...)");

            /*
             * Removing each key manual to clear existing configuration, as loading a blank config does not work here
             * for any reason.
             */
            this.storage.getKeys(false).forEach(key -> this.storage.set(key, null));

            this.instance.getLogger().info("Loaded default configuration from template: " + config.getName());
        }

        this.initial = BigDecimal.valueOf(this._getInitialBalance());
        this.bankName = this._getBankName();
        this.bankInfinite = this._getBankInfinite();
        this.bankInitial = BigDecimal.valueOf(this._getBankInitial());

        this.debug = this._getDebug();
    }

    @SuppressWarnings("unused")
    public void save() {
        /*
         * Disabled, because it is not intended to save the config file, as this breaks the comments.
         */
    }

    private boolean _getDebug() {
        return this.storage.getBoolean("debug", false);
    }

    public boolean isDebug() {
        return this.debug;
    }

    private long _getInitialBalance() {
        final long initial = this.storage.getLong("balance.initial", 1000L);

        if (initial <= 0) {
            this.instance.getLogger().warning("Detected invalid initial balance: Value must be greater than zero");

            return 1000L;
        }

        return initial;
    }

    public @NotNull BigDecimal getInitialBalance() {
        return this.initial;
    }

    private boolean _getBankInfinite() {
        return this.storage.getBoolean("bank.infinite", true);
    }

    public boolean isBankInfinite() {
        return this.bankInfinite;
    }

    private long _getBankInitial() {
        final long initial = this.storage.getLong("bank.initial", 1000000000L);

        if (initial <= 0) {
            this.instance.getLogger().warning("Detected invalid initial bank balance: Value must be greater than zero");

            return 1000000000L;
        }

        return initial;
    }

    public @NotNull BigDecimal getBankInitial() {
        return this.bankInitial;
    }

    private @NotNull String _getBankName() {
        final String name = this.storage.getString("bank.name");

        if (name == null || name.isBlank()) {
            this.instance.getLogger().warning("Detected invalid bank name: Name is missing or is blank");

            return "Bank";
        }

        return name;
    }

    public @NotNull String getBankName() {
        return this.bankName;
    }
}
