package com.salfit.util;

import com.google.gson.JsonObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DraftStoreTest {

    private final List<String> usedKeys = new ArrayList<>();

    private String newKey() {
        String key = "test_" + UUID.randomUUID();
        usedKeys.add(key);
        return key;
    }

    @AfterEach
    void cleanup() {
        usedKeys.forEach(DraftStore::clearDraft);
    }

    @Test
    void zapisIOdczytWersjiRoboczej() {
        String key = newKey();
        JsonObject json = new JsonObject();
        json.addProperty("imie", "Jan");

        DraftStore.saveDraft(key, json);
        Optional<JsonObject> loaded = DraftStore.loadDraft(key);

        assertTrue(loaded.isPresent());
        assertEquals(json, loaded.get());
    }

    @Test
    void odczytNieistniejacejWersjiRoboczej() {
        Optional<JsonObject> loaded = DraftStore.loadDraft("nieistniejacy_klucz_" + UUID.randomUUID());
        assertTrue(loaded.isEmpty());
    }

    @Test
    void usuniecieWersjiRoboczej() {
        String key = newKey();
        JsonObject json = new JsonObject();
        json.addProperty("imie", "Jan");

        DraftStore.saveDraft(key, json);
        DraftStore.clearDraft(key);

        assertTrue(DraftStore.loadDraft(key).isEmpty());
    }

    @Test
    void nadpisanieIstniejacejWersjiRoboczej() {
        String key = newKey();
        JsonObject json1 = new JsonObject();
        json1.addProperty("imie", "Jan");
        JsonObject json2 = new JsonObject();
        json2.addProperty("imie", "Anna");

        DraftStore.saveDraft(key, json1);
        DraftStore.saveDraft(key, json2);

        Optional<JsonObject> loaded = DraftStore.loadDraft(key);
        assertTrue(loaded.isPresent());
        assertEquals(json2, loaded.get());
    }

    @Test
    void niezaleznoscKluczy() {
        String keyA = newKey();
        String keyB = newKey();
        JsonObject jsonA = new JsonObject();
        jsonA.addProperty("imie", "Jan");
        JsonObject jsonB = new JsonObject();
        jsonB.addProperty("imie", "Anna");

        DraftStore.saveDraft(keyA, jsonA);
        DraftStore.saveDraft(keyB, jsonB);

        assertEquals(jsonA, DraftStore.loadDraft(keyA).orElseThrow());
        assertEquals(jsonB, DraftStore.loadDraft(keyB).orElseThrow());
    }
}
