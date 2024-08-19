package org.jenkinsci.plugins.pitmutation.portlets;

import lombok.AllArgsConstructor;

import java.awt.*;

@AllArgsConstructor
public enum CoverageRange {
    /*
     * This ordering is important for the lookup; the lowest value should be
     * last
     */
    PERFECT(100.0, new Color(0x00, 0x8B, 0x00), new Color(0xEE, 0xEE, 0xEE)), //
    EXCELLENT(90.0, new Color(0x00, 0xCD, 0x00), new Color(0x00, 0x00, 0x00)), //
    GOOD(80.0, new Color(0x7A, 0xFF, 0x3F), new Color(0x00, 0x00, 0x00)), //
    SUFFICIENT(70.0, new Color(0xC8, 0xFF, 0x3F), new Color(0x00, 0x00, 0x00)), //
    FAIR(60.0, new Color(0xFF, 0xFF, 0x00), new Color(0x00, 0x00, 0x00)), //
    AVERAGE(50.0, new Color(255, 165, 0), new Color(0x00, 0x00, 0x00)),
    POOR(40.0, new Color(0xFF, 0x7F, 0x00), new Color(0x00, 0x00, 0x00)), //
    TRAGIC(25.0, new Color(255, 69, 0), new Color(0xEE, 0xEE, 0xEE)), //
    ABYSMAL(0.0, new Color(0xFF, 0x00, 0x00), new Color(0xEE, 0xEE, 0xEE)),
    NA(0.0, new Color(0xFF, 0xFF, 0xFF), new Color(0x00, 0x00, 0x00));

    /**
     * Minimum coverage amount for this range
     */
    private final double floor;
    private final Color fillColor;
    private final Color lineColor;

    public static CoverageRange valueOf(final double amount) {
        for (final CoverageRange range : values()) {
            if (amount >= range.floor) {
                return range;
            }
        }
        return ABYSMAL;
    }

    public String getFillHexString() {
        return colorAsHexString(fillColor);
    }

    public String getLineHexString() {
        return colorAsHexString(lineColor);
    }

    public static Color fillColorOf(final double amount) {
        for (int i = 0; i < values().length; i++) {
            final CoverageRange range = values()[i];
            if (amount == range.floor) {
                return range.fillColor;
            } else if (amount > range.floor) {
                if (i == 0) {
                    return values()[i].fillColor;
                }
                final CoverageRange range1 = values()[i - 1];
                final double t0 = amount - range.floor;
                final double t1 = range1.floor - amount;
                return blendedColor(range.fillColor, range1.fillColor, t0, t1);
            }
        }
        return ABYSMAL.fillColor;
    }

    private static Color blendedColor(final Color fillColor0,
                                      final Color fillColor1,
                                      final double t0,
                                      final double t1) {
        final double total = t0 + t1;
        final int r = (int) ((fillColor0.getRed() * t1 + fillColor1.getRed() * t0) / total);
        final int g = (int) ((fillColor0.getGreen() * t1 + fillColor1.getGreen() * t0) / total);
        final int b = (int) ((fillColor0.getBlue() * t1 + fillColor1.getBlue() * t0) / total);
        return new Color(r, g, b);
    }

    public static String colorAsHexString(final Color c) {
        return String.format("%02X%02X%02X", c.getRed(), c.getGreen(), c.getBlue());
    }
}
