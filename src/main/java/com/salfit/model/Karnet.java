package com.salfit.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Karnet {

    private String id;
    private String czlonekId;
    private RodzajKarnetu rodzaj;
    private LocalDate dataOd;
    private LocalDate dataDo;

    public String getId() { return id; }
    public String getCzlonekId() { return czlonekId; }
    public RodzajKarnetu getRodzaj() { return rodzaj; }
    public LocalDate getDataOd() { return dataOd; }
    public LocalDate getDataDo() { return dataDo; }

    public StatusKarnetu getStatus() {
        if (dataDo == null) return StatusKarnetu.WYGASL;
        LocalDate today = LocalDate.now();

        if (dataDo.isBefore(today)) {
            return StatusKarnetu.WYGASL;
        }
        //Przyjąto 14 dni dla statusu "Wygasa wkrótce"
        if (wygasaWCiagu(14)) {
            return StatusKarnetu.WYGASA_WKROTCE;
        }
        return StatusKarnetu.AKTYWNY;
    }

    public boolean wygasaWCiagu(int dni) {
        if (dataDo == null) return false;
        LocalDate today = LocalDate.now();
        long daysBetween = ChronoUnit.DAYS.between(today, dataDo);
        return daysBetween >= 0 && daysBetween <= dni;
    }
}