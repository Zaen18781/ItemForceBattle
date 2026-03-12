package dev.zaen.itemforcebattle.config;

import dev.zaen.itemforcebattle.BetterItemForceBattle;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import dev.zaen.itemforcebattle.utils.ColorUtils;

import java.util.HashMap;
import java.util.Map;

public class ConfigManager {

    private final BetterItemForceBattle plugin;
    private FileConfiguration config;

    // Farben
    private String colorPrimary;
    private String colorSecondary;
    private String colorSuccess;
    private String colorError;

    // Event Einstellungen
    private int eventDuration;
    private int skipsPerPlayer;
    private int countdownTime;
    private int endBorderRadius;

    // Spawn
    private Location spawnLocation;

    // Display
    private double heightAboveHead;
    private String actionbarFormat;

    // Sounds
    private boolean soundsEnabled;
    private Sound countdownTick;
    private Sound countdownGo;
    private Sound itemCollected;
    private Sound skipUsed;
    private Sound eventEnd;

    // Starter Items
    private Map<Integer, ItemStack> starterItems;

    public ConfigManager(BetterItemForceBattle plugin) {
        this.plugin = plugin;
        this.starterItems = new HashMap<>();
        reload();
    }

    public void reload() {
        plugin.reloadConfig();
        config = plugin.getConfig();

        // Farben laden
        colorPrimary = config.getString("colors.primary", "<#478ED2>");
        colorSecondary = config.getString("colors.secondary", "<#6953B5>");
        colorSuccess = config.getString("colors.success", "<#00EE39>");
        colorError = config.getString("colors.error", "<#ff0000>");

        // Event Einstellungen
        eventDuration = config.getInt("event.duration", 60);
        skipsPerPlayer = config.getInt("event.skips", 5);
        countdownTime = config.getInt("event.countdown", 5);
        endBorderRadius = config.getInt("event.end-border-radius", 10);

        // Spawn
        loadSpawnLocation();

        // Display
        heightAboveHead = config.getDouble("display.height-above-head", 1.0);
        actionbarFormat = config.getString("display.actionbar-format", 
            "<#478ED2>🎯 {item} <#6953B5>| <#478ED2>⏱ {time} <#6953B5>| <#00EE39>🏆 {points} Punkte");

        // Sounds
        soundsEnabled = config.getBoolean("sounds.enabled", true);
        countdownTick = loadSound("sounds.countdown-tick", Sound.BLOCK_NOTE_BLOCK_PLING);
        countdownGo = loadSound("sounds.countdown-go", Sound.ENTITY_ENDER_DRAGON_GROWL);
        itemCollected = loadSound("sounds.item-collected", Sound.ENTITY_PLAYER_LEVELUP);
        skipUsed = loadSound("sounds.skip-used", Sound.ENTITY_VILLAGER_NO);
        eventEnd = loadSound("sounds.event-end", Sound.UI_TOAST_CHALLENGE_COMPLETE);

        // Starter Items
        loadStarterItems();
    }

    private void loadSpawnLocation() {
        String worldName = config.getString("spawn.world", "world");
        double x = config.getDouble("spawn.x", 0);
        double y = config.getDouble("spawn.y", 64);
        double z = config.getDouble("spawn.z", 0);
        float yaw = (float) config.getDouble("spawn.yaw", 0);
        float pitch = (float) config.getDouble("spawn.pitch", 0);

        if (Bukkit.getWorld(worldName) != null) {
            spawnLocation = new Location(Bukkit.getWorld(worldName), x, y, z, yaw, pitch);
        } else {
            spawnLocation = null;
        }
    }

    public void saveSpawnLocation(Location location) {
        config.set("spawn.world", location.getWorld().getName());
        config.set("spawn.x", location.getX());
        config.set("spawn.y", location.getY());
        config.set("spawn.z", location.getZ());
        config.set("spawn.yaw", location.getYaw());
        config.set("spawn.pitch", location.getPitch());
        plugin.saveConfig();
        this.spawnLocation = location;
    }

    private Sound loadSound(String path, Sound defaultSound) {
        String soundName = config.getString(path, defaultSound.name());
        try {
            return Sound.valueOf(soundName.toUpperCase());
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("Ungültiger Sound: " + soundName + " - Verwende Standard");
            return defaultSound;
        }
    }

    private void loadStarterItems() {
        starterItems.clear();

        ConfigurationSection itemsSection = config.getConfigurationSection("starter-items");
        if (itemsSection == null) return;

        for (String key : itemsSection.getKeys(false)) {
            ConfigurationSection itemSection = itemsSection.getConfigurationSection(key);
            if (itemSection == null || !itemSection.getBoolean("enabled", true)) continue;

            int slot = itemSection.getInt("slot", 0);
            String materialName = itemSection.getString("material", "STONE");
            int amount = itemSection.getInt("amount", 1);
            String displayName = itemSection.getString("display-name", null);
            boolean unbreakable = itemSection.getBoolean("unbreakable", false);

            try {
                Material material = Material.valueOf(materialName.toUpperCase());
                ItemStack item = new ItemStack(material, amount);
                ItemMeta meta = item.getItemMeta();

                if (meta != null) {
                    if (displayName != null) {
                        meta.displayName(ColorUtils.colorize(displayName));
                    }

                    if (unbreakable) {
                        meta.setUnbreakable(true);
                    }

                    // Enchantments laden
                    if (itemSection.contains("enchantments")) {
                        for (String enchString : itemSection.getStringList("enchantments")) {
                            String[] parts = enchString.split(":");
                            if (parts.length == 2) {
                                try {
                                    Enchantment ench = Enchantment.getByName(parts[0].toUpperCase());
                                    if (ench != null) {
                                        int level = Integer.parseInt(parts[1]);
                                        meta.addEnchant(ench, level, true);
                                    }
                                } catch (Exception e) {
                                    plugin.getLogger().warning("Ungültiges Enchantment: " + enchString);
                                }
                            }
                        }
                    }

                    item.setItemMeta(meta);
                }

                starterItems.put(slot, item);
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Ungültiges Material: " + materialName);
            }
        }
    }

    // Getter

    public String getColorPrimary() {
        return colorPrimary;
    }

    public String getColorSecondary() {
        return colorSecondary;
    }

    public String getColorSuccess() {
        return colorSuccess;
    }

    public String getColorError() {
        return colorError;
    }

    public int getEventDuration() {
        return eventDuration;
    }

    public int getSkipsPerPlayer() {
        return skipsPerPlayer;
    }

    public int getCountdownTime() {
        return countdownTime;
    }

    public int getEndBorderRadius() {
        return endBorderRadius;
    }

    public Location getSpawnLocation() {
        return spawnLocation;
    }

    public double getHeightAboveHead() {
        return heightAboveHead;
    }

    public String getActionbarFormat() {
        return actionbarFormat;
    }

    public boolean isSoundsEnabled() {
        return soundsEnabled;
    }

    public Sound getCountdownTick() {
        return countdownTick;
    }

    public Sound getCountdownGo() {
        return countdownGo;
    }

    public Sound getItemCollected() {
        return itemCollected;
    }

    public Sound getSkipUsed() {
        return skipUsed;
    }

    public Sound getEventEnd() {
        return eventEnd;
    }

    public Map<Integer, ItemStack> getStarterItems() {
        return new HashMap<>(starterItems);
    }
}
