package dev.zaen.itemforcebattle.managers;

import dev.zaen.itemforcebattle.BetterItemForceBattle;
import dev.zaen.itemforcebattle.config.ConfigManager;
import org.bukkit.Material;
import org.bukkit.entity.Display;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ItemDisplayManager {

    private final BetterItemForceBattle plugin;
    private final Map<UUID, ItemDisplay> playerDisplays = new HashMap<>();

    public ItemDisplayManager(BetterItemForceBattle plugin) {
        this.plugin = plugin;
    }

    public void updateDisplay(Player player, Material material) {
        removeDisplay(player);
        ConfigManager cfg = plugin.getConfigManager();
        if (!cfg.isDisplayEnabled() || material == null) return;

        ItemStack item = new ItemStack(material);
        Display.Billboard billboard = parseBillboard(cfg.getDisplayBillboard());
        float scale = cfg.getDisplayScale();
        float height = (float) cfg.getDisplayHeight();

        org.bukkit.Location spawnLoc = player.getLocation().clone();
        spawnLoc.setPitch(0);
        spawnLoc.setYaw(0);

        ItemDisplay display = player.getWorld().spawn(spawnLoc, ItemDisplay.class, d -> {
            d.setItemStack(item);
            d.setBillboard(billboard);
            d.setViewRange(cfg.getDisplayViewRange());
            d.setTransformation(new Transformation(
                new Vector3f(0, height, 0),
                new AxisAngle4f(0, 0, 0, 1),
                new Vector3f(scale, scale, scale),
                new AxisAngle4f(0, 0, 0, 1)
            ));
            d.setInterpolationDuration(0);
            d.setTeleportDuration(0);
            d.setGravity(false);
            d.setSilent(true);
        });

        playerDisplays.put(player.getUniqueId(), display);
        player.addPassenger(display);
    }

    private Display.Billboard parseBillboard(String name) {
        return switch (name) {
            case "FIXED" -> Display.Billboard.FIXED;
            case "VERTICAL" -> Display.Billboard.VERTICAL;
            case "HORIZONTAL" -> Display.Billboard.HORIZONTAL;
            default -> Display.Billboard.CENTER;
        };
    }

    public void removeDisplay(Player player) {
        removeDisplay(player.getUniqueId());
    }

    public void removeDisplay(UUID uuid) {
        ItemDisplay display = playerDisplays.remove(uuid);
        if (display != null && display.isValid()) display.remove();
    }

    public void removeAllDisplays() {
        for (ItemDisplay display : playerDisplays.values()) {
            if (display != null && display.isValid()) display.remove();
        }
        playerDisplays.clear();
    }
}
