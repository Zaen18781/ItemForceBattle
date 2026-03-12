package dev.zaen.itemforcebattle.managers;

import org.bukkit.Material;

import java.util.UUID;

public class PlayerData {

    private final UUID playerUUID;
    private Material currentItem;
    private int points;
    private int skipsRemaining;

    public PlayerData(UUID playerUUID, int initialSkips) {
        this.playerUUID = playerUUID;
        this.currentItem = null;
        this.points = 0;
        this.skipsRemaining = initialSkips;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public Material getCurrentItem() {
        return currentItem;
    }

    public void setCurrentItem(Material currentItem) {
        this.currentItem = currentItem;
    }

    public int getPoints() {
        return points;
    }

    public void addPoint() {
        this.points++;
    }

    public int getSkipsRemaining() {
        return skipsRemaining;
    }

    public boolean useSkip() {
        if (skipsRemaining > 0) {
            skipsRemaining--;
            return true;
        }
        return false;
    }

    public boolean hasSkipsRemaining() {
        return skipsRemaining > 0;
    }
}
