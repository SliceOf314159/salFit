package com.salfit.repository;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.salfit.model.Zajecia;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GrafikRepositoryFilterTest {

    private static final Gson GSON = Repository.createGson();

    @TempDir
    Path tempDir;

    private GrafikRepository repository;

    @BeforeEach
    void setUp() throws IOException {
        Path file = tempDir.resolve("zajecia.json");
        Files.writeString(file, "[]");
        repository = new GrafikRepository(file.toString());
    }

    private Zajecia zajecia(String nazwa, String trenerId, String salaId, String termin) {
        JsonObject json = new JsonObject();
        json.addProperty("nazwa", nazwa);
        json.addProperty("trenerId", trenerId);
        json.addProperty("salaId", salaId);
        json.addProperty("termin", termin);
        json.addProperty("czasTrwaniaMinut", 60);
        json.addProperty("limitUczestnikow", 20);
        return GSON.fromJson(json, Zajecia.class);
    }

    @Test
    void filtrowaniePoTrenerze() {
        // poniedzialek 2026-06-22
        repository.save(zajecia("Yoga", "t1", "s1", "2026-06-22T08:00:00"));
        repository.save(zajecia("Pilates", "t1", "s1", "2026-06-23T10:00:00"));
        repository.save(zajecia("HIIT", "t1", "s2", "2026-06-24T12:00:00"));
        repository.save(zajecia("CrossFit", "t2", "s1", "2026-06-22T09:00:00"));
        repository.save(zajecia("Spinning", "t2", "s2", "2026-06-23T11:00:00"));
        repository.save(zajecia("Stretching", "t3", "s1", "2026-06-25T08:00:00"));
        repository.save(zajecia("Inne1", "t2", "s1", "2026-06-26T08:00:00"));
        repository.save(zajecia("Inne2", "t3", "s1", "2026-06-26T10:00:00"));
        repository.save(zajecia("Inne3", "t3", "s2", "2026-06-27T08:00:00"));


        List<Zajecia> wynik = repository.findByTrener("t1");

        assertEquals(3, wynik.size());
        assertTrue(wynik.stream().allMatch(z -> z.getTrenerId().equals("t1")));
    }

    @Test
    void filtrowaniePoSali() {
        repository.save(zajecia("Yoga", "t1", "sA", "2026-06-22T08:00:00"));
        repository.save(zajecia("Pilates", "t2", "sA", "2026-06-23T10:00:00"));
        repository.save(zajecia("HIIT", "t1", "sB", "2026-06-24T12:00:00"));

        List<Zajecia> wynik = repository.findBySala("sA");

        assertEquals(2, wynik.size());
        assertTrue(wynik.stream().allMatch(z -> z.getSalaId().equals("sA")));
    }

    @Test
    void filtrowaniePoTygodniu() {
        LocalDate poniedzialek = LocalDate.of(2026, 6, 22);

        repository.save(zajecia("WTygodniu1", "t1", "s1", "2026-06-22T08:00:00")); // pon
        repository.save(zajecia("WTygodniu2", "t1", "s1", "2026-06-26T18:00:00")); // pt (niedziela 6.28)
        repository.save(zajecia("PoprzedniTydzien", "t1", "s1", "2026-06-19T08:00:00")); // pt poprz. tyg.
        repository.save(zajecia("NastepnyTydzien", "t1", "s1", "2026-06-29T08:00:00")); // pon nast. tyg.

        List<Zajecia> wynik = repository.findByTydzien(poniedzialek);

        assertEquals(2, wynik.size());
        assertTrue(wynik.stream().anyMatch(z -> z.getNazwa().equals("WTygodniu1")));
        assertTrue(wynik.stream().anyMatch(z -> z.getNazwa().equals("WTygodniu2")));
    }

    @Test
    void tydzienBezZajecZwracaPustaListe() {
        repository.save(zajecia("Yoga", "t1", "s1", "2026-06-22T08:00:00"));

        List<Zajecia> wynik = repository.findByTydzien(LocalDate.of(2026, 7, 6));

        assertTrue(wynik.isEmpty());
    }

    @Test
    void trenerBezZajecZwracaPustaListe() {
        repository.save(zajecia("Yoga", "t1", "s1", "2026-06-22T08:00:00"));

        List<Zajecia> wynik = repository.findByTrener("t999");

        assertTrue(wynik.isEmpty());
    }
}