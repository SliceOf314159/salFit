package com.salfit;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class SceneManager {

    private static SceneManager instance;
    private Stage stage;

    private SceneManager() {}

    public static SceneManager getInstance() {
        if (instance == null) {
            instance = new SceneManager();
        }
        return instance;
    }

    public void init(Stage stage) {
        this.stage = stage;
        stage.setTitle("SalFit");
        stage.setWidth(1280);
        stage.setHeight(800);
        stage.setMinWidth(900);
        stage.setMinHeight(600);
    }

    public Stage getStage() { return stage; }

    public void showLogin()          { loadScene("/views/LoginView.fxml"); }
    public void showForgotPassword() { loadScene("/views/ForgotPasswordView.fxml"); }
    public void showChangePassword() { loadScene("/views/ChangePasswordView.fxml"); }
    public void showAdminShell()     { loadScene("/views/MainView.fxml"); }
    public void showTrainerShell()   { loadScene("/views/TrainerView.fxml"); }

    private void loadScene(String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            if (stage.getScene() == null) {
                stage.setScene(new Scene(root));
            } else {
                stage.getScene().setRoot(root);
            }
            stage.show();
        } catch (IOException e) {
            System.err.println("Failed to load view: " + fxmlPath);
            e.printStackTrace();
        }
    }
}
