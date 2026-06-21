package com.salfit.model;

// Model sali treningowej
public class Sala {

    private String id;
    private String nazwa;
    private int pojemnosc;          // ile osob maksymalnie sie zmiesci
    private int minPrzerwaMinut;    // minimalna przerwa miedzy zajeciami w tej sali (zeby ludzie sie nie mijali w drzwiach)
    private StatusSali status;

    public String getId() { return id; }
    public String getNazwa() { return nazwa; }
    public int getPojemnosc() { return pojemnosc; }
    public int getMinPrzerwaMinut() { return minPrzerwaMinut; }
    public StatusSali getStatus() { return status; }

    // status ma setter, bo to jedyne pole ktore zmieniamy "na zywo" z tabeli (przycisk "Zmien status")
    // bez przechodzenia przez caly formularz edycji
    public void setStatus(StatusSali status) {
        this.status = status;
    }
}
