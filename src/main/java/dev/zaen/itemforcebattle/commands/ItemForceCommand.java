package dev.zaen.itemforcebattle.commands;

import dev.zaen.itemforcebattle.BetterItemForceBattle;
import dev.zaen.itemforcebattle.config.MessageManager;
import dev.zaen.itemforcebattle.managers.GameManager;
import dev.zaen.itemforcebattle.utils.Colors;
import dev.zaen.itemforcebattle.utils.TextUtil;
import dev.zaen.itemforcebattle.utils.Unicodes;
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

    private static final String SEP = Colors.BLUE.getHex() + Unicodes.ROUND_DOT.getString() + "&m                                    &r</color>" + Colors.BLUE.getHex() + Unicodes.ROUND_DOT.getString() + "</color>";

    private final BetterItemForceBattle plugin;
    private final MessageManager messageManager;
    private final GameManager gameManager;

    public ItemForceCommand(BetterItemForceBattle plugin) {
        this.plugin = plugin;
        this.messageManager = plugin.getMessageManager();
        this.gameManager = plugin.getGameManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) { sendHelp(sender); return true; }

        switch (args[0].toLowerCase()) {
            case "start" -> handleStart(sender);
            case "stop" -> handleStop(sender);
            case "setspawn" -> handleSetSpawn(sender);
            case "addplayer" -> handleAddPlayer(sender, args);
            case "reload" -> handleReload(sender);
            case "gui" -> handleGUI(sender);
            case "help" -> sendHelp(sender);
            default -> sender.sendMessage(TextUtil.parse(
                messageManager.getPrefix() + "<#ff0000>Unbekannter Befehl! <gray>Nutze /itemforce help"));
        }
        return true;
    }

    private void handleStart(CommandSender sender) {
        if (!sender.hasPermission("itemforce.admin")) { sender.sendMessage(messageManager.getNoPermission()); return; }
        if (gameManager.isGameRunning() || gameManager.isCountdownActive()) { sender.sendMessage(messageManager.getEventAlreadyRunning()); return; }
        if (plugin.getConfigManager().getSpawnLocation() == null) { sender.sendMessage(messageManager.getSpawnNotSet()); return; }
        if (!gameManager.startGame()) {
            sender.sendMessage(TextUtil.parse(messageManager.getPrefix() + "<#ff0000>ᴇᴠᴇɴᴛ ᴋᴏɴɴᴛᴇ ɴɪᴄʜᴛ ɢᴇsᴛᴀʀᴛᴇᴛ ᴡᴇʀᴅᴇɴ!"));
        }
    }

    private void handleStop(CommandSender sender) {
        if (!sender.hasPermission("itemforce.admin")) { sender.sendMessage(messageManager.getNoPermission()); return; }
        if (!gameManager.isGameRunning() && !gameManager.isCountdownActive()) { sender.sendMessage(messageManager.getEventNotRunning()); return; }
        gameManager.stopGame(true);
    }

    private void handleSetSpawn(CommandSender sender) {
        if (!sender.hasPermission("itemforce.admin")) { sender.sendMessage(messageManager.getNoPermission()); return; }
        if (!(sender instanceof Player player)) {
            sender.sendMessage(messageManager.getPlayerOnly());
            return;
        }
        plugin.getConfigManager().saveSpawnLocation(player.getLocation());
        sender.sendMessage(messageManager.getSpawnSet());
    }

    private void handleAddPlayer(CommandSender sender, String[] args) {
        if (!sender.hasPermission("itemforce.admin")) { sender.sendMessage(messageManager.getNoPermission()); return; }
        if (!gameManager.isGameRunning()) { sender.sendMessage(messageManager.getEventNotRunning()); return; }
        if (args.length < 2) {
            sender.sendMessage(TextUtil.parse(messageManager.getPrefix() + "<#ff0000>ʙᴇɴᴜᴛᴢᴜɴɢ: <white>/itemforce addplayer <Spieler>"));
            return;
        }
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) { sender.sendMessage(messageManager.getPlayerNotFound(args[1])); return; }
        if (gameManager.getPlayerData(target.getUniqueId()) != null) { sender.sendMessage(messageManager.getPlayerAlreadyInGame()); return; }
        if (gameManager.addPlayer(target)) {
            sender.sendMessage(messageManager.getPlayerAdded(target.getName()));
            target.sendMessage(messageManager.getPlayerAddedSelf());
        } else {
            sender.sendMessage(TextUtil.parse(messageManager.getPrefix() + "<#ff0000>sᴘɪᴇʟᴇʀ ᴋᴏɴɴᴛᴇ ɴɪᴄʜᴛ ʜɪɴᴢᴜɢᴇғüɢᴛ ᴡᴇʀᴅᴇɴ!"));
        }
    }

    private void handleReload(CommandSender sender) {
        if (!sender.hasPermission("itemforce.admin")) { sender.sendMessage(messageManager.getNoPermission()); return; }
        plugin.reload();
        sender.sendMessage(messageManager.getConfigReloaded());
    }

    private void handleGUI(CommandSender sender) {
        if (!(sender instanceof Player player)) { sender.sendMessage(messageManager.getPlayerOnly()); return; }
        if (!gameManager.isGameRunning()) { sender.sendMessage(messageManager.getEventNotRunning()); return; }
        plugin.getPlayerListGUI().openGUI(player);
    }

    private void sendHelp(CommandSender sender) {
        String b = Colors.BLUE.getHex();
        String dot = Unicodes.ROUND_DOT.getString();
        String arrow = Unicodes.ARROW.getString();
        sender.sendMessage(TextUtil.parse(SEP));
        sender.sendMessage(TextUtil.parse(" " + b + "<b>ɪᴛᴇᴍ</b></color><grey><b>ʙᴀᴛᴛʟᴇ</b></grey> <dark_gray>" + arrow + " <white>ʜɪʟꜰᴇ</white>"));
        sender.sendMessage(TextUtil.parse(SEP));
        sender.sendMessage(TextUtil.parse("<grey>" + b + dot + "</color> <white>/itemforce start</white> <dark_gray>" + arrow + "</dark_gray> <grey>sᴛᴀʀᴛᴇᴛ ᴅᴀs ᴇᴠᴇɴᴛ</grey>"));
        sender.sendMessage(TextUtil.parse("<grey>" + b + dot + "</color> <white>/itemforce stop</white> <dark_gray>" + arrow + "</dark_gray> <grey>sᴛᴏᴘᴘᴛ ᴅᴀs ᴇᴠᴇɴᴛ</grey>"));
        sender.sendMessage(TextUtil.parse("<grey>" + b + dot + "</color> <white>/itemforce setspawn</white> <dark_gray>" + arrow + "</dark_gray> <grey>sᴇᴛᴢᴛ ᴅᴇɴ sᴘᴀᴡɴ</grey>"));
        sender.sendMessage(TextUtil.parse("<grey>" + b + dot + "</color> <white>/itemforce addplayer <" + b + "Spieler</color>></white> <dark_gray>" + arrow + "</dark_gray> <grey>ꜰüɢᴛ sᴘɪᴇʟᴇʀ ʜɪɴᴢᴜ</grey>"));
        sender.sendMessage(TextUtil.parse("<grey>" + b + dot + "</color> <white>/itemforce gui</white> <dark_gray>" + arrow + "</dark_gray> <grey>öꜰꜰɴᴇᴛ ᴅɪᴇ sᴘɪᴇʟᴇʀʟɪsᴛᴇ</grey>"));
        sender.sendMessage(TextUtil.parse("<grey>" + b + dot + "</color> <white>/itemforce reload</white> <dark_gray>" + arrow + "</dark_gray> <grey>ʟäᴅᴛ ᴅɪᴇ ᴋᴏɴꜰɪɢ ɴᴇᴜ</grey>"));
        sender.sendMessage(TextUtil.parse("<grey>" + b + dot + "</color> <white>/sb</white> <dark_gray>" + arrow + "</dark_gray> <grey>sᴄᴏʀᴇʙᴏᴀʀᴅ ᴇɪɴ/ᴀᴜsʙʟᴇɴᴅᴇɴ</grey>"));
        sender.sendMessage(TextUtil.parse("<grey>" + b + dot + "</color> <white>/skip</white> <dark_gray>" + arrow + "</dark_gray> <grey>ᴀᴋᴛᴜᴇʟʟᴇs ɪᴛᴇᴍ üʙᴇʀsᴘʀɪɴɢᴇɴ</grey>"));
        sender.sendMessage(TextUtil.parse(SEP));
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            List<String> subs = Arrays.asList("start", "stop", "setspawn", "addplayer", "reload", "gui", "help");
            List<String> completions = new ArrayList<>();
            for (String sub : subs) {
                if (sub.startsWith(args[0].toLowerCase())) completions.add(sub);
            }
            return completions;
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("addplayer")) {
            List<String> completions = new ArrayList<>();
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getName().toLowerCase().startsWith(args[1].toLowerCase())) completions.add(player.getName());
            }
            return completions;
        }
        return new ArrayList<>();
    }
}
