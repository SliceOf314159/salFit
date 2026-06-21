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
import java.util.stream.Collectors;

// Repozytorium dla Czlonkow ORAZ Karnetow - jedna klasa ogarnia oba (bo sa silnie zwiazane,
// karnet zawsze nalezy do jakiegos czlonka). Trzyma dwa pliki JSON i dwa cache.
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
        // ustawiamy id przez refleksje, tak jak w innych repo (bo brak settera na "id")
        try {
            java.lang.reflect.Field idField = czlonek.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(czlonek, generateCzlonekId());
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
        // jak usuwamy czlonka, to usuwamy mu rowniez wszystkie jego karnety
        cacheKarnety.removeIf(k -> k.getCzlonekId().equals(id));
        saveToFile();
    }

    // znajduje "aktywny" karnet czlonka - czyli taki ktory jest AKTYWNY albo WYGASA_WKROTCE
    public Optional<Karnet> findAktywnyKarnet(String czlonekId) {
        return cacheKarnety.stream()
                .filter(k -> k.getCzlonekId().equals(czlonekId))
                .filter(k -> k.getStatus().name().startsWith("AKTYWNY") || k.getStatus().name().startsWith("WYGASA"))
                .findFirst();
    }

    // wszystkie karnety ktore wygasaja w ciagu X dni
    public List<Karnet> findKarnetyWygasajace(int dni) {
        return cacheKarnety.stream()
                .filter(k -> k.wygasaWCiagu(dni))
                .collect(Collectors.toList());
    }

    // zapisuje nowy karnet (oddzielna metoda od save() bo to inny typ obiektu - Karnet a nie Czlonek)
    public void saveKarnet(Karnet karnet) {
        try {
            java.lang.reflect.Field idField = karnet.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(karnet, generateKarnetId());
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

    // wczytuje OBA pliki (czlonkow i karnety) do cache - kazdy w osobnym try-catch
    // zeby blad jednego pliku nie blokowal wczytania drugiego
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

    // zapisuje OBA cache do ich plikow
    private void saveToFile() {
        new File("data").mkdirs();
        try (Writer writer = new FileWriter(czlonkowiePath)) {
            gson.toJson(cacheCzlonkowie, writer);
        } catch (IOException e) { e.printStackTrace(); }

        try (Writer writer = new FileWriter(karnetyPath)) {
            gson.toJson(cacheKarnety, writer);
        } catch (IOException e) { e.printStackTrace(); }
    }

    // generowanie kolejnego id dla czlonka
    private String generateCzlonekId() {
        return Repository.nextSequentialId(cacheCzlonkowie.stream().map(Czlonek::getId).collect(Collectors.toList()));
    }

    // to samo, ale dla karnetow (osobna numeracja niz czlonkowie)
    private String generateKarnetId() {
        return Repository.nextSequentialId(cacheKarnety.stream().map(Karnet::getId).collect(Collectors.toList()));
    }
}
