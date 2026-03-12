package dev.zaen.itemforcebattle.listeners;

import dev.zaen.itemforcebattle.BetterItemForceBattle;
import dev.zaen.itemforcebattle.managers.GameManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerProtectionListener implements Listener {

    private final BetterItemForceBattle plugin;
    private final GameManager gameManager;

    public PlayerProtectionListener(BetterItemForceBattle plugin) {
        this.plugin = plugin;
        this.gameManager = plugin.getGameManager();
    }

    /**
     * Verhindert jeglichen Schaden während des Events
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        
        // Während Event oder Countdown - kein Schaden
        if (gameManager.isGameRunning() || gameManager.isCountdownActive()) {
            if (gameManager.getPlayerData(player.getUniqueId()) != null) {
                event.setCancelled(true);
            }
        }
    }

    /**
     * Verhindert PvP während des Events
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player victim)) return;
        if (!(event.getDamager() instanceof Player attacker)) return;

        // Während Event - kein PvP
        if (gameManager.isGameRunning() || gameManager.isCountdownActive()) {
            event.setCancelled(true);
        }
    }

    /**
     * Verhindert Bewegung während des Countdowns (zusätzlich zum Freeze-Effekt)
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!gameManager.isCountdownActive()) return;

        Player player = event.getPlayer();
        if (gameManager.getPlayerData(player.getUniqueId()) == null) return;

        // Nur Kopfbewegung erlauben, keine tatsächliche Bewegung
        if (event.getFrom().getX() != event.getTo().getX() ||
            event.getFrom().getY() != event.getTo().getY() ||
            event.getFrom().getZ() != event.getTo().getZ()) {
            
            event.setTo(event.getFrom());
        }
    }
}
