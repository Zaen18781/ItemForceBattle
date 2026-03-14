# Design & Performance Documentation

Dieses Dokument beschreibt das vollständige Design-System und die Performance-Richtlinien von BetterItemForceBattle, übernommen und adaptiert aus BetterHideAndSeek. Es dient als verbindliche Referenz für alle UI-Entscheidungen im Projekt — jede neue Nachricht, jedes GUI-Element und jede Interaktion soll sich nahtlos in dieses System einfügen.

---

# Abschnitt 1 — Design-System

## 1.1 Farbsystem (`Colors.java`)

Alle Farben sind zentral in `dev.zaen.itemforcebattle.utils.Colors` als Enum definiert. Hex-Codes niemals direkt als String-Literale im Code schreiben — immer `Colors.X.getHex()` verwenden. Das stellt sicher, dass eine Farbanpassung an einer einzigen Stelle das gesamte Plugin betrifft.

### Vollständige Farbtabelle

| Enum-Konstante | Hex-Code | Verwendung |
|---|---|---|
| `BLUE` | `#08a8f8` | Primärfarbe — Spielernamen, Akzente, Navigation, aktive Filter, Lore-Bullets |
| `GREEN` | `#4bfb00` | Aktive/verfügbare Elemente — z.B. Seitenbuttons wenn Seite vorhanden |
| `SMOOTH_GREEN` | `#2ecc71` | Sanfteres Grün, alternative Erfolgsfarbe |
| `PASTEL_GREEN` | `#77dd77` | Dezente Erfolgshinweise, weniger aufdringlich als GREEN |
| `MINT_GREEN_DARK` | `#08FB95` | Reserviert (HideAndSeek: Verstecker-Rolle) |
| `MINT_GREEN_LIGHT` | `#B5FFE0` | Reserviert (HideAndSeek: sekundäre Verstecker-Anzeige) |
| `RED` | `#ff0000` | Fehler, verbotene Aktionen, deaktivierte Buttons |
| `PASTEL_RED` | `#ff6961` | Sanfteres Rot für Warnungen ohne Kritikalität |
| `DARKER_RED` | `#dc2626` | Reserviert (HideAndSeek: Sucher-Rolle) |
| `YELLOW` | `#fede00` | Info-Items (Paper im GUI), Überschriften, neutrale Hinweise |
| `PASTEL_YELLOW` | `#fdfd96` | Sehr dezente Info-Texte |
| `ORANGE` | `#ffa500` | Akzentfarbe für besondere Hervorhebungen |
| `PASTEL_ORANGE` | `#ffb347` | Sanftes Orange |
| `PURPLE` | `#7F00FF` | Nicht für primäre UI verwenden — zu dominant |
| `PASTEL_PURPLE` | `#cba6f7` | Dezente Dekorationselemente |
| `DISCORD_BLURPLE` | `#7289DA` | Dekorativ |
| `PASTEL_BLURPLE` | `#a3b9f7` | Dekorativ |
| `GEM_PURPLE` | `#e43a96` | Spiel-Titel-Akzente (z.B. in Titeln und Countdowns) |
| `GREY` | `#AAAAAA` | Standard-Lore-Text, Beschreibungen, inaktive Elemente |
| `GREY_CUSTOM` | `#96a7b2` | Gedämpftere Lore-Zeilen, sekundäre Infos |
| `DARK_GREY` | `#181a1f` | Nicht für sichtbare UI empfohlen |
| `PASTEL_GREY` | `#d3d3d3` | Heller Grauton für sehr zurückhaltende Texte |
| `GOLD_GRADIENT` | `#fff200 → #ff9900` | Gradient-Dekorationen, besondere Auszeichnungen |

### Verwendungsregel

```java
// Richtig
meta.displayName(TextUtil.parse("<!i>" + Colors.BLUE.getHex() + "Spielername</color>"));

// Falsch — Hex direkt hardcoded
meta.displayName(TextUtil.parse("<!i><color:#08a8f8>Spielername</color>"));
```

---

## 1.2 Unicode-Symbole (`Unicodes.java`)

Alle Sonderzeichen sind in `dev.zaen.itemforcebattle.utils.Unicodes` definiert. Unicode-Escapes niemals direkt im Code verwenden — immer `Unicodes.X.getString()` nutzen.

### Vollständige Symbol-Tabelle

| Enum-Konstante | Symbol | Primäre Verwendung |
|---|---|---|
| `ROUND_DOT` | `●` | Lore-Bullet-Points, Filter-Einträge, Nav-Lore, Chat-Nachrichten |
| `ARROW` | `»` | Trennzeichen zwischen Kontext-Elementen (`ɪᴛᴇᴍʙᴀᴛᴛʟᴇ » ᴇʀɢᴇʙɴɪssᴇ`) |
| `DOT` | `•` | Alternativer Aufzählungspunkt |
| `PREFIX_DOT` | `◘` | Prefix-Dekoration |
| `HEAVY_CHECK_MARK` | `✔` | Erfolgs-Feedback in Chat-Nachrichten (grüne Bestätigungen) |
| `HEAVY_CROSS_MARK` | `✖` | Fehler-Feedback in Chat-Nachrichten (rote Fehlermeldungen) |
| `CHECK_MARK` | `✓` | Leichteres Häkchen, dezentere Bestätigung |
| `CROSS_MARK` | `✗` | Leichteres Kreuz |
| `HEAVY_EXCLAMATION_MARK` | `❗` | Warnungen |
| `HEAVY_QUESTION_MARK` | `❓` | Hinweise / Hilfe |
| `HEAVY_PLUS_SIGN` | `✚` | Hinzufügen, positive Aktion |
| `HEAVY_MINUS_SIGN` | `➖` | Entfernen, negative Aktion |
| `CROWN` | `👑` | Platz 1, Auszeichnungen |
| `FLAME` | `🔥` | Streak, Highlights |
| `STAR` | `★` | Besondere Elemente |
| `SKULL` | `☠` | Tod, Ausscheiden |
| `HEART` | `💜` | Dekorativ |
| `HOURGLASS` | `⌛` | Zeitanzeigen |
| `FLOPPY_DISK` | `💾` | Speicher-Aktionen |
| `CLIPBOARD` | `📋` | Listen, Übersichten |
| `GEM` | `❖` | Dekorativ, Premium-Elemente |
| `SWORD` | `🗡` | Kampf, PvP |
| `CLOUD` | `☁` | Wetter, Umgebung |

