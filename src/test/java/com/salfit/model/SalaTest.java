package com.salfit.model;

import com.google.gson.Gson;
import com.salfit.repository.Repository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SalaTest {

    private static final Gson GSON = Repository.createGson();

    private Sala build(String id, String nazwa, int pojemnosc, int minPrzerwa, StatusSali status) {
        String json = String.format(
            "{\"id\":\"%s\",\"nazwa\":\"%s\",\"pojemnosc\":%d,\"minPrzerwaMinut\":%d,\"status\":\"%s\"}",
            id, nazwa, pojemnosc, minPrzerwa, status.name()
        );
        return GSON.fromJson(json, Sala.class);
    }

    @Test
    void getters_returnCorrectValues() {
        Sala s = build("s1", "Sala Fitness", 30, 15, StatusSali.DOSTEPNA);
        assertEquals("s1", s.getId());
        assertEquals("Sala Fitness", s.getNazwa());
        assertEquals(30, s.getPojemnosc());
        assertEquals(15, s.getMinPrzerwaMinut());
        assertEquals(StatusSali.DOSTEPNA, s.getStatus());
    }

    @Test
    void setStatus_changesStatus() {
        Sala s = build("s2", "Sala B", 20, 10, StatusSali.DOSTEPNA);
        s.setStatus(StatusSali.W_REMONCIE);
        assertEquals(StatusSali.W_REMONCIE, s.getStatus());
    }

    @Test
    void setStatus_toZajeta() {
        Sala s = build("s3", "Sala C", 15, 5, StatusSali.DOSTEPNA);
        s.setStatus(StatusSali.ZAJETA);
        assertEquals(StatusSali.ZAJETA, s.getStatus());
    }

    @Test
    void setStatus_backToDostepna() {
        Sala s = build("s4", "Sala D", 25, 20, StatusSali.W_REMONCIE);
        s.setStatus(StatusSali.DOSTEPNA);
        assertEquals(StatusSali.DOSTEPNA, s.getStatus());
    }
}
