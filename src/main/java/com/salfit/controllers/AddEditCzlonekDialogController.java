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

// Kontroler dialogu dodania/edycji czlonka
public class AddEditCzlonekDialogController {

    @FXML private TextField fieldImie;
    @FXML private TextField fieldNazwisko;
    @FXML private TextField fieldEmail;
    @FXML private TextField fieldTelefon;
    @FXML private DatePicker fieldDataUr;
    @FXML private Button btnSave;
    @FXML private Label formError;
    @FXML private Label draftHint; // mala podpiska informujaca o wczytaniu/zapisaniu wersji roboczej

    private static final Gson GSON = Repository.createGson();
    private boolean editMode = false; // false = dodajemy nowego, true = edytujemy istniejacego
    private Czlonek czlonek;

    @FXML
    public void initialize() {
        fieldDataUr.setValue(LocalDate.now()); // domyslna data urodzenia to "dzis" (user i tak ja zmieni)
        // Platform.runLater - odkladamy wczytanie draftu na "po" pelnej inicjalizacji widoku,
        // zeby na pewno wszystkie pola FXML byly juz gotowe (zaladowane) zanim w nich cos ustawimy
        Platform.runLater(this::maybeLoadDraft);
    }

    // wywolywane z zewnatrz (np z CzlonekController) gdy otwieramy dialog w trybie EDYCJI -
    // wypelniamy formularz danymi istniejacego czlonka
    public void setCzlonek(Czlonek c) {
        this.czlonek = c;
        fieldImie.setText(c.getImie());
        fieldNazwisko.setText(c.getNazwisko());
        fieldEmail.setText(c.getEmail());
        fieldTelefon.setText(c.getTelefon());
        if (c.getDataUrodzenia() != null) fieldDataUr.setValue(c.getDataUrodzenia());
    }

    // wlacza/wylacza tryb edycji - zmienia tekst na przycisku zapisu
    public void setEditMode(boolean edit) {
        editMode = edit;
        btnSave.setText(edit ? "Zapisz zmiany" : "Zapisz");
    }

    // glowna metoda zapisu formularza
    @FXML
    private void onSave() {
        // prosta walidacja - imie i nazwisko sa wymagane
        if (fieldImie.getText().isBlank() || fieldNazwisko.getText().isBlank()) {
            showError("Wypełnij wszystkie wymagane pola.");
            return;
        }
        hideError();

        // budujemy obiekt JSON z wartosci pol formularza
        JsonObject obj = new JsonObject();
        // w trybie edycji musimy dodac id, zeby update() wiedzial KTOREGO czlonka zaktualizowac
        if (editMode && czlonek != null) obj.addProperty("id", czlonek.getId());
        obj.addProperty("imie",     fieldImie.getText().trim());
        obj.addProperty("nazwisko", fieldNazwisko.getText().trim());
        obj.addProperty("email",    fieldEmail.getText().trim());
        obj.addProperty("telefon",  fieldTelefon.getText().trim());
        if (fieldDataUr.getValue() != null)
            obj.addProperty("dataUrodzenia", fieldDataUr.getValue().toString());

        // GSON sam zamieni ten JsonObject na obiekt Czlonek (dopasowuje pola po nazwach)
        Czlonek c = GSON.fromJson(obj, Czlonek.class);
        if (editMode) {
            CzlonekRepository.getInstance().update(c);
        } else {
            CzlonekRepository.getInstance().save(c); // save() sam wygeneruje nowe id
        }
        DraftStore.clearDraft(draftKey()); // po faktycznym zapisaniu, usuwamy ewentualny draft (juz niepotrzebny)
        closeDialog();
    }

    @FXML private void onCancel()      { closeDialog(); }

    // zapisuje "wersje robocza" formularza - przydatne jak user nie skonczyl wypelniac i zamknal dialog
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

    // klucz draftu zalezny od tego czy edytujemy istniejacego czlonka (po id) czy tworzymy nowego (stale "czlonek_new")
    private String draftKey() {
        return editMode && czlonek != null ? "czlonek_" + czlonek.getId() : "czlonek_new";
    }

    // przy otwarciu dialogu sprawdzamy czy jest jakis zapisany draft dla tego klucza i jesli tak - wczytujemy go
    private void maybeLoadDraft() {
        DraftStore.loadDraft(draftKey()).ifPresent(obj -> {
            // sprawdzamy has() dla kazdego pola, bo draft mogl byc zapisany czesciowo (nie wszystkie pola wypelnione)
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

    // zamyka dialog - pobiera Stage z aktualnej sceny (musimy isc przez "scene" jakiegokolwiek pola FXML)
    private void closeDialog() {
        ((Stage) fieldImie.getScene().getWindow()).close();
    }
}
