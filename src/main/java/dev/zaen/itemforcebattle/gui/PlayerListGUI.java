package dev.zaen.itemforcebattle.gui;

import dev.zaen.itemforcebattle.BetterItemForceBattle;
import dev.zaen.itemforcebattle.managers.GameManager;
import dev.zaen.itemforcebattle.managers.PlayerData;
import dev.zaen.itemforcebattle.utils.ColorUtils;
import dev.zaen.itemforcebattle.utils.Colors;
import dev.zaen.itemforcebattle.utils.TextUtil;
import dev.zaen.itemforcebattle.utils.Unicodes;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class PlayerListGUI {

    public enum FilterOption {
        BY_POINTS_DESC("ᴍᴇɪsᴛᴇ ᴘᴜɴᴋᴛᴇ"),
        BY_POINTS_ASC("ᴡᴇɴɪɢsᴛᴇ ᴘᴜɴᴋᴛᴇ"),
        ALPHABETICAL("ᴀʟᴘʜᴀʙᴇᴛɪsᴄʜ");

        private final String displayName;
        FilterOption(String displayName) { this.displayName = displayName; }
        public String getDisplayName() { return displayName; }

        public FilterOption next() {
            FilterOption[] v = FilterOption.values();
            return v[(this.ordinal() + 1) % v.length];
        }
    }

    private static final UUID STEVE_UUID = UUID.fromString("8667ba71-b85a-4004-af54-457a9734eed7");

    private final BetterItemForceBattle plugin;
    private final GameManager gameManager;
    private FileConfiguration guiConfig;

    public PlayerListGUI(BetterItemForceBattle plugin) {
        this.plugin = plugin;
        this.gameManager = plugin.getGameManager();
        loadConfig();
    }

    public void reload() { loadConfig(); }

    private void loadConfig() {
        File guiFile = new File(plugin.getDataFolder(), "GUI.yml");
        if (!guiFile.exists()) plugin.saveResource("GUI.yml", false);
        guiConfig = YamlConfiguration.loadConfiguration(guiFile);
    }

    public void openGUI(Player player) {
        openGUI(player, FilterOption.BY_POINTS_DESC);
    }

    public void openGUI(Player player, FilterOption filter) {
        if (!gameManager.isGameRunning()) {
            player.sendMessage(ColorUtils.colorize(
                plugin.getMessageManager().getPrefix()
                + Colors.RED.getHex() + "<b>" + Unicodes.HEAVY_CROSS_MARK.getString() + "</b></color> <white>ᴋᴇɪɴ sᴘɪᴇʟ ᴀᴋᴛɪᴠ!</white>"));
            return;
        }

        PaginatedGui gui = dev.triumphteam.gui.guis.Gui.paginated()
            .title(buildTitle(filter))
            .rows(6)
            .pageSize(45)
            .disableAllInteractions()
            .create();

        populateItems(gui, filter, player);
        gui.open(player);
    }

    private Component buildTitle(FilterOption filter) {
        return TextUtil.parse("<!i><white>ɪᴛᴇᴍʙᴀᴛᴛʟᴇ</white> <dark_gray>"
            + Unicodes.ARROW.getString() + "</dark_gray> "
            + Colors.BLUE.getHex() + filter.getDisplayName() + "</color>");
    }

    private void populateItems(PaginatedGui gui, FilterOption filter, Player player) {
        gui.clearPageItems();

        List<Map.Entry<UUID, PlayerData>> players = getFilteredPlayers(filter);
        for (int i = 0; i < players.size(); i++) {
            Map.Entry<UUID, PlayerData> entry = players.get(i);
            Player target = Bukkit.getPlayer(entry.getKey());
            gui.addItem(createPlayerHead(target, i + 1, entry.getValue().getPoints()));
        }

        // Bottom bar filler
        GuiItem filler = createFiller();
        for (int slot : new int[]{45, 46, 47, 51, 52}) {
            gui.setItem(slot, filler);
        }

        updateNavigation(player, gui, filter);
    }

    private void refreshGUI(Player player, PaginatedGui gui, FilterOption filter) {
        gui.updateTitle(buildTitle(filter));
        populateItems(gui, filter, player);
        gui.update();
    }

    private List<Map.Entry<UUID, PlayerData>> getFilteredPlayers(FilterOption filter) {
        List<Map.Entry<UUID, PlayerData>> all = gameManager.getSortedPlayers(); // already desc
        return switch (filter) {
            case BY_POINTS_ASC -> all.stream()
                .sorted(Comparator.comparingInt(e -> e.getValue().getPoints()))
                .collect(Collectors.toList());
            case ALPHABETICAL -> all.stream()
                .sorted((a, b) -> {
                    String na = Optional.ofNullable(Bukkit.getPlayer(a.getKey())).map(Player::getName).orElse("");
                    String nb = Optional.ofNullable(Bukkit.getPlayer(b.getKey())).map(Player::getName).orElse("");
                    return na.compareToIgnoreCase(nb);
                })
                .collect(Collectors.toList());
            default -> all; // BY_POINTS_DESC already sorted
        };
    }

    private void updateNavigation(Player player, PaginatedGui gui, FilterOption filter) {
        boolean hasPrev = gui.getCurrentPageNum() > 1;
        boolean hasNext = gui.getPagesNum() > gui.getCurrentPageNum();

        gui.setItem(48, createNavItem(
            hasPrev ? Material.LIME_CANDLE : Material.RED_CANDLE,
            "<!i>" + (hasPrev ? Colors.GREEN.getHex() : Colors.RED.getHex()) + "ᴠᴏʀʜᴇʀɪɢᴇ sᴇɪᴛᴇ</color>",
            event -> {
                event.setCancelled(true);
                if (gui.getCurrentPageNum() > 1) {
                    gui.previous();
                    playPageSound(player);
                    updateNavigation(player, gui, filter);
                    gui.update();
                } else {
                    playDenySound(player);
                }
            }
        ));

        gui.setItem(49, createInfoItem(gui));

        gui.setItem(50, createNavItem(
            hasNext ? Material.LIME_CANDLE : Material.RED_CANDLE,
            "<!i>" + (hasNext ? Colors.GREEN.getHex() : Colors.RED.getHex()) + "ɴäᴄʜsᴛᴇ sᴇɪᴛᴇ</color>",
            event -> {
                event.setCancelled(true);
                if (gui.getPagesNum() > gui.getCurrentPageNum()) {
                    gui.next();
                    playPageSound(player);
                    updateNavigation(player, gui, filter);
                    gui.update();
                } else {
                    playDenySound(player);
                }
            }
        ));

        gui.setItem(53, createFilterItem(player, gui, filter));
    }

    private GuiItem createFilterItem(Player player, PaginatedGui gui, FilterOption currentFilter) {
        ItemStack item = new ItemStack(Material.HOPPER);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(TextUtil.parse("<!i>" + Colors.BLUE.getHex() + "ꜰɪʟᴛᴇʀ</color>"));
            List<Component> lore = new ArrayList<>();
            lore.add(Component.empty());
            for (FilterOption option : FilterOption.values()) {
                boolean active = option == currentFilter;
                String line = active
                    ? "<!i>" + Colors.BLUE.getHex() + Unicodes.ARROW.getString() + " " + option.getDisplayName() + "</color>"
                    : "<!i><grey>  " + option.getDisplayName() + "</grey>";
                lore.add(TextUtil.parse(line));
            }
            lore.add(Component.empty());
            lore.add(TextUtil.parse("<!i>" + Colors.BLUE.getHex() + "ᴋʟɪᴄᴋᴇɴ ᴜᴍ ᴢᴜ ꜱᴏʀᴛɪᴇʀᴇɴ</color>"));
            meta.lore(lore);
            item.setItemMeta(meta);
        }

        return ItemBuilder.from(item).asGuiItem(event -> {
            event.setCancelled(true);
            playClickSound(player);
            FilterOption next = currentFilter.next();
            refreshGUI(player, gui, next);
        });
    }

    private GuiItem createInfoItem(PaginatedGui gui) {
        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(TextUtil.parse("<!i>" + Colors.YELLOW.getHex() + "ɪɴꜰᴏʀᴍᴀᴛɪᴏɴᴇɴ</color>"));
            List<Component> lore = new ArrayList<>();
            lore.add(Component.empty());
            lore.add(TextUtil.parse("<!i><grey>" + Colors.BLUE.getHex() + Unicodes.ROUND_DOT.getString() + "</color> sᴇɪᴛᴇ "
                + Colors.BLUE.getHex() + gui.getCurrentPageNum() + "</color> / "
                + Colors.BLUE.getHex() + gui.getPagesNum() + "</color></grey>"));
            lore.add(Component.empty());
            meta.lore(lore);
            item.setItemMeta(meta);
        }
        return ItemBuilder.from(item).asGuiItem(e -> {
            e.setCancelled(true);
            playDenySound((Player) e.getWhoClicked());
        });
    }

    private GuiItem createPlayerHead(Player target, int rank, int points) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        if (meta == null) return ItemBuilder.from(head).asGuiItem(e -> e.setCancelled(true));

        meta.setOwningPlayer(target != null ? target : Bukkit.getOfflinePlayer(STEVE_UUID));

        String playerName = target != null ? target.getName() : "N/A";
        String nameRaw = guiConfig.getString("player-head.name", Colors.BLUE.getHex() + "{player}</color>");
        meta.displayName(TextUtil.parse("<!i>" + applyPlaceholders(nameRaw, playerName, rank, points)));

        List<Component> lore = new ArrayList<>();
        for (String line : guiConfig.getStringList("player-head.lore")) {
            lore.add(TextUtil.parse("<!i>" + applyPlaceholders(line, playerName, rank, points)));
        }
        meta.lore(lore);
        head.setItemMeta(meta);

        return ItemBuilder.from(head).asGuiItem(e -> {
            e.setCancelled(true);
            if (e.isLeftClick() && target != null) {
                Player clicker = (Player) e.getWhoClicked();
                clicker.closeInventory();
                playClickSound(clicker);
                clicker.teleportAsync(target.getLocation()).thenAccept(success -> {
                    if (!success) clicker.sendMessage(ColorUtils.colorize(
                        plugin.getMessageManager().getPrefix() + Colors.RED.getHex() + "ᴛᴇʟᴇᴘᴏʀᴛ ꜰᴇʜʟɢᴇsᴄʜʟᴀɢᴇɴ.</color>"));
                });
            }
        });
    }

    private GuiItem createNavItem(Material material, String nameRaw,
                                   dev.triumphteam.gui.components.GuiAction<org.bukkit.event.inventory.InventoryClickEvent> action) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(TextUtil.parse(nameRaw));
            List<Component> lore = new ArrayList<>();
            lore.add(Component.empty());
            lore.add(TextUtil.parse("<!i><grey>" + Colors.BLUE.getHex() + Unicodes.ROUND_DOT.getString() + "</color> ᴋʟɪᴄᴋᴇ ᴢᴜᴍ ɴᴀᴠɪɢɪᴇʀᴇɴ</grey>"));
            lore.add(Component.empty());
            meta.lore(lore);
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
        if (meta != null) { meta.displayName(Component.space()); item.setItemMeta(meta); }
        return ItemBuilder.from(item).asGuiItem(e -> e.setCancelled(true));
    }

    private void playPageSound(Player player) {
        player.playSound(Sound.sound(Key.key("item.book.page_turn"), Sound.Source.MASTER, 1f, 1f));
    }

    private void playDenySound(Player player) {
        player.playSound(Sound.sound(Key.key("block.wood.step"), Sound.Source.MASTER, 1f, 1.9f));
    }

    private void playClickSound(Player player) {
        player.playSound(Sound.sound(Key.key("block.lever.click"), Sound.Source.MASTER, 1f, 1f));
    }

    private String applyPlaceholders(String text, String player, int rank, int points) {
        return text.replace("{player}", player)
                   .replace("{rank}", String.valueOf(rank))
                   .replace("{points}", String.valueOf(points));
    }
}
