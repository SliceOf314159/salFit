package com.salfit.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.salfit.model.Sala;
import com.salfit.model.StatusSali;
import com.salfit.repository.Repository;
import com.salfit.repository.SalaRepository;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class AddEditSalaDialogController {

    @FXML private TextField fieldNazwa;
    @FXML private TextField fieldPojemnosc;
    @FXML private TextField fieldPrzerwa;
    @FXML private ComboBox<String> fieldStatus;
    @FXML private Button btnUsun;
    @FXML private Button btnSave;

    private static final Gson GSON = Repository.createGson();
    private boolean editMode = false;
    private Sala sala;

    @FXML
    public void initialize() {
        fieldStatus.setItems(FXCollections.observableArrayList("Dostępna", "Zajęta", "W remoncie"));
        fieldStatus.getSelectionModel().selectFirst();
        fieldPojemnosc.setText("20");
        fieldPrzerwa.setText("10");
    }

    public void setSala(Sala s) {
        this.sala = s;
        fieldNazwa.setText(s.getNazwa());
        fieldPojemnosc.setText(String.valueOf(s.getPojemnosc()));
        fieldPrzerwa.setText(String.valueOf(s.getMinPrzerwaMinut()));
        String statusLabel = switch (s.getStatus()) {
            case DOSTEPNA   -> "Dostępna";
            case ZAJETA     -> "Zajęta";
            case W_REMONCIE -> "W remoncie";
        };
        fieldStatus.setValue(statusLabel);
    }

    public void setEditMode(boolean edit) {
        editMode = edit;
        btnUsun.setVisible(edit);
        btnUsun.setManaged(edit);
        btnSave.setText(edit ? "Zapisz zmiany" : "Dodaj salę");
    }

    @FXML private void stepUp()          { step(fieldPojemnosc,  1); }
    @FXML private void stepDown()        { step(fieldPojemnosc, -1); }
    @FXML private void stepPrzerwaUp()   { step(fieldPrzerwa,    5); }
    @FXML private void stepPrzerwaDown() { step(fieldPrzerwa,   -5); }

    private void step(TextField field, int delta) {
        try {
            int val = Integer.parseInt(field.getText().trim());
            field.setText(String.valueOf(Math.max(0, val + delta)));
        } catch (NumberFormatException ignored) {}
    }

    @FXML
    private void onSave() {
        if (fieldNazwa.getText().isBlank()) return;

        StatusSali status = switch (fieldStatus.getValue() != null ? fieldStatus.getValue() : "Dostępna") {
            case "Zajęta"     -> StatusSali.ZAJETA;
            case "W remoncie" -> StatusSali.W_REMONCIE;
            default           -> StatusSali.DOSTEPNA;
        };

        JsonObject obj = new JsonObject();
        if (editMode && sala != null) obj.addProperty("id", sala.getId());
        obj.addProperty("nazwa",           fieldNazwa.getText().trim());
        obj.addProperty("pojemnosc",       parseSafe(fieldPojemnosc.getText(), 10));
        obj.addProperty("minPrzerwaMinut", parseSafe(fieldPrzerwa.getText(), 10));
        obj.addProperty("status",          status.name());

        Sala s = GSON.fromJson(obj, Sala.class);
        if (editMode) SalaRepository.getInstance().update(s);
        else          SalaRepository.getInstance().save(s);
        closeDialog();
    }

    @FXML private void onUsun()   { if (sala != null) SalaRepository.getInstance().delete(sala.getId()); closeDialog(); }
    @FXML private void onCancel() { closeDialog(); }

    private int parseSafe(String text, int fallback) {
        try { return Integer.parseInt(text.trim()); }
        catch (NumberFormatException e) { return fallback; }
    }

    private void closeDialog() {
        ((Stage) fieldNazwa.getScene().getWindow()).close();
    }
}
