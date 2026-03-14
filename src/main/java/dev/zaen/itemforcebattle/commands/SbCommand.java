package dev.zaen.itemforcebattle.commands;

import dev.zaen.itemforcebattle.BetterItemForceBattle;
import dev.zaen.itemforcebattle.managers.ScoreboardManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SbCommand implements CommandExecutor {

    private final BetterItemForceBattle plugin;
    private final ScoreboardManager scoreboardManager;

    public SbCommand(BetterItemForceBattle plugin) {
        this.plugin = plugin;
        this.scoreboardManager = plugin.getScoreboardManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.getMessageManager().getPlayerOnly());
            return true;
        }

        if (!plugin.getGameManager().isGameRunning()) {
            sender.sendMessage(plugin.getMessageManager().getEventNotRunning());
            return true;
        }

        scoreboardManager.toggleScoreboard(player);
        return true;
    }
}
