package dev.zaen.itemforcebattle.managers;

import dev.zaen.itemforcebattle.BetterItemForceBattle;
import dev.zaen.itemforcebattle.config.ConfigManager;
import dev.zaen.itemforcebattle.config.MessageManager;
import dev.zaen.itemforcebattle.utils.ColorUtils;
import dev.zaen.itemforcebattle.utils.Colors;
import dev.zaen.itemforcebattle.utils.Unicodes;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.translation.GlobalTranslator;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

public class GameManager {

    private final BetterItemForceBattle plugin;
    private final ConfigManager configManager;
    private final MessageManager messageManager;

    private boolean gameRunning = false;
    private boolean countdownActive = false;
    private Map<UUID, PlayerData> playerDataMap;
    private BukkitTask gameTimer;
    private BukkitTask actionbarTask;
    private int remainingSeconds;

    public GameManager(BetterItemForceBattle plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
        this.messageManager = plugin.getMessageManager();
        this.playerDataMap = new HashMap<>();
    }

    public boolean startGame() {
        if (gameRunning || countdownActive) return false;
        Location spawn = configManager.getSpawnLocation();
        if (spawn == null || spawn.getWorld() == null) return false;

        countdownActive = true;
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.teleport(spawn);
            player.getInventory().clear();
            for (PotionEffect effect : player.getActivePotionEffects()) {
                player.removePotionEffect(effect.getType());
            }
            playerDataMap.put(player.getUniqueId(), new PlayerData(player.getUniqueId(), configManager.getSkipsPerPlayer()));
        }

        World world = spawn.getWorld();
        world.getWorldBorder().reset();

