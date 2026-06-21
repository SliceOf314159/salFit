package com.salfit.controllers;

import com.salfit.SceneManager;
import javafx.fxml.FXML;

// prosty kontroler ekranu "zapomnialem hasla" - na razie tylko umozliwia
// powrot do ekranu logowania
public class ForgotPasswordController {

    @FXML
    private void goLogin() {
        SceneManager.getInstance().showLogin();
    }
}
