package com.salfit.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

// Model karnetu czlonka. Status NIE jest zapisany jako pole - liczymy go "na zywo"
// w getStatus() na podstawie aktualnej daty, zeby nie trzeba bylo nigdzie recznie
// aktualizowac statusu
public class Karnet {

    private String id;
    private String czlonekId;     // do kogo nalezy ten karnet
    private RodzajKarnetu rodzaj; // miesieczny/kwartalny/roczny
    private LocalDate dataOd;
    private LocalDate dataDo;

    public String getId() { return id; }
    public String getCzlonekId() { return czlonekId; }
    public RodzajKarnetu getRodzaj() { return rodzaj; }
    public LocalDate getDataOd() { return dataOd; }
    public LocalDate getDataDo() { return dataDo; }

    // tutaj liczymy aktualny status karnetu
    public StatusKarnetu getStatus() {
        if (dataDo == null) return StatusKarnetu.WYGASL; // brak daty konca = traktujemy jako wygasly
        LocalDate today = LocalDate.now();

        if (dataDo.isBefore(today)) {
            return StatusKarnetu.WYGASL; // data konca juz minela
        }
        // Przyjeto 14 dni dla statusu "Wygasa wkrotce"
        if (wygasaWCiagu(14)) {
            return StatusKarnetu.WYGASA_WKROTCE;
        }
        return StatusKarnetu.AKTYWNY; // wszystko ok
    }

    // sprawdza czy karnet wygasa w ciagu 14 dni od teraz (uzywane np do listy "karnety wygasajace")
    public boolean wygasaWCiagu(int dni) {
        if (dataDo == null) return false;
        LocalDate today = LocalDate.now();
        // liczymy ile dni zostalo do daty konca (moze byc ujemne jak juz minelo)
        long daysBetween = ChronoUnit.DAYS.between(today, dataDo);
        // wygasa "wkrotce" jesli zostalo 0..dni dni (czyli jeszcze nie minelo, ale juz blisko)
        return daysBetween >= 0 && daysBetween <= dni;
    }
}
