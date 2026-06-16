package com.salfit.model;

import com.google.gson.Gson;
import com.salfit.repository.Repository;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class KarnetTest {

    private static final Gson GSON = Repository.createGson();

    private Karnet build(String dataOd, String dataDo) {
        String json = String.format(
            "{\"id\":\"k1\",\"czlonekId\":\"c1\",\"rodzaj\":\"MIESIECZNY\",\"dataOd\":\"%s\",\"dataDo\":\"%s\"}",
            dataOd, dataDo
        );
        return GSON.fromJson(json, Karnet.class);
    }

    private Karnet buildNullDate() {
        return GSON.fromJson("{\"id\":\"k1\",\"czlonekId\":\"c1\",\"rodzaj\":\"KWARTALNY\"}", Karnet.class);
    }

    @Test
    void getters_returnCorrectValues() {
        LocalDate od = LocalDate.now().minusDays(10);
        LocalDate doDate = LocalDate.now().plusDays(20);
        Karnet k = build(od.toString(), doDate.toString());

        assertEquals("k1", k.getId());
        assertEquals("c1", k.getCzlonekId());
        assertEquals(RodzajKarnetu.MIESIECZNY, k.getRodzaj());
        assertEquals(od, k.getDataOd());
        assertEquals(doDate, k.getDataDo());
    }

    @Test
    void getStatus_nullDataDo_returnsWygasl() {
        Karnet k = buildNullDate();
        assertEquals(StatusKarnetu.WYGASL, k.getStatus());
    }

    @Test
    void getStatus_expiredYesterday_returnsWygasl() {
        Karnet k = build(LocalDate.now().minusDays(30).toString(), LocalDate.now().minusDays(1).toString());
        assertEquals(StatusKarnetu.WYGASL, k.getStatus());
    }

    @Test
    void getStatus_expiresIn5Days_returnsWygasaWkrotce() {
        Karnet k = build(LocalDate.now().minusDays(25).toString(), LocalDate.now().plusDays(5).toString());
        assertEquals(StatusKarnetu.WYGASA_WKROTCE, k.getStatus());
    }

    @Test
    void getStatus_expiresExactlyIn14Days_returnsWygasaWkrotce() {
        Karnet k = build(LocalDate.now().minusDays(16).toString(), LocalDate.now().plusDays(14).toString());
        assertEquals(StatusKarnetu.WYGASA_WKROTCE, k.getStatus());
    }

    @Test
    void getStatus_expiresIn15Days_returnsAktywny() {
        Karnet k = build(LocalDate.now().toString(), LocalDate.now().plusDays(15).toString());
        assertEquals(StatusKarnetu.AKTYWNY, k.getStatus());
    }

    @Test
    void getStatus_expiresIn30Days_returnsAktywny() {
        Karnet k = build(LocalDate.now().toString(), LocalDate.now().plusDays(30).toString());
        assertEquals(StatusKarnetu.AKTYWNY, k.getStatus());
    }

    @Test
    void wygasaWCiagu_nullDataDo_returnsFalse() {
        Karnet k = buildNullDate();
        assertFalse(k.wygasaWCiagu(14));
    }

    @Test
    void wygasaWCiagu_expiresWithinRange_returnsTrue() {
        Karnet k = build(LocalDate.now().minusDays(10).toString(), LocalDate.now().plusDays(5).toString());
        assertTrue(k.wygasaWCiagu(7));
    }

    @Test
    void wygasaWCiagu_expiresOutsideRange_returnsFalse() {
        Karnet k = build(LocalDate.now().toString(), LocalDate.now().plusDays(30).toString());
        assertFalse(k.wygasaWCiagu(7));
    }

    @Test
    void wygasaWCiagu_expiresOnExactBoundary_returnsTrue() {
        Karnet k = build(LocalDate.now().toString(), LocalDate.now().plusDays(14).toString());
        assertTrue(k.wygasaWCiagu(14));
    }

    @Test
    void wygasaWCiagu_alreadyExpired_returnsFalse() {
        Karnet k = build(LocalDate.now().minusDays(10).toString(), LocalDate.now().minusDays(1).toString());
        assertFalse(k.wygasaWCiagu(14));
    }

    @Test
    void wygasaWCiagu_expiresOnBoundaryOfZero_returnsTrue() {
        Karnet k = build(LocalDate.now().toString(), LocalDate.now().toString());
        assertTrue(k.wygasaWCiagu(0));
    }
}
