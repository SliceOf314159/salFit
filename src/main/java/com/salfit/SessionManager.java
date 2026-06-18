package com.salfit;

import com.salfit.model.Zajecia;

public class SessionManager {

    private static SessionManager instance;
    private String loggedInTrenerId;
    private Zajecia selectedZajecia;

    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instance == null) instance = new SessionManager();
        return instance;
    }

    public String getLoggedInTrenerId() { return loggedInTrenerId; }
    public void setLoggedInTrenerId(String id) { this.loggedInTrenerId = id; }

    public Zajecia getSelectedZajecia() { return selectedZajecia; }
    public void setSelectedZajecia(Zajecia z) { this.selectedZajecia = z; }
}
