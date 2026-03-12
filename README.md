# BetterItemForceBattle

Ein Minecraft Paper Plugin für das Item Force Battle Event, inspiriert von BastiGHG.

## 📋 Features

- **Item Force Battle**: Jeder Spieler bekommt zufällige Items zugeteilt, die er sammeln muss
- **Floating Item Display**: Das zu sammelnde Item schwebt über dem Kopf des Spielers (für alle sichtbar)
- **Actionbar Anzeige**: Item, Zeit und Punkte werden in der Actionbar angezeigt
- **Skip System**: 5 Skips pro Spieler, um schwierige Items zu überspringen
- **Dynamisches Scoreboard**: Zeigt die Spieler um deinen Rang herum an
- **Umfangreiche Blacklist**: Nether, End, Creative-Only Items sind ausgeschlossen
- **PvP & Damage Protection**: Kein PvP, kein Schaden während des Events
- **Countdown mit Freeze**: 5 Sekunden Freeze + Countdown zum Start
- **Konfigurierbare Starter-Items**: Dia-Pickaxe mit Silk Touch + Goldene Karotten
- **Sound-Effekte**: Bei Countdown, Item gesammelt, Skip, Event Ende
- **HEX & Legacy Farbcodes**: Volle Unterstützung für `<#RRGGBB>` und `&c` Codes

## 🎮 Befehle

| Befehl | Beschreibung | Permission |
|--------|--------------|------------|
| `/itemforce start` | Startet das Event | `itemforce.admin` |
| `/itemforce stop` | Stoppt das Event | `itemforce.admin` |
| `/itemforce setspawn` | Setzt den Spawn-Punkt | `itemforce.admin` |
| `/itemforce reload` | Lädt die Config neu | `itemforce.admin` |
| `/skip` | Überspringt dein aktuelles Item | `itemforce.play` |

## 🔧 Installation

1. Baue das Plugin mit Gradle:
   ```bash
   ./gradlew build
   ```
   Oder unter Windows:
   ```cmd
   gradlew.bat build
   ```

2. Die JAR-Datei findest du unter `build/libs/BetterItemForceBattle-1.0.0.jar`

3. Kopiere die JAR in deinen `plugins` Ordner

4. Starte den Server neu

5. Setze den Spawn mit `/itemforce setspawn`

6. Starte das Event mit `/itemforce start`

## ⚙️ Konfiguration

### config.yml

- Event-Dauer, Skips, Countdown-Zeit
- Spawn-Position
- Starter-Items (anpassbar)
- Scoreboard-Einstellungen
- Sound-Effekte
- Alle Nachrichten anpassbar
- Farbpalette

### blacklist.yml

- Kategorien blacklisten (z.B. alle NETHERITE Items)
- Einzelne Items blacklisten
- Vorkonfiguriert mit allen nicht-obtainable Items

## 📦 Anforderungen

- Paper 1.21.4+
- Java 21

## 🎨 Farbpalette

```yaml
colors:
  primary: "<#478ED2>"    # Blau
  secondary: "<#6953B5>"  # Lila
  success: "<#00EE39>"    # Grün
  error: "<#ff0000>"      # Rot
```

## 📝 Lizenz

Dieses Plugin wurde von Claude (Anthropic) erstellt.
