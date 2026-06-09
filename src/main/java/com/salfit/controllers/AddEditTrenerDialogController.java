package com.salfit.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;

public class AddEditTrenerDialogController {

    @FXML private TextField fieldImie;
    @FXML private TextField fieldNazwisko;
    @FXML private TextField fieldEmail;
    @FXML private TextField fieldTelefon;
    @FXML private DatePicker fieldDataUr;
    @FXML private ComboBox<String> fieldPlec;
    @FXML private ComboBox<String> fieldPoziom;
    @FXML private TextField fieldSocial;
    @FXML private TextArea fieldBio;
    @FXML private PasswordField fieldHaslo;
    @FXML private CheckBox cbGrupowy;
    @FXML private CheckBox cbPersonalny;
    @FXML private CheckBox cbOnline;
    @FXML private VBox passwordSection;
    @FXML private Button btnUsun;
    @FXML private Button btnSave;

    private boolean editMode = false;

    @FXML
    public void initialize() {
        fieldPlec.setItems(FXCollections.observableArrayList(
                "Kobieta", "Mężczyzna", "Inna"));
        fieldPoziom.setItems(FXCollections.observableArrayList(
                "★★★★★ Ekspert",
                "★★★★☆ Zaawansowany",
                "★★★☆☆ Średniozaawansowany",
                "★★☆☆☆ Podstawowy",
                "★☆☆☆☆ Początkujący"));
        fieldPoziom.getSelectionModel().select(2);
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
        closeDialog();
    }

    @FXML
    private void onUsun() {
        closeDialog();
    }

    @FXML
    private void onCancel() {
        closeDialog();
    }

    @FXML
    private void onChoosePhoto() {
        /* Open file chooser for avatar image. */
    }

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
