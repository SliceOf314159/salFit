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

public final class DraftStore {

    private static final Gson GSON = Repository.createGson();
    private static final String DIR = "resources/data/drafts";

    private DraftStore() {}

    public static void saveDraft(String key, JsonObject data) {
        new File(DIR).mkdirs();
        try (Writer w = new FileWriter(DIR + "/" + key + ".json")) {
            GSON.toJson(data, w);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Optional<JsonObject> loadDraft(String key) {
        File f = new File(DIR + "/" + key + ".json");
        if (!f.exists()) return Optional.empty();
        try (Reader r = new FileReader(f)) {
            return Optional.ofNullable(GSON.fromJson(r, JsonObject.class));
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    public static void clearDraft(String key) {
        new File(DIR + "/" + key + ".json").delete();
    }
}
