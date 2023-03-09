package axolotlstudios.utils;

import axolotlstudios.AxolotlWelcome;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AxolotlWelcomePlaceholderHook extends PlaceholderExpansion {

    private final AxolotlWelcome plugin;

    public AxolotlWelcomePlaceholderHook(AxolotlWelcome plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "axolotlwelcome";
    }

    @Override
    public @NotNull String getAuthor() {
        return "AxolotlStudios";
    }

    @Override
    public @NotNull String getVersion() {
        return "1";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        if (player == null) return null; // Always do this to start

        if (params.equalsIgnoreCase("count")) { // %axolotlwelcome_count%
            return String.valueOf(this.plugin.getDataConfig().getInt(player.getUniqueId().toString(), 0));
        }

        return null;
    }
}
