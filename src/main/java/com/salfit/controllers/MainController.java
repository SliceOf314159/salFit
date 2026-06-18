package com.salfit.controllers;

import com.salfit.SceneManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class MainController {

    @FXML private StackPane contentArea;
    @FXML private Label topbarDate;
    @FXML private Button btnTrenerzy;
    @FXML private Button btnSale;
    @FXML private Button btnGrafik;
    @FXML private Button btnCzlonkowie;
    @FXML private Button btnKarnety;
    @FXML private Button btnRaporty;

    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("d MMMM yyyy").withLocale(Locale.forLanguageTag("pl"));

    private static MainController instance;

    public static MainController getInstance() { return instance; }

    @FXML
    public void initialize() {
        instance = this;
        topbarDate.setText(LocalDate.now().format(DATE_FMT));
        showTrenerView();
    }

    @FXML public void showTrenerView()  { loadContent("/views/TrenerView.fxml",   btnTrenerzy);  }
    @FXML public void showSalaView()    { loadContent("/views/SalaView.fxml",     btnSale);      }
    @FXML public void showGrafikView()  { loadContent("/views/GrafikView.fxml",   btnGrafik);    }
    @FXML public void showCzlonekView() { loadContent("/views/CzlonekView.fxml",  btnCzlonkowie);}
    @FXML public void showKarnetView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/CzlonekView.fxml"));
            Node view = loader.load();
            loader.<CzlonekController>getController().showPassesTab();
            contentArea.getChildren().setAll(view);
            updateActiveButton(btnKarnety);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML public void showRaportView()  { loadContent("/views/RaportView.fxml",   btnRaporty);   }

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
        List<Button> all = List.of(btnTrenerzy, btnSale, btnGrafik,
                                   btnCzlonkowie, btnKarnety, btnRaporty);
        for (Button b : all) {
            b.getStyleClass().removeAll("sidebar-item-active");
            if (!b.getStyleClass().contains("sidebar-item")) {
                b.getStyleClass().add("sidebar-item");
            }
        }
        if (active != null) {
            active.getStyleClass().remove("sidebar-item");
            active.getStyleClass().add("sidebar-item-active");
        }
    }
}
