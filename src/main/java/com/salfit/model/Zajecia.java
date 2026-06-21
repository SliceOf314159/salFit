package com.salfit.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// Model pojedynczych zajec w grafiku (np "Yoga, poniedzialek 18:00").
// Trzyma liste uczestnikow (kto sie zapisal) i liste "potwierdzonych" (kto faktycznie potwierdzil obecnosc)
public class Zajecia {

    private String id;
    private String nazwa;
    private String trenerId;
    private String salaId;
    private LocalDateTime termin;        // data + godzina zajec
    private int czasTrwaniaMinut;
    private int limitUczestnikow;        // max ile osob moze sie zapisac
    private List<String> uczestnicyIds = new ArrayList<>();         // id czlonkow zapisanych na zajecia
    private List<String> potwierdzeniUczestnicy = new ArrayList<>(); // id czlonkow ktorzy potwierdzili obecnosc

    public String getId() { return id; }
    public String getNazwa() { return nazwa; }
    public String getTrenerId() { return trenerId; }
    public String getSalaId() { return salaId; }
    public LocalDateTime getTermin() { return termin; }
    public int getCzasTrwaniaMinut() { return czasTrwaniaMinut; }
    public int getLimitUczestnikow() { return limitUczestnikow; }
    public List<String> getUczestnicyIds() { return uczestnicyIds; }
    public List<String> getPotwierdzeniUczestnicy() { return potwierdzeniUczestnicy; }

    // dodaje czlonka do listy uczestnikow zajec (jesli juz go tam nie ma, zeby nie bylo duplikatow)
    public void dodajUczestnika(String czlonekId) {
        if (!uczestnicyIds.contains(czlonekId)) {
            uczestnicyIds.add(czlonekId);
        }
    }

    // usuwa czlonka z zajec i od razu z listy potwierdzonych, bo jak go nie ma na zajeciach
    // to nie moze byc tez "potwierdzony"
    public void usunUczestnika(String czlonekId) {
        uczestnicyIds.remove(czlonekId);
        potwierdzeniUczestnicy.remove(czlonekId);
    }

    // oznacza czlonka jako potwierdzonego
    public void potwierdzUczestnika(String czlonekId) {
        if (!potwierdzeniUczestnicy.contains(czlonekId)) {
            potwierdzeniUczestnicy.add(czlonekId);
        }
    }

    // odwrotnosc - odznacza potwierdzenie
    public void odznaczUczestnika(String czlonekId) {
        potwierdzeniUczestnicy.remove(czlonekId);
    }

    // sprawdza czy dany czlonek jest na liscie potwierdzonych
    public boolean czyPotwierdzony(String czlonekId) {
        return potwierdzeniUczestnicy.contains(czlonekId);
    }
}
