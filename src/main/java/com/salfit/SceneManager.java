package com.salfit;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

// Klasa do zarzadzania "scenami" czyli widokami w aplikacj.
// Singleton - mamy tylko jedna instancje w calym programie (zeby nie mnozyc okien/stage'y).
public class SceneManager {

    private static SceneManager instance;
    private Stage stage; // glowne okno aplikacji, trzymane tutaj zeby moc je podmieniac z kazdego miejsca

    // konstruktor prywatny
    private SceneManager() {}

    // wzorzec singleton
    public static SceneManager getInstance() {
        if (instance == null) {
            instance = new SceneManager();
        }
        return instance;
    }

    // metoda startowa, wywolywana raz na poczatku z Main.java
    public void init(Stage stage) {
        this.stage = stage;
        stage.setTitle("SalFit");
        // ustawiamy jakies sensowne rozmiary okna na start
        stage.setWidth(1280);
        stage.setHeight(800);
        // i minimalne rozmiary, zeby ktos nie zmniejszyl okna do 10x10 pikseli
        stage.setMinWidth(900);
        stage.setMinHeight(600);
    }

    public Stage getStage() { return stage; }

    // same gettery-helpery do przelaczania widokow, kazdy odpala inny plik fxml
    public void showLogin()          { loadScene("/views/LoginView.fxml"); }
    public void showForgotPassword() { loadScene("/views/ForgotPasswordView.fxml"); }
    public void showChangePassword() { loadScene("/views/ChangePasswordView.fxml"); }
    public void showAdminShell()     { loadScene("/views/MainView.fxml"); } // panel admina
    public void showTrainerShell()   { loadScene("/views/TrainerView.fxml"); } // panel trenera

    // wczytujemy plik fxml i wstawiamy go do okna
    private void loadScene(String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            // jak jeszcze nie ma sceny (czyli pierwsze odpalenie) to tworzymy nowa
            if (stage.getScene() == null) {
                stage.setScene(new Scene(root));
            } else {
                // a jak juz jest scena, to tylko podmieniamy "root" - dzieki temu
                // okno nie miga i nie zmienia rozmiaru przy kazdym przejsciu
                stage.getScene().setRoot(root);
            }
            stage.show();
        } catch (IOException e) {
            // jak plik fxml nie istnieje albo jest zepsuty - wypisujemy w konsoli co sie stalo
            System.err.println("Failed to load view: " + fxmlPath);
            e.printStackTrace();
        }
    }
}
