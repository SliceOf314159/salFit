package com.salfit.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.salfit.model.Czlonek;
import com.salfit.repository.CzlonekRepository;
import com.salfit.repository.Repository;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class AddEditCzlonekDialogController {

    @FXML private TextField fieldImie;
    @FXML private TextField fieldNazwisko;
    @FXML private TextField fieldEmail;
    @FXML private TextField fieldTelefon;
    @FXML private DatePicker fieldDataUr;
    @FXML private Button btnSave;

    private static final Gson GSON = Repository.createGson();
    private boolean editMode = false;
    private Czlonek czlonek;

    public void setCzlonek(Czlonek c) {
        this.czlonek = c;
        fieldImie.setText(c.getImie());
        fieldNazwisko.setText(c.getNazwisko());
        fieldEmail.setText(c.getEmail());
        fieldTelefon.setText(c.getTelefon());
        if (c.getDataUrodzenia() != null) fieldDataUr.setValue(c.getDataUrodzenia());
    }

    public void setEditMode(boolean edit) {
        editMode = edit;
        btnSave.setText(edit ? "Zapisz zmiany" : "Zapisz");
    }

    @FXML
    private void onSave() {
        if (fieldImie.getText().isBlank() || fieldNazwisko.getText().isBlank()) return;

        JsonObject obj = new JsonObject();
        if (editMode && czlonek != null) obj.addProperty("id", czlonek.getId());
        obj.addProperty("imie",     fieldImie.getText().trim());
        obj.addProperty("nazwisko", fieldNazwisko.getText().trim());
        obj.addProperty("email",    fieldEmail.getText().trim());
        obj.addProperty("telefon",  fieldTelefon.getText().trim());
        if (fieldDataUr.getValue() != null)
            obj.addProperty("dataUrodzenia", fieldDataUr.getValue().toString());

        Czlonek c = GSON.fromJson(obj, Czlonek.class);
        if (editMode) {
            CzlonekRepository.getInstance().update(c);
        } else {
            CzlonekRepository.getInstance().save(c);
        }
        closeDialog();
    }

    @FXML private void onCancel()      { closeDialog(); }
    @FXML private void onChoosePhoto() { /* photo chooser stub */ }

    private void closeDialog() {
        ((Stage) fieldImie.getScene().getWindow()).close();
    }
}
