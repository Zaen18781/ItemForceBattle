# 🎮 BetterItemForceBattle

> Ein kompetitives **Item Force Battle** Plugin für PaperMC 1.21.4+, inspiriert von **BastiGHG**.
> Spieler sammeln zufällig zugewiesene Items, kämpfen um Punkte und ehere den besten Spieler! 🏆

---

## 📋 Inhaltsverzeichnis

- [✨ Features](#-features)
- [⚙️ Installation](#️-installation)
- [🎮 Spielablauf](#-spielablauf)
- [🛠️ Befehle](#️-befehle)
- [🔐 Berechtigungen](#-berechtigungen)
- [📁 Konfiguration](#-konfiguration)
  - [config.yml](#configyml)
  - [scoreboard.yml](#scoreboardyml)
  - [GUI.yml](#guiyml)
  - [whitelist.yml](#whitelistyml)
- [📊 Scoreboard](#-scoreboard)
- [🖥️ GUI](#️-gui)
- [🔊 Sounds](#-sounds)
- [🧱 Item Display](#-item-display)
- [🏗️ Anforderungen](#️-anforderungen)
- [📦 Dependencies](#-dependencies)

---

## ✨ Features

| Feature | Beschreibung |
|---|---|
| 🎯 **Zufällige Items** | Jeder Spieler bekommt ein anderes zufälliges Item zugewiesen |
| 🏆 **Live-Rangliste** | Scoreboard mit Top-5-Spielern und Punktestand in Echtzeit |
| 🧊 **Freeze-System** | Spieler werden beim Countdown eingefroren (Position-Reset, keine Effekte) |
| 🃏 **Joker-System** | Spieler können ihr aktuelles Item überspringen (konfigurierbar) |
| 🖥️ **3D Item Display** | Item schwebt als 3D-Entität über dem Spielerkopf |
| 📋 **Actionbar** | Aktuell gesuchtes Item wird dauerhaft in der Actionbar angezeigt |
| 🎨 **Vollständig konfigurierbar** | Farben, Nachrichten, Sounds, Items — alles in YAML |
| 🔠 **Small Caps Stil** | Elegante Unicode-Buchstaben (ᴀʙᴄᴅᴇ) durch die gesamte UI |
| 🛡️ **Anti-PvP** | Kein Schaden zwischen Spielern während des Events |
| 📄 **Paginiertes GUI** | Übersichtliche Spielerliste mit Teleport-Funktion per Klick |
| 🌐 **Whitelist** | 250+ Materialien als spielbare Items konfigurierbar |
| 🔄 **Keine Duplikate** | Spieler bekommen erst neue Items wenn alle einmal gesammelt wurden |
| ➕ **Mid-Game Join** | Spieler können laufendem Event nachträglich hinzugefügt werden |

---

## ⚙️ Installation

1. **JAR herunterladen** und in den `/plugins`-Ordner deines Servers legen
2. **Server starten** — alle Konfigurationsdateien werden automatisch erstellt
3. **Spawn setzen** mit `/itemforce setspawn` an deiner gewünschten Position
4. **Event starten** mit `/itemforce start`

> ⚠️ **Voraussetzung:** PaperMC 1.21.4+ und Java 21

---

## 🎮 Spielablauf

```
1. 👑 Admin startet das Event mit /itemforce start
         ↓
2. ⏳ 5-Sekunden-Countdown
        • Spieler werden an Spawn teleportiert
        • Spieler werden eingefroren (können sich nicht bewegen)
        • Countdown-Sound bei jedem Tick
         ↓
3. 🚀 Event beginnt — alle Spieler erhalten:
        • 🪓 Diamant-Spitzhacke (Silk Touch, unzerstörbar)
        • 🥕 64x Goldene Möhre
        • 🃏 Joker-Item (zum Überspringen per Rechtsklick)
         ↓
4. 🎯 Jeder Spieler bekommt ein zufälliges Item zugewiesen
        • Item schwebt als 3D-Display über dem Spielerkopf
        • Item-Name erscheint in der Actionbar (Minecraft-Systemsprache)
         ↓
5. ✅ Item einsammeln → +1 Punkt → sofort neues Item zugewiesen
         ↓
6. 🃏 Joker nutzen → Item überspringen (max. 5x, konfigurierbar)
        • Via /skip oder Rechtsklick auf das Joker-Item
         ↓
7. ⏱️ Nach 60 Minuten endet das Event automatisch
         ↓
8. 🏆 Abschluss-Leaderboard mit Medaillen wird allen angezeigt
        • 🥇 1. Platz
        • 🥈 2. Platz
        • 🥉 3. Platz
        • Alle weiteren Platzierungen
```

---

## 🛠️ Befehle

### 👑 Admin-Befehle — `itemforce.admin`

| Befehl | Beschreibung |
|---|---|
| `/itemforce start` | Startet das Event mit Countdown |
| `/itemforce stop` | Bricht das laufende Event sofort ab |
| `/itemforce setspawn` | Setzt den Spawn an deine aktuelle Position |
| `/itemforce addplayer <Name>` | Fügt einen Spieler zum laufenden Event hinzu |
| `/itemforce reload` | Lädt alle Konfigurationsdateien neu |
| `/itemforce gui` | Öffnet die Spielerliste als GUI |
| `/itemforce help` | Zeigt die Hilfe-Übersicht |

**Aliase:** `/itemforce` → `/if` · `/ifb` · `/bifb`

### 🎮 Spieler-Befehle — `itemforce.play`

| Befehl | Beschreibung |
|---|---|
| `/skip` | Überspringt das aktuelle Item (verbraucht einen Joker) |
| `/sb` | Scoreboard ein-/ausblenden |

> 💡 **Tipp:** Der Joker kann auch direkt per **Rechtsklick** auf das Joker-Item im Inventar genutzt werden — ohne `/skip` einzutippen!

---

## 🔐 Berechtigungen

| Permission | Standard | Beschreibung |
|---|---|---|
| `itemforce.admin` | OP | Zugriff auf alle Admin-Befehle |
| `itemforce.play` | Alle Spieler | Zugriff auf `/skip` und das Spieler-GUI |

---

## 📁 Konfiguration

### `config.yml`

Die Hauptkonfigurationsdatei des Plugins.

```yaml
# 🎨 Farben (MiniMessage-Format)
colors:
  primary: "<#478ED2>"
  secondary: "<#6953B5>"
  success: "<#00EE39>"
  error: "<#ff0000>"

# ⏱️ Event-Einstellungen
event:
  duration: 60          # Dauer in Minuten
  skips: 5              # Max. Joker pro Spieler
  countdown: 5          # Countdown-Sekunden vor Start
  end-border-radius: 10

# 📍 Spawn-Position (wird automatisch via /itemforce setspawn gesetzt)
spawn:
  world: "world"
  x: 0.0
  y: 64.0
  z: 0.0
  yaw: 0.0
  pitch: 0.0

# 🎒 Starter-Items (beim Event-Start erhalten)
starter-items:
  pickaxe:
    enabled: true
    slot: 0
    material: DIAMOND_PICKAXE
    unbreakable: true
    enchantments:
      - "SILK_TOUCH:1"
    display-name: "<#478ED2><b>ᴘɪᴄᴋᴀxᴇ"
  food:
    enabled: true
    slot: 7
    material: GOLDEN_CARROT
    amount: 64
    display-name: "<#FFD700><b>ɢᴏʟᴅᴇɴᴇ ᴋᴀʀᴏᴛᴛᴇɴ"
  joker:
    enabled: true
    slot: 8
    material: BARRIER
    amount: 1
    unbreakable: true
    display-name: "<red><b>ᴊᴏᴋᴇʀ"

# 🧱 3D Item Display über dem Spielerkopf
display:
  enabled: true
  height-above-head: 1.0    # Höhe über dem Spielerkopf
  scale: 0.6                 # Größe des Displays (1.0 = normal)
  billboard: FIXED           # CENTER | FIXED | VERTICAL | HORIZONTAL
  view-range: 64             # Sichtweite in Blöcken
  actionbar-format: "<#FFD700>🎯 <white>{item}"

# 🔊 Sounds
sounds:
  enabled: true
  countdown-tick: "BLOCK_NOTE_BLOCK_PLING"
  countdown-go: "ENTITY_ENDER_DRAGON_GROWL"
  item-collected: "ENTITY_PLAYER_LEVELUP"
  skip-used: "ENTITY_VILLAGER_NO"
  event-end: "UI_TOAST_CHALLENGE_COMPLETE"
```

---

### `scoreboard.yml`

Vollständige Kontrolle über das Scoreboard-Layout. Jede Zeile ist frei konfigurierbar.

```yaml
scoreboard:
  scoreboards:
    default:
      title: "<#478ED2><b>ɪᴛᴇᴍ ʙᴀᴛᴛʟᴇ"
      small-caps: true      # Small-Caps-Konvertierung aktivieren
      text-shadow: true     # Textshadow aktivieren
      lines:
        - "<#478ED2>ᴅᴇɪɴᴇ sᴛᴀᴛs"
        - "<white>ᴘᴜɴᴋᴛᴇ: <#FFD700>%points%"
        - "<white>ᴢᴇɪᴛ: <#FFD700>%time%"
        - ""
        - "<#478ED2>ᴛᴏᴘ sᴘɪᴇʟᴇʀ"
        - "#1"       # Platz 1 — wird automatisch befüllt
        - "#2"       # Platz 2
        - "#3"       # Platz 3
        - "#4"
        - "#5"
```

**Sonder-Platzhalter für Zeilen:**

| Platzhalter | Beschreibung |
|---|---|
| `%time%` | Verbleibende Spielzeit (`MM:SS`) |
| `%points%` | Eigene Punkte des Spielers |
| `#1` – `#9` | Automatisch: Spielername + Punkte des jeweiligen Platzes |

---

### `GUI.yml`

Styling der Spieler-Köpfe und Navigation im GUI.

```yaml
player-head:
  name: "<#478ED2>{player}"
  lore:
    - ""
    - "<white>ᴘʟᴀᴛᴢɪᴇʀᴜɴɢ: <#FFD700>#{rank}"
    - "<white>ᴘᴜɴᴋᴛᴇ: <#FFD700>{points}"
    - ""
    - "<dark_gray>« <gray>ʟɪɴᴋsᴋʟɪᴄᴋ <#478ED2>→ <white>ᴛᴇʟᴇᴘᴏʀᴛ"

top-player-head:
  name: "<#FFD700>🏆 {player}"
  lore:
    - ""
    - "<#478ED2><b>ᴛᴏᴘ ꜱᴘɪᴇʟᴇʀ</b>"
    - "<#478ED2>ᴘᴜɴᴋᴛᴇ: <#FFD700>{points}"
    - ""
    - "<dark_gray>« <gray>ʟɪɴᴋsᴋʟɪᴄᴋ <#478ED2>→ <white>ᴛᴇʟᴇᴘᴏʀᴛ"

pagination:
  previous-page:
    material: RED_CANDLE
    name: "<dark_gray>« <#478ED2>ᴠᴏʀʜᴇʀɪɢᴇ sᴇɪᴛᴇ"
  next-page:
    material: RED_CANDLE
    name: "<#478ED2>ɴäᴄʜsᴛᴇ sᴇɪᴛᴇ <dark_gray>»"

filler:
  material: BLACK_STAINED_GLASS_PANE
  name: " "
```

**Verfügbare Platzhalter in `GUI.yml`:**

| Platzhalter | Beschreibung |
|---|---|
| `{player}` | Spielername |
| `{rank}` | Platzierung des Spielers |
| `{points}` | Punktestand des Spielers |

---

### `whitelist.yml`

Alle Materialien, die als Sammel-Ziel im Spiel erscheinen können.
Du kannst beliebige [Bukkit Material-Namen](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html) eintragen oder entfernen.

```yaml
whitelist:
  - GRASS_BLOCK
  - DIRT
  - STONE
  - COBBLESTONE
  - OAK_LOG
  - COAL
  - IRON_INGOT
  # ... 250+ weitere Materialien vorinstalliert
```

> 🔄 **Duplikat-Schutz:** Ein Spieler bekommt dasselbe Item erst wieder zugewiesen, wenn er alle Items der Whitelist einmal gesammelt hat — dann startet der Zyklus neu.

---

## 📊 Scoreboard

Das Live-Scoreboard zeigt jedem Spieler individuell:

- ⏱️ **Verbleibende Spielzeit** im Format `MM:SS`
- 🎯 **Eigene Punkte**
- 🥇🥈🥉 **Top-5-Spieler** mit Namen und Punkteständen

Das komplette Layout wird über `scoreboard.yml` gesteuert — keine Hardcoding.
Mit `/sb` kann jeder Spieler das Scoreboard jederzeit **ein- oder ausblenden**.

---

## 🖥️ GUI

Das Spieler-GUI (`/itemforce gui`) zeigt alle Teilnehmer als **Spielerköpfe** in einer paginierten Ansicht (9×5 Slots).

- 🖱️ **Linksklick** auf einen Spielerkopf → Teleport zu diesem Spieler
- 🏆 Der **Top-Spieler** erscheint zusätzlich gesondert im unteren Bereich mit goldenem Styling
- ◀ **Vorherige Seite** (Slot 48) / **Nächste Seite** (Slot 50) zum Blättern
- 🖼️ Schwarze Glasscheiben als Füller im unteren Bereich

---

## 🔊 Sounds

Alle Sounds sind in `config.yml` konfigurierbar und können global deaktiviert werden (`sounds.enabled: false`).

| Ereignis | Standard-Sound |
|---|---|
| ⏳ Countdown-Tick (jede Sekunde) | `BLOCK_NOTE_BLOCK_PLING` |
| 🚀 Start-Signal (`LOS GEHT'S!`) | `ENTITY_ENDER_DRAGON_GROWL` |
| ✅ Item erfolgreich gesammelt | `ENTITY_PLAYER_LEVELUP` |
| 🃏 Joker verwendet | `ENTITY_VILLAGER_NO` |
| 🏁 Event beendet | `UI_TOAST_CHALLENGE_COMPLETE` |

Gültige Sound-Namen findest du in der [Bukkit Sound-Dokumentation](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Sound.html).

---

## 🧱 Item Display

Über jedem Spieler schwebt ein **3D-ItemDisplay**, das das aktuell gesuchte Item zeigt und dem Spieler als Passenger folgt.

### 🎛️ Billboard-Modi

| Modus | Verhalten |
|---|---|
| `CENTER` | Dreht sich immer zur Kamera des Betrachters |
| `FIXED` | Statisch — keine Rotation (empfohlen ✅) |
| `VERTICAL` | Rotiert nur auf der Y-Achse |
| `HORIZONTAL` | Rotiert nur in der horizontalen Ebene |

> 💡 **Empfehlung:** `FIXED` sorgt dafür, dass das Item immer aufrecht steht — unabhängig davon, in welche Richtung der Spieler beim Spielstart schaut.

### ⚙️ Konfigurationsoptionen

| Option | Beschreibung | Standard |
|---|---|---|
| `enabled` | Display aktivieren/deaktivieren | `true` |
| `height-above-head` | Höhe über dem Spielerkopf | `1.0` |
| `scale` | Größe des Displays | `0.6` |
| `billboard` | Rotationsmodus (siehe oben) | `FIXED` |
| `view-range` | Sichtweite in Blöcken | `64` |

---

## 🏗️ Anforderungen

| Anforderung | Version |
|---|---|
| ☕ Java | **21+** |
| 🗒️ PaperMC | **1.21.4+** |
| 🔌 Spigot / Bukkit | ❌ Nicht unterstützt |

---

## 📦 Dependencies

Alle Libraries sind **eingebettet (shaded)** — keine separate Installation nötig.

| Library | Zweck |
|---|---|
| [triumph-gui](https://github.com/TriumphTeam/triumph-gui) | Paginated GUI Framework |
| [Adventure API](https://docs.advntr.dev/) | MiniMessage Text-Formatting |
| [XSeries](https://github.com/CryptoMorin/XSeries) | Cross-Version Sound Support |

---

## 👤 Autor

**Zaen** Inspiriert von [BastiGHG](https://www.youtube.com/@BastiGHG)

---

<div align="center">

**BetterItemForceBattle**  Made with ❤️ for BetterAttack

</div>

_README.MD with KI_
