package dev.zaen.itemforcebattle.commands;

import dev.zaen.itemforcebattle.BetterItemForceBattle;
import dev.zaen.itemforcebattle.config.MessageManager;
import dev.zaen.itemforcebattle.managers.GameManager;
import dev.zaen.itemforcebattle.utils.TextUtil;
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

    private static final String SEP = "<#478ED2>●&m                                    &r<#478ED2>●";

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
        sender.sendMessage(TextUtil.parse(SEP));
        sender.sendMessage(TextUtil.parse(" <#C539EC><b>ɪᴛᴇᴍꜰᴏʀᴄᴇʙᴀᴛᴛʟᴇ</b> <dark_gray>» <white>ʜɪʟꜰᴇ"));
        sender.sendMessage(TextUtil.parse(SEP));
        sender.sendMessage(TextUtil.parse("<#478ED2>▎ <white>/itemforce start <dark_gray>→ <gray>sᴛᴀʀᴛᴇᴛ ᴅᴀs ᴇᴠᴇɴᴛ"));
        sender.sendMessage(TextUtil.parse("<#478ED2>▎ <white>/itemforce stop <dark_gray>→ <gray>sᴛᴏᴘᴘᴛ ᴅᴀs ᴇᴠᴇɴᴛ"));
        sender.sendMessage(TextUtil.parse("<#478ED2>▎ <white>/itemforce setspawn <dark_gray>→ <gray>sᴇᴛᴢᴛ ᴅᴇɴ sᴘᴀᴡɴ"));
        sender.sendMessage(TextUtil.parse("<#478ED2>▎ <white>/itemforce addplayer <gray><Spieler> <dark_gray>→ <gray>ꜰüɢᴛ sᴘɪᴇʟᴇʀ ʜɪɴᴢᴜ"));
        sender.sendMessage(TextUtil.parse("<#478ED2>▎ <white>/itemforce gui <dark_gray>→ <gray>öꜰꜰɴᴇᴛ ᴅɪᴇ sᴘɪᴇʟᴇʀʟɪsᴛᴇ"));
        sender.sendMessage(TextUtil.parse("<#478ED2>▎ <white>/itemforce reload <dark_gray>→ <gray>ʟäᴅᴛ ᴅɪᴇ ᴋᴏɴғɪɢ ɴᴇᴜ"));
        sender.sendMessage(TextUtil.parse("<#478ED2>▎ <white>/sb <dark_gray>→ <gray>sᴄᴏʀᴇʙᴏᴀʀᴅ ᴇɪɴ/ᴀᴜsʙʟᴇɴᴅᴇɴ"));
        sender.sendMessage(TextUtil.parse("<#478ED2>▎ <white>/skip <dark_gray>→ <gray>ᴀᴋᴛᴜᴇʟʟᴇs ɪᴛᴇᴍ üʙᴇʀsᴘʀɪɴɢᴇɴ"));
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
