package dev.zaen.itemforcebattle.utils;

public enum Unicodes {
    ARROW("»"),
    DOT("•"),
    PREFIX_DOT("◘"),
    FLOPPY_DISK("\uD83D\uDCBE"),
    CHECK_MARK("\u2713"),
    CROSS_MARK("\u2717"),
    HEAVY_CHECK_MARK("\u2714"),
    HEAVY_CROSS_MARK("\u2716"),
    HEAVY_PLUS_SIGN("\u271A"),
    HEAVY_MINUS_SIGN("\u2796"),
    HEAVY_EXCLAMATION_MARK("\u2757"),
    HEAVY_QUESTION_MARK("\u2753"),
    CROWN("\uD83D\uDC51"),
    DIAMOND_HOLLOW("\u2662"),
    TRADEMARK("\u2122"),
    COPYRIGHT("\u00A9"),
    REGISTERED("\u00AE"),
    CLIPBOARD("\uD83D\uDCCB"),
    ROUND_DOT("\u25CF"),
    SKULL("\u2620"),
    SWORD("\uD83D\uDDE1"),
    CLOUD("\u2601"),
    HOURGLASS("\u231B"),
    GEM("\u2756"),
    HEART("\uD83D\uDC9C"),
    HEART_HOLLOW("\u2661"),
    GEM_HOLLOW("\u2662"),
    FLAME("\uD83D\uDD25"),
    STAR("\u2605");

    private final String string;

    Unicodes(String string) {
        this.string = string;
    }

    public String getString() {
        return string;
    }
}
