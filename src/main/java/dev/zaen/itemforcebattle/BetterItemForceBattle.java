package dev.zaen.itemforcebattle;

import dev.zaen.itemforcebattle.commands.ItemForceCommand;
import dev.zaen.itemforcebattle.commands.SkipCommand;
import dev.zaen.itemforcebattle.config.ConfigManager;
import dev.zaen.itemforcebattle.config.BlacklistManager;
import dev.zaen.itemforcebattle.config.MessageManager;
import dev.zaen.itemforcebattle.config.ScoreboardConfig;
import dev.zaen.itemforcebattle.listeners.GameListener;
import dev.zaen.itemforcebattle.listeners.PlayerProtectionListener;
import dev.zaen.itemforcebattle.managers.GameManager;
import dev.zaen.itemforcebattle.managers.ItemDisplayManager;
import dev.zaen.itemforcebattle.managers.ScoreboardManager;
import org.bukkit.plugin.java.JavaPlugin;

public class BetterItemForceBattle extends JavaPlugin {

    private static BetterItemForceBattle instance;

    private ConfigManager configManager;
    private BlacklistManager blacklistManager;
    private MessageManager messageManager;
    private ScoreboardConfig scoreboardConfig;
    private GameManager gameManager;
    private ItemDisplayManager itemDisplayManager;
    private ScoreboardManager scoreboardManager;

    @Override
    public void onEnable() {
        instance = this;
        
        // Config laden
        saveDefaultConfig();
        saveResource("blacklist.yml", false);
        saveResource("scoreboard.yml", false);

        // Manager initialisieren
        this.configManager = new ConfigManager(this);
        this.blacklistManager = new BlacklistManager(this);
        this.messageManager = new MessageManager(this);
        this.scoreboardConfig = new ScoreboardConfig(this);
        this.itemDisplayManager = new ItemDisplayManager(this);
        this.scoreboardManager = new ScoreboardManager(this);
        this.gameManager = new GameManager(this);
        
        // Commands registrieren
        getCommand("itemforce").setExecutor(new ItemForceCommand(this));
        getCommand("itemforce").setTabCompleter(new ItemForceCommand(this));
        getCommand("skip").setExecutor(new SkipCommand(this));
        
        // Listener registrieren
        getServer().getPluginManager().registerEvents(new GameListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerProtectionListener(this), this);
        
        getLogger().info("BetterItemForceBattle wurde aktiviert!");
    }

    @Override
    public void onDisable() {
        if (gameManager != null && gameManager.isGameRunning()) {
            gameManager.stopGame(true);
        }
        
        if (itemDisplayManager != null) {
            itemDisplayManager.removeAllDisplays();
        }
        
        getLogger().info("BetterItemForceBattle wurde deaktiviert!");
    }
    
    public void reload() {
        reloadConfig();
        configManager.reload();
        blacklistManager.reload();
        messageManager.reload();
        scoreboardConfig.reload();
    }

    public static BetterItemForceBattle getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public BlacklistManager getBlacklistManager() {
        return blacklistManager;
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public ItemDisplayManager getItemDisplayManager() {
        return itemDisplayManager;
    }

    public ScoreboardManager getScoreboardManager() {
        return scoreboardManager;
    }

    public ScoreboardConfig getScoreboardConfig() {
        return scoreboardConfig;
    }
}
