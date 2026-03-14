package dev.zaen.itemforcebattle.config;

import dev.zaen.itemforcebattle.BetterItemForceBattle;
import dev.zaen.itemforcebattle.utils.ColorUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class MessageManager {

    private final BetterItemForceBattle plugin;
    private FileConfiguration messagesConfig;

    private String prefix;
    private String playerOnly;
    private String noPermission;
    private String eventNotRunning;
    private String eventAlreadyRunning;
    private String spawnNotSet;
    private String spawnSet;
    private String eventStarted;
    private String eventStopped;
    private String eventEnded;
    private String countdownGo;
    private String newItem;
    private String itemCollected;
    private String skipUsed;
    private String noSkipsLeft;
    private String playerAdded;
    private String playerAddedSelf;
    private String playerAlreadyInGame;
    private String playerNotFound;
    private String configReloaded;
    private String scoreboardHidden;
    private String scoreboardShown;
    private String leaderboardHeader;
    private String leaderboardFooter;

    public MessageManager(BetterItemForceBattle plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        File messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) plugin.saveResource("messages.yml", false);
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);

        prefix = messagesConfig.getString("prefix", "<#478ED2><b>ɪᴛᴇᴍʙᴀᴛᴛʟᴇ</b> <dark_gray>»</dark_gray> ");
        playerOnly = messagesConfig.getString("player-only", "<#ff0000><b>❌</b> <white>ɴᴜʀ ғüʀ sᴘɪᴇʟᴇʀ!");
        noPermission = messagesConfig.getString("no-permission", "<#ff0000><b>❌</b> <white>ᴋᴇɪɴᴇ ʙᴇʀᴇᴄʜᴛɪɢᴜɴɢ!");
        eventNotRunning = messagesConfig.getString("event-not-running", "<#ff0000><b>❌</b> <white>ᴋᴇɪɴ ᴇᴠᴇɴᴛ ᴀᴋᴛɪᴠ!");
        eventAlreadyRunning = messagesConfig.getString("event-already-running", "<#ff0000><b>❌</b> <white>ᴇᴠᴇɴᴛ ʟäᴜғᴛ ʙᴇʀᴇɪᴛs!");
        spawnNotSet = messagesConfig.getString("spawn-not-set", "<#ff0000><b>❌</b> <white>sᴘᴀᴡɴ ɴɪᴄʜᴛ ɢᴇsᴇᴛᴢᴛ!");
        spawnSet = messagesConfig.getString("spawn-set", "<#00EE39>✔ <white>sᴘᴀᴡɴ ɢᴇsᴇᴛᴢᴛ.");
        eventStarted = messagesConfig.getString("event-started", "<#00EE39>✔ <white>ᴅᴀs ɪᴛᴇᴍ ʙᴀᴛᴛʟᴇ ʜᴀᴛ ʙᴇɢᴏɴɴᴇɴ!");
        eventStopped = messagesConfig.getString("event-stopped", "<#ff0000><b>❌</b> <white>ᴅᴀs ᴇᴠᴇɴᴛ ᴡᴜʀᴅᴇ ʙᴇᴇɴᴅᴇᴛ!");
        eventEnded = messagesConfig.getString("event-ended", "<#00EE39>✔ <white>ᴅᴀs ᴇᴠᴇɴᴛ ɪsᴛ ᴠᴏʀʙᴇɪ!");
        countdownGo = messagesConfig.getString("countdown-go", "<#00EE39><b>ʟᴏs ɢᴇʜᴛs!");
        newItem = messagesConfig.getString("new-item", "<#478ED2>🎯 <white>ᴅᴇɪɴ ɴᴇᴜᴇs ɪᴛᴇᴍ: <white>{item}");
        itemCollected = messagesConfig.getString("item-collected", "<#00EE39>✔ <white>{item} <#00EE39>ɢᴇsᴀᴍᴍᴇʟᴛ! <#FFD700>(+1 ᴘᴜɴᴋᴛ)");
        skipUsed = messagesConfig.getString("skip-used", "<#FFD700>● <white>{item} <#FFD700>ɢᴇsᴋɪᴘᴘᴛ! <#ff0000>({remaining} sᴋɪᴘs üʙʀɪɢ)");
        noSkipsLeft = messagesConfig.getString("no-skips-left", "<#ff0000><b>❌</b> <white>ᴋᴇɪɴᴇ sᴋɪᴘs ᴍᴇʜʀ üʙʀɪɢ!");
        playerAdded = messagesConfig.getString("player-added", "<#00EE39>✔ <white>{player} <#00EE39>ᴡᴜʀᴅᴇ ʜɪɴᴢᴜɢᴇғüɢᴛ.");
        playerAddedSelf = messagesConfig.getString("player-added-self", "<#00EE39>✔ <white>ᴅᴜ ᴡᴜʀᴅᴇsᴛ ᴢᴜᴍ ɪᴛᴇᴍ ʙᴀᴛᴛʟᴇ ʜɪɴᴢᴜɢᴇғüɢᴛ!");
        playerAlreadyInGame = messagesConfig.getString("player-already-in-game", "<#ff0000><b>❌</b> <white>sᴘɪᴇʟᴇʀ ɪsᴛ ʙᴇʀᴇɪᴛs ɪᴍ sᴘɪᴇʟ!");
        playerNotFound = messagesConfig.getString("player-not-found", "<#ff0000><b>❌</b> <white>sᴘɪᴇʟᴇʀ <white>{player}</white> ɴɪᴄʜᴛ ɢᴇғᴜɴᴅᴇɴ!");
        configReloaded = messagesConfig.getString("config-reloaded", "<#00EE39>✔ <white>ᴋᴏɴғɪɢ ɴᴇᴜ ɢᴇʟᴀᴅᴇɴ.");
        scoreboardHidden = messagesConfig.getString("scoreboard-hidden", "<#FFD700>● <white>sᴄᴏʀᴇʙᴏᴀʀᴅ <#ff0000>ᴀᴜsɢᴇʙʟᴇɴᴅᴇᴛ.");
        scoreboardShown = messagesConfig.getString("scoreboard-shown", "<#FFD700>● <white>sᴄᴏʀᴇʙᴏᴀʀᴅ <#00EE39>ᴀɴɢᴇᴢᴇɪɢᴛ.");
        leaderboardHeader = messagesConfig.getString("leaderboard-header", "<#478ED2>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        leaderboardFooter = messagesConfig.getString("leaderboard-footer", "<#478ED2>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }

    private boolean isSmallCapsEnabled() {
        ScoreboardConfig cfg = plugin.getScoreboardConfig();
        return cfg != null && cfg.isSmallCapsEnabled();
    }

    private String applySmallCaps(String text) {
        return isSmallCapsEnabled() ? ColorUtils.toSmallCaps(text) : text;
    }

    private Component fmt(String raw) {
        return ColorUtils.colorize(applySmallCaps(raw));
    }

    public Component getPlayerOnly() { return fmt(playerOnly); }
    public Component getNoPermission() { return fmt(noPermission); }
    public Component getEventNotRunning() { return fmt(eventNotRunning); }
    public Component getEventAlreadyRunning() { return fmt(eventAlreadyRunning); }
    public Component getSpawnNotSet() { return fmt(spawnNotSet); }
    public Component getSpawnSet() { return fmt(spawnSet); }
    public Component getEventStarted() { return fmt(eventStarted); }
    public Component getEventStopped() { return fmt(eventStopped); }
    public Component getEventEnded() { return fmt(eventEnded); }
    public Component getCountdownGo() { return fmt(countdownGo); }
    public Component getNoSkipsLeft() { return fmt(noSkipsLeft); }
    public Component getPlayerAddedSelf() { return fmt(playerAddedSelf); }
    public Component getPlayerAlreadyInGame() { return fmt(playerAlreadyInGame); }
    public Component getConfigReloaded() { return fmt(configReloaded); }
    public Component getScoreboardHidden() { return fmt(scoreboardHidden); }
    public Component getScoreboardShown() { return fmt(scoreboardShown); }
    public Component getLeaderboardHeader() { return fmt(leaderboardHeader); }
    public Component getLeaderboardFooter() { return fmt(leaderboardFooter); }

    public Component getNewItem(String itemName) {
        return fmt(ColorUtils.replacePlaceholders(newItem, "{item}", itemName));
    }

    public Component getItemCollected(String itemName) {
        return fmt(ColorUtils.replacePlaceholders(itemCollected, "{item}", itemName));
    }

    public Component getSkipUsed(String itemName, int remaining) {
        return fmt(ColorUtils.replacePlaceholders(skipUsed, "{item}", itemName, "{remaining}", String.valueOf(remaining)));
    }

    public Component getPlayerAdded(String playerName) {
        return fmt(ColorUtils.replacePlaceholders(playerAdded, "{player}", playerName));
    }

    public Component getPlayerNotFound(String playerName) {
        return fmt(ColorUtils.replacePlaceholders(playerNotFound, "{player}", playerName));
    }

    public String getPrefix() { return prefix; }
}
