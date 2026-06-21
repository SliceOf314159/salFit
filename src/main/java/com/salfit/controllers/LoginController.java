package com.salfit.controllers;

import com.salfit.SceneManager;
import com.salfit.SessionManager;
import com.salfit.repository.AdminRepository;
import com.salfit.repository.TrenerRepository;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

// Kontroler ekranu logowania - mamy tu DWA formularze na jednym widoku:
// jeden do logowania admina, drugi do logowania trenera. Kazdy ma swoja walidacje
public class LoginController {

    @FXML private TextField adminLogin;
    @FXML private PasswordField adminPassword;
    @FXML private Label adminError;

    @FXML private TextField trainerLogin;
    @FXML private PasswordField trainerPassword;
    @FXML private Label trainerError;

    // logowanie admina - sprawdzamy login i haslo w AdminRepository
    @FXML
    private void loginAdmin() {
        String login = adminLogin.getText().trim();
        String pass  = adminPassword.getText();
        // walidacja - czy pola nie sa puste
        if (login.isEmpty() || pass.isEmpty()) {
            showError(adminError, "Podaj login i hasło.");
            return;
        }
        var admin = AdminRepository.getInstance().findAll().stream()
                .filter(a -> login.equals(a.getLogin()) && pass.equals(a.getHaslo()))
                .findFirst();
        if (admin.isEmpty()) {
            showError(adminError, "Nieprawidłowy login lub hasło.");
            return;
        }
        hideError(adminError);
        // logowanie ok - przechodzimy do panelu admina
        SceneManager.getInstance().showAdminShell();
    }

    // logowanie trenera - tu logujemy sie po emailu, nie po loginie jak admin
    @FXML
    private void loginTrainer() {
        String email = trainerLogin.getText().trim();
        String pass  = trainerPassword.getText();
        if (email.isEmpty() || pass.isEmpty()) {
            showError(trainerError, "Podaj e-mail i hasło.");
            return;
        }
        // najpierw szukamy trenera o danym mailu (ignorujac wielkosc liter )
        var trener = TrenerRepository.getInstance().findAll().stream()
                .filter(t -> email.equalsIgnoreCase(t.getEmail()))
                .findFirst();
        if (trener.isEmpty()) {
            showError(trainerError, "Nie znaleziono trenera o podanym e-mailu.");
            return;
        }
        // dopiero teraz sprawdzamy haslo - dzieki temu mozemy dac inny komunikat bledu
        // (nie znaleziono maila VS zle haslo), co jest wygodniejsze dla usera
        if (!pass.equals(trener.get().getHaslo())) {
            showError(trainerError, "Nieprawidłowe hasło.");
            return;
        }
        // zapamietujemy w SessionManager ktory trener jest zalogowany - bedzie potrzebne
        // w calym panelu trenera (np do filtrowania jego wlasnych zajec)
        SessionManager.getInstance().setLoggedInTrenerId(trener.get().getId());
        hideError(trainerError);
        SceneManager.getInstance().showTrainerShell();
    }

    @FXML
    private void goForgotPassword() {
        SceneManager.getInstance().showForgotPassword();
    }

    // male helpery do pokazywania/chowania labelek z bledami, zeby nie powtarzac kodu w kazdej metodzie
    private void showError(Label lbl, String msg) { lbl.setText(msg); lbl.setVisible(true); }
    private void hideError(Label lbl)             { lbl.setVisible(false); }
}
