package axolotlstudios;

import axolotlstudios.inventory.AxolotlMenu;
import axolotlstudios.utils.AxolotlWelcomePlaceholderHook;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AxolotlWelcome extends JavaPlugin implements Listener {

    private final List<UUID> repliedPlayers = new ArrayList<>();
    private boolean isWelcomeActive = false;

    private FileConfiguration config;

    private File dataFile;
    private YamlConfiguration dataConfig;

    private boolean broadcastEnabled;

    private String prefix;

    private static final Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");

    @Override
    public void onEnable() {
        // Load config
        saveDefaultConfig();
        config = getConfig();

        // Register events
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new AxolotlMenu(), this);

        // Read config options
        broadcastEnabled = config.getBoolean("welcome-enabled");
        prefix = config.getString("prefix");

        // Register the placeholder api hook
        if (this.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new AxolotlWelcomePlaceholderHook(this).register();
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        if (broadcastEnabled) {
            String welcomeMsg = config.getString("welcome-message", "")
                    .replace("{player}", player.getName());
            Bukkit.broadcastMessage(c(prefix + " &8⋙" + " " + welcomeMsg));
        }

        if (!this.isWelcomeActive) {
            this.isWelcomeActive = true;
            Bukkit.getScheduler().runTaskLater(this, () -> {
                this.isWelcomeActive = false;
                this.repliedPlayers.clear();
            },20L * 5);
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        final Player player = event.getPlayer();
        if (!this.isWelcomeActive) return;
        if (this.repliedPlayers.contains(player.getUniqueId())) return;

        String message = event.getMessage();
        if (config.getStringList("defined-words").stream().noneMatch(message::contains)) {
            return;
        }

        this.repliedPlayers.add(player.getUniqueId());
        player.sendMessage(c(prefix + " &f⋙ &aReward received, make sure to keep welcoming players!"));

        String command = config.getString("command-to-run", "")
                .replace("{player}", player.getName());
        Bukkit.getScheduler().runTask(this, () ->
                getServer().dispatchCommand(getServer().getConsoleSender(), command));

        // Up the variable
        this.upVariable(player);
    }


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("axolotlstudios.admin")) {
            sender.sendMessage(c(prefix + " &f⋙ &cPermission denied.."));
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("axolotlwelcome")) {
            if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                reloadConfig();
                config = getConfig();
                broadcastEnabled = config.getBoolean("welcome-enabled");
                prefix = config.getString("prefix");
                sender.sendMessage(c(prefix + " &f⋙ &aConfig reloaded."));
                return true;
            } else if (args.length == 1 && args[0].equalsIgnoreCase("info")) {
                AxolotlMenu.openGUI((Player) sender);
            }
        }
        return false;
    }


    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.add("reload");
            completions.add("info");
        }

        return completions;
    }

    public YamlConfiguration getDataConfig() {
        // if it's not null, aka, it's already loaded, just return
        if (this.dataConfig != null) return this.dataConfig;

        try {
            // Make sure the yml file exists on the machine
            this.dataFile = new File(this.getDataFolder(), "data.yml");
            if (!dataFile.exists()) dataFile.createNewFile();

            // Load the config from the file
            this.dataConfig = YamlConfiguration.loadConfiguration(dataFile);
            return this.dataConfig;
        } catch (Throwable throwable) {
            throw new IllegalStateException(throwable);
        }
    }

    public void saveDataConfig() {
        try {
            // Make sure the file exists, remove if you don't want this ig
            if (this.dataConfig == null) this.getDataConfig();

            // Save the actual config to the file
            this.dataConfig.save(this.dataFile);
        }catch (Throwable throwable) {
            throw new IllegalStateException(throwable);
        }
    }
    
    public void upVariable(Player player) {
        String key = player.getUniqueId().toString();

        YamlConfiguration config = this.getDataConfig();
        int currentVariable = config.getInt(key, 0);

        config.set(key, currentVariable + 1);
        this.saveDataConfig();
    }

    public static String getHex(String msg) {
        Matcher matcher = pattern.matcher(msg);
        while (matcher.find()) {
            String color = msg.substring(matcher.start(), matcher.end());
            msg = msg.replace(color, net.md_5.bungee.api.ChatColor.of(color) + "");
            matcher = pattern.matcher(msg);
        }
        return net.md_5.bungee.api.ChatColor.translateAlternateColorCodes('&', msg);
    }

    public static String c(String s){
        return ChatColor.translateAlternateColorCodes('&', getHex(s));
    }
}
