package dev.zaen.itemforcebattle.gui;

import dev.zaen.itemforcebattle.BetterItemForceBattle;
import dev.zaen.itemforcebattle.managers.GameManager;
import dev.zaen.itemforcebattle.managers.PlayerData;
import dev.zaen.itemforcebattle.utils.ColorUtils;
import dev.zaen.itemforcebattle.utils.TextUtil;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.io.File;
import java.util.*;

public class PlayerListGUI {

    private static final UUID STEVE_UUID = UUID.fromString("8667ba71-b85a-4004-af54-457a9734eed7");

    private final BetterItemForceBattle plugin;
    private final GameManager gameManager;
    private FileConfiguration guiConfig;

    public PlayerListGUI(BetterItemForceBattle plugin) {
        this.plugin = plugin;
        this.gameManager = plugin.getGameManager();
        loadConfig();
    }

    public void reload() {
        loadConfig();
    }

    private void loadConfig() {
        File guiFile = new File(plugin.getDataFolder(), "GUI.yml");
        if (!guiFile.exists()) plugin.saveResource("GUI.yml", false);
        guiConfig = YamlConfiguration.loadConfiguration(guiFile);
    }

    public void openGUI(Player player) {
        if (!gameManager.isGameRunning()) {
            player.sendMessage(ColorUtils.colorize(
                plugin.getMessageManager().getPrefix() + "<#ff0000><b>❌</b> <white>ᴋᴇɪɴ sᴘɪᴇʟ ᴀᴋᴛɪᴠ!"));
            return;
        }

        List<Map.Entry<UUID, PlayerData>> sortedPlayers = gameManager.getSortedPlayers();

        PaginatedGui gui = dev.triumphteam.gui.guis.Gui.paginated()
            .title(ColorUtils.colorize("<white>ɪᴛᴇᴍʙᴀᴛᴛʟᴇ"))
            .rows(6)
            .pageSize(45)
            .disableAllInteractions()
            .create();

        for (int i = 0; i < sortedPlayers.size(); i++) {
            Map.Entry<UUID, PlayerData> entry = sortedPlayers.get(i);
            Player target = Bukkit.getPlayer(entry.getKey());
            gui.addItem(createPlayerHead(target, i + 1, entry.getValue().getPoints(), false));
        }

        GuiItem filler = createFiller();
        for (int slot = 45; slot <= 53; slot++) {
            gui.setItem(slot, filler);
        }

        gui.setItem(48, createNavItem(
            guiConfig.getString("pagination.previous-page.material", "RED_CANDLE"),
            guiConfig.getString("pagination.previous-page.name", "<dark_gray>« <#478ED2>ᴠᴏʀʜᴇʀɪɢᴇ sᴇɪᴛᴇ"),
            event -> { event.setCancelled(true); gui.previous(); gui.update(); }
        ));

        if (!sortedPlayers.isEmpty()) {
            Map.Entry<UUID, PlayerData> top = sortedPlayers.get(0);
            Player topPlayer = Bukkit.getPlayer(top.getKey());
            gui.setItem(49, createPlayerHead(topPlayer, 1, top.getValue().getPoints(), true));
        }

        gui.setItem(50, createNavItem(
            guiConfig.getString("pagination.next-page.material", "RED_CANDLE"),
            guiConfig.getString("pagination.next-page.name", "<#478ED2>ɴäᴄʜsᴛᴇ sᴇɪᴛᴇ <dark_gray>»"),
            event -> { event.setCancelled(true); gui.next(); gui.update(); }
        ));

        gui.open(player);
    }

    private GuiItem createPlayerHead(Player target, int rank, int points, boolean isTop) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        if (meta == null) return ItemBuilder.from(head).asGuiItem(e -> e.setCancelled(true));

        if (target != null) {
            meta.setOwningPlayer(target);
        } else {
            meta.setOwningPlayer(Bukkit.getOfflinePlayer(STEVE_UUID));
        }

        String nameKey = isTop ? "top-player-head.name" : "player-head.name";
        String loreKey = isTop ? "top-player-head.lore" : "player-head.lore";
        String playerName = target != null ? target.getName() : "N/A";

        String nameRaw = guiConfig.getString(nameKey, isTop ? "<#FFD700>🏆 {player}" : "<white>{player}");
        nameRaw = applyPlaceholders(nameRaw, playerName, rank, points);
        meta.displayName(TextUtil.parse(nameRaw));

        List<String> loreRaw = guiConfig.getStringList(loreKey);
        List<Component> lore = new ArrayList<>();
        for (String line : loreRaw) {
            lore.add(TextUtil.parse(applyPlaceholders(line, playerName, rank, points)));
        }
        meta.lore(lore);
        head.setItemMeta(meta);

        return ItemBuilder.from(head).asGuiItem(e -> {
            e.setCancelled(true);
            if (e.isLeftClick() && target != null) {
                Player viewer = (Player) e.getWhoClicked();
                viewer.closeInventory();
                viewer.teleportAsync(target.getLocation()).thenAccept(success -> {
                    if (!success) viewer.sendMessage(ColorUtils.colorize(
                        plugin.getMessageManager().getPrefix() + " <red>ᴛᴇʟᴇᴘᴏʀᴛ ꜰᴇʜʟɢᴇsᴄʜʟᴀɢᴇɴ."));
                });
            }
        });
    }

    private GuiItem createNavItem(String materialName, String nameRaw,
                                   dev.triumphteam.gui.components.GuiAction<org.bukkit.event.inventory.InventoryClickEvent> action) {
        Material mat;
        try { mat = Material.valueOf(materialName.toUpperCase()); }
        catch (IllegalArgumentException e) { mat = Material.YELLOW_CANDLE; }

        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(TextUtil.parse(nameRaw));
            item.setItemMeta(meta);
        }
        return ItemBuilder.from(item).asGuiItem(action);
    }

    private GuiItem createFiller() {
        String matName = guiConfig.getString("filler.material", "BLACK_STAINED_GLASS_PANE");
        Material mat;
        try { mat = Material.valueOf(matName.toUpperCase()); }
        catch (IllegalArgumentException e) { mat = Material.BLACK_STAINED_GLASS_PANE; }

        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(Component.space());
            item.setItemMeta(meta);
        }
        return ItemBuilder.from(item).asGuiItem(e -> e.setCancelled(true));
    }

    private String applyPlaceholders(String text, String player, int rank, int points) {
        return text.replace("{player}", player)
                   .replace("{rank}", String.valueOf(rank))
                   .replace("{points}", String.valueOf(points));
    }
}
