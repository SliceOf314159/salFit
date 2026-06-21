package com.salfit.model;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.salfit.repository.Repository;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class KarnetTest {

    private static final Gson GSON = Repository.createGson();

    private Karnet karnet(LocalDate dataOd, LocalDate dataDo) {
        JsonObject json = new JsonObject();
        json.addProperty("czlonekId", "1");
        json.addProperty("rodzaj", RodzajKarnetu.MIESIECZNY.name());
        if (dataOd != null) json.addProperty("dataOd", dataOd.toString());
        if (dataDo != null) json.addProperty("dataDo", dataDo.toString());
        return GSON.fromJson(json, Karnet.class);
    }

    @Test
    void statusAktywnyGdyDataDoDalekoWPrzyszlosci() {
        Karnet k = karnet(LocalDate.now().minusDays(10), LocalDate.now().plusDays(30));
        assertEquals(StatusKarnetu.AKTYWNY, k.getStatus());
    }

    @Test
    void statusWygasaWkrotceGdyMniejNiz14Dni() {
        Karnet k = karnet(LocalDate.now().minusDays(10), LocalDate.now().plusDays(5));
        assertEquals(StatusKarnetu.WYGASA_WKROTCE, k.getStatus());
    }

    @Test
    void statusWygaslGdyDataDoWPrzeszlosci() {
        Karnet k = karnet(LocalDate.now().minusDays(30), LocalDate.now().minusDays(1));
        assertEquals(StatusKarnetu.WYGASL, k.getStatus());
    }

    @Test
    void statusWygaslGdyDataDoBrak() {
        Karnet k = karnet(LocalDate.now(), null);
        assertEquals(StatusKarnetu.WYGASL, k.getStatus());
    }

    @Test
    void wygasaWCiaguZwracaFalseGdyDataDoBrak() {
        Karnet k = karnet(LocalDate.now(), null);
        assertFalse(k.wygasaWCiagu(14));
    }

    @Test
    void wygasaWCiaguZwracaFalseGdyJuzWygasl() {
        Karnet k = karnet(LocalDate.now().minusDays(30), LocalDate.now().minusDays(1));
        assertFalse(k.wygasaWCiagu(14));
    }

    @Test
    void wygasaWCiaguZwracaTrueNaGranicyOkna() {
        Karnet k = karnet(LocalDate.now(), LocalDate.now().plusDays(14));
        assertTrue(k.wygasaWCiagu(14));
    }
}
