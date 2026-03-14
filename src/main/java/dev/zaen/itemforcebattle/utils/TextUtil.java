package dev.zaen.itemforcebattle.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;

public final class TextUtil {

    private TextUtil() {}

    public static Component parse(String text) {
        return ColorUtils.colorize(text).decoration(TextDecoration.ITALIC, false);
    }

    public static String smallCaps(String text) {
        return ColorUtils.toSmallCaps(text);
    }

    public static Component parseSmallCaps(String text) {
        return parse(smallCaps(text));
    }
}
