package com.salfit.repository;

import com.google.gson.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

// Generyczny interfejs dla wszystkich repozytoriow - kazde repo (CzlonekRepository, SalaRepository itd)
// implementuje te same podstawowe operacje (Create Read Update Delete)
public interface Repository<T> {
    List<T> findAll();              // zwroc wszystkie obiekty
    Optional<T> findById(String id); // znajdz jeden po id (Optional bo moze nie istniec)
    void save(T entity);            // zapisz nowy obiekt (z autogenerowanym id)
    void update(T entity);          // zaktualizuj istniejacy obiekt
    void delete(String id);         // usun po id

    // Metoda statyczna wspolna dla wszystkich do generowania nastepnego id w stylu "0", "1", "2"...
    // Bierzemy wszystkie istniejace id, wyciagamy z nich cyfry,
    // znajdujemy najwieksze i zwracamy +1. Dzieki temu id sa zawsze unikalne i rosnace.
    static String nextSequentialId(List<String> existingIds) {
        int max = existingIds.stream()
                .map(id -> id.replaceAll("\\D+", "")) // usuwamy wszystko co nie jest cyfra
                .filter(s -> !s.isEmpty())             // pomijamy stringi ktore po czyszczeniu sa puste
                .mapToInt(Integer::parseInt)
                .max().orElse(-1);                     // jak nie ma zadnych id, zaczynamy od -1 (+1 = 0)
        return String.valueOf(max + 1);
    }

    // Tworzy skonfigurowany obiekt Gson uzywany przez wszystkie repozytoria do zapisu/odczytu JSON-a.
    static Gson createGson() {
        return new GsonBuilder()
                .setPrettyPrinting() // zeby pliki JSON byly czytelne (z wciecia mi), a nie jedna linia
                // serializer LocalDate -> zapisuje jako string
                .registerTypeAdapter(LocalDate.class, (JsonSerializer<LocalDate>) (src, typeOfSrc, context) ->
                        new JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE)))
                // deserializer LocalDate -> z stringa robi z powrotem obiekt LocalDate
                .registerTypeAdapter(LocalDate.class, (JsonDeserializer<LocalDate>) (json, typeOfT, context) ->
                        LocalDate.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE))
                // to samo ale dla LocalDateTime (data + godzina)
                .registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>) (src, typeOfSrc, context) ->
                        new JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>) (json, typeOfT, context) ->
                        LocalDateTime.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .create();
    }
}
