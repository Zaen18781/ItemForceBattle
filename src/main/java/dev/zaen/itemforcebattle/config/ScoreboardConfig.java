package dev.zaen.itemforcebattle.config;

import dev.zaen.itemforcebattle.BetterItemForceBattle;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ScoreboardConfig {

    private final BetterItemForceBattle plugin;
    private File configFile;
    private FileConfiguration config;

    // Einstellungen
    private boolean enabled;
    private String title;
    private boolean smallCapsEnabled;
    private int playersAround;
    private boolean highlightOwn;
    private String highlightColor;
    private boolean showTime;
    private boolean showPoints;
    private boolean showSkips;
    private List<String> customLines;

    public ScoreboardConfig(BetterItemForceBattle plugin) {
        this.plugin = plugin;
        this.customLines = new ArrayList<>();
        loadConfig();
    }

    public void loadConfig() {
        configFile = new File(plugin.getDataFolder(), "scoreboard.yml");

        if (!configFile.exists()) {
            plugin.saveResource("scoreboard.yml", false);
        }

        config = YamlConfiguration.loadConfiguration(configFile);

        // Werte laden
        enabled = config.getBoolean("scoreboard.enabled", true);
        title = config.getString("scoreboard.title", "<#478ED2><bold>Item Force Battle");
        smallCapsEnabled = config.getBoolean("scoreboard.small-caps", true);
        playersAround = config.getInt("scoreboard.players-around", 3);
        highlightOwn = config.getBoolean("scoreboard.highlight-own", true);
        highlightColor = config.getString("scoreboard.highlight-color", "<#00EE39>");
        showTime = config.getBoolean("scoreboard.show-time", true);
        showPoints = config.getBoolean("scoreboard.show-points", true);
        showSkips = config.getBoolean("scoreboard.show-skips", true);
        customLines = config.getStringList("scoreboard.custom-lines");
    }

    public void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Konnte scoreboard.yml nicht speichern: " + e.getMessage());
        }
    }

    public void reload() {
        loadConfig();
    }

    // Getter
    public boolean isEnabled() {
        return enabled;
    }

    public String getTitle() {
        return title;
    }

    public boolean isSmallCapsEnabled() {
        return smallCapsEnabled;
    }

    public int getPlayersAround() {
        return playersAround;
    }

    public boolean isHighlightOwn() {
        return highlightOwn;
    }

    public String getHighlightColor() {
        return highlightColor;
    }

    public boolean isShowTime() {
        return showTime;
    }

    public boolean isShowPoints() {
        return showPoints;
    }

    public boolean isShowSkips() {
        return showSkips;
    }

    public List<String> getCustomLines() {
        return customLines;
    }
}
