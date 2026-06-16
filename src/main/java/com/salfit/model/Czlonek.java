package com.salfit.model;

import java.time.LocalDate;

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

    public String getImieNazwisko() {
        return imie + " " + nazwisko;
    }
}