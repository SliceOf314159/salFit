package com.salfit.controllers;

import com.salfit.SceneManager;
import com.salfit.SessionManager;
import com.salfit.repository.AdminRepository;
import com.salfit.repository.TrenerRepository;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML private TextField adminLogin;
    @FXML private PasswordField adminPassword;
    @FXML private Label adminError;

    @FXML private TextField trainerLogin;
    @FXML private PasswordField trainerPassword;
    @FXML private Label trainerError;

    @FXML
    private void loginAdmin() {
        String login = adminLogin.getText().trim();
        String pass  = adminPassword.getText();
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
        SceneManager.getInstance().showAdminShell();
    }

    @FXML
    private void loginTrainer() {
        String email = trainerLogin.getText().trim();
        String pass  = trainerPassword.getText();
        if (email.isEmpty() || pass.isEmpty()) {
            showError(trainerError, "Podaj e-mail i hasło.");
            return;
        }
        var trener = TrenerRepository.getInstance().findAll().stream()
                .filter(t -> email.equalsIgnoreCase(t.getEmail()))
                .findFirst();
        if (trener.isEmpty()) {
            showError(trainerError, "Nie znaleziono trenera o podanym e-mailu.");
            return;
        }
        if (!pass.equals(trener.get().getHaslo())) {
            showError(trainerError, "Nieprawidłowe hasło.");
            return;
        }
        SessionManager.getInstance().setLoggedInTrenerId(trener.get().getId());
        hideError(trainerError);
        SceneManager.getInstance().showTrainerShell();
    }

    @FXML
    private void goForgotPassword() {
        SceneManager.getInstance().showForgotPassword();
    }

    private void showError(Label lbl, String msg) { lbl.setText(msg); lbl.setVisible(true); }
    private void hideError(Label lbl)             { lbl.setVisible(false); }
}
