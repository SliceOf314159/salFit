package com.salfit.controllers;

import com.salfit.SceneManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.util.List;

public class TrainerShellController {

    @FXML private StackPane contentArea;
    @FXML private Button btnMojeZajecia;
    @FXML private Button btnProfil;
    @FXML private Button btnAktywnosc;

    private static TrainerShellController instance;

    public static TrainerShellController getInstance() { return instance; }

    @FXML
    public void initialize() {
        instance = this;
        showGrafik();
    }

    @FXML public void showGrafik()    { loadContent("/views/trainer/TrainerGrafikView.fxml",    btnMojeZajecia); }
    @FXML public void showProfil()    { loadContent("/views/trainer/TrainerProfilView.fxml",    btnProfil);      }
    @FXML public void showAktywnosc() { loadContent("/views/trainer/TrainerAktywnoscView.fxml", btnAktywnosc);   }

    public void showUczestnicy() {
        loadContent("/views/trainer/TrainerUczestnicyView.fxml", btnMojeZajecia);
    }

    @FXML
    private void logout() {
        SceneManager.getInstance().showLogin();
    }

    @FXML
    private void goChangePassword() {
        SceneManager.getInstance().showChangePassword();
    }

    public void loadContent(String fxmlPath, Button activeBtn) {
        try {
            Node view = FXMLLoader.load(getClass().getResource(fxmlPath));
            contentArea.getChildren().setAll(view);
            updateActiveButton(activeBtn);
        } catch (IOException e) {
            System.err.println("Cannot load view: " + fxmlPath);
            e.printStackTrace();
        }
    }

    private void updateActiveButton(Button active) {
        List<Button> all = List.of(btnMojeZajecia, btnProfil, btnAktywnosc);
        for (Button b : all) {
            b.getStyleClass().removeAll("sidebar-item-active", "trainer");
            if (!b.getStyleClass().contains("sidebar-item")) {
                b.getStyleClass().add("sidebar-item");
            }
        }
        if (active != null) {
            active.getStyleClass().remove("sidebar-item");
            active.getStyleClass().addAll("sidebar-item-active", "trainer");
        }
    }
}
