package com.salfit.repository;

import com.google.gson.Gson;
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

    private SalaRepository repo;

    @BeforeEach
    void setUp(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("sale.json");
        Files.writeString(file, "[]");
        repo = new SalaRepository(file.toString());
    }

    private Sala makeSala(String nazwa, int pojemnosc, int przerwa, StatusSali status) {
        String json = String.format(
            "{\"nazwa\":\"%s\",\"pojemnosc\":%d,\"minPrzerwaMinut\":%d,\"status\":\"%s\"}",
            nazwa, pojemnosc, przerwa, status.name()
        );
        return GSON.fromJson(json, Sala.class);
    }

    @Test
    void findAll_emptyOnStart_returnsEmptyList() {
        assertTrue(repo.findAll().isEmpty());
    }

    @Test
    void save_assignsId() {
        Sala s = makeSala("Sala A", 20, 10, StatusSali.DOSTEPNA);
        assertNull(s.getId());
        repo.save(s);
        assertNotNull(s.getId());
        assertFalse(s.getId().isBlank());
    }

    @Test
    void save_addsSalaToList() {
        Sala s = makeSala("Sala B", 30, 15, StatusSali.DOSTEPNA);
        repo.save(s);
        assertEquals(1, repo.findAll().size());
        assertTrue(repo.findAll().contains(s));
    }

    @Test
    void findById_existingSala_returnsIt() {
        Sala s = makeSala("Sala C", 25, 10, StatusSali.DOSTEPNA);
        repo.save(s);
        Optional<Sala> found = repo.findById(s.getId());
        assertTrue(found.isPresent());
        assertEquals(s, found.get());
    }

    @Test
    void findById_nonExistent_returnsEmpty() {
        Optional<Sala> found = repo.findById("does-not-exist");
        assertFalse(found.isPresent());
    }

    @Test
    void update_replacesExistingSala() {
        Sala s = makeSala("Sala D", 10, 5, StatusSali.DOSTEPNA);
        repo.save(s);
        s.setStatus(StatusSali.W_REMONCIE);
        repo.update(s);
        Optional<Sala> found = repo.findById(s.getId());
        assertTrue(found.isPresent());
        assertEquals(StatusSali.W_REMONCIE, found.get().getStatus());
    }

    @Test
    void update_nonExistentId_doesNothing() {
        Sala s = makeSala("Sala E", 10, 5, StatusSali.DOSTEPNA);
        repo.save(s);
        Sala ghost = GSON.fromJson(
            "{\"id\":\"ghost\",\"nazwa\":\"Ghost\",\"pojemnosc\":1,\"minPrzerwaMinut\":0,\"status\":\"DOSTEPNA\"}",
            Sala.class
        );
        repo.update(ghost);
        assertEquals(1, repo.findAll().size());
    }

    @Test
    void delete_removesExistingSala() {
        Sala s = makeSala("Sala F", 15, 10, StatusSali.DOSTEPNA);
        repo.save(s);
        repo.delete(s.getId());
        assertTrue(repo.findAll().isEmpty());
        assertFalse(repo.findById(s.getId()).isPresent());
    }

    @Test
    void delete_nonExistentId_doesNothing() {
        Sala s = makeSala("Sala G", 20, 10, StatusSali.DOSTEPNA);
        repo.save(s);
        repo.delete("non-existent");
        assertEquals(1, repo.findAll().size());
    }

    @Test
    void findByStatus_filtersByStatus() {
        Sala dostepna = makeSala("Sala H", 20, 10, StatusSali.DOSTEPNA);
        Sala wRemoncie = makeSala("Sala I", 15, 5, StatusSali.W_REMONCIE);
        Sala zajeta = makeSala("Sala J", 10, 10, StatusSali.ZAJETA);
        repo.save(dostepna);
        repo.save(wRemoncie);
        repo.save(zajeta);

        List<Sala> dostepne = repo.findByStatus(StatusSali.DOSTEPNA);
        assertEquals(1, dostepne.size());
        assertEquals(dostepna, dostepne.get(0));
    }

    @Test
    void findByStatus_noMatch_returnsEmpty() {
        Sala s = makeSala("Sala K", 20, 10, StatusSali.DOSTEPNA);
        repo.save(s);
        List<Sala> result = repo.findByStatus(StatusSali.ZAJETA);
        assertTrue(result.isEmpty());
    }

    @Test
    void findByStatus_multipleMatches_returnsAll() {
        repo.save(makeSala("S1", 10, 5, StatusSali.DOSTEPNA));
        repo.save(makeSala("S2", 20, 5, StatusSali.DOSTEPNA));
        repo.save(makeSala("S3", 15, 5, StatusSali.W_REMONCIE));

        List<Sala> result = repo.findByStatus(StatusSali.DOSTEPNA);
        assertEquals(2, result.size());
    }

    @Test
    void persistence_savedDataReloadsCorrectly(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("sale_persist.json");
        Files.writeString(file, "[]");

        SalaRepository repo1 = new SalaRepository(file.toString());
        Sala s = makeSala("Sala Persist", 30, 10, StatusSali.DOSTEPNA);
        repo1.save(s);

        SalaRepository repo2 = new SalaRepository(file.toString());
        assertEquals(1, repo2.findAll().size());
        assertEquals("Sala Persist", repo2.findAll().get(0).getNazwa());
    }

    @Test
    void getInstance_returnsSingleton() {
        SalaRepository.resetInstance();
        SalaRepository a = SalaRepository.getInstance();
        SalaRepository b = SalaRepository.getInstance();
        assertSame(a, b);
        SalaRepository.resetInstance();
    }
}
