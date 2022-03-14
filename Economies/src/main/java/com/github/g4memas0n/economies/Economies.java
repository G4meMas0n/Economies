package com.github.g4memas0n.economies;

import com.github.g4memas0n.economies.config.Settings;
import com.github.g4memas0n.economies.storage.StorageManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * The Economies main class.
 *
 * @author G4meMas0n
 * @since Release 1.0.0
 */
public final class Economies extends JavaPlugin {

    private StorageManager storage;
    private Settings settings;

    private boolean loaded;
    private boolean enabled;

    public @NotNull StorageManager getStorage() {
        return this.storage;
    }

    public @NotNull Settings getSettings() {
        return this.settings;
    }

    @Override
    public void onLoad() {
        if (this.loaded) {
            this.getLogger().severe("Tried to load plugin twice. Plugin is already loaded.");
            return;
        }

        this.settings = new Settings(this);
        //TODO Create storage manager.
        this.settings.load();
        this.loaded = true;
    }

    @Override
    public void onEnable() {
        if (this.enabled) {
            this.getLogger().severe("Tried to enable plugin twice. Plugin is already enabled.");
            return;
        }

        if (!this.loaded) {
            this.getLogger().warning("Plugin was not loaded. Loading it now...");
            this.onLoad();
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
        this.loaded = false;
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
}
