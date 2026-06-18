package com.salfit.repository;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.salfit.model.Sala;
import com.salfit.model.StatusSali;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SalaRepository implements Repository<Sala> {

    private static SalaRepository instance;
    private String filePath = "resources/data/sale.json";
    private List<Sala> cache = new ArrayList<>();
    private final Gson gson = Repository.createGson();

    private SalaRepository() { loadFromFile(); }

    SalaRepository(String filePath) {
        this.filePath = filePath;
        loadFromFile();
    }

    static void resetInstance() { instance = null; }

    public static SalaRepository getInstance() {
        if (instance == null) instance = new SalaRepository();
        return instance;
    }

    @Override
    public List<Sala> findAll() { return cache; }

    @Override
    public Optional<Sala> findById(String id) {
        return cache.stream().filter(s -> s.getId().equals(id)).findFirst();
    }

    @Override
    public void save(Sala sala) {
        try {
            java.lang.reflect.Field idField = sala.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(sala, generateId());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        cache.add(sala);
        saveToFile();
    }

    @Override
    public void update(Sala sala) {
        for (int i = 0; i < cache.size(); i++) {
            if (cache.get(i).getId().equals(sala.getId())) {
                cache.set(i, sala);
                break;
            }
        }
        saveToFile();
    }

    @Override
    public void delete(String id) {
        cache.removeIf(s -> s.getId().equals(id));
        saveToFile();
    }

    public List<Sala> findByStatus(StatusSali status) {
        return cache.stream().filter(s -> s.getStatus() == status).collect(Collectors.toList());
    }

    private void loadFromFile() {
        try (Reader reader = new FileReader(filePath)) {
            Type listType = new TypeToken<ArrayList<Sala>>(){}.getType();
            List<Sala> loaded = gson.fromJson(reader, listType);
            if (loaded != null) cache = loaded;
        } catch (IOException e) {}
    }

    private void saveToFile() {
        new File("data").mkdirs();
        try (Writer writer = new FileWriter(filePath)) {
            gson.toJson(cache, writer);
        } catch (IOException e) { e.printStackTrace(); }
    }

    private String generateId() {
        return Repository.nextSequentialId(cache.stream().map(Sala::getId).collect(Collectors.toList()));
    }
}