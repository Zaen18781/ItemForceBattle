package dev.zaen.itemforcebattle.commands;

import dev.zaen.itemforcebattle.BetterItemForceBattle;
import dev.zaen.itemforcebattle.config.MessageManager;
import dev.zaen.itemforcebattle.managers.GameManager;
import dev.zaen.itemforcebattle.utils.ColorUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SkipCommand implements CommandExecutor {

    private final BetterItemForceBattle plugin;
    private final MessageManager messageManager;
    private final GameManager gameManager;

    public SkipCommand(BetterItemForceBattle plugin) {
        this.plugin = plugin;
        this.messageManager = plugin.getMessageManager();
        this.gameManager = plugin.getGameManager();
    }

    private String applySmallCaps(String text) {
        if (plugin.getScoreboardConfig() != null && plugin.getScoreboardConfig().isSmallCapsEnabled()) {
            return ColorUtils.toSmallCaps(text);
        }
        return text;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ColorUtils.colorize(applySmallCaps("<#ff0000>Dieser Befehl kann nur von Spielern ausgefuehrt werden!")));
            return true;
        }

        if (!player.hasPermission("itemforce.play")) {
            player.sendMessage(messageManager.getNoPermission());
            return true;
        }

        if (!gameManager.isGameRunning()) {
            player.sendMessage(messageManager.getEventNotRunning());
            return true;
        }

        gameManager.useSkip(player);

        return true;
    }
}
