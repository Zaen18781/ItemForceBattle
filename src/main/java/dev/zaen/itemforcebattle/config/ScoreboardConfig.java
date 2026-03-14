package dev.zaen.itemforcebattle.config;

import dev.zaen.itemforcebattle.BetterItemForceBattle;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class ScoreboardConfig {

    private final BetterItemForceBattle plugin;
    private File configFile;
    private FileConfiguration config;

    private String title;
    private boolean smallCapsEnabled;
    private boolean textShadowEnabled;
    private List<String> lines;

    public ScoreboardConfig(BetterItemForceBattle plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    public void loadConfig() {
        configFile = new File(plugin.getDataFolder(), "scoreboard.yml");
        if (!configFile.exists()) plugin.saveResource("scoreboard.yml", false);
        config = YamlConfiguration.loadConfiguration(configFile);

        title = config.getString("scoreboard.scoreboards.default.title", "<b><color:#C539EC>ɪᴛᴇᴍꜰᴏʀᴄᴇ</color></b>");
        smallCapsEnabled = config.getBoolean("scoreboard.small-caps", true);
        textShadowEnabled = config.getBoolean("scoreboard.text-shadow", true);
        lines = config.getStringList("scoreboard.scoreboards.default.lines");
        if (lines.isEmpty()) lines = Collections.emptyList();
    }

    public void reload() { loadConfig(); }

    public String getTitle() { return title; }
    public boolean isSmallCapsEnabled() { return smallCapsEnabled; }
    public boolean isTextShadowEnabled() { return textShadowEnabled; }
    public List<String> getLines() { return lines; }
}
