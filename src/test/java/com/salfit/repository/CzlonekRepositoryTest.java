package com.salfit.repository;

import com.google.gson.Gson;
import com.salfit.model.Czlonek;
import com.salfit.model.Karnet;
import com.salfit.model.RodzajKarnetu;
import com.salfit.model.StatusKarnetu;
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

class CzlonekRepositoryTest {

    private static final Gson GSON = Repository.createGson();

    private CzlonekRepository repo;
    private Path czlonkowiePath;
    private Path karnetyPath;

    @BeforeEach
    void setUp(@TempDir Path tempDir) throws IOException {
        czlonkowiePath = tempDir.resolve("czlonkowie.json");
        karnetyPath = tempDir.resolve("karnety.json");
        Files.writeString(czlonkowiePath, "[]");
        Files.writeString(karnetyPath, "[]");
        repo = new CzlonekRepository(czlonkowiePath.toString(), karnetyPath.toString());
    }

    private Czlonek makeCzlonek(String imie, String nazwisko, String email) {
        String json = String.format(
            "{\"imie\":\"%s\",\"nazwisko\":\"%s\",\"email\":\"%s\",\"telefon\":\"000\",\"dataUrodzenia\":\"1990-01-01\"}",
            imie, nazwisko, email
        );
        return GSON.fromJson(json, Czlonek.class);
    }

    private Karnet makeKarnet(String czlonekId, String rodzaj, String dataOd, String dataDo) {
        String json = String.format(
            "{\"czlonekId\":\"%s\",\"rodzaj\":\"%s\",\"dataOd\":\"%s\",\"dataDo\":\"%s\"}",
            czlonekId, rodzaj, dataOd, dataDo
        );
        return GSON.fromJson(json, Karnet.class);
    }

    @Test
    void findAll_emptyOnStart_returnsEmptyList() {
        assertTrue(repo.findAll().isEmpty());
    }

    @Test
    void save_assignsId() {
        Czlonek c = makeCzlonek("Jan", "Kowalski", "j@test.pl");
        assertNull(c.getId());
        repo.save(c);
        assertNotNull(c.getId());
        assertFalse(c.getId().isBlank());
    }

    @Test
    void save_addsCzlonekToList() {
        Czlonek c = makeCzlonek("Anna", "Nowak", "a@test.pl");
        repo.save(c);
        assertEquals(1, repo.findAll().size());
        assertTrue(repo.findAll().contains(c));
    }

    @Test
    void findById_existingCzlonek_returnsIt() {
        Czlonek c = makeCzlonek("Piotr", "Wiśniewski", "p@test.pl");
        repo.save(c);
        Optional<Czlonek> found = repo.findById(c.getId());
        assertTrue(found.isPresent());
        assertEquals(c, found.get());
    }

    @Test
    void findById_nonExistent_returnsEmpty() {
        assertFalse(repo.findById("no-such-id").isPresent());
    }

    @Test
    void update_replacesExistingCzlonek() {
        Czlonek c = makeCzlonek("Maria", "Kowalczyk", "m@test.pl");
        repo.save(c);
        // Create updated version with same id
        Czlonek updated = GSON.fromJson(
            String.format("{\"id\":\"%s\",\"imie\":\"Maria\",\"nazwisko\":\"Malinowska\",\"email\":\"m@test.pl\",\"telefon\":\"111\",\"dataUrodzenia\":\"1990-01-01\"}", c.getId()),
            Czlonek.class
        );
        repo.update(updated);
        Optional<Czlonek> found = repo.findById(c.getId());
        assertTrue(found.isPresent());
        assertEquals("Malinowska", found.get().getNazwisko());
    }

    @Test
    void update_nonExistentId_doesNothing() {
        Czlonek c = makeCzlonek("X", "Y", "x@test.pl");
        repo.save(c);
        Czlonek ghost = GSON.fromJson(
            "{\"id\":\"ghost\",\"imie\":\"G\",\"nazwisko\":\"H\",\"email\":\"g@test.pl\",\"telefon\":\"0\",\"dataUrodzenia\":\"2000-01-01\"}",
            Czlonek.class
        );
        repo.update(ghost);
        assertEquals(1, repo.findAll().size());
    }

