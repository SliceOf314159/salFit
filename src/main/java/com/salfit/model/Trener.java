package com.salfit.model;

public class Trener {

    private String id;
    private String imie;
    private String nazwisko;
    private String email;
    private String telefon;
    private String specjalizacja;
    private String poziom;
    private String haslo;
    private boolean aktywny;

    public String getId() { return id; }
    public String getImie() { return imie; }
    public String getNazwisko() { return nazwisko; }
    public String getEmail() { return email; }
    public String getTelefon() { return telefon; }
    public String getSpecjalizacja() { return specjalizacja; }
    public String getPoziom() { return poziom; }
    public String getHaslo() { return haslo; }
    public boolean isAktywny() { return aktywny; }

    public void setAktywny(boolean aktywny) {
        this.aktywny = aktywny;
    }

    public String getImieNazwisko() {
        return imie + " " + nazwisko;
    }
}