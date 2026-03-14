package dev.zaen.itemforcebattle.config;

import dev.zaen.itemforcebattle.BetterItemForceBattle;
import dev.zaen.itemforcebattle.utils.ColorUtils;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;

public class ConfigManager {

    private final BetterItemForceBattle plugin;
    private FileConfiguration config;

    private String colorPrimary;
    private String colorSecondary;
    private String colorSuccess;
    private String colorError;

    private int eventDuration;
    private int skipsPerPlayer;
    private int countdownTime;
    private int endBorderRadius;

    private Location spawnLocation;

    private boolean displayEnabled;
    private double displayHeight;
    private float displayScale;
    private String displayBillboard;
    private int displayViewRange;

    private boolean soundsEnabled;
    private Sound countdownTick;
    private Sound countdownGo;
    private Sound itemCollected;
    private Sound skipUsed;
    private Sound eventEnd;

    private Map<Integer, ItemStack> starterItems;

    public ConfigManager(BetterItemForceBattle plugin) {
        this.plugin = plugin;
        this.starterItems = new HashMap<>();
        reload();
    }

    public void reload() {
        plugin.reloadConfig();
        config = plugin.getConfig();

        colorPrimary = config.getString("colors.primary", "<#478ED2>");
        colorSecondary = config.getString("colors.secondary", "<#6953B5>");
        colorSuccess = config.getString("colors.success", "<#00EE39>");
        colorError = config.getString("colors.error", "<#ff0000>");

        eventDuration = config.getInt("event.duration", 60);
        skipsPerPlayer = config.getInt("event.skips", 5);
        countdownTime = config.getInt("event.countdown", 5);
        endBorderRadius = config.getInt("event.end-border-radius", 10);

        loadSpawnLocation();

        displayEnabled = config.getBoolean("display.enabled", true);
        displayHeight = config.getDouble("display.height-above-head", 0.3);
        displayScale = (float) config.getDouble("display.scale", 0.6);
        displayBillboard = config.getString("display.billboard", "CENTER").toUpperCase();
        displayViewRange = config.getInt("display.view-range", 64);

        soundsEnabled = config.getBoolean("sounds.enabled", true);
        countdownTick = loadSound("sounds.countdown-tick", "block.note_block.pling");
        countdownGo = loadSound("sounds.countdown-go", "entity.ender_dragon.growl");
        itemCollected = loadSound("sounds.item-collected", "entity.player.levelup");
        skipUsed = loadSound("sounds.skip-used", "entity.villager.no");
        eventEnd = loadSound("sounds.event-end", "ui.toast.challenge_complete");

        loadStarterItems();
    }

    private void loadSpawnLocation() {
        String worldName = config.getString("spawn.world", "world");
        double x = config.getDouble("spawn.x", 0);
        double y = config.getDouble("spawn.y", 64);
        double z = config.getDouble("spawn.z", 0);
        float yaw = (float) config.getDouble("spawn.yaw", 0);
        float pitch = (float) config.getDouble("spawn.pitch", 0);
        spawnLocation = Bukkit.getWorld(worldName) != null
            ? new Location(Bukkit.getWorld(worldName), x, y, z, yaw, pitch)
            : null;
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

    private Sound loadSound(String path, String defaultKey) {
        String soundName = config.getString(path, defaultKey);
        Sound found = RegistryAccess.registryAccess()
            .getRegistry(RegistryKey.SOUND_EVENT)
            .get(NamespacedKey.minecraft(soundName.toLowerCase()));
        if (found != null) return found;
        Sound fallback = RegistryAccess.registryAccess()
            .getRegistry(RegistryKey.SOUND_EVENT)
            .get(NamespacedKey.minecraft(defaultKey));
        if (fallback != null) {
            plugin.getLogger().warning("Ungültiger Sound: " + soundName + " - Verwende Standard");
            return fallback;
        }
        plugin.getLogger().severe("Auch Standard-Sound nicht gefunden: " + defaultKey);
        return null;
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
                        meta.displayName(ColorUtils.colorize(displayName)
                            .decoration(TextDecoration.ITALIC, false));
                    }
                    if (unbreakable) meta.setUnbreakable(true);
                    if (itemSection.contains("enchantments")) {
                        for (String enchString : itemSection.getStringList("enchantments")) {
                            String[] parts = enchString.split(":");
                            if (parts.length == 2) {
                                try {
                                    Enchantment ench = RegistryAccess.registryAccess()
                                        .getRegistry(RegistryKey.ENCHANTMENT)
                                        .get(NamespacedKey.minecraft(parts[0].toLowerCase()));
                                    if (ench != null) meta.addEnchant(ench, Integer.parseInt(parts[1]), true);
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

    public boolean isDisplayEnabled() { return displayEnabled; }
    public double getDisplayHeight() { return displayHeight; }
    public float getDisplayScale() { return displayScale; }
    public String getDisplayBillboard() { return displayBillboard; }
    public int getDisplayViewRange() { return displayViewRange; }

    public String getColorPrimary() { return colorPrimary; }
    public String getColorSecondary() { return colorSecondary; }
    public String getColorSuccess() { return colorSuccess; }
    public String getColorError() { return colorError; }
    public int getEventDuration() { return eventDuration; }
    public int getSkipsPerPlayer() { return skipsPerPlayer; }
    public int getCountdownTime() { return countdownTime; }
    public int getEndBorderRadius() { return endBorderRadius; }
    public Location getSpawnLocation() { return spawnLocation; }
    public boolean isSoundsEnabled() { return soundsEnabled; }
    public Sound getCountdownTick() { return countdownTick; }
    public Sound getCountdownGo() { return countdownGo; }
    public Sound getItemCollected() { return itemCollected; }
    public Sound getSkipUsed() { return skipUsed; }
    public Sound getEventEnd() { return eventEnd; }
    public Map<Integer, ItemStack> getStarterItems() { return new HashMap<>(starterItems); }
}
