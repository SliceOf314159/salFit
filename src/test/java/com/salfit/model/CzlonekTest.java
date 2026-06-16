package com.salfit.model;

import com.google.gson.Gson;
import com.salfit.repository.Repository;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class CzlonekTest {

    private static final Gson GSON = Repository.createGson();

    private Czlonek build(String id, String imie, String nazwisko, String email, String telefon, String dataUrodzenia) {
        String json = String.format(
            "{\"id\":\"%s\",\"imie\":\"%s\",\"nazwisko\":\"%s\",\"email\":\"%s\",\"telefon\":\"%s\",\"dataUrodzenia\":\"%s\"}",
            id, imie, nazwisko, email, telefon, dataUrodzenia
        );
        return GSON.fromJson(json, Czlonek.class);
    }

    @Test
    void getters_returnCorrectValues() {
        Czlonek c = build("id-1", "Jan", "Kowalski", "jan@test.pl", "123456789", "1990-05-15");
        assertEquals("id-1", c.getId());
        assertEquals("Jan", c.getImie());
        assertEquals("Kowalski", c.getNazwisko());
        assertEquals("jan@test.pl", c.getEmail());
        assertEquals("123456789", c.getTelefon());
        assertEquals(LocalDate.of(1990, 5, 15), c.getDataUrodzenia());
    }

    @Test
    void getImieNazwisko_concatenatesWithSpace() {
        Czlonek c = build("x", "Anna", "Nowak", "a@b.pl", "000", "2000-01-01");
        assertEquals("Anna Nowak", c.getImieNazwisko());
    }

    @Test
    void getImieNazwisko_singleWordNames_stillJoinsWithSpace() {
        Czlonek c = build("x", "Maria", "Wiśniewska", "m@b.pl", "000", "1985-03-20");
        assertEquals("Maria Wiśniewska", c.getImieNazwisko());
    }
}
