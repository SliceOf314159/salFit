package com.salfit.controllers;

import com.salfit.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

// Kontroler ekranu zmiany hasla
public class ChangePasswordController {

    @FXML private PasswordField currentPw;
    @FXML private PasswordField newPw;
    @FXML private PasswordField confirmPw;
    @FXML private Label errorLabel;
    @FXML private Label strengthLabel;
    @FXML private Pane strengthFill; // mniejszy pasek ktory pokazuje sile (rosnie/maleje)
    @FXML private VBox changeForm;
    @FXML private VBox changeSuccess;

    // tablice indeksowane "punktami sily" hasla (0 = najslabsze, 4 = najmocniejsze)
    private static final String[] LABELS = {"—", "Słabe", "Średnie", "Dobre", "Silne"};
    private static final String[] COLORS = {"transparent", "#d03b3b", "#e08c2c", "#2ab5a0", "#1e8a5e"};
    private static final double[] WIDTHS = {0, 0.25, 0.50, 0.75, 1.0}; // ile % paska wypelnic

    // wywolywane przy kazdej zmianie tekstu w polu "nowe haslo" - liczy aktualna sile hasla
    @FXML
    private void updateStrength() {
        String val = newPw.getText();
        int score = 0;
        // kazdy ponizszy warunek dodaje 1 punkt do "score" - im wiecej warunkow spelnionych, tym mocniejsze haslo
        if (val.length() >= 8) score++;                                   // dlugosc min 8 znakow
        if (val.matches(".*[a-z].*") && val.matches(".*[A-Z].*")) score++; // ma male i wielkie litery
        if (val.matches(".*\\d.*")) score++;                              // ma cyfre
        if (val.matches(".*[^a-zA-Z0-9].*")) score++;                     // ma znak specjalny

        double trackWidth = strengthFill.getParent() != null
                ? strengthFill.getParent().getBoundsInLocal().getWidth() : 316;
        // ustawiamy szerokosc wewnetrznego paska proporcjonalnie do score
        strengthFill.setPrefWidth(trackWidth * WIDTHS[score]);
        strengthFill.setMaxWidth(trackWidth * WIDTHS[score]);
        // ustawiamy kolor paska zaleznie od score (czerwony -> zielony)
        strengthFill.setStyle(String.format(
                "-fx-background-color: %s; -fx-background-radius: 2; -fx-pref-height: 4px; -fx-max-height: 4px;",
                COLORS[score]));
        strengthLabel.setText("Siła hasła: " + LABELS[score]);
    }

    // wysyla formularz zmiany hasla
    @FXML
    private void submitChange() {
        String current = currentPw.getText().trim();
        String np      = newPw.getText();
        String confirm = confirmPw.getText();

        hideError();
        // kolejne walidacje - kazda zwraca od razu jak cos jest nie tak
        if (current.isEmpty()) { showError("Podaj bieżące hasło."); return; }
        if (np.length() < 8)   { showError("Nowe hasło musi mieć co najmniej 8 znaków."); return; }
        if (!np.equals(confirm)){ showError("Nowe hasło i jego potwierdzenie nie są zgodne."); return; }

        // UWAGA: tutaj nie ma faktycznego zapisu nowego hasla do repozytorium
        changeForm.setVisible(false);
        changeForm.setManaged(false);
        changeSuccess.setVisible(true);
        changeSuccess.setManaged(true);
        hideError();
    }

    @FXML
    private void goLogin() {
        SceneManager.getInstance().showLogin();
    }


    private void showError(String msg) {
        errorLabel.setText(msg);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }

    private void hideError() {
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
    }
}
