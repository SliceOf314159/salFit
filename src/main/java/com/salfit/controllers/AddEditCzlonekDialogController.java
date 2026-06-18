package com.salfit.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.salfit.model.Czlonek;
import com.salfit.repository.CzlonekRepository;
import com.salfit.repository.Repository;
import com.salfit.util.DraftStore;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;

public class AddEditCzlonekDialogController {

    @FXML private TextField fieldImie;
    @FXML private TextField fieldNazwisko;
    @FXML private TextField fieldEmail;
    @FXML private TextField fieldTelefon;
    @FXML private DatePicker fieldDataUr;
    @FXML private Button btnSave;
    @FXML private Label formError;
    @FXML private Label draftHint;

    private static final Gson GSON = Repository.createGson();
    private boolean editMode = false;
    private Czlonek czlonek;

    @FXML
    public void initialize() {
        fieldDataUr.setValue(LocalDate.now());
        Platform.runLater(this::maybeLoadDraft);
    }

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
        if (fieldImie.getText().isBlank() || fieldNazwisko.getText().isBlank()) {
            showError("Wypełnij wszystkie wymagane pola.");
            return;
        }
        hideError();

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
        DraftStore.clearDraft(draftKey());
        closeDialog();
    }

    @FXML private void onCancel()      { closeDialog(); }

    @FXML
    private void onZachowajWersjeRobocza() {
        JsonObject obj = new JsonObject();
        obj.addProperty("imie", fieldImie.getText());
        obj.addProperty("nazwisko", fieldNazwisko.getText());
        obj.addProperty("email", fieldEmail.getText());
        obj.addProperty("telefon", fieldTelefon.getText());
        DraftStore.saveDraft(draftKey(), obj);
        draftHint.setText("Zapisano wersję roboczą.");
        draftHint.setVisible(true);
        draftHint.setManaged(true);
    }

    private String draftKey() {
        return editMode && czlonek != null ? "czlonek_" + czlonek.getId() : "czlonek_new";
    }

    private void maybeLoadDraft() {
        DraftStore.loadDraft(draftKey()).ifPresent(obj -> {
            if (obj.has("imie")) fieldImie.setText(obj.get("imie").getAsString());
            if (obj.has("nazwisko")) fieldNazwisko.setText(obj.get("nazwisko").getAsString());
            if (obj.has("email")) fieldEmail.setText(obj.get("email").getAsString());
            if (obj.has("telefon")) fieldTelefon.setText(obj.get("telefon").getAsString());
            draftHint.setText("Wczytano zapisaną wersję roboczą.");
            draftHint.setVisible(true);
            draftHint.setManaged(true);
        });
    }

    private void showError(String msg) { formError.setText(msg); formError.setVisible(true); }
    private void hideError() { formError.setVisible(false); }

    private void closeDialog() {
        ((Stage) fieldImie.getScene().getWindow()).close();
    }
}
