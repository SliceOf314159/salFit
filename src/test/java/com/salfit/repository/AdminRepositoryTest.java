package com.salfit.repository;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.salfit.model.Admin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AdminRepositoryTest {

    private static final Gson GSON = Repository.createGson();

    @TempDir
    Path tempDir;

    private AdminRepository repository;

    @BeforeEach
    void setUp() throws IOException {
        Path file = tempDir.resolve("admin.json");
        Files.writeString(file, "[]");
        repository = new AdminRepository(file.toString());
    }

    private Admin admin(String login, String haslo) {
        JsonObject json = new JsonObject();
        json.addProperty("login", login);
        json.addProperty("haslo", haslo);
        return GSON.fromJson(json, Admin.class);
    }

    @Test
    void zapisNadajeAdminowiId() {
        Admin a = admin("admin1", "tajne");
        repository.save(a);

        assertNotNull(a.getId());
        assertEquals(1, repository.findAll().size());
    }

    @Test
    void findByIdZnajdujeZapisanegoAdmina() {
        Admin a = admin("admin1", "tajne");
        repository.save(a);

        Optional<Admin> found = repository.findById(a.getId());

        assertTrue(found.isPresent());
        assertEquals("admin1", found.get().getLogin());
    }

    @Test
    void findByLoginZnajdujeAdminaPoLoginie() {
        repository.save(admin("admin1", "tajne"));
        repository.save(admin("admin2", "inne"));

        Optional<Admin> found = repository.findByLogin("admin2");

        assertTrue(found.isPresent());
        assertEquals("inne", found.get().getHaslo());
    }

    @Test
    void findByLoginZwracaPustyOptionalGdyNieIstnieje() {
        Optional<Admin> found = repository.findByLogin("brak");
        assertFalse(found.isPresent());
    }

    @Test
    void updateZmieniaHaslo() {
        Admin a = admin("admin1", "stare");
        repository.save(a);

        Admin edytowany = admin("admin1", "nowe");
        JsonObject json = new JsonObject();
        json.addProperty("id", a.getId());
        json.addProperty("login", "admin1");
        json.addProperty("haslo", "nowe");
        edytowany = GSON.fromJson(json, Admin.class);

        repository.update(edytowany);

        Optional<Admin> found = repository.findById(a.getId());
        assertTrue(found.isPresent());
        assertEquals("nowe", found.get().getHaslo());
    }

    @Test
    void deleteUsuwaAdmina() {
        Admin a = admin("admin1", "tajne");
        repository.save(a);

        repository.delete(a.getId());

        assertTrue(repository.findAll().isEmpty());
        assertFalse(repository.findById(a.getId()).isPresent());
    }
}
