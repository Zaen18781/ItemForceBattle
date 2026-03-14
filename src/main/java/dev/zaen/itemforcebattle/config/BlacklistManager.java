package dev.zaen.itemforcebattle.config;

import dev.zaen.itemforcebattle.BetterItemForceBattle;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.*;

public class BlacklistManager {

    private final BetterItemForceBattle plugin;
    private final Random random = new Random();
    private File whitelistFile;
    private FileConfiguration whitelistConfig;
    private List<Material> whitelistedItems;
    private Map<UUID, Set<Material>> playerCollectedItems;

    public BlacklistManager(BetterItemForceBattle plugin) {
        this.plugin = plugin;
        this.whitelistedItems = new ArrayList<>();
        this.playerCollectedItems = new HashMap<>();
        reload();
    }

    public void reload() {
        whitelistFile = new File(plugin.getDataFolder(), "whitelist.yml");
        if (!whitelistFile.exists()) plugin.saveResource("whitelist.yml", false);
        whitelistConfig = YamlConfiguration.loadConfiguration(whitelistFile);
        loadWhitelist();
    }

    private void loadWhitelist() {
        whitelistedItems.clear();
        for (String itemName : whitelistConfig.getStringList("whitelist")) {
            try {
                Material material = Material.valueOf(itemName.toUpperCase());
                if (material.isItem()) whitelistedItems.add(material);
            } catch (IllegalArgumentException ignored) {}
        }
        plugin.getLogger().info("Whitelisted items loaded: " + whitelistedItems.size());
    }

    public Material getRandomItem(UUID playerUuid) {
        Set<Material> collected = playerCollectedItems.getOrDefault(playerUuid, Collections.emptySet());
        List<Material> available = new ArrayList<>(whitelistedItems.size());
        for (Material material : whitelistedItems) {
            if (!collected.contains(material)) available.add(material);
        }
        if (available.isEmpty()) available.addAll(whitelistedItems);
        if (available.isEmpty()) {
            plugin.getLogger().severe("No available items in whitelist!");
            return Material.STONE;
        }
        return available.get(random.nextInt(available.size()));
    }

    public Material getRandomItem() {
        if (whitelistedItems.isEmpty()) {
            plugin.getLogger().severe("No items in whitelist!");
            return Material.STONE;
        }
        return whitelistedItems.get(random.nextInt(whitelistedItems.size()));
    }

    public void markCollected(UUID playerUuid, Material material) {
        playerCollectedItems.computeIfAbsent(playerUuid, k -> new HashSet<>()).add(material);
    }

    public void clearCollectedItems(UUID playerUuid) {
        playerCollectedItems.remove(playerUuid);
    }

    public void clearAllCollectedItems() {
        playerCollectedItems.clear();
    }

    public boolean isAvailable(Material material) {
        return whitelistedItems.contains(material);
    }

    public int getAvailableItemCount() {
        return whitelistedItems.size();
    }

    public List<Material> getAvailableItems() {
        return new ArrayList<>(whitelistedItems);
    }
}
