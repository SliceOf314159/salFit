package com.salfit.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class TrainerProfilController {

    @FXML private Label valNazwa;
    @FXML private Label valSpec;
    @FXML private Label valEmail;
    @FXML private Label valTelefon;

    @FXML
    public void initialize() {
        /* Data will be loaded from TrenerRepository for the logged-in trainer. */
    }
}
