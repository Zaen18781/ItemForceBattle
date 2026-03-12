package dev.zaen.itemforcebattle.config;

import dev.zaen.itemforcebattle.BetterItemForceBattle;
import dev.zaen.itemforcebattle.utils.ColorUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.configuration.file.FileConfiguration;

public class MessageManager {

    private final BetterItemForceBattle plugin;
    private FileConfiguration config;

    private boolean isSmallCapsEnabled() {
        ScoreboardConfig scoreboardConfig = plugin.getScoreboardConfig();
        return scoreboardConfig != null && scoreboardConfig.isSmallCapsEnabled();
    }

    private String applySmallCaps(String text) {
        if (isSmallCapsEnabled()) {
            return ColorUtils.toSmallCaps(text);
        }
        return text;
    }

    private String prefix;

    // Event Nachrichten
    private String eventStarted;
    private String eventStopped;
    private String eventEnded;

    // Countdown
    private String countdown;
    private String countdownGo;

    // Item Nachrichten
    private String newItem;
    private String itemCollected;

    // Skip Nachrichten
    private String skipUsed;
    private String noSkipsLeft;

    // Fehler
    private String noPermission;
    private String eventNotRunning;
    private String eventAlreadyRunning;
    private String spawnNotSet;
    private String spawnSet;

    // Leaderboard
    private String leaderboardHeader;
    private String leaderboardEntry;
    private String leaderboardFooter;

    public MessageManager(BetterItemForceBattle plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        config = plugin.getConfig();

        prefix = config.getString("messages.prefix", "<#478ED2>[<#6953B5>ItemForce<#478ED2>] ");

        eventStarted = config.getString("messages.event-started", "<#00EE39>Das Item Force Battle hat begonnen!");
        eventStopped = config.getString("messages.event-stopped", "<#ff0000>Das Event wurde beendet!");
        eventEnded = config.getString("messages.event-ended", "<#00EE39>Das Event ist vorbei! Hier sind die Ergebnisse:");

        countdown = config.getString("messages.countdown", "<#478ED2>Start in <#00EE39>{seconds} <#478ED2>Sekunden...");
        countdownGo = config.getString("messages.countdown-go", "<#00EE39><bold>LOS GEHT'S!");

        newItem = config.getString("messages.new-item", "<#478ED2>Dein neues Item: <#00EE39>{item}");
        itemCollected = config.getString("messages.item-collected", "<#00EE39>Du hast <#478ED2>{item} <#00EE39>gesammelt! <#6953B5>(+1 Punkt)");

        skipUsed = config.getString("messages.skip-used", "<#6953B5>Du hast <#478ED2>{item} <#6953B5>geskippt! <#ff0000>({remaining} Skips übrig)");
        noSkipsLeft = config.getString("messages.no-skips-left", "<#ff0000>Du hast keine Skips mehr übrig!");

        noPermission = config.getString("messages.no-permission", "<#ff0000>Du hast keine Berechtigung für diesen Befehl!");
        eventNotRunning = config.getString("messages.event-not-running", "<#ff0000>Es läuft gerade kein Event!");
        eventAlreadyRunning = config.getString("messages.event-already-running", "<#ff0000>Es läuft bereits ein Event!");
        spawnNotSet = config.getString("messages.spawn-not-set", "<#ff0000>Der Spawn wurde noch nicht gesetzt! Nutze /itemforce setspawn");
        spawnSet = config.getString("messages.spawn-set", "<#00EE39>Spawn wurde gesetzt!");

        leaderboardHeader = config.getString("messages.leaderboard-header", "<#478ED2>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        leaderboardEntry = config.getString("messages.leaderboard-entry", "<#6953B5>{rank}. <#478ED2>{player} <#6953B5>- <#00EE39>{points} Punkte");
        leaderboardFooter = config.getString("messages.leaderboard-footer", "<#478ED2>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }

    // Hilfsmethoden

    public Component formatMessage(String message) {
        return ColorUtils.colorize(applySmallCaps(prefix + message));
    }

    public Component formatMessageNoPrefix(String message) {
        return ColorUtils.colorize(applySmallCaps(message));
    }

    // Getter mit Platzhalter-Ersetzung

    public Component getEventStarted() {
        return formatMessage(eventStarted);
    }

    public Component getEventStopped() {
        return formatMessage(eventStopped);
    }

    public Component getEventEnded() {
        return formatMessage(eventEnded);
    }

    public Component getCountdown(int seconds) {
        return formatMessage(ColorUtils.replacePlaceholders(countdown, "{seconds}", String.valueOf(seconds)));
    }

    public Component getCountdownGo() {
        return formatMessage(countdownGo);
    }

    public Component getNewItem(String itemName) {
        return formatMessage(ColorUtils.replacePlaceholders(newItem, "{item}", itemName));
    }

    public Component getItemCollected(String itemName) {
        return formatMessage(ColorUtils.replacePlaceholders(itemCollected, "{item}", itemName));
    }

    public Component getSkipUsed(String itemName, int remaining) {
        return formatMessage(ColorUtils.replacePlaceholders(skipUsed, 
            "{item}", itemName, 
            "{remaining}", String.valueOf(remaining)));
    }

    public Component getNoSkipsLeft() {
        return formatMessage(noSkipsLeft);
    }

    public Component getNoPermission() {
        return formatMessage(noPermission);
    }

    public Component getEventNotRunning() {
        return formatMessage(eventNotRunning);
    }

    public Component getEventAlreadyRunning() {
        return formatMessage(eventAlreadyRunning);
    }

    public Component getSpawnNotSet() {
        return formatMessage(spawnNotSet);
    }

    public Component getSpawnSet() {
        return formatMessage(spawnSet);
    }

    public Component getLeaderboardHeader() {
        return formatMessageNoPrefix(leaderboardHeader);
    }

    public Component getLeaderboardEntry(int rank, String player, int points) {
        return formatMessageNoPrefix(ColorUtils.replacePlaceholders(leaderboardEntry,
            "{rank}", String.valueOf(rank),
            "{player}", player,
            "{points}", String.valueOf(points)));
    }

    public Component getLeaderboardFooter() {
        return formatMessageNoPrefix(leaderboardFooter);
    }

    public String getPrefix() {
        return prefix;
    }
}
