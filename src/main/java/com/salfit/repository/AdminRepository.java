package com.salfit.repository;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.salfit.model.Admin;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

// Repozytorium adminow - "baza danych" w pliku JSON dla obiektow Admin
// Tutaj nie ma bazy SQL, caly projekt zapisuje dane do plikow JSON na dysku.
public class AdminRepository implements Repository<Admin> {

    private static AdminRepository instance; // singleton
    private String filePath = "resources/data/admin.json"; // gdzie trzymamy dane
    private List<Admin> cache = new ArrayList<>(); // dane wczytane do pamieci (zeby nie czytac pliku co chwila)
    private final Gson gson = Repository.createGson();

    //przy tworzeniu od razu wczytujemy dane z pliku do cache
    private AdminRepository() { loadFromFile(); }

    AdminRepository(String filePath) {
        this.filePath = filePath;
        loadFromFile();
    }

    static void resetInstance() { instance = null; }

    public static AdminRepository getInstance() {
        if (instance == null) instance = new AdminRepository();
        return instance;
    }

    @Override public List<Admin> findAll() { return cache; }

    @Override
    public Optional<Admin> findById(String id) {
        // przeszukujemy cache liniowo (nie ma indeksow)
        return cache.stream().filter(a -> a.getId().equals(id)).findFirst();
    }

    // specjalna metoda tylko dla Admina - szukanie po loginie (do logowania)
    public Optional<Admin> findByLogin(String login) {
        return cache.stream().filter(a -> login.equals(a.getLogin())).findFirst();
    }

    @Override
    public void save(Admin admin) {
        // uzywamy refleksji zeby ustawic prywatne pole "id",
        // bo Admin nie ma normalnego settera (tylko gettery)
        try {
            java.lang.reflect.Field idField = admin.getClass().getDeclaredField("id");
            idField.setAccessible(true); // pozwala ustawic pole prywatne z zewnatrz klasy
            idField.set(admin, generateId());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        cache.add(admin);
        saveToFile(); // od razu zapisujemy zmiany na dysk
    }

    @Override
    public void update(Admin admin) {
        // szukamy w cache obiektu z takim samym id i zamieniamy go na nowy
        for (int i = 0; i < cache.size(); i++) {
            if (cache.get(i).getId().equals(admin.getId())) {
                cache.set(i, admin);
                break;
            }
        }
        saveToFile();
    }

    @Override
    public void delete(String id) {
        cache.removeIf(a -> a.getId().equals(id));
        saveToFile();
    }

    // wczytuje dane z pliku JSON do cache. Jak plik nie istnieje, po prostu zostaje pusta lista
    private void loadFromFile() {
        try (Reader reader = new FileReader(filePath)) {
            Type listType = new TypeToken<ArrayList<Admin>>(){}.getType(); // Gson musi wiedziec ze to lista Adminow
            List<Admin> loaded = gson.fromJson(reader, listType);
            if (loaded != null) cache = loaded;
        } catch (IOException e) {}
    }

    // zapisuje cala liste z cache do pliku - nadpisuje caly plik kazdym razem
    private void saveToFile() {
        new File("data").mkdirs(); // hmm, tworzy folder "data" a nie ten z filePath - drobna niekonsekwencja w kodzie
        try (Writer writer = new FileWriter(filePath)) {
            gson.toJson(cache, writer);
        } catch (IOException e) { e.printStackTrace(); }
    }

    // dla admina generujemy id w innym stylu niz reszta repo - tu UUID (losowy unikalny string),
    // a nie kolejny numerek jak w innych repozytoriach
    private String generateId() { return UUID.randomUUID().toString(); }
}
