package com.salfit.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.salfit.model.Trener;
import com.salfit.repository.Repository;
import com.salfit.repository.TrenerRepository;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class AddEditTrenerDialogController {

    @FXML private TextField fieldImie;
    @FXML private TextField fieldNazwisko;
    @FXML private TextField fieldEmail;
    @FXML private TextField fieldTelefon;
    @FXML private ComboBox<String> fieldPoziom;
    @FXML private CheckBox cbGrupowy;
    @FXML private CheckBox cbPersonalny;
    @FXML private CheckBox cbOnline;
    @FXML private VBox passwordSection;
    @FXML private PasswordField fieldHaslo;
    @FXML private Button btnUsun;
    @FXML private Button btnSave;

    private static final Gson GSON = Repository.createGson();
    private boolean editMode = false;
    private Trener trener;

    @FXML
    public void initialize() {
        fieldPoziom.setItems(FXCollections.observableArrayList(
                "★★★★★ Ekspert",
                "★★★★☆ Zaawansowany",
                "★★★☆☆ Średniozaawansowany",
                "★★☆☆☆ Podstawowy",
                "★☆☆☆☆ Początkujący"));
        fieldPoziom.getSelectionModel().select(2);
    }

    public void setTrener(Trener t) {
        this.trener = t;
        fieldImie.setText(t.getImie());
        fieldNazwisko.setText(t.getNazwisko());
        fieldEmail.setText(t.getEmail());
        fieldTelefon.setText(t.getTelefon());
        String spec = t.getSpecjalizacja() != null ? t.getSpecjalizacja() : "";
        cbGrupowy.setSelected(spec.contains("grupowy"));
        cbPersonalny.setSelected(spec.contains("personalny"));
        cbOnline.setSelected(spec.contains("online"));
        if (t.getPoziom() != null) fieldPoziom.setValue(t.getPoziom());
    }

    public void setEditMode(boolean edit) {
        this.editMode = edit;
        passwordSection.setVisible(!edit);
        passwordSection.setManaged(!edit);
        btnUsun.setVisible(edit);
        btnUsun.setManaged(edit);
        btnSave.setText(edit ? "Zapisz zmiany" : "Zapisz");
    }

    @FXML
    private void onSave() {
        if (!validate()) return;

        List<String> specs = new ArrayList<>();
        if (cbGrupowy.isSelected())    specs.add("grupowy");
        if (cbPersonalny.isSelected()) specs.add("personalny");
        if (cbOnline.isSelected())     specs.add("online");
        String specjalizacja = specs.isEmpty() ? "—" : String.join(", ", specs);

        JsonObject obj = new JsonObject();
        if (editMode && trener != null) obj.addProperty("id", trener.getId());
        obj.addProperty("imie",          fieldImie.getText().trim());
        obj.addProperty("nazwisko",      fieldNazwisko.getText().trim());
        obj.addProperty("email",         fieldEmail.getText().trim());
        obj.addProperty("telefon",       fieldTelefon.getText().trim());
        obj.addProperty("specjalizacja", specjalizacja);
        obj.addProperty("poziom",        fieldPoziom.getValue());
        obj.addProperty("aktywny",       true);
        if (!editMode) {
            obj.addProperty("haslo", fieldHaslo.getText());
        } else if (trener != null && trener.getHaslo() != null) {
            obj.addProperty("haslo", trener.getHaslo());
        }

        Trener t = GSON.fromJson(obj, Trener.class);
        if (editMode) {
            TrenerRepository.getInstance().update(t);
        } else {
            TrenerRepository.getInstance().save(t);
        }
        closeDialog();
    }

    @FXML private void onUsun()       { if (trener != null) TrenerRepository.getInstance().delete(trener.getId()); closeDialog(); }
    @FXML private void onCancel()     { closeDialog(); }
    @FXML private void onChoosePhoto(){ /* photo chooser stub */ }

    private boolean validate() {
        return !fieldImie.getText().isBlank()
                && !fieldNazwisko.getText().isBlank()
                && !fieldEmail.getText().isBlank()
                && !fieldTelefon.getText().isBlank();
    }

    private void closeDialog() {
        ((Stage) fieldImie.getScene().getWindow()).close();
    }
}
