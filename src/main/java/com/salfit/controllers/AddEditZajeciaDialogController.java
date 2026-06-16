package com.salfit.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.salfit.model.Sala;
import com.salfit.model.Trener;
import com.salfit.model.Zajecia;
import com.salfit.repository.GrafikRepository;
import com.salfit.repository.Repository;
import com.salfit.repository.SalaRepository;
import com.salfit.repository.TrenerRepository;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class AddEditZajeciaDialogController {

    @FXML private TextField fieldNazwa;
    @FXML private ComboBox<String> fieldRodzaj;
    @FXML private ComboBox<Trener> fieldTrener;
    @FXML private ComboBox<Sala> fieldSala;
    @FXML private ComboBox<String> fieldPoziom;
    @FXML private DatePicker fieldData;
    @FXML private ComboBox<String> fieldGodzina;
    @FXML private TextField fieldLimit;
    @FXML private TextField fieldCzas;
    @FXML private TextArea fieldOpis;
    @FXML private TextArea fieldWymagania;

    private static final Gson GSON = Repository.createGson();
    private boolean editMode = false;
    private Zajecia zajecia;

    private static final String[] HOURS = {
        "06:00", "07:00", "08:00", "09:00", "10:00", "11:00",
        "12:00", "13:00", "14:00", "15:00", "16:00", "17:00",
        "18:00", "19:00", "20:00", "21:00"
    };

    @FXML
    public void initialize() {
        fieldRodzaj.setItems(FXCollections.observableArrayList(
                "Yoga", "Pilates", "CrossFit", "HIIT", "Stretching", "Spinning", "Inne"));

        fieldTrener.setItems(FXCollections.observableArrayList(
                TrenerRepository.getInstance().findAktywni()));
        fieldTrener.setConverter(new StringConverter<>() {
            @Override public String toString(Trener t) { return t != null ? t.getImieNazwisko() : ""; }
            @Override public Trener fromString(String s) { return null; }
        });

        fieldSala.setItems(FXCollections.observableArrayList(
                SalaRepository.getInstance().findAll()));
        fieldSala.setConverter(new StringConverter<>() {
            @Override public String toString(Sala s) { return s != null ? s.getNazwa() : ""; }
            @Override public Sala fromString(String s) { return null; }
        });

        fieldPoziom.setItems(FXCollections.observableArrayList(
                "★★★★★", "★★★★☆", "★★★☆☆", "★★☆☆☆", "★☆☆☆☆"));
        fieldPoziom.getSelectionModel().select(2);

        fieldGodzina.setItems(FXCollections.observableArrayList(HOURS));
        fieldGodzina.getSelectionModel().select("08:00");

        fieldLimit.setText("20");
        fieldCzas.setText("60");
    }

    public void setZajecia(Zajecia z) {
        this.zajecia = z;
        fieldNazwa.setText(z.getNazwa());
        if (z.getTermin() != null) {
            fieldData.setValue(z.getTermin().toLocalDate());
            String hour = String.format("%02d:00", z.getTermin().getHour());
            fieldGodzina.setValue(hour);
        }
        fieldLimit.setText(String.valueOf(z.getLimitUczestnikow()));
        fieldCzas.setText(String.valueOf(z.getCzasTrwaniaMinut()));
        TrenerRepository.getInstance().findById(z.getTrenerId())
                .ifPresent(fieldTrener::setValue);
        SalaRepository.getInstance().findById(z.getSalaId())
                .ifPresent(fieldSala::setValue);
    }

    public void setEditMode(boolean edit) {
        editMode = edit;
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

    @FXML
    private void onSave() {
        if (fieldNazwa.getText().isBlank()) return;
        if (fieldTrener.getValue() == null || fieldSala.getValue() == null) return;
        if (fieldData.getValue() == null) return;

        String godzinaStr = fieldGodzina.getValue() != null ? fieldGodzina.getValue() : "08:00";
        LocalTime time = LocalTime.parse(godzinaStr);
        LocalDateTime termin = LocalDateTime.of(fieldData.getValue(), time);

        int limit = parseSafe(fieldLimit.getText(), 20);
        int czas  = parseSafe(fieldCzas.getText(), 60);

        JsonObject obj = new JsonObject();
        if (editMode && zajecia != null) obj.addProperty("id", zajecia.getId());
        obj.addProperty("nazwa",            fieldNazwa.getText().trim());
        obj.addProperty("trenerId",         fieldTrener.getValue().getId());
        obj.addProperty("salaId",           fieldSala.getValue().getId());
        obj.addProperty("termin",           termin.toString());
        obj.addProperty("czasTrwaniaMinut", czas);
        obj.addProperty("limitUczestnikow", limit);
        obj.add("uczestnicyIds", new JsonArray());

        Zajecia z = GSON.fromJson(obj, Zajecia.class);
        if (editMode) {
            GrafikRepository.getInstance().update(z);
        } else {
            GrafikRepository.getInstance().save(z);
        }
        closeDialog();
    }

    @FXML private void onCancel() { closeDialog(); }

    private int parseSafe(String text, int fallback) {
        try { return Integer.parseInt(text.trim()); }
        catch (NumberFormatException e) { return fallback; }
    }

    private void closeDialog() {
        ((Stage) fieldNazwa.getScene().getWindow()).close();
    }
}
