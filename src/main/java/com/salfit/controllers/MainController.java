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

// Glowny kontroler  panelu admina
public class MainController {

    @FXML private StackPane contentArea; // aktualnie wybrany widok
    @FXML private Label topbarDate;
    @FXML private Button btnTrenerzy;
    @FXML private Button btnSale;
    @FXML private Button btnGrafik;
    @FXML private Button btnCzlonkowie;
    @FXML private Button btnKarnety;
    @FXML private Button btnRaporty;

    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("d MMMM yyyy").withLocale(Locale.forLanguageTag("pl"));

    // statyczna referencja do "siebie" - dzieki temu inne kontrolery (np dzieci wewnatrz contentArea)
    // moga sobie wziac instancje MainController i np kazac mu przeladowac widok
    private static MainController instance;

    public static MainController getInstance() { return instance; }

    @FXML
    public void initialize() {
        instance = this;
        topbarDate.setText(LocalDate.now().format(DATE_FMT));
        showTrenerView(); // domyslnie po wejsciu do panelu admina pokazujemy widok trenerow
    }

    // kazda z tych metod laduje inny widok fxml do contentArea i podswietla odpowiedni przycisk w menu
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

    // wylogowanie - wracamy do ekranu logowania
    @FXML
    private void logout() {
        SceneManager.getInstance().showLogin();
    }

    @FXML
    private void goChangePassword() {
        SceneManager.getInstance().showChangePassword();
    }

    // glowna metoda do podmiany widoku w contentArea - wczytuje fxml i ustawia go jako jedyne dziecko
    public void loadContent(String fxmlPath, Button activeBtn) {
        try {
            Node view = FXMLLoader.load(getClass().getResource(fxmlPath));
            // setAll - czysci stara liste dzieci i wstawia nowa
            contentArea.getChildren().setAll(view);
            updateActiveButton(activeBtn);
        } catch (IOException e) {
            System.err.println("Cannot load view: " + fxmlPath);
            e.printStackTrace();
        }
    }

    // ustawia styl "aktywny" tylko na klikniety przycisk menu, resztę zwraca do normalnego stylu
    private void updateActiveButton(Button active) {
        List<Button> all = List.of(btnTrenerzy, btnSale, btnGrafik,
                                   btnCzlonkowie, btnKarnety, btnRaporty);
        for (Button b : all) {
            // najpierw zdejmujemy klase "aktywny" z wszystkich
            b.getStyleClass().removeAll("sidebar-item-active");
            // i pilnujemy zeby kazdy mial przynajmniej zwykla klase "sidebar-item"
            if (!b.getStyleClass().contains("sidebar-item")) {
                b.getStyleClass().add("sidebar-item");
            }
        }
        if (active != null) {
            // a temu jednemu klikniete przyciskowi zamieniamy styl na "aktywny"
            active.getStyleClass().remove("sidebar-item");
            active.getStyleClass().add("sidebar-item-active");
        }
    }
}
