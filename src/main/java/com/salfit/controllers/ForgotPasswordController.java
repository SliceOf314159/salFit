package com.salfit.controllers;

import com.salfit.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class ForgotPasswordController {

    @FXML private TextField emailField;
    @FXML private VBox resetForm;
    @FXML private VBox resetSuccess;

    @FXML
    private void submitReset() {
        String email = emailField.getText().trim();
        if (email.isEmpty()) {
            emailField.requestFocus();
            return;
        }
        resetForm.setVisible(false);
        resetForm.setManaged(false);
        resetSuccess.setVisible(true);
        resetSuccess.setManaged(true);
    }

    @FXML
    private void goLogin() {
        SceneManager.getInstance().showLogin();
    }
}
