package dev.zaen.itemforcebattle.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorUtils {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private static final Pattern HEX_PATTERN = Pattern.compile("<#([A-Fa-f0-9]{6})>");
    private static final Pattern LEGACY_PATTERN = Pattern.compile("&([0-9a-fk-orA-FK-OR])");

    /**
     * Konvertiert einen String mit HEX (<#RRGGBB>) und Legacy (&c) Codes zu einem Component
     */
    public static Component colorize(String message) {
        if (message == null || message.isEmpty()) {
            return Component.empty();
        }
        
        // Legacy codes (&c) zu MiniMessage Format konvertieren
        message = convertLegacyToMiniMessage(message);
        
        // HEX codes (<#RRGGBB>) zu MiniMessage Format konvertieren
        message = convertHexToMiniMessage(message);
        
        return MINI_MESSAGE.deserialize(message);
    }

    /**
     * Konvertiert Legacy Codes (&c) zu MiniMessage Format
     */
    private static String convertLegacyToMiniMessage(String message) {
        StringBuilder result = new StringBuilder();
        Matcher matcher = LEGACY_PATTERN.matcher(message);
        
        while (matcher.find()) {
            String code = matcher.group(1).toLowerCase();
            String replacement = switch (code) {
                case "0" -> "<black>";
                case "1" -> "<dark_blue>";
                case "2" -> "<dark_green>";
                case "3" -> "<dark_aqua>";
                case "4" -> "<dark_red>";
                case "5" -> "<dark_purple>";
                case "6" -> "<gold>";
                case "7" -> "<gray>";
                case "8" -> "<dark_gray>";
                case "9" -> "<blue>";
                case "a" -> "<green>";
                case "b" -> "<aqua>";
                case "c" -> "<red>";
                case "d" -> "<light_purple>";
                case "e" -> "<yellow>";
                case "f" -> "<white>";
                case "k" -> "<obfuscated>";
                case "l" -> "<bold>";
                case "m" -> "<strikethrough>";
                case "n" -> "<underlined>";
                case "o" -> "<italic>";
                case "r" -> "<reset>";
                default -> "";
            };
            matcher.appendReplacement(result, replacement);
        }
        matcher.appendTail(result);
        
        return result.toString();
    }

    /**
     * Konvertiert HEX codes (<#RRGGBB>) zu MiniMessage Format (<color:#RRGGBB>)
     */
    private static String convertHexToMiniMessage(String message) {
        Matcher matcher = HEX_PATTERN.matcher(message);
        StringBuilder result = new StringBuilder();
        
        while (matcher.find()) {
            String hex = matcher.group(1);
            matcher.appendReplacement(result, "<color:#" + hex + ">");
        }
        matcher.appendTail(result);
        
        return result.toString();
    }

    /**
     * Konvertiert einen String zu einem einfachen String ohne Farben (für Logs etc.)
     */
    public static String stripColors(String message) {
        if (message == null) return "";
        
        // Entferne alle MiniMessage Tags
        message = message.replaceAll("<[^>]+>", "");
        // Entferne Legacy Codes
        message = message.replaceAll("&[0-9a-fk-orA-FK-OR]", "");
        // Entferne HEX Codes
        message = message.replaceAll("<#[A-Fa-f0-9]{6}>", "");
        
        return message;
    }

    /**
     * Ersetzt Platzhalter in einem String
     */
    public static String replacePlaceholders(String message, String... replacements) {
        if (message == null) return "";

        for (int i = 0; i < replacements.length - 1; i += 2) {
            message = message.replace(replacements[i], replacements[i + 1]);
        }

        return message;
    }

    /**
     * Konvertiert einen String zu SmallCaps (Unicode)
     */
    public static String toSmallCaps(String text) {
        if (text == null || text.isEmpty()) return text;

        StringBuilder result = new StringBuilder();
        boolean inTag = false;

        for (char c : text.toCharArray()) {
            if (c == '<') {
                inTag = true;
                result.append(c);
            } else if (c == '>') {
                inTag = false;
                result.append(c);
            } else if (inTag || c == '§') {
                result.append(c);
            } else {
                result.append(convertToSmallCap(c));
            }
        }

        return result.toString();
    }

    private static char convertToSmallCap(char c) {
        return switch (Character.toLowerCase(c)) {
            case 'a' -> 'ᴀ';
            case 'b' -> 'ʙ';
            case 'c' -> 'ᴄ';
            case 'd' -> 'ᴅ';
            case 'e' -> 'ᴇ';
            case 'f' -> 'ғ';
            case 'g' -> 'ɢ';
            case 'h' -> 'ʜ';
            case 'i' -> 'ɪ';
            case 'j' -> 'ᴊ';
            case 'k' -> 'ᴋ';
            case 'l' -> 'ʟ';
            case 'm' -> 'ᴍ';
            case 'n' -> 'ɴ';
            case 'o' -> 'ᴏ';
            case 'p' -> 'ᴘ';
            case 'q' -> 'ǫ';
            case 'r' -> 'ʀ';
            case 's' -> 's';
            case 't' -> 'ᴛ';
            case 'u' -> 'ᴜ';
            case 'v' -> 'ᴠ';
            case 'w' -> 'ᴡ';
            case 'x' -> 'x';
            case 'y' -> 'ʏ';
            case 'z' -> 'ᴢ';
            default -> c;
        };
    }
}