### Verwendungsregel

```java
// Richtig
lore.add(TextUtil.parse("<!i><grey>" + Colors.BLUE.getHex()
    + Unicodes.ROUND_DOT.getString() + "</color> Beschreibung</grey>"));

// Falsch — direkt als Literal
lore.add(TextUtil.parse("<!i><grey><color:#08a8f8>●</color> Beschreibung</grey>"));
```

---

## 1.3 Small Caps

Small Caps sind Unicode-Buchstaben, die wie Kapitälchen aussehen und dem Plugin seinen charakteristischen Stil geben. Sie werden über `ColorUtils.toSmallCaps(String)` erzeugt.

### Konvertiertabelle

```
a → ᴀ    b → ʙ    c → ᴄ    d → ᴅ    e → ᴇ    f → ꜰ    g → ɢ
h → ʜ    i → ɪ    j → ᴊ    k → ᴋ    l → ʟ    m → ᴍ    n → ɴ
o → ᴏ    p → ᴘ    q → q    r → ʀ    s → s    t → ᴛ    u → ᴜ
v → ᴠ    w → ᴡ    x → x    y → ʏ    z → ᴢ
```

Großbuchstaben, Ziffern und Sonderzeichen werden nicht konvertiert.

### Wann Small Caps verwenden

| Kontext | Small Caps | Begründung |
|---|---|---|
| GUI-Titel | Ja | Einheitliches Erscheinungsbild |
| Item-Namen im GUI | Ja | Konsistenz |
| Lore-Zeilen | Ja | Konsistenz |
| Chat-Nachrichten | Ja | Stil |
| Spielernamen | Nein | Originalname bleibt lesbar und klar |
| Befehls-Syntax | Nein | Muss kopierbar sein |
| Zahlen / Punkte | Nein | Ziffern haben keine Small-Caps-Variante |
| Fehler-/Warn-Text | Optional | Konsistenz, aber Lesbarkeit hat Vorrang |

### In YAML vs. in Code

```yaml
# In YAML — einmalig direkt eintragen (spart Laufzeit-Konvertierung)
name: "ɪᴛᴇᴍʙᴀᴛᴛʟᴇ"
```

```java
// In Java — dynamische Strings zur Laufzeit konvertieren
String name = ColorUtils.toSmallCaps(player.getName()); // Spielernamen NICHT konvertieren
String label = ColorUtils.toSmallCaps("Punkte");        // Labels konvertieren
```

---

## 1.4 MiniMessage-Formatierung

