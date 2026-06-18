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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class SalaRepositoryTest {

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

    private Sala sala(String id, String nazwa, int pojemnosc, StatusSali status) {
        JsonObject json = new JsonObject();
        json.addProperty("id", id);
        json.addProperty("nazwa", nazwa);
        json.addProperty("pojemnosc", pojemnosc);
        json.addProperty("minPrzerwaMinut", 10);
        json.addProperty("status", status.name());
        return GSON.fromJson(json, Sala.class);
    }

    @Test
    void zapisIOdczytEncji() {
        Sala sala = sala(null, "Sala Yoga", 20, StatusSali.DOSTEPNA);
        repository.save(sala);

        Optional<Sala> found = repository.findById(sala.getId());
        assertTrue(found.isPresent());
        assertEquals("Sala Yoga", found.get().getNazwa());
        assertEquals(20, found.get().getPojemnosc());
        assertEquals(StatusSali.DOSTEPNA, found.get().getStatus());
    }

    @Test
    void aktualizacjaPersystujeZmiany() {
        Sala sala = sala(null, "Sala Yoga", 20, StatusSali.DOSTEPNA);
        repository.save(sala);

        sala.setStatus(StatusSali.W_REMONCIE);
        repository.update(sala);

        Optional<Sala> found = repository.findById(sala.getId());
        assertTrue(found.isPresent());
        assertEquals(StatusSali.W_REMONCIE, found.get().getStatus());
    }

    @Test
    void usuniecieUsuwaEncje() {
        Sala sala = sala(null, "Sala Yoga", 20, StatusSali.DOSTEPNA);
        repository.save(sala);

        repository.delete(sala.getId());

        assertTrue(repository.findById(sala.getId()).isEmpty());
    }

    @Test
    void findAllPoWieluOperacjach() {
        Sala s1 = sala(null, "Sala A", 10, StatusSali.DOSTEPNA);
        repository.save(s1);
        Sala s2 = sala(null, "Sala B", 15, StatusSali.DOSTEPNA);
        repository.save(s2);
        Sala s3 = sala(null, "Sala C", 25, StatusSali.DOSTEPNA);
        repository.save(s3);

        repository.delete(s2.getId());

        s3.setStatus(StatusSali.W_REMONCIE);
        repository.update(s3);

        List<Sala> all = repository.findAll();
        assertEquals(2, all.size());

        Optional<Sala> foundS1 = all.stream().filter(s -> s.getId().equals(s1.getId())).findFirst();
        Optional<Sala> foundS3 = all.stream().filter(s -> s.getId().equals(s3.getId())).findFirst();

        assertTrue(foundS1.isPresent());
        assertTrue(foundS3.isPresent());
        assertEquals(StatusSali.W_REMONCIE, foundS3.get().getStatus());
    }
}
