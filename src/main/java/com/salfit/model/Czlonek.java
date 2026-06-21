package com.salfit.model;

import java.time.LocalDate;

// Model czlonka silowni (klienta). Tylko gettery - dane wpisywane przez formularz
// i tworzone przez Gson/refleksje, nie potrzeba tu setterow
public class Czlonek {

    private String id;
    private String imie;
    private String nazwisko;
    private String email;
    private String telefon;
    private LocalDate dataUrodzenia;

    public String getId() { return id; }
    public String getImie() { return imie; }
    public String getNazwisko() { return nazwisko; }
    public String getEmail() { return email; }
    public String getTelefon() { return telefon; }
    public LocalDate getDataUrodzenia() { return dataUrodzenia; }

    // helper zeby nie skladac imienia i nazwiska wszedzie recznie w kontrolerach
    public String getImieNazwisko() {
        return imie + " " + nazwisko;
    }
}
