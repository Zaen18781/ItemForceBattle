package dev.zaen.itemforcebattle.managers;

import dev.zaen.itemforcebattle.BetterItemForceBattle;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Display;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ItemDisplayManager {

    private final BetterItemForceBattle plugin;
    private final Map<UUID, ItemDisplay> playerDisplays;
    private BukkitTask updateTask;

    public ItemDisplayManager(BetterItemForceBattle plugin) {
        this.plugin = plugin;
        this.playerDisplays = new HashMap<>();
    }

    public void updateDisplay(Player player, Material material) {
        removeDisplay(player);

        if (material == null) return;

        double height = plugin.getConfigManager().getHeightAboveHead() + 1.8;
        Location displayLocation = player.getLocation().add(0, height, 0);

        ItemDisplay display = player.getWorld().spawn(displayLocation, ItemDisplay.class, itemDisplay -> {
            itemDisplay.setItemStack(new ItemStack(material));
            itemDisplay.setBillboard(Display.Billboard.CENTER);
            itemDisplay.setViewRange(64);
            itemDisplay.setTransformation(new Transformation(
                new Vector3f(0, 0, 0),
                new AxisAngle4f(0, 0, 1, 0),
                new Vector3f(0.6f, 0.6f, 0.6f),
                new AxisAngle4f(0, 0, 1, 0)
            ));
            itemDisplay.setInterpolationDuration(0);
            itemDisplay.setTeleportDuration(3);
        });

        playerDisplays.put(player.getUniqueId(), display);
        startUpdateTask();
    }

    public void removeDisplay(Player player) {
        removeDisplay(player.getUniqueId());
    }

    public void removeDisplay(UUID uuid) {
        ItemDisplay display = playerDisplays.remove(uuid);
        if (display != null && display.isValid()) {
            display.remove();
        }
    }

    public void removeAllDisplays() {
        for (ItemDisplay display : playerDisplays.values()) {
            if (display != null && display.isValid()) {
                display.remove();
            }
        }
        playerDisplays.clear();

        if (updateTask != null) {
            updateTask.cancel();
            updateTask = null;
        }
    }

    private void startUpdateTask() {
        if (updateTask != null) return;

        updateTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (playerDisplays.isEmpty()) {
                    cancel();
                    updateTask = null;
                    return;
                }

                double height = plugin.getConfigManager().getHeightAboveHead() + 1.8;

                for (Map.Entry<UUID, ItemDisplay> entry : playerDisplays.entrySet()) {
                    Player player = Bukkit.getPlayer(entry.getKey());
                    ItemDisplay display = entry.getValue();

                    if (player == null || !player.isOnline() || display == null || !display.isValid()) {
                        continue;
                    }

                    Location newLoc = player.getLocation().add(0, height, 0);
                    display.teleport(newLoc);
                }
            }
        }.runTaskTimer(plugin, 1L, 1L);
    }

    public boolean hasDisplay(UUID uuid) {
        return playerDisplays.containsKey(uuid);
    }

    public ItemDisplay getDisplay(UUID uuid) {
        return playerDisplays.get(uuid);
    }
}
