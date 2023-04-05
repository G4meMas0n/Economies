package com.github.g4memas0n.economies;

import com.github.g4memas0n.economies.config.Settings;
import com.github.g4memas0n.economies.economy.account.AccountManager;
import com.github.g4memas0n.economies.economy.currency.BasicCurrency;
import com.github.g4memas0n.economies.storage.StorageManager;
import com.google.common.base.Preconditions;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.logging.Level;

/**
 * The Economies main class.
 *
 * @author G4meMas0n
 * @since Release 1.0.0
 */
public final class Economies extends JavaPlugin {

    private static Economies instance;

    private AccountManager accounts;
    private BasicCurrency currency;

    private StorageManager storage;
    private Settings settings;

    private boolean enabled;

    public @NotNull AccountManager getAccounts() {
        return this.accounts;
    }

    public @NotNull BasicCurrency getCurrency() {
        return this.currency;
    }

    public @NotNull Settings getSettings() {
        return this.settings;
    }

    @Override
    public void onLoad() {
        if (instance != null) {
            this.getLogger().severe("Tried to load plugin twice. Plugin is already loaded.");
            return;
        }

        instance = this;

        this.settings = new Settings(this);
        this.settings.load();

        this.currency = new BasicCurrency(this.settings);
    }

    @Override
    public void onEnable() {
        if (instance == null) {
            this.getLogger().warning("Plugin was not loaded. Loading it now...");
            this.onLoad();
        }

        if (this.enabled) {
            this.getLogger().severe("Tried to enable plugin twice. Plugin is already enabled.");
            return;
        }

        this.enabled = true;
    }

    @Override
    public void onDisable() {
        if (!this.enabled) {
            this.getLogger().severe("Tried to disable plugin twice. Plugin is already disabled.");
            return;
        }

        this.settings = null;
        this.storage = null;
        this.enabled = false;

        instance = null;
    }

    @Override
    public void reloadConfig() {
        this.settings.load();
    }

    @Override
    public void saveConfig() {
        /*
         * Disabled, because it is not intended to save the config file, as this breaks the comments.
         */
    }




    public static void debug(@NotNull final String message, @Nullable final Object... parameters) {
        Preconditions.checkNotNull(instance);

        if (instance.getSettings().isDebug()) {
            instance.getLogger().info(String.format(message, parameters));
        }
    }

    public static void info(@NotNull final String message, @Nullable final Object... parameters) {
        log(Level.INFO, message, parameters);
    }

    public static void warn(@NotNull final String message, @Nullable final Object... parameters) {
        log(Level.WARNING, message, parameters);
    }

    public static void log(@NotNull final Level level, @NotNull final String message,
                           @Nullable final Object... parameters) {
        Preconditions.checkNotNull(instance);
        instance.getLogger().log(level, String.format(message, parameters));
    }
}
