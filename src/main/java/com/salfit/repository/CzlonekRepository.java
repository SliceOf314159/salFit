package com.salfit.repository;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.salfit.model.Czlonek;
import com.salfit.model.Karnet;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class CzlonekRepository implements Repository<Czlonek> {

    private static CzlonekRepository instance;
    private String czlonkowiePath = "resources/data/czlonkowie.json";
    private String karnetyPath = "resources/data/karnety.json";

    private List<Czlonek> cacheCzlonkowie = new ArrayList<>();
    private List<Karnet> cacheKarnety = new ArrayList<>();
    private final Gson gson = Repository.createGson();

    private CzlonekRepository() { loadFromFile(); }

    CzlonekRepository(String czlonkowiePath, String karnetyPath) {
        this.czlonkowiePath = czlonkowiePath;
        this.karnetyPath = karnetyPath;
        loadFromFile();
    }

    static void resetInstance() { instance = null; }

    public static CzlonekRepository getInstance() {
        if (instance == null) instance = new CzlonekRepository();
        return instance;
    }

    @Override
    public List<Czlonek> findAll() { return cacheCzlonkowie; }

    @Override
    public Optional<Czlonek> findById(String id) {
        return cacheCzlonkowie.stream().filter(c -> c.getId().equals(id)).findFirst();
    }

    @Override
    public void save(Czlonek czlonek) {
        try {
            java.lang.reflect.Field idField = czlonek.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(czlonek, generateId());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        cacheCzlonkowie.add(czlonek);
        saveToFile();
    }

    @Override
    public void update(Czlonek czlonek) {
        for (int i = 0; i < cacheCzlonkowie.size(); i++) {
            if (cacheCzlonkowie.get(i).getId().equals(czlonek.getId())) {
                cacheCzlonkowie.set(i, czlonek);
                break;
            }
        }
        saveToFile();
    }

    @Override
    public void delete(String id) {
        cacheCzlonkowie.removeIf(c -> c.getId().equals(id));
        cacheKarnety.removeIf(k -> k.getCzlonekId().equals(id));
        saveToFile();
    }

    public Optional<Karnet> findAktywnyKarnet(String czlonekId) {
        return cacheKarnety.stream()
                .filter(k -> k.getCzlonekId().equals(czlonekId))
                .filter(k -> k.getStatus().name().startsWith("AKTYWNY") || k.getStatus().name().startsWith("WYGASA"))
                .findFirst();
    }

    public List<Karnet> findKarnetyWygasajace(int dni) {
        return cacheKarnety.stream()
                .filter(k -> k.wygasaWCiagu(dni))
                .collect(Collectors.toList());
    }

    public void saveKarnet(Karnet karnet) {
        try {
            java.lang.reflect.Field idField = karnet.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(karnet, generateId());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        cacheKarnety.add(karnet);
        saveToFile();
    }

    public List<Karnet> findAllKarnety() { return cacheKarnety; }

    public void updateKarnet(Karnet karnet) {
        for (int i = 0; i < cacheKarnety.size(); i++) {
            if (cacheKarnety.get(i).getId().equals(karnet.getId())) {
                cacheKarnety.set(i, karnet);
                break;
            }
        }
        saveToFile();
    }

    private void loadFromFile() {
        try (Reader reader = new FileReader(czlonkowiePath)) {
            Type listType = new TypeToken<ArrayList<Czlonek>>(){}.getType();
            List<Czlonek> loaded = gson.fromJson(reader, listType);
            if (loaded != null) cacheCzlonkowie = loaded;
        } catch (IOException e) {}

        try (Reader reader = new FileReader(karnetyPath)) {
            Type listType = new TypeToken<ArrayList<Karnet>>(){}.getType();
            List<Karnet> loaded = gson.fromJson(reader, listType);
            if (loaded != null) cacheKarnety = loaded;
        } catch (IOException e) {}
    }

    private void saveToFile() {
        new File("data").mkdirs();
        try (Writer writer = new FileWriter(czlonkowiePath)) {
            gson.toJson(cacheCzlonkowie, writer);
        } catch (IOException e) { e.printStackTrace(); }

        try (Writer writer = new FileWriter(karnetyPath)) {
            gson.toJson(cacheKarnety, writer);
        } catch (IOException e) { e.printStackTrace(); }
    }

    private String generateId() { return UUID.randomUUID().toString(); }
}