package com.salfit;

import com.salfit.model.Zajecia;

//  singleton  - ten trzyma dane o "sesji" czyli kto jest zalogowany
// i co aktualnie robi (np. ktore zajecia trener kliknal w grafiku)
// Dzieki temu nie trzeba przekazywac tych danych przez 100 konstruktorow miedzy ekranami.
public class SessionManager {

    private static SessionManager instance;
    private String loggedInTrenerId;   // id trenera ktory sie zalogowal
    private Zajecia selectedZajecia;   // zajecia kliknete np w grafiku trenera

    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instance == null) instance = new SessionManager();
        return instance;
    }

    // standardowe get/set
    public String getLoggedInTrenerId() { return loggedInTrenerId; }
    public void setLoggedInTrenerId(String id) { this.loggedInTrenerId = id; }

    // to samo ale dla wybranych zajec (np jak trener klika w karte zajec w grafiku
    // zeby zobaczyc liste uczestnikow)
    public Zajecia getSelectedZajecia() { return selectedZajecia; }
    public void setSelectedZajecia(Zajecia z) { this.selectedZajecia = z; }
}
