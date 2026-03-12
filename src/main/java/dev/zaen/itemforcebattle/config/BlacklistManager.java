package dev.zaen.itemforcebattle.config;

import dev.zaen.itemforcebattle.BetterItemForceBattle;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.*;

public class BlacklistManager {

    private final BetterItemForceBattle plugin;
    private File blacklistFile;
    private FileConfiguration blacklistConfig;
    
    private Set<Material> blacklistedMaterials;
    private Set<String> blacklistedCategories;
    private List<Material> availableItems;

    public BlacklistManager(BetterItemForceBattle plugin) {
        this.plugin = plugin;
        this.blacklistedMaterials = new HashSet<>();
        this.blacklistedCategories = new HashSet<>();
        this.availableItems = new ArrayList<>();
        reload();
    }

    public void reload() {
        blacklistFile = new File(plugin.getDataFolder(), "blacklist.yml");
        if (!blacklistFile.exists()) {
            plugin.saveResource("blacklist.yml", false);
        }
        
        blacklistConfig = YamlConfiguration.loadConfiguration(blacklistFile);
        
        loadBlacklist();
        buildAvailableItems();
    }

    private void loadBlacklist() {
        blacklistedMaterials.clear();
        blacklistedCategories.clear();

        // Kategorien laden
        List<String> categories = blacklistConfig.getStringList("blacklisted-categories");
        for (String category : categories) {
            blacklistedCategories.add(category.toUpperCase());
        }

        // Einzelne Items laden
        List<String> items = blacklistConfig.getStringList("blacklisted-items");
        for (String itemName : items) {
            try {
                Material material = Material.valueOf(itemName.toUpperCase());
                blacklistedMaterials.add(material);
            } catch (IllegalArgumentException e) {
                // Material existiert nicht, ignorieren
            }
        }
    }

    private void buildAvailableItems() {
        availableItems.clear();

        for (Material material : Material.values()) {
            // Nur Items, keine Blöcke ohne Item-Form
            if (!material.isItem()) continue;
            
            // Luft und Legacy ausschließen
            if (material.isAir() || material.isLegacy()) continue;
            
            // Direkt geblacklistet
            if (blacklistedMaterials.contains(material)) continue;
            
            // Kategorie geblacklistet
            if (isInBlacklistedCategory(material)) continue;
            
            availableItems.add(material);
        }

        plugin.getLogger().info("Verfügbare Items: " + availableItems.size());
    }

    private boolean isInBlacklistedCategory(Material material) {
        String name = material.name();
        
        for (String category : blacklistedCategories) {
            if (name.contains(category)) {
                return true;
            }
        }
        
        return false;
    }

    /**
     * Gibt ein zufälliges verfügbares Item zurück
     */
    public Material getRandomItem() {
        if (availableItems.isEmpty()) {
            plugin.getLogger().severe("Keine verfügbaren Items! Überprüfe die Blacklist.");
            return Material.STONE; // Fallback
        }
        
        Random random = new Random();
        return availableItems.get(random.nextInt(availableItems.size()));
    }

    /**
     * Prüft ob ein Material verfügbar (nicht geblacklistet) ist
     */
    public boolean isAvailable(Material material) {
        return availableItems.contains(material);
    }

    /**
     * Gibt die Anzahl der verfügbaren Items zurück
     */
    public int getAvailableItemCount() {
        return availableItems.size();
    }

    /**
     * Gibt alle verfügbaren Items zurück
     */
    public List<Material> getAvailableItems() {
        return new ArrayList<>(availableItems);
    }
}
