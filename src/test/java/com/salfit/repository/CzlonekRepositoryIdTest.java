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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CzlonekRepositoryIdTest {

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
    void nowyCzlonekDostajeKolejneId() {
        repository.save(czlonek("Jan", "Kowalski"));
        Czlonek drugi = czlonek("Anna", "Nowak");
        repository.save(drugi);

        assertEquals("1", drugi.getId());
        assertEquals(2, repository.findAll().size());
    }

    @Test
    void nowyKarnetDostajeKolejneId() {
        Czlonek c = czlonek("Jan", "Kowalski");
        repository.save(c);

        repository.saveKarnet(karnet(c.getId(), RodzajKarnetu.MIESIECZNY,
                LocalDate.now(), LocalDate.now().plusDays(30)));
        Karnet drugiKarnet = karnet(c.getId(), RodzajKarnetu.KWARTALNY,
                LocalDate.now(), LocalDate.now().plusDays(90));
        repository.saveKarnet(drugiKarnet);

        assertEquals("1", drugiKarnet.getId());
        assertEquals(2, repository.findAllKarnety().size());
    }

    @Test
    void licznikiCzlonkowIKarnetowSaNiezalezne() {
        Czlonek c1 = czlonek("Jan", "Kowalski");
        Czlonek c2 = czlonek("Anna", "Nowak");
        Czlonek c3 = czlonek("Piotr", "Wisniewski");
        repository.save(c1);
        repository.save(c2);
        repository.save(c3);

        Karnet k1 = karnet(c1.getId(), RodzajKarnetu.MIESIECZNY,
                LocalDate.now(), LocalDate.now().plusDays(30));
        repository.saveKarnet(k1);

        assertEquals("0", k1.getId());
        assertEquals("2", c3.getId());
        assertTrue(repository.findById(c1.getId()).isPresent());
        assertEquals(1, repository.findAllKarnety().size());
    }
}