package com.salfit.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

public class RaportController {

    @FXML private Label statZajecia;
    @FXML private Label statCzlonkowie;
    @FXML private Label statWygasajace;

    @FXML private ProgressBar barSalaA;
    @FXML private ProgressBar barSalaB;
    @FXML private ProgressBar barSalaC;
    @FXML private ProgressBar barSalaD;

    @FXML private ProgressBar barT1;
    @FXML private ProgressBar barT2;
    @FXML private ProgressBar barT3;
    @FXML private ProgressBar barT4;

    @FXML
    public void initialize() {
        refresh();
    }

    public void refresh() {
        /* Statistics will be computed from repositories. */
    }

    @FXML
    public void onEksportCSV() {
        /* Serialize report data to CSV and open save dialog. */
    }

    @FXML
    public void onEksportTXT() {
        /* Serialize report data to TXT and open save dialog. */
    }
}
