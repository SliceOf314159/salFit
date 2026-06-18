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

public class AdminRepository implements Repository<Admin> {

    private static AdminRepository instance;
    private String filePath = "resources/data/admin.json";
    private List<Admin> cache = new ArrayList<>();
    private final Gson gson = Repository.createGson();

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
        return cache.stream().filter(a -> a.getId().equals(id)).findFirst();
    }

    public Optional<Admin> findByLogin(String login) {
        return cache.stream().filter(a -> login.equals(a.getLogin())).findFirst();
    }

    @Override
    public void save(Admin admin) {
        try {
            java.lang.reflect.Field idField = admin.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(admin, generateId());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        cache.add(admin);
        saveToFile();
    }

    @Override
    public void update(Admin admin) {
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

    private void loadFromFile() {
        try (Reader reader = new FileReader(filePath)) {
            Type listType = new TypeToken<ArrayList<Admin>>(){}.getType();
            List<Admin> loaded = gson.fromJson(reader, listType);
            if (loaded != null) cache = loaded;
        } catch (IOException e) {}
    }

    private void saveToFile() {
        new File("data").mkdirs();
        try (Writer writer = new FileWriter(filePath)) {
            gson.toJson(cache, writer);
        } catch (IOException e) { e.printStackTrace(); }
    }

    private String generateId() { return UUID.randomUUID().toString(); }
}
