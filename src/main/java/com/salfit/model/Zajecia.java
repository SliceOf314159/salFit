package com.salfit.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Zajecia {

    private String id;
    private String nazwa;
    private String trenerId;
    private String salaId;
    private LocalDateTime termin;
    private int czasTrwaniaMinut;
    private int limitUczestnikow;
    private List<String> uczestnicyIds = new ArrayList<>();
    private List<String> potwierdzeniUczestnicy = new ArrayList<>();

    public String getId() { return id; }
    public String getNazwa() { return nazwa; }
    public String getTrenerId() { return trenerId; }
    public String getSalaId() { return salaId; }
    public LocalDateTime getTermin() { return termin; }
    public int getCzasTrwaniaMinut() { return czasTrwaniaMinut; }
    public int getLimitUczestnikow() { return limitUczestnikow; }
    public List<String> getUczestnicyIds() { return uczestnicyIds; }
    public List<String> getPotwierdzeniUczestnicy() { return potwierdzeniUczestnicy; }

    public void dodajUczestnika(String czlonekId) {
        if (!uczestnicyIds.contains(czlonekId)) {
            uczestnicyIds.add(czlonekId);
        }
    }

    public void usunUczestnika(String czlonekId) {
        uczestnicyIds.remove(czlonekId);
        potwierdzeniUczestnicy.remove(czlonekId);
    }

    public void potwierdzUczestnika(String czlonekId) {
        if (!potwierdzeniUczestnicy.contains(czlonekId)) {
            potwierdzeniUczestnicy.add(czlonekId);
        }
    }

    public void odznaczUczestnika(String czlonekId) {
        potwierdzeniUczestnicy.remove(czlonekId);
    }

    public boolean czyPotwierdzony(String czlonekId) {
        return potwierdzeniUczestnicy.contains(czlonekId);
    }
}