Alle Texte werden mit der [Adventure MiniMessage API](https://docs.advntr.dev/minimessage/format.html) formatiert. Legacy-Format (`&c`, `§c`) wird nicht verwendet.

### Wichtige Tags

| Tag | Bedeutung | Beispiel |
|---|---|---|
| `<!i>` | Kursiv deaktivieren | `<!i>Text` — Pflicht bei jedem Item-Namen und jeder Lore-Zeile |
| `<color:#hex>` | Hex-Farbe | `<color:#08a8f8>Text</color>` |
| `<grey>` | Grauer Text (`#AAAAAA`) | `<grey>Beschreibung</grey>` |
| `<white>` | Weißer Text | `<white>Titel</white>` |
| `<dark_gray>` | Dunkles Grau | `<dark_gray>»</dark_gray>` — für Trennzeichen |
| `<b>` | Fett | `<b>Titel</b>` |
| `<gradient:#a:#b>` | Farbverlauf | `<gradient:#fff200:#ff9900>Gold</gradient>` |

### Pflichtregeln

1. **Jeder Item-Name und jede Lore-Zeile** beginnt mit `<!i>` — ohne diesen Tag erscheint der Text kursiv (Minecraft-Standard).
2. **Tags immer schließen** — `<color:#hex>...</color>` — offene Tags färben den restlichen Text ein.
3. **Kein Legacy-Format** — ausschließlich MiniMessage verwenden.
4. **Farben via Colors-Enum** — niemals Hex-Strings hardcoden.

```java
// Korrekt
meta.displayName(TextUtil.parse("<!i>" + Colors.BLUE.getHex() + "Name</color>"));

// Falsch — kein <!i>
meta.displayName(TextUtil.parse(Colors.BLUE.getHex() + "Name</color>"));

// Falsch — Legacy
meta.displayName(TextUtil.parse("&bName"));
```

---

## 1.5 Nachrichten-System

Alle Nachrichten sind in `messages.yml` konfigurierbar und werden über den `MessageManager` geladen. Niemals Nachrichten-Strings direkt im Code hardcoden.

### Prefix-Format

Der Prefix steht vor jeder Admin-Feedback-Nachricht und identifiziert das Plugin.

```
<Primärfarbe><b>ᴘʟᴜɢɪɴɴᴀᴍᴇ</b> <dark_gray>»</dark_gray> (Leerzeichen)
```

Aktuell:
```
<#478ED2><b>ʙᴇᴛᴛᴇʀᴀᴛᴛᴀᴄᴋ</b> <dark_gray>»</dark_gray>
```

Der Prefix wird immer als roher String (`getPrefix()`) abgerufen und manuell mit der Nachricht verknüpft, da er kein eigenständiges Component ist:
```java
sender.sendMessage(ColorUtils.colorize(messageManager.getPrefix() + nachricht));
```

### Nachrichtentypen und ihre Symbole

Jede Nachricht beginnt nach dem Prefix mit einem Typ-Symbol, das sofort erkennbar macht, um was es sich handelt:

| Typ | Symbol | Farbe | Verwendung |
|---|---|---|---|
| Erfolg | `✔` | `#00EE39` (Grün) | Aktion erfolgreich ausgeführt |
| Fehler | `❌` fett | `#ff0000` (Rot) | Ungültige Aktion, Berechtigung fehlt, Fehler |
| Info / Neutral | `●` | `#FFD700` (Gold) | Status-Meldungen, toggle-Nachrichten |
| Spielereignis | `🎯` | `#478ED2` (Primär) | Spielmechanik-Nachrichten (neues Item etc.) |

### Vollständige Nachrichten-Referenz

Alle Nachrichten folgen dem Muster: `[Symbol] [weißer Text] [farbiger Kontext]`

```yaml
# Fehler-Nachrichten — rotes Symbol + weißer Text
player-only:           "<#ff0000><b>❌</b> <white>ɴᴜʀ ғüʀ sᴘɪᴇʟᴇʀ!"
no-permission:         "<#ff0000><b>❌</b> <white>ᴋᴇɪɴᴇ ʙᴇʀᴇᴄʜᴛɪɢᴜɴɢ!"
event-not-running:     "<#ff0000><b>❌</b> <white>ᴋᴇɪɴ ᴇᴠᴇɴᴛ ᴀᴋᴛɪᴠ!"
event-already-running: "<#ff0000><b>❌</b> <white>ᴇᴠᴇɴᴛ ʟäᴜғᴛ ʙᴇʀᴇɪᴛs!"
spawn-not-set:         "<#ff0000><b>❌</b> <white>sᴘᴀᴡɴ ɴɪᴄʜᴛ ɢᴇsᴇᴛᴢᴛ!"
no-skips-left:         "<#ff0000><b>❌</b> <white>ᴋᴇɪɴᴇ sᴋɪᴘs ᴍᴇʜʀ üʙʀɪɢ!"
player-already-in-game:"<#ff0000><b>❌</b> <white>sᴘɪᴇʟᴇʀ ɪsᴛ ʙᴇʀᴇɪᴛs ɪᴍ sᴘɪᴇʟ!"
player-not-found:      "<#ff0000><b>❌</b> <white>sᴘɪᴇʟᴇʀ <white>{player}</white> ɴɪᴄʜᴛ ɢᴇғᴜɴᴅᴇɴ!"
event-stopped:         "<#ff0000><b>❌</b> <white>ᴅᴀs ᴇᴠᴇɴᴛ ᴡᴜʀᴅᴇ ʙᴇᴇɴᴅᴇᴛ!"

# Erfolgs-Nachrichten — grünes Häkchen + weißer Text
spawn-set:        "<#00EE39>✔ <white>sᴘᴀᴡɴ ɢᴇsᴇᴛᴢᴛ."
event-started:    "<#00EE39>✔ <white>ᴅᴀs ɪᴛᴇᴍ ʙᴀᴛᴛʟᴇ ʜᴀᴛ ʙᴇɢᴏɴɴᴇɴ!"
event-ended:      "<#00EE39>✔ <white>ᴅᴀs ᴇᴠᴇɴᴛ ɪsᴛ ᴠᴏʀʙᴇɪ!"
player-added:     "<#00EE39>✔ <white>{player} <#00EE39>ᴡᴜʀᴅᴇ ʜɪɴᴢᴜɢᴇғüɢᴛ."
player-added-self:"<#00EE39>✔ <white>ᴅᴜ ᴡᴜʀᴅᴇsᴛ ᴢᴜᴍ ɪᴛᴇᴍ ʙᴀᴛᴛʟᴇ ʜɪɴᴢᴜɢᴇғüɢᴛ!"
config-reloaded:  "<#00EE39>✔ <white>ᴋᴏɴғɪɢ ɴᴇᴜ ɢᴇʟᴀᴅᴇɴ."
item-collected:   "<#00EE39>✔ <white>{item} <#00EE39>ɢᴇsᴀᴍᴍᴇʟᴛ! <#FFD700>(+1 ᴘᴜɴᴋᴛ)"

# Info-Nachrichten — goldener Dot + weißer/farbiger Text
scoreboard-hidden: "<#FFD700>● <white>sᴄᴏʀᴇʙᴏᴀʀᴅ <#ff0000>ᴀᴜsɢᴇʙʟᴇɴᴅᴇᴛ."
scoreboard-shown:  "<#FFD700>● <white>sᴄᴏʀᴇʙᴏᴀʀᴅ <#00EE39>ᴀɴɢᴇᴢᴇɪɢᴛ."
skip-used:         "<#FFD700>● <white>{item} <#FFD700>ɢᴇsᴋɪᴘᴘᴛ! <#ff0000>({remaining} sᴋɪᴘs üʙʀɪɢ)"

# Spielereignis-Nachrichten — primäres Symbol + weißer Text
new-item:          "<#478ED2>🎯 <white>ᴅᴇɪɴ ɴᴇᴜᴇs ɪᴛᴇᴍ: <white>{item}"
countdown-go:      "<#00EE39><b>ʟᴏs ɢᴇʜᴛs!"
```

### Aufbau einer neuen Nachricht

Beim Hinzufügen einer neuen Nachricht immer dieses Schema befolgen:

```
1. Typ bestimmen (Fehler / Erfolg / Info / Spielereignis)
2. Passendes Symbol wählen (Tabelle oben)
3. Symbol in Typ-Farbe, danach Text in <white>
4. Variablen als {placeholder} eintragen
5. In messages.yml und MessageManager ergänzen
6. Getter im MessageManager hinzufügen
```

Beispiel — neue Nachricht "Zeit abgelaufen":
```yaml
# messages.yml
time-up: "<#ff0000><b>❌</b> <white>ᴅɪᴇ ᴢᴇɪᴛ ɪsᴛ ᴀʙɢᴇʟᴀᴜғᴇɴ!"
```
```java
// MessageManager.java
private String timeUp;
// in reload():
timeUp = messagesConfig.getString("time-up", "<#ff0000><b>❌</b> <white>ᴅɪᴇ ᴢᴇɪᴛ ɪsᴛ ᴀʙɢᴇʟᴀᴜғᴇɴ!");
// Getter:
public Component getTimeUp() { return fmt(timeUp); }
```

### Broadcast vs. einzelner Spieler

```java
// Nur an einen Spieler
player.sendMessage(messageManager.getNoPermission());

// Mit Prefix an einen Spieler (für Admin-Feedback)
sender.sendMessage(ColorUtils.colorize(messageManager.getPrefix() + "<#ff0000>..."));

// An alle (Spielereignis ohne Prefix)
Bukkit.broadcast(messageManager.getEventStarted());
```

---

## 1.6 Title & Countdown-Format

Titles folgen dem BHaS-Muster: Der kontextuelle Text steht oben, der dynamische Wert unten.

### Countdown

```
Oben (Title):    Colors.BLUE + <b>sᴘɪᴇʟ sᴛᴀʀᴛᴇᴛ ɪɴ:</b>
Unten (Subtitle): <white><b>5</b>  →  4  →  3  →  2  →  1
```

### Spielstart

```
Oben (Title):    <b>Colors.GEM_PURPLE + ɪᴛᴇᴍ + Colors.BLUE + ʙᴀᴛᴛʟᴇ</b>
Unten (Subtitle): <white>ʟᴏs ɢᴇʜᴛs!
```

### Times-Konfiguration

```java
// Countdown — kein Fade, damit der Wechsel sofort wirkt
Title.Times.times(Duration.ZERO, Duration.ofMillis(1100), Duration.ZERO)

// Spielstart — kurzes Fade-out
Title.Times.times(Duration.ZERO, Duration.ofSeconds(2), Duration.ofMillis(500))
```

---

## 1.7 GUI-Aufbau

### Slot-Layout eines PaginatedGui (6 Reihen)

```
Reihe 1–5 (Slots 0–44): Paginated Content (Spielerköpfe etc.)

+----+----+----+----+----+----+----+----+----+
|  0 |  1 |  2 |  3 |  4 |  5 |  6 |  7 |  8 |  Reihe 1
+----+----+----+----+----+----+----+----+----+
|  9 | 10 | 11 | 12 | 13 | 14 | 15 | 16 | 17 |  Reihe 2
+----+----+----+----+----+----+----+----+----+
| 18 | 19 | 20 | 21 | 22 | 23 | 24 | 25 | 26 |  Reihe 3
+----+----+----+----+----+----+----+----+----+
| 27 | 28 | 29 | 30 | 31 | 32 | 33 | 34 | 35 |  Reihe 4
+----+----+----+----+----+----+----+----+----+
| 36 | 37 | 38 | 39 | 40 | 41 | 42 | 43 | 44 |  Reihe 5
+----+----+----+----+----+----+----+----+----+
| 45 | 46 | 47 | 48 | 49 | 50 | 51 | 52 | 53 |  Reihe 6 (Navigation)
+----+----+----+----+----+----+----+----+----+
  FIL FIL FIL PRV INF NXT FIL FIL FLT

FIL = Filler (BLACK_STAINED_GLASS_PANE, Name: Component.space())
PRV = Vorherige Seite (Slot 48)
INF = Seitenanzeige / Info-Item (Slot 49)
NXT = Nächste Seite (Slot 50)
FLT = Filter-Button (Slot 53, rechte untere Ecke)
```

### Navigation — Material und Farbe

| Element | Material (aktiv) | Material (inaktiv) | Namensfarbe |
|---|---|---|---|
| Vorherige Seite | `LIME_CANDLE` | `RED_CANDLE` | `Colors.GREEN` / `Colors.RED` |
| Nächste Seite | `LIME_CANDLE` | `RED_CANDLE` | `Colors.GREEN` / `Colors.RED` |
| Info-Item | `PAPER` | — | `Colors.YELLOW` |
| Filter | `HOPPER` | — | `Colors.BLUE` |
| Filler | `BLACK_STAINED_GLASS_PANE` | — | `Component.space()` |

### Standard-Lore für Nav-Items

```
(leer)
● ᴋʟɪᴄᴋᴇ ᴢᴜᴍ ɴᴀᴠɪɢɪᴇʀᴇɴ   ← Colors.BLUE Dot, grey Text
(leer)
```

### Standard-Lore für Info-Item (Paper)

```
(leer)
● sᴇɪᴛᴇ X / Y   ← Colors.BLUE Dot, "sᴇɪᴛᴇ" grau, Zahlen in Colors.BLUE
(leer)
```

### Standard-Lore für Player-Heads

```
(leer)
● ᴘʟᴀᴛᴢɪᴇʀᴜɴɢ: #1    ← Dot in Colors.BLUE, Wert in Colors.BLUE, Text grey
● ᴘᴜɴᴋᴛᴇ: 15           ← gleiche Struktur
(leer)
» ᴋʟɪᴄᴋᴇ ᴜᴍ ᴅɪᴄʜ ᴢᴜ Spieler ᴢᴜ ᴛᴇʟᴇᴘᴏʀᴛɪᴇʀᴇɴ!
(leer)
```

### GUI-Titel-Format

```
<!i><white>ɪᴛᴇᴍʙᴀᴛᴛʟᴇ</white> <dark_gray>»</dark_gray> <Colors.BLUE>[Kontext]</color>
```

Beispiele:
- `ɪᴛᴇᴍʙᴀᴛᴛʟᴇ » ᴍᴇɪsᴛᴇ ᴘᴜɴᴋᴛᴇ`
- `ɪᴛᴇᴍʙᴀᴛᴛʟᴇ » ᴀʟᴘʜᴀʙᴇᴛɪsᴄʜ`

Der Kontext (rechts vom Pfeil) ist immer in `Colors.BLUE` und zeigt den aktuellen Zustand.

---

## 1.8 Filter-System

### Filter-Optionen (PlayerListGUI)

| Enum-Wert | Anzeigename | Sortierung |
|---|---|---|
| `BY_POINTS_DESC` | `ᴍᴇɪsᴛᴇ ᴘᴜɴᴋᴛᴇ` | Absteigend nach Punkten (Standard) |
| `BY_POINTS_ASC` | `ᴡᴇɴɪɢsᴛᴇ ᴘᴜɴᴋᴛᴇ` | Aufsteigend nach Punkten |
| `ALPHABETICAL` | `ᴀʟᴘʜᴀʙᴇᴛɪsᴄʜ` | Alphabetisch nach Spielername |

### Lore-Aufbau des Filter-Buttons

```
(leer)
» ᴍᴇɪsᴛᴇ ᴘᴜɴᴋᴛᴇ      ← aktiv: Colors.BLUE + ARROW + Leerzeichen + Name
  ᴡᴇɴɪɢsᴛᴇ ᴘᴜɴᴋᴛᴇ    ← inaktiv: grey, zwei Leerzeichen Einrückung
  ᴀʟᴘʜᴀʙᴇᴛɪsᴄʜ        ← inaktiv: grey
(leer)
ᴋʟɪᴄᴋᴇɴ ᴜᴍ ᴢᴜ ꜱᴏʀᴛɪᴇʀᴇɴ   ← Colors.BLUE
```

### In-place Update — kein GUI neu öffnen

Filterwechsel schließt das GUI nicht. Stattdessen:

```java
private void refreshGUI(Player player, PaginatedGui gui, FilterOption filter) {
    gui.updateTitle(buildTitle(filter));  // 1. Titel aktualisieren
    gui.clearPageItems();                 // 2. Seiten-Items leeren
    populateItems(gui, filter, player);   // 3. Neu befüllen + Nav setzen
    gui.update();                         // 4. Inventory neu rendern
}
```

---

## 1.9 Sounds

Alle GUI-Sounds stammen aus BHaS's `GuiUtil` und sind einheitlich zu verwenden.

| Aktion | Sound-Key | Quelle | Lautstärke | Pitch |
|---|---|---|---|---|
| Seite blättern | `item.book.page_turn` | `MASTER` | 1.0 | 1.0 |
| Ablehnung (deaktivierter Button) | `block.wood.step` | `MASTER` | 1.0 | 1.9 |
| Klick / Teleport / Filter | `block.lever.click` | `MASTER` | 1.0 | 1.0 |
| Erfolg | `entity.experience_orb.pickup` | `MASTER` | 1.0 | 1.0 |
| Fehler | `entity.villager.no` | `MASTER` | 1.0 | 1.0 |
| Notiz / Warten | `block.note_block.pling` | `MASTER` | 1.0 | 1.0 |
| Bestätigung | `entity.player.levelup` | `MASTER` | 1.0 | 1.0 |
| Deaktivieren | `block.note_block.bass` | `MASTER` | 1.0 | 1.0 |

Sound-Format:
```java
player.playSound(Sound.sound(Key.key("item.book.page_turn"), Sound.Source.MASTER, 1f, 1f));
```

Spieler-Sounds (außerhalb GUI, über `configManager`):
```java
player.playSound(player.getLocation(), configManager.getCountdownTick(), 1.0f, 1.0f);
```

---

## 1.10 Chat-Leaderboard-Format

### Vollständiger Aufbau

```
(leer)
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━   Colors.BLUE
  ɪᴛᴇᴍʙᴀᴛᴛʟᴇ » ᴇʀɢᴇʙɴɪssᴇ                        Colors.BLUE + dark_gray »  + Colors.YELLOW
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
(leer)
  🥇 Spielername  »  25 ᴘᴜɴᴋᴛᴇ    #FFD700 Gold, Spielername white+fett, Punkte in Gold
  🥈 Spielername  »  18 ᴘᴜɴᴋᴛᴇ    #C0C0C0 Silber
  🥉 Spielername  »  12 ᴘᴜɴᴋᴛᴇ    #CD7F32 Bronze
  ● 4. Spielername  »  8 ᴘᴜɴᴋᴛᴇ   Colors.BLUE Dot, Nummer+Name grey
(leer)
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
(leer)
```

### Platzierungs-Farbcodes

| Platz | Farbe | Symbol |
|---|---|---|
| 1 | `#FFD700` (Gold) | 🥇 |
| 2 | `#C0C0C0` (Silber) | 🥈 |
| 3 | `#CD7F32` (Bronze) | 🥉 |
| 4+ | `Colors.BLUE` mit `●` Dot | — |

---

## 1.11 Help-Befehl-Format

### Aufbau

```
● ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ ●   Colors.BLUE + Strikethrough
  ɪᴛᴇᴍʙᴀᴛᴛʟᴇ » ʜɪʟꜰᴇ                   Blau-Name + dark_gray »  + white Text
● ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ ●
● /befehl subcommand  »  Beschreibung    Blau-Dot + white Befehl + dark_gray »  + grey Beschreibung
● /befehl argument    »  Beschreibung
...
● ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ ●
```

### Befehlszeilen-Struktur

```java
// Muster pro Zeile
"<grey>" + Colors.BLUE.getHex() + Unicodes.ROUND_DOT.getString() + "</color> "
+ "<white>/befehl</white> "
+ "<dark_gray>" + Unicodes.ARROW.getString() + "</dark_gray> "
+ "<grey>Beschreibung</grey>"
```

---

## 1.12 Scoreboard (Sidebar)

Das Scoreboard wird jedem Spieler als Sidebar angezeigt und zeigt Live-Daten wie Zeit, Punkte und die aktuelle Rangliste. Es ist vollständig über `scoreboard.yml` konfigurierbar.

### Visueller Aufbau (Standard-Layout)

```
┌─────────────────────────────┐
│       ɪᴛᴇᴍꜰᴏʀᴄᴇ            │  ← Titel (fett, Colors.BLUE)
├─────────────────────────────┤
│ ● ━━━━━━━━━━━━━━━━━━━━━━ ●  │  ← Trennlinie (Dot + Strikethrough)
│ ᴛᴏᴘ ꜱᴘɪᴇʟᴇʀ                │  ← Abschnittsüberschrift (fett, Colors.BLUE)
│                             │
│ 1. Spieler1  15             │  ← #1 Platz (Rang blau, Name weiß, Punkte gold)
│ 2. Spieler2  12             │  ← #2
│ 3. Spieler3   9             │  ← #3
│ 4. Spieler4   7             │  ← #4
│ 5. Spieler5   4             │  ← #5
│                             │
│ ꜱᴛᴀᴛꜱ                      │  ← Abschnittsüberschrift (fett, Colors.BLUE)
│ ▎ ᴢᴇɪᴛ:    42:17           │  ← Primärfarbe-Balken, grauer Text, gold Wert
│ ▎ ᴘᴜɴᴋᴛᴇ:  9               │  ← gleiche Struktur
│ ● ━━━━━━━━━━━━━━━━━━━━━━ ●  │  ← Trennlinie
│  ⛏ ʙᴇᴛᴛᴇʀᴀᴛᴛᴀᴄᴋ.ɴᴇᴛ ⛏      │  ← Footer (grau)
└─────────────────────────────┘
```

### `scoreboard.yml` — vollständige Konfiguration

```yaml
enabled: true

scoreboard:
  toggle-command: sb              # Befehl zum Ein-/Ausblenden
  remember-toggle-choice: false   # Wahl über Server-Neustart merken
  hidden-by-default: false        # Standardmäßig sichtbar
  delay-on-join-milliseconds: 0   # Verzögerung beim Join (ms)
  text-shadow: true               # Textshadow aktivieren (Paper 1.21.4+)
  small-caps: true                # Small-Caps-Konvertierung für alle Zeilen

  scoreboards:
    default:
      title: "<b><color:#478ED2>ɪᴛᴇᴍꜰᴏʀᴄᴇ</color></b>"
      lines:
        - "<#478ED2>●&m                                  &r<#478ED2>●"
        - "<b><color:#478ED2>ᴛᴏᴘ ꜱᴘɪᴇʟᴇʀ</color></b>"
        - ""
        - "#1"
        - "#2"
        - "#3"
        - "#4"
        - "#5"
        - ""
        - "<b><#478ED2>ꜱᴛᴀᴛꜱ</b>"
        - "<#478ED2><b>▎</b> <#B1B1B1>ᴢᴇɪᴛ: <#FFD700>%time%"
        - "<#478ED2><b>▎</b> <#B1B1B1>ᴘᴜɴᴋᴛᴇ: <#FFD700>%points%"
        - "<#478ED2>●&m                                  &r<#478ED2>●"
        - " <gray>⛏ <b><#478ED2>ʙᴇᴛᴛᴇʀᴀᴛᴛᴀᴄᴋ.ɴᴇᴛ</b><gray>⛏"
```

### Platzhalter und Sonderzeilen

| Wert in `lines` | Bedeutung |
|---|---|
| `%time%` | Verbleibende Spielzeit, automatisch als `MM:SS` formatiert |
| `%points%` | Punkte des jeweiligen Spielers (individuell pro Spieler) |
| `#1` – `#9` | Automatisch ersetzt durch Rang + Spielername + Punkte |
| `""` | Leere Zeile als Abstandshalter |
| Jeder andere String | Wird direkt als MiniMessage-Text gerendert |

### Rang-Zeilen-Format (`#1` – `#9`)

```
<#478ED2>1. <white>Spielername <#FFD700>15
```

Aufbau: `Colors.BLUE + Rang + ". " + white Spielername + gold Punkte`

Wenn ein Platz nicht besetzt ist (weniger Spieler als Rang-Einträge):
```
<#478ED2>1. <dark_gray>N/A
```

### Text Shadow

Text Shadow (`text-shadow: true`) fügt jedem Scoreboard-Text einen halbtransparenten Schatten hinzu. Das verbessert die Lesbarkeit auf hellen Hintergründen erheblich.

- Implementiert über `ShadowColor.shadowColor(0xAA000000)` — schwarz mit ~67% Deckkraft
- Wird auf **jede** Zeile und den **Titel** angewendet wenn aktiviert
- Erfordert PaperMC 1.21.4+

```java
// Intern: Shadow auf Component anwenden
private Component withShadow(Component c) {
    return c.style(c.style().merge(
        Style.style().shadowColor(ShadowColor.shadowColor(0xAA000000)).build()
    ));
}
```

### Small Caps im Scoreboard

`small-caps: true` aktiviert die Small-Caps-Konvertierung über `ColorUtils.toSmallCaps()`. Diese wird in `MessageManager.applySmallCaps()` auf alle Nachrichten angewendet, die über den `MessageManager` laufen.

Direkt in `scoreboard.yml` eingetragene Texte müssen manuell in Small Caps geschrieben werden (da sie nicht durch den MessageManager laufen):

```yaml
# Manuell als Small Caps in YAML eintragen
- "<b><color:#478ED2>ᴛᴏᴘ ꜱᴘɪᴇʟᴇʀ</color></b>"
- "<b><#478ED2>ꜱᴛᴀᴛꜱ</b>"
```

### Trennlinie-Format

```yaml
# Dot + Strikethrough-Linie + Dot (Legacy-Format für Strikethrough nötig)
"<#478ED2>●&m                                  &r<#478ED2>●"
```

- `&m` aktiviert Strikethrough (Legacy-Code, da MiniMessage `<st>` im Scoreboard nicht immer funktioniert)
- `&r` setzt das Format zurück, damit der zweite Dot wieder normal erscheint
- Leerzeichen zwischen den Dots erzeugen die Linie

### Toggle-Verhalten (`/sb`)

```
Spieler ruft /sb auf
    ├── Scoreboard sichtbar  →  Main-Scoreboard setzen + Nachricht "ausgeblendet"
    └── Scoreboard hidden    →  Plugin-Scoreboard setzen + Nachricht "angezeigt"
```

```java
// Intern: toggle
public void toggleScoreboard(Player player) {
    if (isHidden(player)) showScoreboard(player);
    else hideScoreboard(player);
}

// Hide: Main-Scoreboard zurückgeben (entfernt die Sidebar)
player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());

// Show: Plugin-Scoreboard wieder zuweisen
player.setScoreboard(boards.get(player.getUniqueId()));
```

### Lifecycle

```
Spielstart (actuallyStartGame)
    └── ScoreboardManager.createScoreboards()
            └── Für jeden Spieler: createScoreboard(player)

Punkt erzielt / Zeit-Tick
    └── ScoreboardManager.updateAllScoreboards()

Spieler skippt
    └── ScoreboardManager.updateScoreboard(player)

Spielende (stopGame)
    └── ScoreboardManager.removeAllScoreboards()
            └── Alle Spieler erhalten wieder Main-Scoreboard
```

### Design-Regeln für neue Scoreboard-Zeilen

1. Abschnittsüberschriften: fett + `Colors.BLUE`, Small Caps
2. Stat-Zeilen: `Colors.BLUE <b>▎</b>` als Balken-Prefix, `#B1B1B1` für Label, `#FFD700` für Wert
3. Leerzeilen (`""`) als Abstandshalter zwischen Abschnitten
4. Trennlinien nur am Anfang und Ende — nicht zwischen jeden Abschnitt
5. Footer (letzte Zeile): grau, zentriert mit Dekoration

---

# Abschnitt 2 — Performance-Richtlinien

## 2.1 Haupt-Thread vs. Async-Thread

Die Minecraft-Server-API (Bukkit/Paper) ist nicht thread-safe. Alle Bukkit-API-Aufrufe müssen auf dem Haupt-Thread stattfinden. Schwere Operationen (Datei-I/O, Berechnungen) laufen async.

### Kategorisierung

| Operation | Thread |
|---|---|
| Bukkit API (Player, Entity, World) | Haupt-Thread |
| Inventory / GUI öffnen, update | Haupt-Thread |
| Teleportation | `teleportAsync()` (intern gehandhabt) |
| Datei lesen/schreiben | Async |
| Datenbankzugriffe | Async |
| Schwere Sortierungen / Berechnungen | Async, Ergebnis auf Main Thread anwenden |

```java
// Async starten, dann auf Main Thread zurückwechseln
Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
    List<Data> result = loadFromDatabase(); // schwer, async
    Bukkit.getScheduler().runTask(plugin, () -> {
        applyToPlayers(result); // Bukkit API, Main Thread
    });
});
```

---

## 2.2 Scheduler

### BukkitRunnable für kancelierbare Tasks

```java
// Richtig — Referenz behalten für cancel()
BukkitTask task = new BukkitRunnable() {
    @Override
    public void run() {
        if (shouldStop) { cancel(); return; }
        // Logik
    }
}.runTaskTimer(plugin, 0L, 20L);

// Später kanzellieren
if (task != null) { task.cancel(); task = null; }
```

### Task-Felder immer nullen nach cancel()

```java
if (gameTimer != null) { gameTimer.cancel(); gameTimer = null; }
if (actionbarTask != null) { actionbarTask.cancel(); actionbarTask = null; }
```

### Ticks-Referenz

| Wert | Entspricht |
|---|---|
| `1L` | 1 Tick (~50ms) |
| `20L` | 1 Sekunde |
| `200L` | 10 Sekunden |
| `1200L` | 1 Minute |

---

## 2.3 Event-Listener

### Frühzeitig aussteigen

Die erste Zeile jedes Listeners prüft, ob die Verarbeitung überhaupt nötig ist. Je früher der Return, desto weniger Overhead.

```java
@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
public void onPickup(EntityPickupItemEvent event) {
    if (!gameManager.isGameRunning()) return;      // 1. Spielstatus
    if (!(event.getEntity() instanceof Player p)) return; // 2. Entitytyp
    PlayerData data = gameManager.getPlayerData(p.getUniqueId());
    if (data == null) return;                      // 3. Spieler im Spiel
    // Verarbeitung
}
```

### `ignoreCancelled = true`

Immer setzen, wenn der Listener nicht auf bereits gecancelte Events reagieren muss. Verhindert doppelte Verarbeitung.

### EventPriority

| Priority | Verwendung |
|---|---|
| `LOWEST` | Frühzeitige Überprüfungen, die andere Plugins berücksichtigen sollen |
| `NORMAL` | Standard — für die meisten Spiellogik-Listener |
| `HIGH` | Wenn das Plugin das letzte Wort haben soll |
| `MONITOR` | Nur beobachten, niemals canceln |

---

## 2.4 GUI-Performance

### `disableAllInteractions()` ist Pflicht

```java
Gui.paginated()
    .disableAllInteractions() // verhindert Item-Bewegung, weniger Event-Overhead
    .create();
```

### GUI nie auf Async-Thread öffnen oder updaten

```java
// Falsch
CompletableFuture.runAsync(() -> gui.open(player));

// Richtig
Bukkit.getScheduler().runTask(plugin, () -> gui.open(player));
```

### In-place Update statt Neu-Öffnen

| Methode | Kosten | Wann verwenden |
|---|---|---|
| `gui.update()` | Sehr gering — rendert Inventory neu | Nach Item-Änderungen |
| `gui.updateTitle(Component)` | Gering — aktualisiert Titel | Bei Filterwechsel |
| `gui.clearPageItems()` + neu befüllen | Mittel | Bei Inhaltsänderungen |
| `player.closeInventory()` + `gui.open()` | Hoch — schließt + öffnet Inventory | Nie für Filter/Sort |

---

## 2.5 Scoreboard-Updates

### Nur bei echten Änderungen updaten

```java
// Falsch — jede Sekunde alle Spieler
new BukkitRunnable() {
    public void run() { scoreboardManager.updateAllScoreboards(); }
}.runTaskTimer(plugin, 0, 20L);

// Richtig — gezielt bei Ereignissen
public void onItemPickup(Player player) {
    data.addPoint();
    scoreboardManager.updateAllScoreboards(); // Rangliste hat sich geändert
}

public void onTimerTick() {
    remainingSeconds--;
    scoreboardManager.updateAllScoreboards(); // Zeit hat sich geändert
}
```

### Einzelne vs. alle Scoreboards

```java
// Nur den betroffenen Spieler updaten (z.B. nach eigenem Skip)
scoreboardManager.updateScoreboard(player);

// Alle updaten wenn sich die Rangliste geändert hat
scoreboardManager.updateAllScoreboards();
```

---

## 2.6 Teleportation

```java
// Richtig — async, lädt Chunks ohne Blockierung
player.teleportAsync(location).thenAccept(success -> {
    if (!success) {
        // thenAccept läuft auf Async-Thread — für Bukkit API zurückwechseln
        Bukkit.getScheduler().runTask(plugin, () ->
            player.sendMessage(ColorUtils.colorize(prefix + Colors.RED.getHex() + "ᴛᴇʟᴇᴘᴏʀᴛ ꜰᴇʜʟɢᴇsᴄʜʟᴀɢᴇɴ.")));
    }
});

// Vermeiden bei großen Distanzen oder ungeladenen Chunks
player.teleport(location); // blockiert Main Thread
```

---

## 2.7 Item Display Management

### Passenger-System

Das ItemDisplay ist dem Spieler als Passenger zugewiesen und folgt automatisch — kein eigener Scheduler nötig.

```java
// Richtig — einmalig zuweisen
player.addPassenger(display);

// Falsch — eigener Positions-Task
scheduler.runTaskTimer(() -> display.teleport(player.getLocation().add(0, 2, 0)), 0, 1L);
```

### Cleanup bei Spielende — Pflicht

```java
public void removeAllDisplays() {
    displays.values().forEach(Entity::remove);
    displays.clear();
}
```

Wird nicht aufgerufen: Entities bleiben als Zombies in der Welt und werden beim nächsten Server-Start als verwaiste Entities gewertet.

---

## 2.8 Memory-Management

### Spielerdaten bei Spielende leeren

```java
// Pflicht am Ende jedes Spiels
playerDataMap.clear();
plugin.getBlacklistManager().clearAllCollectedItems();
```

### Keine statischen Referenzen auf Player-Objekte

Player-Objekte statisch zu halten verhindert Garbage Collection.

```java
// Falsch — GC kann Player nicht freigeben
private static Player currentLeader;

// Richtig — UUID ist ein leichtgewichtiger Wert
private static UUID currentLeaderUuid;
// Bei Bedarf live auflösen:
Player leader = Bukkit.getPlayer(currentLeaderUuid);
if (leader == null) return; // Spieler offline — graceful handle
```

### Map-Größen im Blick behalten

```java
// Beim Entfernen eines Spielers explizit aus allen Maps austragen
playerDataMap.remove(uuid);
scoreboards.remove(uuid);
displays.remove(uuid);
```

---

## 2.9 Logging

### Log-Level korrekt verwenden

| Level | Wann | Beispiel |
|---|---|---|
| `info` | Wichtige Lifecycle-Ereignisse | Plugin enabled/disabled |
| `warning` | Fehlkonfiguration, Fallback wird verwendet | Ungültiger Sound, Fallback aktiv |
| `severe` | Kritischer Fehler, Funktion nicht verfügbar | Spawn-Welt nicht gefunden |

```java
// Richtig
if (found == null) {
    plugin.getLogger().warning("Ungültiger Sound: " + soundName + " — Verwende Standard");
    return fallback;
}

// Falsch — Debug-Logs in Produktion
plugin.getLogger().info("Sound geladen: " + soundName); // produziert Log-Spam
```

### Keine Stack-Traces für erwartete Fehler

```java
// Falsch — Stack-Trace für erwarteten Zustand
try {
    Material mat = Material.valueOf(name);
} catch (IllegalArgumentException e) {
    e.printStackTrace(); // unnötig
}

// Richtig
try {
    Material mat = Material.valueOf(name);
} catch (IllegalArgumentException e) {
    plugin.getLogger().warning("Ungültiges Material: " + name);
}
```

---

<div align="center">

Dieses Dokument beschreibt den Design- und Performance-Stand zum Zeitpunkt der Implementierung.
Änderungen am System müssen hier nachgepflegt werden.

</div>
