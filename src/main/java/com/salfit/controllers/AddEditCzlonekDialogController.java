package com.salfit.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class AddEditCzlonekDialogController {

    @FXML private TextField fieldImie;
    @FXML private TextField fieldNazwisko;
    @FXML private TextField fieldEmail;
    @FXML private TextField fieldTelefon;
    @FXML private DatePicker fieldDataUr;
    @FXML private ComboBox<String> fieldPlec;
    @FXML private TextField fieldMiasto;
    @FXML private TextField fieldKod;
    @FXML private TextField fieldUlica;
    @FXML private TextArea fieldNotatki;
    @FXML private Button btnSave;

    @FXML
    public void initialize() {
        fieldPlec.setItems(FXCollections.observableArrayList(
                "Kobieta", "Mężczyzna", "Inna"));
    }

    public void setEditMode(boolean edit) {
        btnSave.setText(edit ? "Zapisz zmiany" : "Zapisz");
    }

    @FXML
    private void onSave() {
        if (!fieldImie.getText().isBlank() && !fieldNazwisko.getText().isBlank()) {
            closeDialog();
        }
    }

    @FXML private void onCancel()      { closeDialog(); }
    @FXML private void onChoosePhoto() { /* Open file chooser */ }

    private void closeDialog() {
        ((Stage) fieldImie.getScene().getWindow()).close();
    }
}
