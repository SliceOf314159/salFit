package com.salfit.repository;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.salfit.model.Czlonek;
import com.salfit.model.Karnet;
import com.salfit.model.RodzajKarnetu;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CzlonekRepositoryWygasajaceTest {

    private static final Gson GSON = Repository.createGson();

    @TempDir
    Path tempDir;

    private CzlonekRepository repository;

    @BeforeEach
    void setUp() throws IOException {
        Path czlonkowieFile = tempDir.resolve("czlonkowie.json");
        Path karnetyFile = tempDir.resolve("karnety.json");
        Files.writeString(czlonkowieFile, "[]");
        Files.writeString(karnetyFile, "[]");
        repository = new CzlonekRepository(czlonkowieFile.toString(), karnetyFile.toString());
    }

    private Czlonek czlonek(String imie, String nazwisko) {
        JsonObject json = new JsonObject();
        json.addProperty("imie", imie);
        json.addProperty("nazwisko", nazwisko);
        json.addProperty("email", imie.toLowerCase() + "@example.com");
        json.addProperty("telefon", "123456789");
        json.addProperty("dataUrodzenia", "1990-01-01");
        return GSON.fromJson(json, Czlonek.class);
    }

    private Karnet karnet(String czlonekId, RodzajKarnetu rodzaj, LocalDate dataOd, LocalDate dataDo) {
        JsonObject json = new JsonObject();
        json.addProperty("czlonekId", czlonekId);
        json.addProperty("rodzaj", rodzaj.name());
        json.addProperty("dataOd", dataOd.toString());
        json.addProperty("dataDo", dataDo.toString());
        return GSON.fromJson(json, Karnet.class);
    }

    @Test
    void karnetWygasajacyWZadanymOknie() {
        Czlonek c = czlonek("Jan", "Kowalski");
        repository.save(c);
        Karnet k = karnet(c.getId(), RodzajKarnetu.MIESIECZNY,
                LocalDate.now().minusDays(20), LocalDate.now().plusDays(10));
        repository.saveKarnet(k);

        List<Karnet> result = repository.findKarnetyWygasajace(14);

        assertTrue(result.stream().anyMatch(kar -> kar.getId().equals(k.getId())));
    }

    @Test
    void karnetWygasajacyPozaOknem() {
        Czlonek c = czlonek("Jan", "Kowalski");
        repository.save(c);
        Karnet k = karnet(c.getId(), RodzajKarnetu.MIESIECZNY,
                LocalDate.now().minusDays(60), LocalDate.now().plusDays(30));
        repository.saveKarnet(k);

        List<Karnet> result = repository.findKarnetyWygasajace(14);

        assertTrue(result.stream().noneMatch(kar -> kar.getId().equals(k.getId())));
    }

    @Test
    void karnetJuzWygasly_niePojawiaSieWWynikach() {
        Czlonek c = czlonek("Jan", "Kowalski");
        repository.save(c);
        Karnet k = karnet(c.getId(), RodzajKarnetu.MIESIECZNY,
                LocalDate.now().minusDays(60), LocalDate.now().minusDays(5));
        repository.saveKarnet(k);

        List<Karnet> result = repository.findKarnetyWygasajace(14);

        assertTrue(result.stream().noneMatch(kar -> kar.getId().equals(k.getId())));
    }

    @Test
    void brakKarnetowZwracaPustaListe() {
        List<Karnet> result = repository.findKarnetyWygasajace(14);
        assertTrue(result.isEmpty());
    }
}