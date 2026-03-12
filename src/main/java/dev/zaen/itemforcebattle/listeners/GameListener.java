package dev.zaen.itemforcebattle.listeners;

import dev.zaen.itemforcebattle.BetterItemForceBattle;
import dev.zaen.itemforcebattle.managers.GameManager;
import dev.zaen.itemforcebattle.managers.PlayerData;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class GameListener implements Listener {

    private final BetterItemForceBattle plugin;
    private final GameManager gameManager;

    public GameListener(BetterItemForceBattle plugin) {
        this.plugin = plugin;
        this.gameManager = plugin.getGameManager();
    }

    /**
     * Wird aufgerufen wenn ein Spieler ein Item aufhebt
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onItemPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!gameManager.isGameRunning()) return;

        Material material = event.getItem().getItemStack().getType();
        gameManager.onItemPickup(player, material);
    }

    /**
     * Verhindert das Droppen von Starter-Items (optional)
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onItemDrop(PlayerDropItemEvent event) {
        if (!gameManager.isGameRunning()) return;

        Player player = event.getPlayer();
        PlayerData data = gameManager.getPlayerData(player.getUniqueId());
        if (data == null) return;

        // Starter Items nicht droppen lassen (Pickaxe check)
        ItemStack dropped = event.getItemDrop().getItemStack();
        if (dropped.getItemMeta() != null && dropped.getItemMeta().isUnbreakable()) {
            event.setCancelled(true);
        }
    }

    /**
     * Räumt auf wenn ein Spieler das Spiel verlässt
     * Spieler bleibt im Spiel und kann später wieder joinen
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        // Display entfernen (wird beim Rejoin neu erstellt)
        plugin.getItemDisplayManager().removeDisplay(player);

        // Scoreboard entfernen (wird beim Rejoin neu erstellt)
        plugin.getScoreboardManager().removeScoreboard(player);

        // WICHTIG: Spieler-Daten werden NICHT entfernt!
        // Der Spieler bleibt im Spiel und kann wieder joinen
    }

    /**
     * Stellt Display und Scoreboard wieder her wenn ein Spieler rejoint
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (!gameManager.isGameRunning()) return;

        PlayerData data = gameManager.getPlayerData(player.getUniqueId());
        if (data == null) return;

        // Spieler ist Teil des laufenden Spiels - Display und Scoreboard wiederherstellen
        if (data.getCurrentItem() != null) {
            plugin.getItemDisplayManager().updateDisplay(player, data.getCurrentItem());
        }

        plugin.getScoreboardManager().createScoreboard(player);
    }

    /**
     * Prüft auch bei Inventar-Klicks (z.B. Crafting)
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!gameManager.isGameRunning()) return;

        // Nach kurzer Verzögerung prüfen ob das Ziel-Item im Inventar ist
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            PlayerData data = gameManager.getPlayerData(player.getUniqueId());
            if (data == null || data.getCurrentItem() == null) return;

            if (player.getInventory().contains(data.getCurrentItem())) {
                gameManager.onItemPickup(player, data.getCurrentItem());
            }
        }, 1L);
    }
}
