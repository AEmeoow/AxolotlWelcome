package axolotlstudios.inventory;

import axolotlstudios.AxolotlWelcome;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class AxolotlMenu implements Listener {

    public static void openGUI(Player player) {
        Inventory gui = Bukkit.createInventory(player, 27, "⋙ AXOLOTLSTUDIOS INFO ⋘");

        ItemStack infoOption = new ItemStack(Material.PAPER);
        ItemMeta infoOptionMeta = infoOption.getItemMeta();
        infoOptionMeta.setDisplayName(AxolotlWelcome.c("&f⋙ &d&lAXOLOTL&f&lSTUDIOS &f⋘"));
        infoOptionMeta.setLore(Arrays.asList(
                "",
                AxolotlWelcome.c("&7- &fThe purpose of this plugin is to provide rewards"),
                AxolotlWelcome.c("&7- &fto players who welcome players who join the network."),
                "",
                AxolotlWelcome.c("&7- &fThe current commands of this plugin, only accessible via the node"),
                AxolotlWelcome.c("&7- &eaxolotlstudios.admin &fis &e/axolotlwelcome info &fand &e/axolotlwelcome reload.")
        ));

        infoOption.setItemMeta(infoOptionMeta);
        gui.setItem(13, infoOption);

        ItemStack grayGlass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE, 1, (short) 7);
        ItemMeta grayGlassMeta = grayGlass.getItemMeta();
        grayGlassMeta.setDisplayName("");
        grayGlassMeta.setLocalizedName(" ");
        grayGlass.setItemMeta(grayGlassMeta);

        for (int i = 0; i < 27; i++) {
            if (gui.getItem(i) == null) {
                gui.setItem(i, grayGlass);
            }
        }

        player.openInventory(gui);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (!event.getView().getTitle().equals("⋙ AXOLOTLSTUDIOS INFO ⋘")) {
            return;
        }

        event.setCancelled(true);
        ItemStack clickedItem = event.getCurrentItem();

        if (clickedItem != null && clickedItem.hasItemMeta()) {
            ItemMeta clickedItemMeta = clickedItem.getItemMeta();
            if (clickedItem != null && clickedItem.hasItemMeta()) {
                if (event.getSlot() == (13)) {
                    player.closeInventory();
                    player.sendMessage("");
                    player.sendMessage(AxolotlWelcome.c("#FB5252      &d&lAXOLOTL&f&lWELCOME &e&lINFORMATION:&r #FB5252      "));
                    player.sendMessage("");
                    player.sendMessage(AxolotlWelcome.c("     &8&l➥ #FB9DE9Plugin created by &dAxolotl&fStudios."));
                    player.sendMessage("");
                    player.sendMessage(AxolotlWelcome.c("&7➟ &fJoin the discord at &ddiscord.gg/axolotlplugins."));
                    player.sendMessage("");

                }
            }
        }
    }
}