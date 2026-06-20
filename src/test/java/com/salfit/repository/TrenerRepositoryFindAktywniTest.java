package com.salfit.repository;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.salfit.model.Trener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TrenerRepositoryFindAktywniTest {

    private static final Gson GSON = Repository.createGson();

    @TempDir
    Path tempDir;

    private TrenerRepository repository;

    @BeforeEach
    void setUp() throws IOException {
        Path file = tempDir.resolve("trenerzy.json");
        Files.writeString(file, "[]");
        repository = new TrenerRepository(file.toString());
    }

    private Trener trener(String imie, String nazwisko, boolean aktywny) {
        JsonObject json = new JsonObject();
        json.addProperty("imie", imie);
        json.addProperty("nazwisko", nazwisko);
        json.addProperty("email", imie.toLowerCase() + "@example.com");
        json.addProperty("telefon", "111222333");
        json.addProperty("specjalizacja", "grupowy");
        json.addProperty("poziom", "★★★☆☆ Średniozaawansowany");
        json.addProperty("haslo", "haslo123");
        json.addProperty("aktywny", aktywny);
        return GSON.fromJson(json, Trener.class);
    }

    @Test
    void mieszanaListaAktywnychINieaktywnych() {
        repository.save(trener("Jan", "Kowalski", true));
        repository.save(trener("Anna", "Nowak", false));
        repository.save(trener("Piotr", "Wisniewski", true));
        repository.save(trener("Kasia", "Lis", false));

        List<Trener> aktywni = repository.findAktywni();

        assertEquals(2, aktywni.size());
        assertTrue(aktywni.stream().allMatch(Trener::isAktywny));
        assertTrue(aktywni.stream().anyMatch(t -> t.getImie().equals("Jan")));
        assertTrue(aktywni.stream().anyMatch(t -> t.getImie().equals("Piotr")));
    }

    @Test
    void brakAktywnychTrenerowZwracaPustaListe() {
        repository.save(trener("Anna", "Nowak", false));
        repository.save(trener("Kasia", "Lis", false));

        List<Trener> aktywni = repository.findAktywni();

        assertTrue(aktywni.isEmpty());
    }
}