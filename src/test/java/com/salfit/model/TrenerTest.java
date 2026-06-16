package com.salfit.model;

import com.google.gson.Gson;
import com.salfit.repository.Repository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TrenerTest {

    private static final Gson GSON = Repository.createGson();

    private Trener build(String id, String imie, String nazwisko, String email, String telefon,
                         String specjalizacja, boolean aktywny) {
        String json = String.format(
            "{\"id\":\"%s\",\"imie\":\"%s\",\"nazwisko\":\"%s\",\"email\":\"%s\"," +
            "\"telefon\":\"%s\",\"specjalizacja\":\"%s\",\"aktywny\":%b}",
            id, imie, nazwisko, email, telefon, specjalizacja, aktywny
        );
        return GSON.fromJson(json, Trener.class);
    }

    @Test
    void getters_returnCorrectValues() {
        Trener t = build("t1", "Piotr", "Wiśniewski", "p@gym.pl", "555000111", "Yoga", true);
        assertEquals("t1", t.getId());
        assertEquals("Piotr", t.getImie());
        assertEquals("Wiśniewski", t.getNazwisko());
        assertEquals("p@gym.pl", t.getEmail());
        assertEquals("555000111", t.getTelefon());
        assertEquals("Yoga", t.getSpecjalizacja());
        assertTrue(t.isAktywny());
    }

    @Test
    void getImieNazwisko_concatenatesCorrectly() {
        Trener t = build("t2", "Karolina", "Brzezicka", "k@gym.pl", "000", "Pilates", false);
        assertEquals("Karolina Brzezicka", t.getImieNazwisko());
    }

    @Test
    void setAktywny_toFalse_changesState() {
        Trener t = build("t3", "Marek", "Krawczyk", "m@gym.pl", "000", "CrossFit", true);
        t.setAktywny(false);
        assertFalse(t.isAktywny());
    }

    @Test
    void setAktywny_toTrue_changesState() {
        Trener t = build("t4", "Zofia", "Grabowska", "z@gym.pl", "000", "Spinning", false);
        t.setAktywny(true);
        assertTrue(t.isAktywny());
    }

    @Test
    void isAktywny_initiallyFalse_returnsFalse() {
        Trener t = build("t5", "Adam", "Nowak", "a@gym.pl", "000", "HIIT", false);
        assertFalse(t.isAktywny());
    }
}