    @Test
    void delete_removesCzlonekAndHisKarnety() {
        Czlonek c = makeCzlonek("Adam", "Nowak", "ad@test.pl");
        repo.save(c);
        Karnet k = makeKarnet(c.getId(), "MIESIECZNY",
            LocalDate.now().toString(), LocalDate.now().plusDays(30).toString());
        repo.saveKarnet(k);

        repo.delete(c.getId());

        assertTrue(repo.findAll().isEmpty());
        assertFalse(repo.findAktywnyKarnet(c.getId()).isPresent());
    }

    @Test
    void delete_nonExistentId_doesNothing() {
        Czlonek c = makeCzlonek("Zofia", "Grabowska", "z@test.pl");
        repo.save(c);
        repo.delete("nonexistent");
        assertEquals(1, repo.findAll().size());
    }

    @Test
    void saveKarnet_assignsIdAndAddsToCache() {
        Czlonek c = makeCzlonek("Tomasz", "Lewandowski", "t@test.pl");
        repo.save(c);
        Karnet k = makeKarnet(c.getId(), "KWARTALNY",
            LocalDate.now().toString(), LocalDate.now().plusDays(90).toString());
        assertNull(k.getId());
        repo.saveKarnet(k);
        assertNotNull(k.getId());
    }

    @Test
    void findAktywnyKarnet_activeKarnet_returnsIt() {
        Czlonek c = makeCzlonek("Elzbieta", "Wójcik", "e@test.pl");
        repo.save(c);
        Karnet k = makeKarnet(c.getId(), "MIESIECZNY",
            LocalDate.now().toString(), LocalDate.now().plusDays(30).toString());
        repo.saveKarnet(k);

        Optional<Karnet> found = repo.findAktywnyKarnet(c.getId());
        assertTrue(found.isPresent());
        assertEquals(StatusKarnetu.AKTYWNY, found.get().getStatus());
    }

    @Test
    void findAktywnyKarnet_expiringKarnet_returnsIt() {
        Czlonek c = makeCzlonek("Henryk", "Mazur", "h@test.pl");
        repo.save(c);
        Karnet k = makeKarnet(c.getId(), "MIESIECZNY",
            LocalDate.now().toString(), LocalDate.now().plusDays(5).toString());
        repo.saveKarnet(k);

        Optional<Karnet> found = repo.findAktywnyKarnet(c.getId());
        assertTrue(found.isPresent());
        assertEquals(StatusKarnetu.WYGASA_WKROTCE, found.get().getStatus());
    }

    @Test
    void findAktywnyKarnet_expiredKarnet_returnsEmpty() {
        Czlonek c = makeCzlonek("Irena", "Krawczyk", "i@test.pl");
        repo.save(c);
        Karnet k = makeKarnet(c.getId(), "MIESIECZNY",
            LocalDate.now().minusDays(60).toString(), LocalDate.now().minusDays(1).toString());
        repo.saveKarnet(k);

        assertFalse(repo.findAktywnyKarnet(c.getId()).isPresent());
    }

    @Test
    void findAktywnyKarnet_noCzlonek_returnsEmpty() {
        assertFalse(repo.findAktywnyKarnet("nonexistent").isPresent());
    }

    @Test
    void findKarnetyWygasajace_returnsExpiringKarnety() {
        Czlonek c1 = makeCzlonek("C1", "N1", "c1@t.pl");
        Czlonek c2 = makeCzlonek("C2", "N2", "c2@t.pl");
        repo.save(c1);
        repo.save(c2);

        Karnet expiring = makeKarnet(c1.getId(), "MIESIECZNY",
            LocalDate.now().toString(), LocalDate.now().plusDays(5).toString());
        Karnet active = makeKarnet(c2.getId(), "ROCZNY",
            LocalDate.now().toString(), LocalDate.now().plusDays(300).toString());
        repo.saveKarnet(expiring);
        repo.saveKarnet(active);

        List<Karnet> result = repo.findKarnetyWygasajace(14);
        assertEquals(1, result.size());
        assertEquals(expiring, result.get(0));
    }

