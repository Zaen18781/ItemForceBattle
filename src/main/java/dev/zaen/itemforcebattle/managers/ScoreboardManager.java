package dev.zaen.itemforcebattle.managers;

import dev.zaen.itemforcebattle.BetterItemForceBattle;
import dev.zaen.itemforcebattle.config.ScoreboardConfig;
import dev.zaen.itemforcebattle.utils.TextUtil;
import io.papermc.paper.scoreboard.numbers.NumberFormat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.ShadowColor;
import net.kyori.adventure.text.format.Style;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.*;

public class ScoreboardManager {

    private static final String OBJ = "itemforce";

    private final BetterItemForceBattle plugin;
    private final ScoreboardConfig sbCfg;
    private final Map<UUID, Scoreboard> boards = new HashMap<>();
    private final Set<UUID> hidden = new HashSet<>();

    public ScoreboardManager(BetterItemForceBattle plugin) {
        this.plugin = plugin;
        this.sbCfg = plugin.getScoreboardConfig();
    }

    public void createScoreboards() {
        plugin.getGameManager().getAllPlayerData().keySet().forEach(uuid -> {
            Player p = Bukkit.getPlayer(uuid);
            if (p != null) createScoreboard(p);
        });
    }

    public void createScoreboard(Player player) {
        Scoreboard sb = Bukkit.getScoreboardManager().getNewScoreboard();
        boolean shadow = sbCfg.isTextShadowEnabled();

        Component title = TextUtil.parse(sbCfg.getTitle());
        if (shadow) title = withShadow(title);

        Objective obj = sb.registerNewObjective(OBJ, Criteria.DUMMY, title);
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        boards.put(player.getUniqueId(), sb);
        if (!isHidden(player)) player.setScoreboard(sb);
        updateScoreboard(player);
    }

    public void updateScoreboard(Player player) {
        updateScoreboard(player, plugin.getGameManager().getSortedPlayers());
    }

    private void updateScoreboard(Player player, List<Map.Entry<UUID, PlayerData>> sorted) {
        if (isHidden(player)) return;
        Scoreboard sb = boards.get(player.getUniqueId());
        if (sb == null) return;
        Objective obj = sb.getObjective(OBJ);
        if (obj == null) return;

        GameManager gm = plugin.getGameManager();
        boolean shadow = sbCfg.isTextShadowEnabled();
        List<String> cfgLines = sbCfg.getLines();

        String time = gm.formatTime(gm.getRemainingSeconds());
        PlayerData pd = gm.getPlayerData(player.getUniqueId());
        int pts = pd != null ? pd.getPoints() : 0;

        int lineScore = cfgLines.size();
        for (String raw : cfgLines) {
            String processed;
            if (raw.matches("#[1-9]")) {
                int rank = Integer.parseInt(raw.substring(1));
                int idx = rank - 1;
                processed = idx < sorted.size()
                    ? buildPlayerLine(rank, sorted.get(idx), player)
                    : "<#478ED2>" + rank + ". <dark_gray>N/A";
            } else {
                processed = raw.replace("%time%", time).replace("%points%", String.valueOf(pts));
            }
            lineScore = setLine(sb, obj, lineScore, processed, shadow);
        }
    }

    private String buildPlayerLine(int rank, Map.Entry<UUID, PlayerData> entry, Player viewer) {
        Player p = Bukkit.getPlayer(entry.getKey());
        String name = p != null ? p.getName() : "???";
        int pts = entry.getValue().getPoints();
        return "<#478ED2>" + rank + ". <white>" + name + " <#FFD700>" + pts;
    }

    private int setLine(Scoreboard sb, Objective obj, int score, String mm, boolean shadow) {
        String teamName = "ifb_" + score;
        Team team = sb.getTeam(teamName);
        if (team == null) {
            String entry = "\u00a7" + Integer.toHexString(score & 0xF);
            while (sb.getEntries().contains(entry)) entry += " ";
            team = sb.registerNewTeam(teamName);
            team.addEntry(entry);
            team.suffix(Component.empty());
            var s = obj.getScore(entry);
            s.setScore(score);
            s.numberFormat(NumberFormat.blank());
        }
        Component content = TextUtil.parse(mm);
        if (shadow) content = withShadow(content);
        team.prefix(content);
        return score - 1;
    }

    private Component withShadow(Component c) {
        return c.style(c.style().merge(Style.style().shadowColor(ShadowColor.shadowColor(0xAA000000)).build()));
    }

    public void updateAllScoreboards() {
        List<Map.Entry<UUID, PlayerData>> sorted = plugin.getGameManager().getSortedPlayers();
        for (UUID uuid : new HashSet<>(boards.keySet())) {
            Player p = Bukkit.getPlayer(uuid);
            if (p != null && !isHidden(p)) updateScoreboard(p, sorted);
        }
    }

    public void removeScoreboard(Player player) {
        boards.remove(player.getUniqueId());
        hidden.remove(player.getUniqueId());
        player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
    }

    public void removeAllScoreboards() {
        for (UUID uuid : new HashSet<>(boards.keySet())) {
            Player p = Bukkit.getPlayer(uuid);
            if (p != null) p.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        }
        boards.clear();
        hidden.clear();
    }

    public void toggleScoreboard(Player player) {
        if (isHidden(player)) showScoreboard(player); else hideScoreboard(player);
    }

    public void showScoreboard(Player player) {
        hidden.remove(player.getUniqueId());
        Scoreboard sb = boards.get(player.getUniqueId());
        if (sb != null) {
            player.setScoreboard(sb);
            player.sendMessage(plugin.getMessageManager().getScoreboardShown());
        }
    }

    public void hideScoreboard(Player player) {
        hidden.add(player.getUniqueId());
        player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        player.sendMessage(plugin.getMessageManager().getScoreboardHidden());
    }

    public boolean isHidden(Player player) {
        return hidden.contains(player.getUniqueId());
    }
}
