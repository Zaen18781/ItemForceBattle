package dev.zaen.itemforcebattle.commands;

import dev.zaen.itemforcebattle.BetterItemForceBattle;
import dev.zaen.itemforcebattle.config.MessageManager;
import dev.zaen.itemforcebattle.managers.GameManager;
import dev.zaen.itemforcebattle.utils.ColorUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemForceCommand implements CommandExecutor, TabCompleter {

    private final BetterItemForceBattle plugin;
    private final MessageManager messageManager;
    private final GameManager gameManager;

    public ItemForceCommand(BetterItemForceBattle plugin) {
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
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "start" -> handleStart(sender);
            case "stop" -> handleStop(sender);
            case "setspawn" -> handleSetSpawn(sender);
            case "addplayer" -> handleAddPlayer(sender, args);
            case "reload" -> handleReload(sender);
            case "help" -> sendHelp(sender);
            default -> {
                sender.sendMessage(ColorUtils.colorize(applySmallCaps(messageManager.getPrefix() + "<#ff0000>Unbekannter Befehl! Nutze /itemforce help")));
            }
        }

        return true;
    }

    private void handleStart(CommandSender sender) {
        if (!sender.hasPermission("itemforce.admin")) {
            sender.sendMessage(messageManager.getNoPermission());
            return;
        }

        if (gameManager.isGameRunning() || gameManager.isCountdownActive()) {
            sender.sendMessage(messageManager.getEventAlreadyRunning());
            return;
        }

        if (plugin.getConfigManager().getSpawnLocation() == null) {
            sender.sendMessage(messageManager.getSpawnNotSet());
            return;
        }

        boolean success = gameManager.startGame();
        if (!success) {
            sender.sendMessage(ColorUtils.colorize(applySmallCaps(messageManager.getPrefix() + "<#ff0000>Das Event konnte nicht gestartet werden!")));
        }
    }

    private void handleStop(CommandSender sender) {
        if (!sender.hasPermission("itemforce.admin")) {
            sender.sendMessage(messageManager.getNoPermission());
            return;
        }

        if (!gameManager.isGameRunning() && !gameManager.isCountdownActive()) {
            sender.sendMessage(messageManager.getEventNotRunning());
            return;
        }

        gameManager.stopGame(true);
    }

    private void handleSetSpawn(CommandSender sender) {
        if (!sender.hasPermission("itemforce.admin")) {
            sender.sendMessage(messageManager.getNoPermission());
            return;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage(ColorUtils.colorize(applySmallCaps(messageManager.getPrefix() + "<#ff0000>Dieser Befehl kann nur von Spielern ausgefuehrt werden!")));
            return;
        }

        plugin.getConfigManager().saveSpawnLocation(player.getLocation());
        sender.sendMessage(messageManager.getSpawnSet());
    }

    private void handleAddPlayer(CommandSender sender, String[] args) {
        if (!sender.hasPermission("itemforce.admin")) {
            sender.sendMessage(messageManager.getNoPermission());
            return;
        }

        if (!gameManager.isGameRunning()) {
            sender.sendMessage(messageManager.getEventNotRunning());
            return;
        }

        if (args.length < 2) {
            sender.sendMessage(ColorUtils.colorize(applySmallCaps(messageManager.getPrefix() + "<#ff0000>Benutzung: /itemforce addplayer <spieler>")));
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(ColorUtils.colorize(applySmallCaps(messageManager.getPrefix() + "<#ff0000>Spieler nicht gefunden!")));
            return;
        }

        if (gameManager.getPlayerData(target.getUniqueId()) != null) {
            sender.sendMessage(ColorUtils.colorize(applySmallCaps(messageManager.getPrefix() + "<#ff0000>Dieser Spieler ist bereits im Spiel!")));
            return;
        }

        boolean success = gameManager.addPlayer(target);
        if (success) {
            sender.sendMessage(ColorUtils.colorize(applySmallCaps(messageManager.getPrefix() + "<#00EE39>" + target.getName() + " wurde zum Spiel hinzugefuegt!")));
            target.sendMessage(ColorUtils.colorize(applySmallCaps(messageManager.getPrefix() + "<#00EE39>Du wurdest zum Item Force Battle hinzugefuegt!")));
        } else {
            sender.sendMessage(ColorUtils.colorize(applySmallCaps(messageManager.getPrefix() + "<#ff0000>Spieler konnte nicht hinzugefuegt werden!")));
        }
    }

    private void handleReload(CommandSender sender) {
        if (!sender.hasPermission("itemforce.admin")) {
            sender.sendMessage(messageManager.getNoPermission());
            return;
        }

        plugin.reload();
        sender.sendMessage(ColorUtils.colorize(applySmallCaps(messageManager.getPrefix() + "<#00EE39>Konfiguration wurde neu geladen!")));
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(ColorUtils.colorize(applySmallCaps("<#478ED2>━━━━━━━━━ <#6953B5>ItemForceBattle Hilfe <#478ED2>━━━━━━━━━")));
        sender.sendMessage(ColorUtils.colorize(applySmallCaps("<#478ED2>/itemforce start <#6953B5>- <#7>Startet das Event")));
        sender.sendMessage(ColorUtils.colorize(applySmallCaps("<#478ED2>/itemforce stop <#6953B5>- <#7>Stoppt das Event")));
        sender.sendMessage(ColorUtils.colorize(applySmallCaps("<#478ED2>/itemforce setspawn <#6953B5>- <#7>Setzt den Spawn")));
        sender.sendMessage(ColorUtils.colorize(applySmallCaps("<#478ED2>/itemforce addplayer <Spieler> <#6953B5>- <#7>Fuegt einen Spieler hinzu")));
        sender.sendMessage(ColorUtils.colorize(applySmallCaps("<#478ED2>/itemforce reload <#6953B5>- <#7>Laedt die Config neu")));
        sender.sendMessage(ColorUtils.colorize(applySmallCaps("<#478ED2>/skip <#6953B5>- <#7>Ueberspringt dein aktuelles Item")));
        sender.sendMessage(ColorUtils.colorize(applySmallCaps("<#478ED2>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")));
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            List<String> subCommands = Arrays.asList("start", "stop", "setspawn", "addplayer", "reload", "help");

            for (String sub : subCommands) {
                if (sub.toLowerCase().startsWith(args[0].toLowerCase())) {
                    completions.add(sub);
                }
            }

            return completions;
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("addplayer")) {
            List<String> completions = new ArrayList<>();
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
                    completions.add(player.getName());
                }
            }
            return completions;
        }

        return new ArrayList<>();
    }
}
