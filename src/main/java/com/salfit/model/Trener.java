package com.salfit.model;

// Model trenera - podobny do Czlonka, ale ma dodatkowo specjalizacje, poziom, haslo i status aktywnosci
public class Trener {

    private String id;
    private String imie;
    private String nazwisko;
    private String email;
    private String telefon;
    private String specjalizacja; // np "grupowy, personalny" - string ze spisanymi specjalizacjami
    private String poziom;        // gwiazdki - poziom doswiadczenia trenera
    private String haslo;         // do logowania w panelu trenera
    private boolean aktywny;      // czy trener jest aktywny (moze prowadzic zajecia) czy "wylaczony"

    public String getId() { return id; }
    public String getImie() { return imie; }
    public String getNazwisko() { return nazwisko; }
    public String getEmail() { return email; }
    public String getTelefon() { return telefon; }
    public String getSpecjalizacja() { return specjalizacja; }
    public String getPoziom() { return poziom; }
    public String getHaslo() { return haslo; }
    public boolean isAktywny() { return aktywny; }

    // setter tylko dla aktywny - bo to jedyne co zmieniamy "z tabeli" jednym klikiem (toggle aktywuj/dezaktywuj)
    public void setAktywny(boolean aktywny) {
        this.aktywny = aktywny;
    }

    // helper imie+nazwisko, uzywany w duzej ilosci miejsc
    public String getImieNazwisko() {
        return imie + " " + nazwisko;
    }
}
