package dev.zaen.itemforcebattle.managers;

import dev.zaen.itemforcebattle.BetterItemForceBattle;
import dev.zaen.itemforcebattle.config.ConfigManager;
import dev.zaen.itemforcebattle.config.MessageManager;
import dev.zaen.itemforcebattle.utils.ColorUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
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

    /**
     * Startet das Item Force Battle Event
     */
    public boolean startGame() {
        if (gameRunning || countdownActive) {
            return false;
        }

        Location spawn = configManager.getSpawnLocation();
        if (spawn == null || spawn.getWorld() == null) {
            return false;
        }

        countdownActive = true;

        // Alle Online-Spieler vorbereiten
        Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        
        for (Player player : players) {
            // Teleportiere zum Spawn
            player.teleport(spawn);
            
            // Inventar leeren
            player.getInventory().clear();
            
            // Effekte entfernen
            for (PotionEffect effect : player.getActivePotionEffects()) {
                player.removePotionEffect(effect.getType());
            }
            
            // Freeze Effekt (Slowness 255 + Jump Boost 128 negativ = kann sich nicht bewegen)
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 20 * (configManager.getCountdownTime() + 1), 255, false, false, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, 20 * (configManager.getCountdownTime() + 1), 128, false, false, false));
            
            // Spieler-Daten initialisieren
            PlayerData data = new PlayerData(player.getUniqueId(), configManager.getSkipsPerPlayer());
            playerDataMap.put(player.getUniqueId(), data);
        }

        // World Border entfernen
        World world = spawn.getWorld();
        WorldBorder border = world.getWorldBorder();
        border.reset();

        // Countdown starten
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
                    // Countdown anzeigen
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (playerDataMap.containsKey(player.getUniqueId())) {
                            // Title anzeigen
                            Title title = Title.title(
                                ColorUtils.colorize(configManager.getColorPrimary() + String.valueOf(countdown)),
                                Component.empty(),
                                Title.Times.times(Duration.ZERO, Duration.ofMillis(1100), Duration.ZERO)
                            );
                            player.showTitle(title);
                            
                            // Sound
                            if (configManager.isSoundsEnabled()) {
                                player.playSound(player.getLocation(), configManager.getCountdownTick(), 1.0f, 1.0f);
                            }
                        }
                    }
                    countdown--;
                } else {
                    // Countdown fertig - Spiel starten
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

            // Freeze entfernen
            player.removePotionEffect(PotionEffectType.SLOWNESS);
            player.removePotionEffect(PotionEffectType.JUMP_BOOST);

            // "LOS GEHT'S!" anzeigen
            Title title = Title.title(
                messageManager.getCountdownGo(),
                Component.empty(),
                Title.Times.times(Duration.ZERO, Duration.ofSeconds(1), Duration.ofMillis(500))
            );
            player.showTitle(title);

            // Sound
            if (configManager.isSoundsEnabled()) {
                player.playSound(player.getLocation(), configManager.getCountdownGo(), 1.0f, 1.0f);
            }

            // Starter Items geben
            giveStarterItems(player);

            // Erstes Item zuweisen
            assignNewItem(player);
        }

        // Game Timer starten
        startGameTimer();

        // Actionbar Task starten
        startActionbarTask();

        // Scoreboard für alle erstellen
        plugin.getScoreboardManager().createScoreboards();

        // Nachricht senden
        Bukkit.broadcast(messageManager.getEventStarted());
    }

    private void giveStarterItems(Player player) {
        Map<Integer, ItemStack> starterItems = configManager.getStarterItems();
        for (Map.Entry<Integer, ItemStack> entry : starterItems.entrySet()) {
            player.getInventory().setItem(entry.getKey(), entry.getValue().clone());
        }
    }

    /**
     * Weist dem Spieler ein neues zufälliges Item zu
     */
    public void assignNewItem(Player player) {
        PlayerData data = playerDataMap.get(player.getUniqueId());
        if (data == null) return;

        Material newItem = plugin.getBlacklistManager().getRandomItem();
        data.setCurrentItem(newItem);

        // Item über dem Kopf anzeigen
        plugin.getItemDisplayManager().updateDisplay(player, newItem);

        // Nachricht senden
        String itemName = formatItemName(newItem);
        player.sendMessage(messageManager.getNewItem(itemName));

        // Scoreboard aktualisieren
        plugin.getScoreboardManager().updateScoreboard(player);
    }

    /**
     * Wird aufgerufen wenn ein Spieler ein Item einsammelt
     */
    public void onItemPickup(Player player, Material material) {
        if (!gameRunning) return;

        PlayerData data = playerDataMap.get(player.getUniqueId());
        if (data == null || data.getCurrentItem() == null) return;

        if (data.getCurrentItem() == material) {
            // Richtiges Item gesammelt!
            data.addPoint();

            String itemName = formatItemName(material);
            player.sendMessage(messageManager.getItemCollected(itemName));

            // Sound
            if (configManager.isSoundsEnabled()) {
                player.playSound(player.getLocation(), configManager.getItemCollected(), 1.0f, 1.0f);
            }

            // Neues Item zuweisen
            assignNewItem(player);

            // Scoreboard für alle aktualisieren (Ranking könnte sich ändern)
            plugin.getScoreboardManager().updateAllScoreboards();
        }
    }

    /**
     * Spieler nutzt einen Skip
     */
    public boolean useSkip(Player player) {
        if (!gameRunning) return false;

        PlayerData data = playerDataMap.get(player.getUniqueId());
        if (data == null) return false;

        if (!data.hasSkipsRemaining()) {
            player.sendMessage(messageManager.getNoSkipsLeft());
            return false;
        }

        Material skippedItem = data.getCurrentItem();
        
        // Skip verwenden
        data.useSkip();

        // Geskipptes Item ins Inventar geben
        if (skippedItem != null) {
            player.getInventory().addItem(new ItemStack(skippedItem, 1));
        }

        String itemName = formatItemName(skippedItem);
        player.sendMessage(messageManager.getSkipUsed(itemName, data.getSkipsRemaining()));

        // Sound
        if (configManager.isSoundsEnabled()) {
            player.playSound(player.getLocation(), configManager.getSkipUsed(), 1.0f, 1.0f);
        }

        // Neues Item zuweisen
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
                
                // Scoreboard Zeit aktualisieren
                plugin.getScoreboardManager().updateAllScoreboards();
            }
        }.runTaskTimer(plugin, 20L, 20L);
    }

    private void startActionbarTask() {
        actionbarTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (!gameRunning) {
                    cancel();
                    return;
                }

                for (Player player : Bukkit.getOnlinePlayers()) {
                    PlayerData data = playerDataMap.get(player.getUniqueId());
                    if (data == null || data.getCurrentItem() == null) continue;

                    String format = configManager.getActionbarFormat();
                    String itemName = formatItemName(data.getCurrentItem());
                    String time = formatTime(remainingSeconds);
                    int points = data.getPoints();

                    String message = ColorUtils.replacePlaceholders(format,
                        "{item}", itemName,
                        "{time}", time,
                        "{points}", String.valueOf(points));

                    // SmallCaps anwenden wenn aktiviert
                    if (plugin.getScoreboardConfig().isSmallCapsEnabled()) {
                        message = ColorUtils.toSmallCaps(message);
                    }

                    player.sendActionBar(ColorUtils.colorize(message));
                }
            }
        }.runTaskTimer(plugin, 0L, 10L); // Alle 0.5 Sekunden aktualisieren
    }

    /**
     * Beendet das Spiel normal (Zeit abgelaufen)
     */
    public void endGame() {
        stopGame(false);
    }

    /**
     * Stoppt das Spiel
     * @param force true wenn durch Admin gestoppt, false wenn normal beendet
     */
    public void stopGame(boolean force) {
        if (!gameRunning && !countdownActive) return;

        gameRunning = false;
        countdownActive = false;

        // Tasks stoppen
        if (gameTimer != null) {
            gameTimer.cancel();
            gameTimer = null;
        }
        if (actionbarTask != null) {
            actionbarTask.cancel();
            actionbarTask = null;
        }

        // Item Displays entfernen
        plugin.getItemDisplayManager().removeAllDisplays();

        // Scoreboards entfernen
        plugin.getScoreboardManager().removeAllScoreboards();

        // Leaderboard anzeigen
        showLeaderboard();

        // Spieler zurück zum Spawn + World Border
        Location spawn = configManager.getSpawnLocation();
        if (spawn != null && spawn.getWorld() != null) {
            World world = spawn.getWorld();
            
            // World Border setzen
            WorldBorder border = world.getWorldBorder();
            border.setCenter(spawn);
            border.setSize(configManager.getEndBorderRadius() * 2);

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (playerDataMap.containsKey(player.getUniqueId())) {
                    player.teleport(spawn);
                    
                    // Sound
                    if (configManager.isSoundsEnabled()) {
                        player.playSound(player.getLocation(), configManager.getEventEnd(), 1.0f, 1.0f);
                    }
                }
            }
        }

        // Broadcast
        if (force) {
            Bukkit.broadcast(messageManager.getEventStopped());
        } else {
            Bukkit.broadcast(messageManager.getEventEnded());
        }

        // Daten leeren
        playerDataMap.clear();
    }

    private void showLeaderboard() {
        // Spieler nach Punkten sortieren
        List<Map.Entry<UUID, PlayerData>> sorted = playerDataMap.entrySet().stream()
            .sorted((a, b) -> Integer.compare(b.getValue().getPoints(), a.getValue().getPoints()))
            .collect(Collectors.toList());

        boolean smallCaps = plugin.getScoreboardConfig().isSmallCapsEnabled();

        // Leaderboard an alle senden
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(Component.empty());
            player.sendMessage(messageManager.getLeaderboardHeader());
            player.sendMessage(messageManager.getEventEnded());
            player.sendMessage(Component.empty());

            int rank = 1;
            for (Map.Entry<UUID, PlayerData> entry : sorted) {
                Player p = Bukkit.getPlayer(entry.getKey());
                String name = p != null ? p.getName() : "Unbekannt";
                int points = entry.getValue().getPoints();

                // Top 3 mit speziellen Farben
                String prefix = switch (rank) {
                    case 1 -> "<#FFD700>\uD83E\uDD47 "; // Gold
                    case 2 -> "<#C0C0C0>\uD83E\uDD48 "; // Silber
                    case 3 -> "<#CD7F32>\uD83E\uDD49 "; // Bronze
                    default -> "<#478ED2>";
                };

                String line = prefix + rank + ". " + name + " <#6953B5>- <#00EE39>" + points + " Punkte";
                if (smallCaps) {
                    line = ColorUtils.toSmallCaps(line);
                }
                player.sendMessage(ColorUtils.colorize(line));
                rank++;
            }

            player.sendMessage(Component.empty());
            player.sendMessage(messageManager.getLeaderboardFooter());
        }
    }

    public String formatTime(int seconds) {
        int minutes = seconds / 60;
        int secs = seconds % 60;
        return String.format("%02d:%02d", minutes, secs);
    }

    public String formatItemName(Material material) {
        if (material == null) return "Unbekannt";
        
        String name = material.name().toLowerCase().replace("_", " ");
        // Erster Buchstabe groß
        String[] words = name.split(" ");
        StringBuilder result = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                result.append(Character.toUpperCase(word.charAt(0)))
                      .append(word.substring(1))
                      .append(" ");
            }
        }
        return result.toString().trim();
    }

    // Getter

    public boolean isGameRunning() {
        return gameRunning;
    }

    public boolean isCountdownActive() {
        return countdownActive;
    }

    public PlayerData getPlayerData(UUID uuid) {
        return playerDataMap.get(uuid);
    }

    public Map<UUID, PlayerData> getAllPlayerData() {
        return new HashMap<>(playerDataMap);
    }

    public int getRemainingSeconds() {
        return remainingSeconds;
    }

    /**
     * Gibt eine sortierte Liste der Spieler nach Punkten zurück
     */
    public List<Map.Entry<UUID, PlayerData>> getSortedPlayers() {
        return playerDataMap.entrySet().stream()
            .sorted((a, b) -> Integer.compare(b.getValue().getPoints(), a.getValue().getPoints()))
            .collect(Collectors.toList());
    }

    /**
     * Gibt den Rang eines Spielers zurück (1-basiert)
     */
    public int getPlayerRank(UUID uuid) {
        List<Map.Entry<UUID, PlayerData>> sorted = getSortedPlayers();
        for (int i = 0; i < sorted.size(); i++) {
            if (sorted.get(i).getKey().equals(uuid)) {
                return i + 1;
            }
        }
        return -1;
    }

    /**
     * Fuegt einen Spieler waehrend des laufenden Spiels hinzu
     */
    public boolean addPlayer(Player player) {
        if (!gameRunning) return false;
        if (playerDataMap.containsKey(player.getUniqueId())) return false;

        // Spieler-Daten initialisieren
        PlayerData data = new PlayerData(player.getUniqueId(), configManager.getSkipsPerPlayer());
        playerDataMap.put(player.getUniqueId(), data);

        // Starter Items geben
        giveStarterItems(player);

        // Erstes Item zuweisen
        assignNewItem(player);

        // Scoreboard erstellen
        plugin.getScoreboardManager().createScoreboard(player);

        // Alle Scoreboards aktualisieren (neuer Spieler in der Liste)
        plugin.getScoreboardManager().updateAllScoreboards();

        return true;
    }
}
