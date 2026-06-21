package com.salfit.repository;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.salfit.model.Zajecia;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ZajeciaPotwierdzenieObecnosciTest {

    private static final Gson GSON = Repository.createGson();

    private Zajecia nowaZajecia() {
        JsonObject json = new JsonObject();
        json.addProperty("nazwa", "Yoga");
        json.addProperty("trenerId", "t1");
        json.addProperty("salaId", "s1");
        json.addProperty("termin", "2026-06-22T08:00:00");
        json.addProperty("czasTrwaniaMinut", 60);
        json.addProperty("limitUczestnikow", 20);
        return GSON.fromJson(json, Zajecia.class);
    }

    @Test
    void potwierdzenieNowegoUczestnika() {
        Zajecia z = nowaZajecia();
        z.dodajUczestnika("c1");

        z.potwierdzUczestnika("c1");

        assertTrue(z.czyPotwierdzony("c1"));
        assertTrue(z.getPotwierdzeniUczestnicy().contains("c1"));
    }

    @Test
    void potwierdzenieJuzPotwierdzonegoUczestnika_brakDuplikatow() {
        Zajecia z = nowaZajecia();
        z.dodajUczestnika("c1");

        z.potwierdzUczestnika("c1");
        z.potwierdzUczestnika("c1");

        long liczbaWystapien = z.getPotwierdzeniUczestnicy().stream()
                .filter(id -> id.equals("c1"))
                .count();
        assertEquals(1, liczbaWystapien);
    }

    @Test
    void odznaczeniePotwierdzonegoUczestnika() {
        Zajecia z = nowaZajecia();
        z.dodajUczestnika("c1");
        z.potwierdzUczestnika("c1");

        z.odznaczUczestnika("c1");

        assertFalse(z.czyPotwierdzony("c1"));
        assertFalse(z.getPotwierdzeniUczestnicy().contains("c1"));
    }

    @Test
    void odznaczenieNieobecnegoUczestnika_brakWyjatkuListaNiezmieniona() {
        Zajecia z = nowaZajecia();
        z.dodajUczestnika("c1");
        z.potwierdzUczestnika("c1");

        z.odznaczUczestnika("c2");

        assertEquals(1, z.getPotwierdzeniUczestnicy().size());
        assertTrue(z.czyPotwierdzony("c1"));
    }

    @Test
    void sprawdzenieNiepotwierdzonegoUczestnika() {
        Zajecia z = nowaZajecia();
        z.dodajUczestnika("c1");

        assertFalse(z.czyPotwierdzony("c1"));
    }
}