# BetterItemForceBattle

> Ein kompetitives **Item Force Battle** Plugin für PaperMC 1.21.4+, inspiriert von **BastiGHG**.
> Spieler sammeln zufällig zugewiesene Items, kämpfen um Punkte und küren den besten Sammler.

---

## Inhaltsverzeichnis

- [Features](#features)
- [Installation](#installation)
- [Spielablauf](#spielablauf)
- [Befehle](#befehle)
- [Berechtigungen](#berechtigungen)
- [Konfiguration](#konfiguration)
- [Scoreboard](#scoreboard)
- [GUI](#gui)
- [Sounds](#sounds)
- [Item Display](#item-display)
- [Anforderungen](#anforderungen)
- [Dependencies](#dependencies)

---

## Features

| Feature | Beschreibung |
|---|---|
| Zufällige Items | Jeder Spieler erhält ein eigenes, zufälliges Item |
| Live-Rangliste | Echtzeit-Scoreboard mit Top-Spielern und Punkteständen |
| Joker-System | Spieler können ihr aktuelles Item überspringen (konfigurierbar) |
| 3D Item Display | Das gesuchte Item schwebt als 3D-Entität über dem Spielerkopf |
| Actionbar | Aktuell gesuchtes Item wird dauerhaft in der Actionbar angezeigt |
| Vollständig konfigurierbar | Farben, Nachrichten, Sounds und Items — alles per YAML |
| Anti-PvP | Kein Schaden zwischen Spielern während des Events |
| Paginiertes GUI | Spielerliste mit Filter, Sortierung und Teleport per Klick |
| Item-Whitelist | 250+ Materialien vorinstalliert, vollständig anpassbar |
| Duplikat-Schutz | Erst wenn alle Items gesammelt wurden, startet der Zyklus neu |
| Mid-Game Join | Spieler können einem laufenden Event nachträglich beitreten |

---

## Installation

1. Die JAR-Datei in den `/plugins`-Ordner des Servers legen
2. Server starten — alle Konfigurationsdateien werden automatisch erstellt
3. Spawn setzen mit `/itemforce setspawn`
4. Event starten mit `/itemforce start`

> **Voraussetzung:** PaperMC 1.21.4+ und Java 21

---

## Spielablauf

```
1. Admin startet das Event mit /itemforce start

2. Countdown läuft (konfigurierbar, Standard: 5 Sekunden)
   - Spieler werden an den Spawn teleportiert
   - Countdown-Sound bei jedem Tick

3. Event beginnt — alle Spieler erhalten ihre Starter-Items:
   - Diamant-Spitzhacke (Silk Touch, unzerstörbar)
   - 64x Goldene Mohre
   - Joker-Item (zum Uberspringen)

4. Jedem Spieler wird ein zufalliges Item zugewiesen
   - Item schwebt als 3D-Display uber dem Spielerkopf
   - Itemname erscheint dauerhaft in der Actionbar

5. Item einsammeln  +1 Punkt  sofort neues Item

6. Joker nutzen  Item uberspringen (max. 5x, konfigurierbar)
   - Per /skip oder Rechtsklick auf das Joker-Item

7. Nach der eingestellten Zeit endet das Event automatisch

8. Abschluss-Leaderboard wird allen Spielern angezeigt
   - 1. Platz / 2. Platz / 3. Platz / alle weiteren
```

---

## Befehle

### Admin-Befehle — `itemforce.admin`

| Befehl | Beschreibung |
|---|---|
| `/itemforce start` | Startet das Event mit Countdown |
| `/itemforce stop` | Bricht das laufende Event sofort ab |
| `/itemforce setspawn` | Setzt den Spawn auf die aktuelle Position |
| `/itemforce addplayer <Name>` | Fugt einen Spieler zum laufenden Event hinzu |
| `/itemforce reload` | Ladt alle Konfigurationsdateien neu |
| `/itemforce gui` | Offnet die Spielerliste |
| `/itemforce help` | Zeigt alle Befehle an |

### Spieler-Befehle — `itemforce.play`

| Befehl | Beschreibung |
|---|---|
| `/skip` | Uberspringt das aktuelle Item (verbraucht einen Joker) |
| `/sb` | Scoreboard ein- oder ausblenden |

> **Tipp:** Der Joker funktioniert auch per Rechtsklick direkt im Inventar — ohne Befehl.

---

## Berechtigungen

| Permission | Standard | Beschreibung |
|---|---|---|
| `itemforce.admin` | OP | Zugriff auf alle Admin-Befehle |
| `itemforce.play` | Alle | Zugriff auf `/skip` und das GUI |

---

## Konfiguration

### `config.yml`

```yaml
colors:
  primary: "<#478ED2>"
  secondary: "<#6953B5>"
  success: "<#00EE39>"
  error: "<#ff0000>"

event:
  duration: 60          # Dauer in Minuten
  skips: 5              # Maximale Joker pro Spieler
  countdown: 5          # Countdown-Sekunden vor Start
  end-border-radius: 10 # Weltgrenze am Ende (Radius in Blocken)

spawn:
  world: "world"
  x: 0.0
  y: 64.0
  z: 0.0

starter-items:
  pickaxe:
    enabled: true
    slot: 0
    material: DIAMOND_PICKAXE
    unbreakable: true
    enchantments:
      - "SILK_TOUCH:1"
    display-name: "<#478ED2><b>pikaxe"
  food:
    enabled: true
    slot: 7
    material: GOLDEN_CARROT
    amount: 64
    display-name: "<#FFD700><b>goldene karotten"
  joker:
    enabled: true
    slot: 8
    material: BARRIER
    display-name: "<red><b>joker"

display:
  enabled: true
  height-above-head: 1.0
  scale: 0.6
  billboard: FIXED       # CENTER | FIXED | VERTICAL | HORIZONTAL
  view-range: 64

sounds:
  enabled: true
  countdown-tick: "block.note_block.pling"
  countdown-go: "entity.ender_dragon.growl"
  item-collected: "entity.player.levelup"
  skip-used: "entity.villager.no"
  event-end: "ui.toast.challenge_complete"
```

---

### `scoreboard.yml`

```yaml
scoreboard:
  scoreboards:
    default:
      title: "<#478ED2><b>item battle"
      small-caps: true
      text-shadow: true
      lines:
        - "<#478ED2>deine stats"
        - "<white>punkte: <#FFD700>%points%"
        - "<white>zeit: <#FFD700>%time%"
        - ""
        - "<#478ED2>top spieler"
        - "#1"
        - "#2"
        - "#3"
        - "#4"
        - "#5"
```

**Platzhalter:**

| Platzhalter | Beschreibung |
|---|---|
| `%time%` | Verbleibende Spielzeit (`MM:SS`) |
| `%points%` | Eigene Punkte |
| `#1` – `#9` | Spielername + Punkte des jeweiligen Platzes |

---

### `GUI.yml`

```yaml
player-head:
  name: "<color:#08a8f8>{player}</color>"
  lore:
    - ""
    - "<grey><color:#08a8f8>●</color> Platzierung: <color:#08a8f8>#{rank}</color></grey>"
    - "<grey><color:#08a8f8>●</color> Punkte: <color:#08a8f8>{points}</color></grey>"
    - ""
    - "<grey><color:#08a8f8>»</color> Klicke um dich zu <color:#08a8f8>{player}</color> zu teleportieren!</grey>"
    - ""

filler:
  material: BLACK_STAINED_GLASS_PANE
```

**Platzhalter:**

| Platzhalter | Beschreibung |
|---|---|
| `{player}` | Spielername |
| `{rank}` | Platzierung |
| `{points}` | Punktestand |

---

### `whitelist.yml`

Alle Materialien, die als Sammelziel erscheinen konnen. Beliebige [Bukkit Material-Namen](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html) eintragbar.

```yaml
whitelist:
  - GRASS_BLOCK
  - STONE
  - OAK_LOG
  - IRON_INGOT
  # ... 250+ Materialien vorinstalliert
```

> **Duplikat-Schutz:** Ein Spieler bekommt ein Item erst wieder zugewiesen, wenn er alle Whitelist-Items einmal gesammelt hat.

---

## Scoreboard

Das Live-Scoreboard zeigt jedem Spieler individuell:

- Verbleibende Spielzeit (`MM:SS`)
- Eigene Punkte
- Top-5-Spieler mit Punkteständen

Mit `/sb` kann das Scoreboard jederzeit ein- oder ausgeblendet werden.

---

## GUI

Das Spieler-GUI (`/itemforce gui`) zeigt alle Teilnehmer als Spielerköpfe in einer paginierten Ansicht.

**Navigation:**

| Element | Position | Funktion |
|---|---|---|
| Vorherige Seite | Slot 48 | Grun wenn verfugbar, Rot wenn nicht |
| Seitenanzeige | Slot 49 | Zeigt aktuelle Seite / Gesamtseiten |
| Nachste Seite | Slot 50 | Grun wenn verfugbar, Rot wenn nicht |
| Filter | Slot 53 | Sortierung wechseln (in-place, kein Neuladen) |

**Filter-Optionen:**

| Filter | Beschreibung |
|---|---|
| Meiste Punkte | Absteigende Punkte (Standard) |
| Wenigste Punkte | Aufsteigende Punkte |
| Alphabetisch | Nach Spielername |

Linksklick auf einen Spielerkopf teleportiert direkt zu diesem Spieler.

---

## Sounds

Alle Sounds sind in `config.yml` konfigurierbar. Format: Minecraft Namespaced Key.

| Ereignis | Standard |
|---|---|
| Countdown-Tick | `block.note_block.pling` |
| Start-Signal | `entity.ender_dragon.growl` |
| Item gesammelt | `entity.player.levelup` |
| Joker verwendet | `entity.villager.no` |
| Event beendet | `ui.toast.challenge_complete` |

---

## Item Display

Uber jedem Spieler schwebt ein **3D-ItemDisplay**, das das aktuell gesuchte Item anzeigt.

**Billboard-Modi:**

| Modus | Verhalten |
|---|---|
| `CENTER` | Dreht sich immer zur Kamera |
| `FIXED` | Statisch, keine Rotation (empfohlen) |
| `VERTICAL` | Rotiert nur auf der Y-Achse |
| `HORIZONTAL` | Rotiert nur horizontal |

---

## Anforderungen

| Anforderung | Version |
|---|---|
| Java | 21+ |
| PaperMC | 1.21.4+ |
| Spigot / Bukkit | Nicht unterstutzt |

---

## Dependencies

Alle Libraries sind eingebettet (shaded) — keine separate Installation notwendig.

| Library | Zweck |
|---|---|
| [triumph-gui](https://github.com/TriumphTeam/triumph-gui) | Paginated GUI Framework |
| [Adventure API](https://docs.advntr.dev/) | MiniMessage Text-Formatting |
| [XSeries](https://github.com/CryptoMorin/XSeries) | Cross-Version Sound Support |

---

<div align="center">

**BetterItemForceBattle** — Inspiriert von [BastiGHG](https://www.youtube.com/@BastiGHG)

</div>