        startCountdown();
        return true;
    }

    private void startCountdown() {
        final int countdownTime = configManager.getCountdownTime();
        new BukkitRunnable() {
            int countdown = countdownTime;
            @Override
            public void run() {
                if (countdown > 0) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (!playerDataMap.containsKey(player.getUniqueId())) continue;
                        player.showTitle(Title.title(
                            ColorUtils.colorize(Colors.BLUE.getHex() + "<b>sᴘɪᴇʟ sᴛᴀʀᴛᴇᴛ ɪɴ:</b></color>"),
                            ColorUtils.colorize("<white><b>" + countdown + "</b></white>"),
                            Title.Times.times(Duration.ZERO, Duration.ofMillis(1100), Duration.ZERO)
                        ));
                        if (configManager.isSoundsEnabled()) {
                            player.playSound(player.getLocation(), configManager.getCountdownTick(), 1.0f, 1.0f);
                        }
                    }
                    countdown--;
                } else {
                    cancel();
                    actuallyStartGame();
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    private void actuallyStartGame() {
        countdownActive = false;
        gameRunning = true;
        remainingSeconds = configManager.getEventDuration() * 60;

        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerData data = playerDataMap.get(player.getUniqueId());
            if (data == null) continue;

            player.showTitle(Title.title(
                ColorUtils.colorize("<b><#C539EC>ɪᴛᴇᴍʙᴀᴛᴛʟᴇ</b>"),
                ColorUtils.colorize("<white>ʟᴏs ɢᴇʜᴛs!"),
                Title.Times.times(Duration.ZERO, Duration.ofSeconds(2), Duration.ofMillis(500))
            ));

            if (configManager.isSoundsEnabled()) {
                player.playSound(player.getLocation(), configManager.getCountdownGo(), 1.0f, 1.0f);
            }

            giveStarterItems(player);
            assignNewItem(player);
        }

        startGameTimer();
        startActionbarTask();
        plugin.getScoreboardManager().createScoreboards();
        Bukkit.broadcast(messageManager.getEventStarted());
    }

    private void giveStarterItems(Player player) {
        for (Map.Entry<Integer, ItemStack> entry : configManager.getStarterItems().entrySet()) {
            player.getInventory().setItem(entry.getKey(), entry.getValue().clone());
        }
    }

    public void assignNewItem(Player player) {
        PlayerData data = playerDataMap.get(player.getUniqueId());
        if (data == null) return;

        Material newItem = plugin.getBlacklistManager().getRandomItem(player.getUniqueId());
        data.setCurrentItem(newItem);

        plugin.getItemDisplayManager().updateDisplay(player, newItem);

        String itemName = formatItemName(newItem);
        player.sendMessage(messageManager.getNewItem(itemName));
        plugin.getScoreboardManager().updateScoreboard(player);
    }

    public void onItemPickup(Player player, Material material) {
        if (!gameRunning) return;
        PlayerData data = playerDataMap.get(player.getUniqueId());
        if (data == null || data.getCurrentItem() == null) return;
        if (data.getCurrentItem() != material) return;

        data.addPoint();
        plugin.getBlacklistManager().markCollected(player.getUniqueId(), material);
        player.sendMessage(messageManager.getItemCollected(formatItemName(material)));

        if (configManager.isSoundsEnabled()) {
            player.playSound(player.getLocation(), configManager.getItemCollected(), 1.0f, 1.0f);
        }

        assignNewItem(player);
        plugin.getScoreboardManager().updateAllScoreboards();
    }

    public boolean useSkip(Player player) {
        if (!gameRunning) return false;
        PlayerData data = playerDataMap.get(player.getUniqueId());
        if (data == null) return false;

        if (!data.hasSkipsRemaining()) {
            player.sendMessage(messageManager.getNoSkipsLeft());
            return false;
        }

        Material skippedItem = data.getCurrentItem();
        data.useSkip();

        if (skippedItem != null) player.getInventory().addItem(new ItemStack(skippedItem, 1));
        player.sendMessage(messageManager.getSkipUsed(formatItemName(skippedItem), data.getSkipsRemaining()));

        if (configManager.isSoundsEnabled()) {
            player.playSound(player.getLocation(), configManager.getSkipUsed(), 1.0f, 1.0f);
        }

        assignNewItem(player);
        return true;
    }

    private void startGameTimer() {
        gameTimer = new BukkitRunnable() {
            @Override
            public void run() {
                if (remainingSeconds <= 0) {
                    cancel();
                    endGame();
                    return;
                }
                remainingSeconds--;
                plugin.getScoreboardManager().updateAllScoreboards();
            }
        }.runTaskTimer(plugin, 20L, 20L);
    }

    private void startActionbarTask() {
        actionbarTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (!gameRunning) { cancel(); return; }
                for (Player player : Bukkit.getOnlinePlayers()) {
                    PlayerData data = playerDataMap.get(player.getUniqueId());
                    if (data == null || data.getCurrentItem() == null) continue;
                    player.sendActionBar(ColorUtils.colorize("<#FFD700>🎯 <white>" + formatItemName(data.getCurrentItem())));
                }
            }
        }.runTaskTimer(plugin, 0L, 10L);
    }

    public void endGame() {
        stopGame(false);
    }

    public void stopGame(boolean force) {
        if (!gameRunning && !countdownActive) return;

        gameRunning = false;
        countdownActive = false;

        if (gameTimer != null) { gameTimer.cancel(); gameTimer = null; }
        if (actionbarTask != null) { actionbarTask.cancel(); actionbarTask = null; }

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (playerDataMap.containsKey(player.getUniqueId())) {
                player.getInventory().clear();
            }
        }

        plugin.getItemDisplayManager().removeAllDisplays();
        plugin.getScoreboardManager().removeAllScoreboards();
        showLeaderboard();

        Location spawn = configManager.getSpawnLocation();
        if (spawn != null && spawn.getWorld() != null) {
            WorldBorder border = spawn.getWorld().getWorldBorder();
            border.setCenter(spawn);
            border.setSize(configManager.getEndBorderRadius() * 2);

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!playerDataMap.containsKey(player.getUniqueId())) continue;
                player.teleport(spawn);
                if (configManager.isSoundsEnabled()) {
                    player.playSound(player.getLocation(), configManager.getEventEnd(), 1.0f, 1.0f);
                }
            }
        }

        Bukkit.broadcast(force ? messageManager.getEventStopped() : messageManager.getEventEnded());
        plugin.getBlacklistManager().clearAllCollectedItems();
        playerDataMap.clear();
    }

    private void showLeaderboard() {
        List<Map.Entry<UUID, PlayerData>> sorted = playerDataMap.entrySet().stream()
            .sorted((a, b) -> Integer.compare(b.getValue().getPoints(), a.getValue().getPoints()))
            .collect(Collectors.toList());

        String b = Colors.BLUE.getHex();
        String y = Colors.YELLOW.getHex();
        String dot = Unicodes.ROUND_DOT.getString();
        String sep = b + "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</color>";

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(Component.empty());
            player.sendMessage(ColorUtils.colorize(sep));
            player.sendMessage(ColorUtils.colorize(
                "  <b>" + b + "ɪᴛᴇᴍ</color><grey>ʙᴀᴛᴛʟᴇ</grey></b> <dark_gray>" + Unicodes.ARROW.getString() + "</dark_gray> " + y + "ᴇʀɢᴇʙɴɪssᴇ</color>"));
            player.sendMessage(ColorUtils.colorize(sep));
            player.sendMessage(Component.empty());

            if (sorted.isEmpty()) {
                player.sendMessage(ColorUtils.colorize("<grey>  ᴋᴇɪɴᴇ sᴘɪᴇʟᴇʀᴅᴀᴛᴇɴ.</grey>"));
            } else {
                int rank = 1;
                for (Map.Entry<UUID, PlayerData> entry : sorted) {
                    Player p = Bukkit.getPlayer(entry.getKey());
                    String name = ColorUtils.toSmallCaps(p != null ? p.getName() : "Unbekannt");
                    int points = entry.getValue().getPoints();
                    String ptsLabel = ColorUtils.toSmallCaps("Punkte");

                    String line = switch (rank) {
                        case 1 -> "  <#FFD700>\uD83E\uDD47 <white><b>" + name + "</b></white> <dark_gray>" + Unicodes.ARROW.getString() + "</dark_gray> <#FFD700>" + points + " <grey>" + ptsLabel + "</grey></color>";
                        case 2 -> "  <#C0C0C0>\uD83E\uDD48 <white><b>" + name + "</b></white> <dark_gray>" + Unicodes.ARROW.getString() + "</dark_gray> <#C0C0C0>" + points + " <grey>" + ptsLabel + "</grey></color>";
                        case 3 -> "  <#CD7F32>\uD83E\uDD49 <white><b>" + name + "</b></white> <dark_gray>" + Unicodes.ARROW.getString() + "</dark_gray> <#CD7F32>" + points + " <grey>" + ptsLabel + "</grey></color>";
                        default -> "  <grey>" + b + dot + "</color> " + rank + ". <white>" + name + "</white> <dark_gray>" + Unicodes.ARROW.getString() + "</dark_gray> " + b + points + "</color> <grey>" + ptsLabel + "</grey>";
                    };
                    player.sendMessage(ColorUtils.colorize(line));
                    rank++;
                }
            }

            player.sendMessage(Component.empty());
            player.sendMessage(ColorUtils.colorize(sep));
            player.sendMessage(Component.empty());
        }
    }

    public String formatTime(int seconds) {
        return String.format("%02d:%02d", seconds / 60, seconds % 60);
    }

    public String formatItemName(Material material) {
        if (material == null) return "ᴜɴʙᴇᴋᴀɴɴᴛ";
        Component translated = GlobalTranslator.render(
            Component.translatable(material.translationKey()),
            java.util.Locale.ENGLISH
        );
        String plain = PlainTextComponentSerializer.plainText().serialize(translated);
        return ColorUtils.toSmallCaps(plain);
    }

    public boolean isGameRunning() { return gameRunning; }
    public boolean isCountdownActive() { return countdownActive; }
    public PlayerData getPlayerData(UUID uuid) { return playerDataMap.get(uuid); }
    public Map<UUID, PlayerData> getAllPlayerData() { return new HashMap<>(playerDataMap); }
    public int getRemainingSeconds() { return remainingSeconds; }

    public List<Map.Entry<UUID, PlayerData>> getSortedPlayers() {
        return playerDataMap.entrySet().stream()
            .sorted((a, b) -> Integer.compare(b.getValue().getPoints(), a.getValue().getPoints()))
            .collect(Collectors.toList());
    }

    public int getPlayerRank(UUID uuid) {
        List<Map.Entry<UUID, PlayerData>> sorted = getSortedPlayers();
        for (int i = 0; i < sorted.size(); i++) {
            if (sorted.get(i).getKey().equals(uuid)) return i + 1;
        }
        return -1;
    }

    public boolean addPlayer(Player player) {
        if (!gameRunning) return false;
        if (playerDataMap.containsKey(player.getUniqueId())) return false;
        playerDataMap.put(player.getUniqueId(), new PlayerData(player.getUniqueId(), configManager.getSkipsPerPlayer()));
        giveStarterItems(player);
        assignNewItem(player);
        plugin.getScoreboardManager().createScoreboard(player);
        plugin.getScoreboardManager().updateAllScoreboards();
        return true;
    }
}
