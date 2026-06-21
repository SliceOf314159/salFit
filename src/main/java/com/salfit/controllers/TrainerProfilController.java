package com.salfit.controllers;

import com.salfit.SessionManager;
import com.salfit.model.Trener;
import com.salfit.repository.TrenerRepository;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

// prosty kontroler - ekran "moj profil" w panelu trenera.
// Tylko wyswietla dane zalogowanego trenera
public class TrainerProfilController {

    @FXML private Label valNazwa;
    @FXML private Label valSpec;
    @FXML private Label valEmail;
    @FXML private Label valTelefon;

    @FXML
    public void initialize() {
        // bierzemy id zalogowanego trenera
        String id = SessionManager.getInstance().getLoggedInTrenerId();
        if (id == null) return; // teoretycznie nie powinno sie zdarzyc, ale zabezpieczenie na wszelki wypadek
        TrenerRepository.getInstance().findById(id).ifPresent(this::populate);
    }

    // wypelnia danymi trenera
    private void populate(Trener t) {
        valNazwa.setText(t.getImieNazwisko());
        String spec = t.getSpecjalizacja() != null ? t.getSpecjalizacja() : "—";
        String poziom = t.getPoziom() != null ? " · " + t.getPoziom() : "";
        valSpec.setText(spec + poziom);
        valEmail.setText(t.getEmail() != null ? t.getEmail() : "—");
        valTelefon.setText(t.getTelefon() != null ? t.getTelefon() : "—");
    }
}
