package com.salfit.repository;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.salfit.model.Sala;
import com.salfit.model.StatusSali;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


class SalaRepositoryIdSaveTest{

    private static final Gson GSON = Repository.createGson();

    @TempDir
    Path tempDir;

    private SalaRepository repository;

    @BeforeEach
    void setUp() throws IOException {
        Path file = tempDir.resolve("sale.json");
        Files.writeString(file, "[]");
        repository = new SalaRepository(file.toString());
    }

    private Sala sala(String nazwa, int pojemnosc, StatusSali status) {
        JsonObject json = new JsonObject();
        json.addProperty("nazwa", nazwa);
        json.addProperty("pojemnosc", pojemnosc);
        json.addProperty("minPrzerwaMinut", 10);
        json.addProperty("status", status.name());
        return GSON.fromJson(json, Sala.class);
    }

    @Test
    void nowaSalaDostajeKolejneSekwencyjneId() {
        repository.save(sala("Sala A", 10, StatusSali.DOSTEPNA));
        repository.save(sala("Sala B", 15, StatusSali.DOSTEPNA));

        Sala trzecia = sala("Sala C", 20, StatusSali.DOSTEPNA);
        repository.save(trzecia);

        assertEquals("2", trzecia.getId());
        assertEquals(3, repository.findAll().size());
    }

    @Test
    void zapisNieNadpisujeIdPrzyEdycji() {
        Sala s = sala("Sala A", 10, StatusSali.DOSTEPNA);
        repository.save(s);
        String originalId = s.getId();

        JsonObject edytObj = new JsonObject();
        edytObj.addProperty("id", originalId);
        edytObj.addProperty("nazwa", "Sala A - Zmieniona");
        edytObj.addProperty("pojemnosc", 30);
        edytObj.addProperty("minPrzerwaMinut", 15);
        edytObj.addProperty("status", StatusSali.ZAJETA.name());
        Sala edytowana = GSON.fromJson(edytObj, Sala.class);

        repository.update(edytowana);

        Optional<Sala> found = repository.findById(originalId);
        assertTrue(found.isPresent());
        assertEquals(originalId, found.get().getId());
        assertEquals("Sala A - Zmieniona", found.get().getNazwa());
    }
}