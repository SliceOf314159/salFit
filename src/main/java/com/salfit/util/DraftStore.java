package com.salfit.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.salfit.repository.Repository;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Optional;

// Klasa pomocnicza do zapisywania "wersji roboczych" formularzy
// Np jak user zaczyna wypelniac formularz dodania trenera, ale nie skonczy,
// to mozemy mu to zapisac do pliku JSON i wczytac przy nastepnym otwarciu
public final class DraftStore {

    private static final Gson GSON = Repository.createGson();
    // folder gdzie trzymamy pliki z draftami
    private static final String DIR = "resources/data/drafts";

    private DraftStore() {}

    // zapisuje dany obiekt JSON pod nazwa "key.json" w folderze drafts
    public static void saveDraft(String key, JsonObject data) {
        new File(DIR).mkdirs(); // tworzy folder jak go jeszcze nie ma (i ewentualne foldery nadrzedne)
        try (Writer w = new FileWriter(DIR + "/" + key + ".json")) {
            GSON.toJson(data, w);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // wczytuje draft po kluczu - Optional bo moze go nie byc (np user nigdy nie zapisal)
    public static Optional<JsonObject> loadDraft(String key) {
        File f = new File(DIR + "/" + key + ".json");
        if (!f.exists()) return Optional.empty(); // brak pliku = brak draftu
        try (Reader r = new FileReader(f)) {
            return Optional.ofNullable(GSON.fromJson(r, JsonObject.class));
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    // usuwa draft - wywolywane np po tym jak user faktycznie zapisze formularz
    public static void clearDraft(String key) {
        new File(DIR + "/" + key + ".json").delete();
    }
}
