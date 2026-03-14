package dev.zaen.itemforcebattle.listeners;

import dev.zaen.itemforcebattle.BetterItemForceBattle;
import dev.zaen.itemforcebattle.managers.GameManager;
import dev.zaen.itemforcebattle.managers.PlayerData;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class GameListener implements Listener {

    private final BetterItemForceBattle plugin;
    private final GameManager gameManager;

    public GameListener(BetterItemForceBattle plugin) {
        this.plugin = plugin;
        this.gameManager = plugin.getGameManager();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onItemPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!gameManager.isGameRunning()) return;
        gameManager.onItemPickup(player, event.getItem().getItemStack().getType());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onItemDrop(PlayerDropItemEvent event) {
        if (!gameManager.isGameRunning()) return;
        if (gameManager.getPlayerData(event.getPlayer().getUniqueId()) == null) return;
        ItemStack dropped = event.getItemDrop().getItemStack();
        if (dropped.getItemMeta() != null && dropped.getItemMeta().isUnbreakable()) {
            event.setCancelled(true);
            return;
        }
        if (isJokerItem(dropped)) event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!gameManager.isGameRunning()) return;
        if (event.getItem() == null) return;
        if (event.getAction().isRightClick() && isJokerItem(event.getItem())) {
            event.setCancelled(true);
            gameManager.useSkip(event.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        plugin.getItemDisplayManager().removeDisplay(player);
        plugin.getScoreboardManager().removeScoreboard(player);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!gameManager.isGameRunning()) return;
        PlayerData data = gameManager.getPlayerData(player.getUniqueId());
        if (data == null) return;
        if (data.getCurrentItem() != null) {
            plugin.getItemDisplayManager().updateDisplay(player, data.getCurrentItem());
        }
        plugin.getScoreboardManager().createScoreboard(player);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!gameManager.isGameRunning()) return;
        if (event.getCursor() != null && isJokerItem(event.getCursor())) {
            event.setCancelled(true);
            return;
        }
        if (event.getCurrentItem() != null && isJokerItem(event.getCurrentItem())) {
            event.setCancelled(true);
            return;
        }
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            PlayerData data = gameManager.getPlayerData(player.getUniqueId());
            if (data == null || data.getCurrentItem() == null) return;
            if (player.getInventory().contains(data.getCurrentItem())) {
                gameManager.onItemPickup(player, data.getCurrentItem());
            }
        }, 1L);
    }

    private boolean isJokerItem(ItemStack item) {
        if (item == null || item.getType() != Material.BARRIER) return false;
        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) return false;
        String name = PlainTextComponentSerializer.plainText().serialize(meta.displayName());
        return name.toLowerCase().contains("joker") || name.toLowerCase().contains("ᴊᴏᴋᴇʀ");
    }
}
