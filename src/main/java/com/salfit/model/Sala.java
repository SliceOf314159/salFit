package com.salfit.model;

public class Sala {

    private String id;
    private String nazwa;
    private int pojemnosc;
    private int minPrzerwaMinut;
    private StatusSali status;

    public String getId() { return id; }
    public String getNazwa() { return nazwa; }
    public int getPojemnosc() { return pojemnosc; }
    public int getMinPrzerwaMinut() { return minPrzerwaMinut; }
    public StatusSali getStatus() { return status; }

    public void setStatus(StatusSali status) {
        this.status = status;
    }
}