package com.salfit.repository;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.salfit.model.Zajecia;

import java.io.*;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class GrafikRepository implements Repository<Zajecia> {

    private static GrafikRepository instance;
    private String filePath = "resources/data/zajecia.json";
    private List<Zajecia> cache = new ArrayList<>();
    private final Gson gson = Repository.createGson();

    private GrafikRepository() { loadFromFile(); }

    GrafikRepository(String filePath) {
        this.filePath = filePath;
        loadFromFile();
    }

    static void resetInstance() { instance = null; }

    public static GrafikRepository getInstance() {
        if (instance == null) instance = new GrafikRepository();
        return instance;
    }

    @Override
    public List<Zajecia> findAll() { return cache; }

    @Override
    public Optional<Zajecia> findById(String id) {
        return cache.stream().filter(z -> z.getId().equals(id)).findFirst();
    }

    @Override
    public void save(Zajecia zajecia) {
        try {
            java.lang.reflect.Field idField = zajecia.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(zajecia, generateId());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        cache.add(zajecia);
        saveToFile();
    }

    @Override
    public void update(Zajecia zajecia) {
        for (int i = 0; i < cache.size(); i++) {
            if (cache.get(i).getId().equals(zajecia.getId())) {
                cache.set(i, zajecia);
                break;
            }
        }
        saveToFile();
    }

    @Override
    public void delete(String id) {
        cache.removeIf(z -> z.getId().equals(id));
        saveToFile();
    }

    public List<Zajecia> findByTrener(String trenerId) {
        return cache.stream().filter(z -> z.getTrenerId().equals(trenerId)).collect(Collectors.toList());
    }

    public List<Zajecia> findBySala(String salaId) {
        return cache.stream().filter(z -> z.getSalaId().equals(salaId)).collect(Collectors.toList());
    }

    public List<Zajecia> findByTydzien(LocalDate poniedzialek) {
        LocalDate niedziela = poniedzialek.plusDays(6);
        return cache.stream()
                .filter(z -> {
                    LocalDate dataZajec = z.getTermin().toLocalDate();
                    return !dataZajec.isBefore(poniedzialek) && !dataZajec.isAfter(niedziela);
                })
                .collect(Collectors.toList());
    }

    private void loadFromFile() {
        try (Reader reader = new FileReader(filePath)) {
            Type listType = new TypeToken<ArrayList<Zajecia>>(){}.getType();
            List<Zajecia> loaded = gson.fromJson(reader, listType);
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
        return Repository.nextSequentialId(cache.stream().map(Zajecia::getId).collect(Collectors.toList()));
    }
}