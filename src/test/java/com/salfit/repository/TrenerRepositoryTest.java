package com.salfit.repository;

import com.google.gson.Gson;
import com.salfit.model.Trener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TrenerRepositoryTest {

    private static final Gson GSON = Repository.createGson();

    private TrenerRepository repo;

    @BeforeEach
    void setUp(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("trenerzy.json");
        Files.writeString(file, "[]");
        repo = new TrenerRepository(file.toString());
    }

    private Trener makeTrener(String imie, String nazwisko, String email, boolean aktywny) {
        String json = String.format(
            "{\"imie\":\"%s\",\"nazwisko\":\"%s\",\"email\":\"%s\",\"telefon\":\"000\",\"specjalizacja\":\"Yoga\",\"aktywny\":%b}",
            imie, nazwisko, email, aktywny
        );
        return GSON.fromJson(json, Trener.class);
    }

    @Test
    void findAll_emptyOnStart_returnsEmptyList() {
        assertTrue(repo.findAll().isEmpty());
    }

    @Test
    void save_assignsId() {
        Trener t = makeTrener("Jan", "Kowalski", "j@gym.pl", true);
        assertNull(t.getId());
        repo.save(t);
        assertNotNull(t.getId());
        assertFalse(t.getId().isBlank());
    }

    @Test
    void save_addsTrenerToList() {
        Trener t = makeTrener("Anna", "Nowak", "a@gym.pl", true);
        repo.save(t);
        assertEquals(1, repo.findAll().size());
        assertTrue(repo.findAll().contains(t));
    }

    @Test
    void findById_existingTrener_returnsIt() {
        Trener t = makeTrener("Piotr", "Wiśniewski", "p@gym.pl", true);
        repo.save(t);
        Optional<Trener> found = repo.findById(t.getId());
        assertTrue(found.isPresent());
        assertEquals(t, found.get());
    }

    @Test
    void findById_nonExistent_returnsEmpty() {
        assertFalse(repo.findById("ghost-id").isPresent());
    }

    @Test
    void update_replacesExistingTrener() {
        Trener t = makeTrener("Maria", "Kowalczyk", "m@gym.pl", true);
        repo.save(t);
        t.setAktywny(false);
        repo.update(t);
        Optional<Trener> found = repo.findById(t.getId());
        assertTrue(found.isPresent());
        assertFalse(found.get().isAktywny());
    }

    @Test
    void update_nonExistentId_doesNothing() {
        Trener t = makeTrener("X", "Y", "x@gym.pl", true);
        repo.save(t);
        Trener ghost = GSON.fromJson(
            "{\"id\":\"ghost\",\"imie\":\"G\",\"nazwisko\":\"H\",\"email\":\"g@gym.pl\",\"telefon\":\"0\",\"specjalizacja\":\"X\",\"aktywny\":true}",
            Trener.class
        );
        repo.update(ghost);
        assertEquals(1, repo.findAll().size());
    }

    @Test
    void delete_removesExistingTrener() {
        Trener t = makeTrener("Karolina", "Brzezicka", "k@gym.pl", true);
        repo.save(t);
        repo.delete(t.getId());
        assertTrue(repo.findAll().isEmpty());
    }

    @Test
    void delete_nonExistentId_doesNothing() {
        Trener t = makeTrener("Adam", "Nowakowski", "a@gym.pl", false);
        repo.save(t);
        repo.delete("not-here");
        assertEquals(1, repo.findAll().size());
    }

    @Test
    void findAktywni_returnsOnlyActiveTrenerzy() {
        Trener aktywny = makeTrener("Zofia", "Wiśniewska", "z@gym.pl", true);
        Trener nieaktywny = makeTrener("Robert", "Grabowski", "r@gym.pl", false);
        repo.save(aktywny);
        repo.save(nieaktywny);

        List<Trener> result = repo.findAktywni();
        assertEquals(1, result.size());
        assertTrue(result.get(0).isAktywny());
    }

    @Test
    void findAktywni_allInactive_returnsEmpty() {
        repo.save(makeTrener("T1", "N1", "t1@gym.pl", false));
        repo.save(makeTrener("T2", "N2", "t2@gym.pl", false));
        assertTrue(repo.findAktywni().isEmpty());
    }

    @Test
    void findAktywni_allActive_returnsAll() {
        repo.save(makeTrener("T1", "N1", "t1@gym.pl", true));
        repo.save(makeTrener("T2", "N2", "t2@gym.pl", true));
        assertEquals(2, repo.findAktywni().size());
    }

    @Test
    void persistence_savedDataReloadsCorrectly(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("trenerzy_persist.json");
        Files.writeString(file, "[]");

        TrenerRepository repo1 = new TrenerRepository(file.toString());
        Trener t = makeTrener("Saved", "Trainer", "saved@gym.pl", true);
        repo1.save(t);

        TrenerRepository repo2 = new TrenerRepository(file.toString());
        assertEquals(1, repo2.findAll().size());
        assertEquals("Saved", repo2.findAll().get(0).getImie());
    }

    @Test
    void getInstance_returnsSingleton() {
        TrenerRepository.resetInstance();
        TrenerRepository a = TrenerRepository.getInstance();
        TrenerRepository b = TrenerRepository.getInstance();
        assertSame(a, b);
        TrenerRepository.resetInstance();
    }
}
