package com.salfit.util;

import javafx.scene.paint.Color;

// klasa pomocnicza ktora generuje "losowy" kolor dla zajec
// na podstawie ich id. to samo id zawsze daje ten sam kolor,
// dzieki czemu karty zajec w grafiku nie zmieniaja kolorow przy kazdym odswiezeniu.
public final class ZajeciaColor {

    private ZajeciaColor() {}

    // glowna metoda - zamienia id zajec na kolor w formacie hex
    public static String colorFor(String id) {
        int hash = id != null ? id.hashCode() : 0;
        double hue = Math.abs(hash) % 360; // hue w skali kola kolorow 0-360 stopni
        // HSB = Hue, Saturation, Brightness - tutaj wybielamy/pastelujemy kolor (0.45 sat, 0.92 brightness)
        // zeby karty na grafiku nie byly zbyt ostre/jaskrawe
        Color c = Color.hsb(hue, 0.45, 0.92);
        // konwertujemy skladowe RGB  na hex
        return String.format("#%02X%02X%02X",
                (int) Math.round(c.getRed() * 255),
                (int) Math.round(c.getGreen() * 255),
                (int) Math.round(c.getBlue() * 255));
    }
}
