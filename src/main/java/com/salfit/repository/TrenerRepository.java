package com.salfit.repository;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.salfit.model.Trener;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// Repozytorium trenerow - dziala identycznie jak inne repozytoria (CRUD + JSON na dysku)
public class TrenerRepository implements Repository<Trener> {

    private static TrenerRepository instance;
    private String filePath = "resources/data/trenerzy.json";
    private List<Trener> cache = new ArrayList<>();
    private final Gson gson = Repository.createGson();

    private TrenerRepository() { loadFromFile(); }

    TrenerRepository(String filePath) {
        this.filePath = filePath;
        loadFromFile();
    }

    static void resetInstance() { instance = null; }

    public static TrenerRepository getInstance() {
        if (instance == null) {
            instance = new TrenerRepository();
        }
        return instance;
    }

    @Override
    public List<Trener> findAll() { return cache; }

    @Override
    public Optional<Trener> findById(String id) {
        return cache.stream().filter(t -> t.getId().equals(id)).findFirst();
    }

    @Override
    public void save(Trener trener) {
        // refleksja do ustawienia prywatnego pola "id" (Trener nie ma settera na id)
        try {
            java.lang.reflect.Field idField = trener.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(trener, generateId());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        cache.add(trener);
        saveToFile();
    }

    @Override
    public void update(Trener trener) {
        for (int i = 0; i < cache.size(); i++) {
            if (cache.get(i).getId().equals(trener.getId())) {
                cache.set(i, trener);
                break;
            }
        }
        saveToFile();
    }

    @Override
    public void delete(String id) {
        cache.removeIf(t -> t.getId().equals(id));
        saveToFile();
    }

    // zwraca tylko trenerow aktywnych - przydatne np przy wybieraniu trenera w formularzu nowych zajec
    // (nie chcemy proponowac nieaktywnego trenera do nowych zajec)
    public List<Trener> findAktywni() {
        return cache.stream().filter(Trener::isAktywny).collect(Collectors.toList());
    }

    private void loadFromFile() {
        try (Reader reader = new FileReader(filePath)) {
            Type listType = new TypeToken<ArrayList<Trener>>(){}.getType();
            List<Trener> loaded = gson.fromJson(reader, listType);
            if (loaded != null) cache = loaded;
        } catch (IOException e) { /* Plik nie istnieje, używamy pustego cache */ }
    }

    private void saveToFile() {
        new File("data").mkdirs();
        try (Writer writer = new FileWriter(filePath)) {
            gson.toJson(cache, writer);
        } catch (IOException e) { e.printStackTrace(); }
    }

    private String generateId() {
        return Repository.nextSequentialId(cache.stream().map(Trener::getId).collect(Collectors.toList()));
    }
}
