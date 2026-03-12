package dev.zaen.itemforcebattle.managers;

import dev.zaen.itemforcebattle.BetterItemForceBattle;
import dev.zaen.itemforcebattle.config.ConfigManager;
import dev.zaen.itemforcebattle.config.ScoreboardConfig;
import dev.zaen.itemforcebattle.utils.ColorUtils;
import io.papermc.paper.scoreboard.numbers.NumberFormat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.*;

public class ScoreboardManager {

    private final BetterItemForceBattle plugin;
    private final ConfigManager configManager;
    private final ScoreboardConfig scoreboardConfig;
    private final Map<UUID, Scoreboard> playerScoreboards;

    public ScoreboardManager(BetterItemForceBattle plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
        this.scoreboardConfig = plugin.getScoreboardConfig();
        this.playerScoreboards = new HashMap<>();
    }

    /**
     * Erstellt Scoreboards für alle Spieler im Spiel
     */
    public void createScoreboards() {
        for (UUID uuid : plugin.getGameManager().getAllPlayerData().keySet()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                createScoreboard(player);
            }
        }
    }

    /**
     * Erstellt ein Scoreboard für einen einzelnen Spieler
     */
    public void createScoreboard(Player player) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

        String title = scoreboardConfig.getTitle();
        if (scoreboardConfig.isSmallCapsEnabled()) {
            title = ColorUtils.toSmallCaps(title);
        }

        Objective objective = scoreboard.registerNewObjective(
            "itemforce",
            Criteria.DUMMY,
            ColorUtils.colorize(title)
        );
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        playerScoreboards.put(player.getUniqueId(), scoreboard);
        player.setScoreboard(scoreboard);

        updateScoreboard(player);
    }

    /**
     * Aktualisiert das Scoreboard eines Spielers
     */
    public void updateScoreboard(Player player) {
        Scoreboard scoreboard = playerScoreboards.get(player.getUniqueId());
        if (scoreboard == null) return;

        Objective objective = scoreboard.getObjective("itemforce");
        if (objective == null) return;

        // Alle alten Einträge entfernen
        for (String entry : new HashSet<>(scoreboard.getEntries())) {
            scoreboard.resetScores(entry);
        }

        GameManager gameManager = plugin.getGameManager();
        List<Map.Entry<UUID, PlayerData>> sortedPlayers = gameManager.getSortedPlayers();
        int playerRank = gameManager.getPlayerRank(player.getUniqueId());
        int playersAround = scoreboardConfig.getPlayersAround();

        int line = 15;

        // Leerzeile oben
        setScore(objective, " ", line--);

        // Zeit anzeigen (wenn aktiviert)
        if (scoreboardConfig.isShowTime()) {
            setScore(objective, "§b⏱ §7Zeit: §a" + gameManager.formatTime(gameManager.getRemainingSeconds()), line--);
            // Leerzeile
            setScore(objective, "  ", line--);
        }

        // Spieler anzeigen (3 über, eigener Spieler, 3 unter)
        int startIndex = Math.max(0, playerRank - 1 - playersAround);
        int endIndex = Math.min(sortedPlayers.size(), playerRank + playersAround);

        // Anpassen wenn am Anfang oder Ende
        if (playerRank <= playersAround) {
            endIndex = Math.min(sortedPlayers.size(), playersAround * 2 + 1);
        } else if (playerRank > sortedPlayers.size() - playersAround) {
            startIndex = Math.max(0, sortedPlayers.size() - playersAround * 2 - 1);
        }

        for (int i = startIndex; i < endIndex && line > 1; i++) {
            Map.Entry<UUID, PlayerData> entry = sortedPlayers.get(i);
            Player p = Bukkit.getPlayer(entry.getKey());
            String name = p != null ? p.getName() : "???";
            int points = entry.getValue().getPoints();
            int rank = i + 1;

            String rankPrefix = getRankPrefix(rank);
            boolean isOwnPlayer = entry.getKey().equals(player.getUniqueId());

            String displayLine;
            if (isOwnPlayer && scoreboardConfig.isHighlightOwn()) {
                // Eigener Spieler hervorgehoben
                displayLine = "§a▶ " + rankPrefix + rank + ". §a" + name + " §7- §a" + points;
            } else {
                displayLine = "§7" + rankPrefix + rank + ". §f" + name + " §7- §a" + points;
            }

            // Kuerzen falls zu lang
            if (displayLine.length() > 40) {
                String shortName = name.length() > 10 ? name.substring(0, 10) + "..." : name;
                if (isOwnPlayer && scoreboardConfig.isHighlightOwn()) {
                    displayLine = "§a▶ " + rankPrefix + rank + ". §a" + shortName + " §7- §a" + points;
                } else {
                    displayLine = "§7" + rankPrefix + rank + ". §f" + shortName + " §7- §a" + points;
                }
            }

            setScore(objective, displayLine, line--);
        }

        // Leerzeile
        setScore(objective, "   ", line--);

        // Eigene Stats
        PlayerData ownData = gameManager.getPlayerData(player.getUniqueId());
        if (ownData != null) {
            if (scoreboardConfig.isShowPoints()) {
                setScore(objective, "§a🏆 §7Punkte: §a" + ownData.getPoints(), line--);
            }
            if (scoreboardConfig.isShowSkips()) {
                setScore(objective, "§6⚡ §7Skips: §6" + ownData.getSkipsRemaining(), line--);
            }
        }

        // Leerzeile unten
        setScore(objective, "    ", line--);
    }

    private String getRankPrefix(int rank) {
        return switch (rank) {
            case 1 -> "§6"; // Gold
            case 2 -> "§7"; // Silber
            case 3 -> "§c"; // Bronze/Kupfer
            default -> "§8"; // Grau
        };
    }

    private void setScore(Objective objective, String text, int score) {
        // SmallCaps anwenden wenn aktiviert
        if (scoreboardConfig.isSmallCapsEnabled()) {
            text = ColorUtils.toSmallCaps(text);
        }

        // Einzigartige Einträge sicherstellen
        String uniqueText = text;
        int attempts = 0;
        while (objective.getScoreboard().getEntries().contains(uniqueText) && attempts < 16) {
            uniqueText = text + "§" + Integer.toHexString(attempts);
            attempts++;
        }

        // Text auf max. Länge kürzen
        if (uniqueText.length() > 64) {
            uniqueText = uniqueText.substring(0, 64);
        }

        Score scoreObj = objective.getScore(uniqueText);
        scoreObj.setScore(score);

        // Rote Zahlen entfernen (leeres Format)
        scoreObj.numberFormat(NumberFormat.blank());
    }

    /**
     * Aktualisiert alle Scoreboards
     */
    public void updateAllScoreboards() {
        for (UUID uuid : playerScoreboards.keySet()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                updateScoreboard(player);
            }
        }
    }

    /**
     * Entfernt das Scoreboard eines Spielers
     */
    public void removeScoreboard(Player player) {
        playerScoreboards.remove(player.getUniqueId());
        player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
    }

    /**
     * Entfernt alle Scoreboards
     */
    public void removeAllScoreboards() {
        for (UUID uuid : new HashSet<>(playerScoreboards.keySet())) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
            }
        }
        playerScoreboards.clear();
    }
}
