package com.salfit.controllers;

import com.salfit.SessionManager;
import com.salfit.model.Trener;
import com.salfit.repository.TrenerRepository;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class TrainerProfilController {

    @FXML private Label valNazwa;
    @FXML private Label valSpec;
    @FXML private Label valEmail;
    @FXML private Label valTelefon;

    @FXML
    public void initialize() {
        String id = SessionManager.getInstance().getLoggedInTrenerId();
        if (id == null) return;
        TrenerRepository.getInstance().findById(id).ifPresent(this::populate);
    }

    private void populate(Trener t) {
        valNazwa.setText(t.getImieNazwisko());
        String spec = t.getSpecjalizacja() != null ? t.getSpecjalizacja() : "—";
        String poziom = t.getPoziom() != null ? " · " + t.getPoziom() : "";
        valSpec.setText(spec + poziom);
        valEmail.setText(t.getEmail() != null ? t.getEmail() : "—");
        valTelefon.setText(t.getTelefon() != null ? t.getTelefon() : "—");
    }
}
