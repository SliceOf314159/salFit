package com.salfit.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class AddEditZajeciaDialogController {

    @FXML private TextField fieldNazwa;
    @FXML private ComboBox<String> fieldRodzaj;
    @FXML private ComboBox<String> fieldTrener;
    @FXML private ComboBox<String> fieldSala;
    @FXML private ComboBox<String> fieldPoziom;
    @FXML private DatePicker fieldData;
    @FXML private TextField fieldLimit;
    @FXML private TextField fieldCzas;
    @FXML private TextArea fieldOpis;
    @FXML private TextArea fieldWymagania;

    @FXML
    public void initialize() {
        fieldRodzaj.setItems(FXCollections.observableArrayList(
                "Yoga", "Pilates", "CrossFit", "HIIT", "Stretching", "Spinning"));
        fieldPoziom.setItems(FXCollections.observableArrayList(
                "★★★★★", "★★★★☆", "★★★☆☆", "★★☆☆☆", "★☆☆☆☆"));
        fieldPoziom.getSelectionModel().select(2);
    }

    @FXML private void stepLimitUp()   { step(fieldLimit,  1); }
    @FXML private void stepLimitDown() { step(fieldLimit, -1); }
    @FXML private void stepCzasUp()    { step(fieldCzas,  15); }
    @FXML private void stepCzasDown()  { step(fieldCzas, -15); }

    private void step(TextField f, int delta) {
        try {
            int v = Integer.parseInt(f.getText().trim());
            f.setText(String.valueOf(Math.max(0, v + delta)));
        } catch (NumberFormatException ignored) {}
    }

    @FXML private void onSave()   { if (!fieldNazwa.getText().isBlank()) closeDialog(); }
    @FXML private void onCancel() { closeDialog(); }

    private void closeDialog() {
        ((Stage) fieldNazwa.getScene().getWindow()).close();
    }
}
