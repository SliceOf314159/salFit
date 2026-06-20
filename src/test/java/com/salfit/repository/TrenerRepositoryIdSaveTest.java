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
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TrenerRepositoryIdSaveTest {

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
    void nowyTrenerDostajeKolejneId() {
        repository.save(trener("Jan", "Kowalski", true));
        repository.save(trener("Anna", "Nowak", true));

        Trener trzeci = trener("Piotr", "Wiśniewski", true);
        repository.save(trzeci);

        assertEquals("2", trzeci.getId());
        assertEquals(3, repository.findAll().size());

        assertTrue(repository.findById("0").isPresent());
        assertTrue(repository.findById("1").isPresent());
    }

    @Test
    void zapisNieNadpisujeIdPrzyEdycji() {
        Trener t = trener("Jan", "Kowalski", true);
        repository.save(t);
        String originalId = t.getId();

        JsonObject edytObj = new JsonObject();
        edytObj.addProperty("id", originalId);
        edytObj.addProperty("imie", "Janusz");
        edytObj.addProperty("nazwisko", "Kowalski");
        edytObj.addProperty("email", "janusz@example.com");
        edytObj.addProperty("telefon", "999888777");
        edytObj.addProperty("specjalizacja", "personalny");
        edytObj.addProperty("poziom", "★★★★★ Ekspert");
        edytObj.addProperty("haslo", "haslo123");
        edytObj.addProperty("aktywny", true);
        Trener edytowany = GSON.fromJson(edytObj, Trener.class);

        repository.update(edytowany);

        Optional<Trener> found = repository.findById(originalId);
        assertTrue(found.isPresent());
        assertEquals(originalId, found.get().getId());
        assertEquals("Janusz", found.get().getImie());
    }
}