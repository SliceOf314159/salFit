package com.salfit.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.salfit.model.Czlonek;
import com.salfit.model.Sala;
import com.salfit.model.Trener;
import com.salfit.model.Zajecia;
import com.salfit.repository.CzlonekRepository;
import com.salfit.repository.GrafikRepository;
import com.salfit.repository.Repository;
import com.salfit.repository.SalaRepository;
import com.salfit.repository.TrenerRepository;
import com.salfit.util.DraftStore;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    @FXML private ComboBox<Czlonek> fieldNowyUczestnik;
    @FXML private ListView<Czlonek> uczestnicyList;
    @FXML private Label formError;
    @FXML private Label draftHint;

    private static final Gson GSON = Repository.createGson();
    private boolean editMode = false;
    private Zajecia zajecia;
    private final List<String> currentUczestnicy = new ArrayList<>();
    private final List<String> currentPotwierdzeni = new ArrayList<>();
    private List<Czlonek> allAvailable = new ArrayList<>();

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

        fieldData.setValue(LocalDate.now());

        fieldLimit.setText("20");
        fieldCzas.setText("60");

        fieldNowyUczestnik.setConverter(new StringConverter<>() {
            @Override public String toString(Czlonek c) { return c != null ? c.getImieNazwisko() : ""; }
            @Override public Czlonek fromString(String s) { return null; }
        });
        fieldNowyUczestnik.getEditor().textProperty().addListener((obs, old, text) -> {
            if (fieldNowyUczestnik.getValue() != null
                    && fieldNowyUczestnik.getValue().getImieNazwisko().equals(text)) return;
            String filter = text == null ? "" : text.toLowerCase();
            List<Czlonek> filtered = allAvailable.stream()
                    .filter(c -> c.getImieNazwisko().toLowerCase().contains(filter))
                    .collect(Collectors.toList());
            fieldNowyUczestnik.setItems(FXCollections.observableArrayList(filtered));
            if (!fieldNowyUczestnik.isShowing() && !filtered.isEmpty()) fieldNowyUczestnik.show();
        });
        fieldNowyUczestnik.valueProperty().addListener((obs, old, val) -> {
            if (val != null) onDodajUczestnika();
        });
        uczestnicyList.setCellFactory(lv -> new ListCell<>() {
            private final Label lblNazwa = new Label();
            private final Region spacer = new Region();
            private final CheckBox cbPotwierdzony = new CheckBox("Potwierdzony");
            private final Button btnUsun = new Button("Usuń");
            private final HBox box = new HBox(10, lblNazwa, spacer, cbPotwierdzony, btnUsun);
            {
                HBox.setHgrow(spacer, Priority.ALWAYS);
                box.setAlignment(Pos.CENTER_LEFT);
                btnUsun.getStyleClass().addAll("btn", "btn-ghost", "btn-sm");
                btnUsun.setOnAction(e -> {
                    Czlonek c = getItem();
                    if (c == null) return;
                    currentUczestnicy.remove(c.getId());
                    currentPotwierdzeni.remove(c.getId());
                    refreshUczestnicyUI();
                });
                cbPotwierdzony.setOnAction(e -> {
                    Czlonek c = getItem();
                    if (c == null) return;
                    if (cbPotwierdzony.isSelected()) {
                        if (!currentPotwierdzeni.contains(c.getId())) currentPotwierdzeni.add(c.getId());
                    } else {
                        currentPotwierdzeni.remove(c.getId());
                    }
                });
            }
            @Override
            protected void updateItem(Czlonek item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setGraphic(null); return; }
                lblNazwa.setText(item.getImieNazwisko());
                cbPotwierdzony.setSelected(currentPotwierdzeni.contains(item.getId()));
                setGraphic(box);
            }
        });
        refreshUczestnicyUI();
        Platform.runLater(this::maybeLoadDraft);
    }

    private void refreshUczestnicyUI() {
        List<Czlonek> enrolled = new ArrayList<>();
        for (String id : currentUczestnicy) {
            CzlonekRepository.getInstance().findById(id).ifPresent(enrolled::add);
        }
        uczestnicyList.setItems(FXCollections.observableArrayList(enrolled));

        allAvailable = CzlonekRepository.getInstance().findAll().stream()
                .filter(c -> !currentUczestnicy.contains(c.getId()))
                .collect(Collectors.toList());
        fieldNowyUczestnik.setItems(FXCollections.observableArrayList(allAvailable));
    }

    @FXML
    private void onDodajUczestnika() {
        Czlonek c = fieldNowyUczestnik.getValue();
        if (c == null) return;
        if (!currentUczestnicy.contains(c.getId())) {
            currentUczestnicy.add(c.getId());
        }
        fieldNowyUczestnik.setValue(null);
        refreshUczestnicyUI();
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
        currentUczestnicy.clear();
        currentUczestnicy.addAll(z.getUczestnicyIds());
        currentPotwierdzeni.clear();
        currentPotwierdzeni.addAll(z.getPotwierdzeniUczestnicy());
        refreshUczestnicyUI();
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
        if (fieldNazwa.getText().isBlank()
                || fieldTrener.getValue() == null
                || fieldSala.getValue() == null
                || fieldData.getValue() == null) {
            showError("Wypełnij wszystkie wymagane pola.");
            return;
        }
        hideError();

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
        obj.add("uczestnicyIds", GSON.toJsonTree(currentUczestnicy));
        obj.add("potwierdzeniUczestnicy", GSON.toJsonTree(currentPotwierdzeni));

        Zajecia z = GSON.fromJson(obj, Zajecia.class);
        if (editMode) {
            GrafikRepository.getInstance().update(z);
        } else {
            GrafikRepository.getInstance().save(z);
        }
        DraftStore.clearDraft(draftKey());
        closeDialog();
    }

    @FXML private void onCancel() { closeDialog(); }

    @FXML
    private void onZachowajWersjeRobocza() {
        JsonObject obj = new JsonObject();
        obj.addProperty("nazwa", fieldNazwa.getText());
        if (fieldRodzaj.getValue() != null) obj.addProperty("rodzaj", fieldRodzaj.getValue());
        if (fieldTrener.getValue() != null) obj.addProperty("trenerId", fieldTrener.getValue().getId());
        if (fieldSala.getValue() != null) obj.addProperty("salaId", fieldSala.getValue().getId());
        if (fieldData.getValue() != null) obj.addProperty("data", fieldData.getValue().toString());
        if (fieldGodzina.getValue() != null) obj.addProperty("godzina", fieldGodzina.getValue());
        obj.addProperty("limit", fieldLimit.getText());
        obj.addProperty("czas", fieldCzas.getText());
        obj.addProperty("opis", fieldOpis.getText());
        obj.addProperty("wymagania", fieldWymagania.getText());
        DraftStore.saveDraft(draftKey(), obj);
        draftHint.setText("Zapisano wersję roboczą.");
        draftHint.setVisible(true);
        draftHint.setManaged(true);
    }

    private String draftKey() {
        return editMode && zajecia != null ? "zajecia_" + zajecia.getId() : "zajecia_new";
    }

    private void maybeLoadDraft() {
        DraftStore.loadDraft(draftKey()).ifPresent(obj -> {
            if (obj.has("nazwa")) fieldNazwa.setText(obj.get("nazwa").getAsString());
            if (obj.has("rodzaj")) fieldRodzaj.setValue(obj.get("rodzaj").getAsString());
            if (obj.has("trenerId")) TrenerRepository.getInstance()
                    .findById(obj.get("trenerId").getAsString()).ifPresent(fieldTrener::setValue);
            if (obj.has("salaId")) SalaRepository.getInstance()
                    .findById(obj.get("salaId").getAsString()).ifPresent(fieldSala::setValue);
            if (obj.has("data")) fieldData.setValue(LocalDate.parse(obj.get("data").getAsString()));
            if (obj.has("godzina")) fieldGodzina.setValue(obj.get("godzina").getAsString());
            if (obj.has("limit")) fieldLimit.setText(obj.get("limit").getAsString());
            if (obj.has("czas")) fieldCzas.setText(obj.get("czas").getAsString());
            if (obj.has("opis")) fieldOpis.setText(obj.get("opis").getAsString());
            if (obj.has("wymagania")) fieldWymagania.setText(obj.get("wymagania").getAsString());
            draftHint.setText("Wczytano zapisaną wersję roboczą.");
            draftHint.setVisible(true);
            draftHint.setManaged(true);
        });
    }

    private void showError(String msg) { formError.setText(msg); formError.setVisible(true); }
    private void hideError() { formError.setVisible(false); }

    private int parseSafe(String text, int fallback) {
        try { return Integer.parseInt(text.trim()); }
        catch (NumberFormatException e) { return fallback; }
    }

    private void closeDialog() {
        ((Stage) fieldNazwa.getScene().getWindow()).close();
    }
}
