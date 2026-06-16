package com.salfit.repository;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class RepositoryTest {

    @Test
    void createGson_returnsNonNullInstance() {
        assertNotNull(Repository.createGson());
    }

    @Test
    void createGson_serializesLocalDate() {
        Gson gson = Repository.createGson();
        LocalDate date = LocalDate.of(2026, 1, 15);
        String json = gson.toJson(date);
        assertEquals("\"2026-01-15\"", json);
    }

    @Test
    void createGson_deserializesLocalDate() {
        Gson gson = Repository.createGson();
        LocalDate result = gson.fromJson("\"2025-12-31\"", LocalDate.class);
        assertEquals(LocalDate.of(2025, 12, 31), result);
    }

    @Test
    void createGson_serializesLocalDateTime() {
        Gson gson = Repository.createGson();
        LocalDateTime dt = LocalDateTime.of(2026, 6, 16, 9, 30, 0);
        String json = gson.toJson(dt);
        assertEquals("\"2026-06-16T09:30:00\"", json);
    }

    @Test
    void createGson_deserializesLocalDateTime() {
        Gson gson = Repository.createGson();
        LocalDateTime result = gson.fromJson("\"2026-06-16T09:30:00\"", LocalDateTime.class);
        assertEquals(LocalDateTime.of(2026, 6, 16, 9, 30, 0), result);
    }

    @Test
    void createGson_localDateRoundTrip() {
        Gson gson = Repository.createGson();
        LocalDate original = LocalDate.of(2000, 2, 29);
        LocalDate roundTripped = gson.fromJson(gson.toJson(original), LocalDate.class);
        assertEquals(original, roundTripped);
    }

    @Test
    void createGson_localDateTimeRoundTrip() {
        Gson gson = Repository.createGson();
        LocalDateTime original = LocalDateTime.of(2026, 11, 30, 23, 59, 59);
        LocalDateTime roundTripped = gson.fromJson(gson.toJson(original), LocalDateTime.class);
        assertEquals(original, roundTripped);
    }
}
