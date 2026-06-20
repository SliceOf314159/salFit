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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GrafikRepositoryIdSaveTest {

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
    void noweZajeciaDostajaKolejneId() {
        repository.save(zajecia("Yoga", "t1", "s1", "2026-06-22T08:00:00"));
        repository.save(zajecia("Pilates", "t1", "s1", "2026-06-22T10:00:00"));

        Zajecia trzecie = zajecia("CrossFit", "t2", "s2", "2026-06-23T08:00:00");
        repository.save(trzecie);

        assertEquals("2", trzecie.getId());
        assertEquals(3, repository.findAll().size());
    }

    @Test
    void zapisNieNadpisujeIdPrzyEdycji() {
        Zajecia z = zajecia("Yoga", "t1", "s1", "2026-06-22T08:00:00");
        repository.save(z);
        String originalId = z.getId();

        JsonObject edytObj = new JsonObject();
        edytObj.addProperty("id", originalId);
        edytObj.addProperty("nazwa", "Yoga Zaawansowana");
        edytObj.addProperty("trenerId", "t1");
        edytObj.addProperty("salaId", "s1");
        edytObj.addProperty("termin", "2026-06-22T09:00:00");
        edytObj.addProperty("czasTrwaniaMinut", 90);
        edytObj.addProperty("limitUczestnikow", 15);
        Zajecia edytowane = GSON.fromJson(edytObj, Zajecia.class);

        repository.update(edytowane);

        Optional<Zajecia> found = repository.findById(originalId);
        assertTrue(found.isPresent());
        assertEquals(originalId, found.get().getId());
        assertEquals("Yoga Zaawansowana", found.get().getNazwa());
    }
}