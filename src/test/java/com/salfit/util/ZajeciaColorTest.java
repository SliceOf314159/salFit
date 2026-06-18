package com.salfit.util;

import org.junit.jupiter.api.Test;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ZajeciaColorTest {

    @Test
    void deterministyczność_tegoSamegoId() {
        String first = ZajeciaColor.colorFor("z1");
        String second = ZajeciaColor.colorFor("z1");
        assertEquals(first, second);
    }

    @Test
    void rozneIdDajaWiekszosciowoRozneKolory() {
        List<String> ids = List.of("z1", "z2", "z3", "z4", "z5");
        Set<String> colors = new LinkedHashSet<>();
        for (String id : ids) {
            colors.add(ZajeciaColor.colorFor(id));
        }
        assertTrue(colors.size() > 1, "Większość ID powinna dawać różne kolory");
    }

    @Test
    void formatWynikuJestHeksadecymalnymKoloremCss() {
        String color = ZajeciaColor.colorFor("z1");
        assertTrue(color.matches("^#[0-9A-F]{6}$"), "Wynik powinien mieć format #RRGGBB: " + color);
    }

    @Test
    void tenSamKolorDlaTychSamychZajecMiedzySesjami() {
        String sessionOne = ZajeciaColor.colorFor("z42");
        String sessionTwo = ZajeciaColor.colorFor("z42");
        assertEquals(sessionOne, sessionTwo, "Funkcja czysta - brak stanu/losowości między wywołaniami");
    }
}
