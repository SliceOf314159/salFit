package com.salfit.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EnumTest {

    @Test
    void statusKarnetu_hasExpectedValues() {
        StatusKarnetu[] values = StatusKarnetu.values();
        assertEquals(3, values.length);
        assertNotNull(StatusKarnetu.valueOf("AKTYWNY"));
        assertNotNull(StatusKarnetu.valueOf("WYGASL"));
        assertNotNull(StatusKarnetu.valueOf("WYGASA_WKROTCE"));
    }

    @Test
    void statusSali_hasExpectedValues() {
        StatusSali[] values = StatusSali.values();
        assertEquals(3, values.length);
        assertNotNull(StatusSali.valueOf("DOSTEPNA"));
        assertNotNull(StatusSali.valueOf("ZAJETA"));
        assertNotNull(StatusSali.valueOf("W_REMONCIE"));
    }

    @Test
    void rodzajKarnetu_hasExpectedValues() {
        RodzajKarnetu[] values = RodzajKarnetu.values();
        assertEquals(3, values.length);
        assertNotNull(RodzajKarnetu.valueOf("MIESIECZNY"));
        assertNotNull(RodzajKarnetu.valueOf("KWARTALNY"));
        assertNotNull(RodzajKarnetu.valueOf("ROCZNY"));
    }

    @Test
    void statusKarnetu_ordinalOrder() {
        assertEquals(0, StatusKarnetu.AKTYWNY.ordinal());
        assertEquals(1, StatusKarnetu.WYGASL.ordinal());
        assertEquals(2, StatusKarnetu.WYGASA_WKROTCE.ordinal());
    }

    @Test
    void statusSali_ordinalOrder() {
        assertEquals(0, StatusSali.DOSTEPNA.ordinal());
        assertEquals(1, StatusSali.ZAJETA.ordinal());
        assertEquals(2, StatusSali.W_REMONCIE.ordinal());
    }

    @Test
    void rodzajKarnetu_ordinalOrder() {
        assertEquals(0, RodzajKarnetu.MIESIECZNY.ordinal());
        assertEquals(1, RodzajKarnetu.KWARTALNY.ordinal());
        assertEquals(2, RodzajKarnetu.ROCZNY.ordinal());
    }
}
