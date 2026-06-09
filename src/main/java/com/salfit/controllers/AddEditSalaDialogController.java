package com.salfit.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class AddEditSalaDialogController {

    @FXML private TextField fieldNazwa;
    @FXML private TextField fieldKod;
    @FXML private ComboBox<String> fieldTyp;
    @FXML private TextField fieldPojemnosc;
    @FXML private TextField fieldPrzerwa;
    @FXML private ComboBox<String> fieldStatus;
    @FXML private TextArea fieldOpis;
    @FXML private Button btnUsun;
    @FXML private Button btnSave;

    @FXML
    public void initialize() {
        fieldTyp.setItems(FXCollections.observableArrayList(
                "Ogólna", "Siłownia", "Spinning", "Basen"));
        fieldStatus.setItems(FXCollections.observableArrayList(
                "Dostępna", "Zajęta", "W remoncie"));
        fieldStatus.getSelectionModel().selectFirst();
    }

    public void setEditMode(boolean edit) {
        btnUsun.setVisible(edit); btnUsun.setManaged(edit);
        btnSave.setText(edit ? "Zapisz zmiany" : "Dodaj salę");
    }

    @FXML private void stepUp()         { step(fieldPojemnosc,  1); }
    @FXML private void stepDown()       { step(fieldPojemnosc, -1); }
    @FXML private void stepPrzerwaUp()  { step(fieldPrzerwa,    5); }
    @FXML private void stepPrzerwaDown(){ step(fieldPrzerwa,   -5); }

    private void step(TextField field, int delta) {
        try {
            int val = Integer.parseInt(field.getText().trim());
            field.setText(String.valueOf(Math.max(0, val + delta)));
        } catch (NumberFormatException ignored) {}
    }

    @FXML private void onSave()   { if (!fieldNazwa.getText().isBlank()) closeDialog(); }
    @FXML private void onUsun()   { closeDialog(); }
    @FXML private void onCancel() { closeDialog(); }

    private void closeDialog() {
        ((Stage) fieldNazwa.getScene().getWindow()).close();
    }
}
