package com.salfit.util;

import javafx.scene.paint.Color;

public final class ZajeciaColor {

    private ZajeciaColor() {}

    public static String colorFor(String id) {
        int hash = id != null ? id.hashCode() : 0;
        double hue = Math.abs(hash) % 360;
        Color c = Color.hsb(hue, 0.45, 0.92);
        return String.format("#%02X%02X%02X",
                (int) Math.round(c.getRed() * 255),
                (int) Math.round(c.getGreen() * 255),
                (int) Math.round(c.getBlue() * 255));
    }
}
