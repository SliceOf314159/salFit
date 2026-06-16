package com.salfit.repository;

import com.google.gson.Gson;
import com.salfit.model.Zajecia;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class GrafikRepositoryTest {

    private static final Gson GSON = Repository.createGson();

    private GrafikRepository repo;

    @BeforeEach
    void setUp(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("zajecia.json");
        Files.writeString(file, "[]");
        repo = new GrafikRepository(file.toString());
    }

    private Zajecia makeZajecia(String nazwa, String trenerId, String salaId, String termin) {
        String json = String.format(
            "{\"nazwa\":\"%s\",\"trenerId\":\"%s\",\"salaId\":\"%s\",\"termin\":\"%s\"," +
            "\"czasTrwaniaMinut\":60,\"limitUczestnikow\":15,\"uczestnicyIds\":[]}",
            nazwa, trenerId, salaId, termin
        );
        return GSON.fromJson(json, Zajecia.class);
    }

    @Test
    void findAll_emptyOnStart_returnsEmptyList() {
        assertTrue(repo.findAll().isEmpty());
    }

    @Test
    void save_assignsId() {
        Zajecia z = makeZajecia("Yoga", "t1", "s1", "2026-06-16T09:00:00");
        assertNull(z.getId());
        repo.save(z);
        assertNotNull(z.getId());
        assertFalse(z.getId().isBlank());
    }

    @Test
    void save_addsZajeciaToList() {
        Zajecia z = makeZajecia("Pilates", "t1", "s1", "2026-06-16T10:00:00");
        repo.save(z);
        assertEquals(1, repo.findAll().size());
    }

    @Test
    void findById_existingZajecia_returnsIt() {
        Zajecia z = makeZajecia("CrossFit", "t2", "s2", "2026-06-17T08:00:00");
        repo.save(z);
        Optional<Zajecia> found = repo.findById(z.getId());
        assertTrue(found.isPresent());
        assertEquals(z, found.get());
    }

    @Test
    void findById_nonExistent_returnsEmpty() {
        assertFalse(repo.findById("ghost").isPresent());
    }

    @Test
    void update_replacesExistingZajecia() {
        Zajecia z = makeZajecia("HIIT", "t1", "s1", "2026-06-18T09:00:00");
        repo.save(z);
        z.dodajUczestnika("c1");
        repo.update(z);
        Optional<Zajecia> found = repo.findById(z.getId());
        assertTrue(found.isPresent());
        assertTrue(found.get().getUczestnicyIds().contains("c1"));
    }

    @Test
    void update_nonExistentId_doesNothing() {
        Zajecia z = makeZajecia("X", "t1", "s1", "2026-06-18T09:00:00");
        repo.save(z);
        Zajecia ghost = GSON.fromJson(
            "{\"id\":\"ghost\",\"nazwa\":\"Ghost\",\"trenerId\":\"t9\",\"salaId\":\"s9\"," +
            "\"termin\":\"2026-06-18T09:00:00\",\"czasTrwaniaMinut\":60,\"limitUczestnikow\":15,\"uczestnicyIds\":[]}",
            Zajecia.class
        );
        repo.update(ghost);
        assertEquals(1, repo.findAll().size());
    }

    @Test
    void delete_removesExistingZajecia() {
        Zajecia z = makeZajecia("Stretching", "t1", "s1", "2026-06-19T10:00:00");
        repo.save(z);
        repo.delete(z.getId());
        assertTrue(repo.findAll().isEmpty());
    }

    @Test
    void delete_nonExistentId_doesNothing() {
        Zajecia z = makeZajecia("Spinning", "t1", "s1", "2026-06-20T09:00:00");
        repo.save(z);
        repo.delete("not-here");
        assertEquals(1, repo.findAll().size());
    }

    @Test
    void findByTrener_returnsOnlyMatchingZajecia() {
        repo.save(makeZajecia("Yoga", "t1", "s1", "2026-06-16T09:00:00"));
        repo.save(makeZajecia("Pilates", "t2", "s1", "2026-06-16T11:00:00"));
        repo.save(makeZajecia("CrossFit", "t1", "s2", "2026-06-17T08:00:00"));

        List<Zajecia> result = repo.findByTrener("t1");
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(z -> z.getTrenerId().equals("t1")));
    }

    @Test
    void findByTrener_noMatch_returnsEmpty() {
        repo.save(makeZajecia("Yoga", "t1", "s1", "2026-06-16T09:00:00"));
        assertTrue(repo.findByTrener("t99").isEmpty());
    }

    @Test
    void findBySala_returnsOnlyMatchingZajecia() {
        repo.save(makeZajecia("Yoga", "t1", "s1", "2026-06-16T09:00:00"));
        repo.save(makeZajecia("Pilates", "t1", "s2", "2026-06-16T11:00:00"));
        repo.save(makeZajecia("HIIT", "t2", "s1", "2026-06-17T10:00:00"));

        List<Zajecia> result = repo.findBySala("s1");
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(z -> z.getSalaId().equals("s1")));
    }

    @Test
    void findBySala_noMatch_returnsEmpty() {
        repo.save(makeZajecia("Yoga", "t1", "s1", "2026-06-16T09:00:00"));
        assertTrue(repo.findBySala("s99").isEmpty());
    }

    @Test
    void findByTydzien_returnsZajeciaInWeek() {
        // Week: 2026-06-15 (Mon) to 2026-06-21 (Sun)
        LocalDate poniedzialek = LocalDate.of(2026, 6, 15);

        repo.save(makeZajecia("Mon", "t1", "s1", "2026-06-15T09:00:00"));  // inside
        repo.save(makeZajecia("Thu", "t1", "s1", "2026-06-18T10:00:00"));  // inside
        repo.save(makeZajecia("Sun", "t1", "s1", "2026-06-21T18:00:00"));  // inside (last day)
        repo.save(makeZajecia("NextMon", "t1", "s1", "2026-06-22T09:00:00")); // outside
        repo.save(makeZajecia("PrevFri", "t1", "s1", "2026-06-12T09:00:00")); // outside

        List<Zajecia> result = repo.findByTydzien(poniedzialek);
        assertEquals(3, result.size());
    }

    @Test
    void findByTydzien_noMatchingZajecia_returnsEmpty() {
        repo.save(makeZajecia("Yoga", "t1", "s1", "2026-06-01T09:00:00"));
        LocalDate poniedzialek = LocalDate.of(2026, 6, 22);
        assertTrue(repo.findByTydzien(poniedzialek).isEmpty());
    }

    @Test
    void findByTydzien_firstAndLastDayInclusive() {
        LocalDate poniedzialek = LocalDate.of(2026, 6, 15);
        repo.save(makeZajecia("First", "t1", "s1", "2026-06-15T00:00:00"));
        repo.save(makeZajecia("Last", "t1", "s1", "2026-06-21T23:59:59"));

        List<Zajecia> result = repo.findByTydzien(poniedzialek);
        assertEquals(2, result.size());
    }

    @Test
    void persistence_savedDataReloadsCorrectly(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("zajecia_persist.json");
        Files.writeString(file, "[]");

        GrafikRepository repo1 = new GrafikRepository(file.toString());
        Zajecia z = makeZajecia("Yoga Persist", "t1", "s1", "2026-06-16T09:00:00");
        repo1.save(z);

        GrafikRepository repo2 = new GrafikRepository(file.toString());
        assertEquals(1, repo2.findAll().size());
        assertEquals("Yoga Persist", repo2.findAll().get(0).getNazwa());
    }

    @Test
    void getInstance_returnsSingleton() {
        GrafikRepository.resetInstance();
        GrafikRepository a = GrafikRepository.getInstance();
        GrafikRepository b = GrafikRepository.getInstance();
        assertSame(a, b);
        GrafikRepository.resetInstance();
    }
}
