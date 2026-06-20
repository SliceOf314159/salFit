package com.salfit.repository;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;

class RepositoryTest {

    @Test
    void pustaListaIdZwracaZero() {
        String result = Repository.nextSequentialId(List.of());
        assertEquals("0", result);
    }

    @Test
    void listaZProstymiNumerami() {
        String result = Repository.nextSequentialId(List.of("0", "1", "2"));
        assertEquals("3", result);
    }

    @Test
    void idZPrefiksemNienumerycznym() {
        String result = Repository.nextSequentialId(List.of("t1", "t2", "t5"));
        assertEquals("6", result);
    }

    @Test
    void listaZIdNiemozliwymDoSparsowania() {
        String result = Repository.nextSequentialId(List.of("abc", "xyz"));
        assertEquals("0", result);
    }

    @Test
    void listaZMieszanymiFormatami() {
        String result = Repository.nextSequentialId(List.of("3", "t10", "7"));
        assertEquals("11", result);
    }
}