    @Test
    void findKarnetyWygasajace_noExpiringKarnety_returnsEmpty() {
        Czlonek c = makeCzlonek("C", "N", "c@t.pl");
        repo.save(c);
        Karnet k = makeKarnet(c.getId(), "ROCZNY",
            LocalDate.now().toString(), LocalDate.now().plusDays(300).toString());
        repo.saveKarnet(k);

        assertTrue(repo.findKarnetyWygasajace(7).isEmpty());
    }

    @Test
    void updateKarnet_replacesExistingKarnet() {
        Czlonek c = makeCzlonek("U", "V", "u@test.pl");
        repo.save(c);
        Karnet k = makeKarnet(c.getId(), "MIESIECZNY",
            LocalDate.now().toString(), LocalDate.now().plusDays(30).toString());
        repo.saveKarnet(k);

        LocalDate newEnd = LocalDate.now().plusDays(90);
        Karnet updated = GSON.fromJson(
            String.format("{\"id\":\"%s\",\"czlonekId\":\"%s\",\"rodzaj\":\"KWARTALNY\",\"dataOd\":\"%s\",\"dataDo\":\"%s\"}",
                k.getId(), c.getId(), LocalDate.now(), newEnd),
            Karnet.class
        );
        repo.updateKarnet(updated);

        Optional<Karnet> found = repo.findAktywnyKarnet(c.getId());
        assertTrue(found.isPresent());
        assertEquals(RodzajKarnetu.KWARTALNY, found.get().getRodzaj());
    }

    @Test
    void updateKarnet_nonExistentId_doesNothing() {
        Czlonek c = makeCzlonek("W", "X", "w@test.pl");
        repo.save(c);
        Karnet k = makeKarnet(c.getId(), "MIESIECZNY",
            LocalDate.now().toString(), LocalDate.now().plusDays(30).toString());
        repo.saveKarnet(k);

        Karnet ghost = GSON.fromJson(
            "{\"id\":\"ghost-id\",\"czlonekId\":\"cx\",\"rodzaj\":\"ROCZNY\",\"dataOd\":\"2026-01-01\",\"dataDo\":\"2026-12-31\"}",
            Karnet.class
        );
        repo.updateKarnet(ghost);

        // Still just 1 karnet for the original czlonek
        assertTrue(repo.findAktywnyKarnet(c.getId()).isPresent());
    }

    @Test
    void persistence_savedDataReloadsCorrectly(@TempDir Path tempDir) throws IOException {
        Path czl = tempDir.resolve("czl_persist.json");
        Path karn = tempDir.resolve("karn_persist.json");
        Files.writeString(czl, "[]");
        Files.writeString(karn, "[]");

        CzlonekRepository repo1 = new CzlonekRepository(czl.toString(), karn.toString());
        Czlonek c = makeCzlonek("Persist", "Test", "persist@test.pl");
        repo1.save(c);
        Karnet k = makeKarnet(c.getId(), "MIESIECZNY",
            LocalDate.now().toString(), LocalDate.now().plusDays(30).toString());
        repo1.saveKarnet(k);

        CzlonekRepository repo2 = new CzlonekRepository(czl.toString(), karn.toString());
        assertEquals(1, repo2.findAll().size());
        assertEquals("Persist", repo2.findAll().get(0).getImie());
        assertTrue(repo2.findAktywnyKarnet(c.getId()).isPresent());
    }

    @Test
    void getInstance_returnsSingleton() {
        CzlonekRepository.resetInstance();
        CzlonekRepository a = CzlonekRepository.getInstance();
        CzlonekRepository b = CzlonekRepository.getInstance();
        assertSame(a, b);
        CzlonekRepository.resetInstance();
    }
}
