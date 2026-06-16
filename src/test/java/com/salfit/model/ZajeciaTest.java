package com.salfit.model;

import com.google.gson.Gson;
import com.salfit.repository.Repository;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ZajeciaTest {

    private static final Gson GSON = Repository.createGson();

    private Zajecia build(String id, String nazwa, String trenerId, String salaId,
                          String termin, int czas, int limit) {
        String json = String.format(
            "{\"id\":\"%s\",\"nazwa\":\"%s\",\"trenerId\":\"%s\",\"salaId\":\"%s\"," +
            "\"termin\":\"%s\",\"czasTrwaniaMinut\":%d,\"limitUczestnikow\":%d,\"uczestnicyIds\":[]}",
            id, nazwa, trenerId, salaId, termin, czas, limit
        );
        return GSON.fromJson(json, Zajecia.class);
    }

    @Test
    void getters_returnCorrectValues() {
        Zajecia z = build("z1", "Yoga", "t1", "s1", "2026-06-16T09:00:00", 60, 15);
        assertEquals("z1", z.getId());
        assertEquals("Yoga", z.getNazwa());
        assertEquals("t1", z.getTrenerId());
        assertEquals("s1", z.getSalaId());
        assertEquals(LocalDateTime.of(2026, 6, 16, 9, 0, 0), z.getTermin());
        assertEquals(60, z.getCzasTrwaniaMinut());
        assertEquals(15, z.getLimitUczestnikow());
        assertNotNull(z.getUczestnicyIds());
    }

    @Test
    void dodajUczestnika_addsNewMember() {
        Zajecia z = build("z2", "Pilates", "t1", "s1", "2026-06-17T10:00:00", 45, 10);
        z.dodajUczestnika("c1");
        assertTrue(z.getUczestnicyIds().contains("c1"));
        assertEquals(1, z.getUczestnicyIds().size());
    }

    @Test
    void dodajUczestnika_duplicateIgnored() {
        Zajecia z = build("z3", "CrossFit", "t2", "s2", "2026-06-18T11:00:00", 60, 20);
        z.dodajUczestnika("c2");
        z.dodajUczestnika("c2");
        assertEquals(1, z.getUczestnicyIds().size());
    }

    @Test
    void dodajUczestnika_multipleDistinctMembers() {
        Zajecia z = build("z4", "HIIT", "t3", "s3", "2026-06-19T08:00:00", 30, 25);
        z.dodajUczestnika("c1");
        z.dodajUczestnika("c2");
        z.dodajUczestnika("c3");
        assertEquals(3, z.getUczestnicyIds().size());
    }

    @Test
    void usunUczestnika_removesExistingMember() {
        Zajecia z = build("z5", "Stretching", "t1", "s1", "2026-06-20T09:00:00", 45, 12);
        z.dodajUczestnika("c1");
        z.dodajUczestnika("c2");
        z.usunUczestnika("c1");
        assertFalse(z.getUczestnicyIds().contains("c1"));
        assertTrue(z.getUczestnicyIds().contains("c2"));
        assertEquals(1, z.getUczestnicyIds().size());
    }

    @Test
    void usunUczestnika_nonExistentMember_noError() {
        Zajecia z = build("z6", "Spinning", "t2", "s2", "2026-06-21T10:00:00", 60, 20);
        z.dodajUczestnika("c1");
        z.usunUczestnika("c99");
        assertEquals(1, z.getUczestnicyIds().size());
    }

    @Test
    void usunUczestnika_emptyList_noError() {
        Zajecia z = build("z7", "Yoga", "t1", "s1", "2026-06-22T09:00:00", 60, 15);
        z.usunUczestnika("c1");
        assertTrue(z.getUczestnicyIds().isEmpty());
    }

    @Test
    void uczestnicyIds_initiallyEmpty() {
        Zajecia z = build("z8", "Pilates", "t1", "s1", "2026-06-23T09:00:00", 45, 10);
        assertTrue(z.getUczestnicyIds().isEmpty());
    }
}
