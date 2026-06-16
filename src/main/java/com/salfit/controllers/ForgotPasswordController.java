package com.salfit.controllers;

import com.salfit.SceneManager;
import javafx.fxml.FXML;

public class ForgotPasswordController {

    @FXML
    private void goLogin() {
        SceneManager.getInstance().showLogin();
    }
}
