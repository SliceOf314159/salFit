package com.salfit.controllers;

import com.salfit.SceneManager;
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
        hideError(adminError);
        SceneManager.getInstance().showAdminShell();
    }

    @FXML
    private void loginTrainer() {
        String login = trainerLogin.getText().trim();
        String pass  = trainerPassword.getText();
        if (login.isEmpty() || pass.isEmpty()) {
            showError(trainerError, "Podaj login i hasło.");
            return;
        }
        hideError(trainerError);
        SceneManager.getInstance().showTrainerShell();
    }

    @FXML
    private void goForgotPassword() {
        SceneManager.getInstance().showForgotPassword();
    }

    private void showError(Label lbl, String msg) {
        lbl.setText(msg);
        lbl.setVisible(true);
        lbl.setManaged(true);
    }

    private void hideError(Label lbl) {
        lbl.setVisible(false);
        lbl.setManaged(false);
    }
}
