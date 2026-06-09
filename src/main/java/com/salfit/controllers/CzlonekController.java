package com.salfit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class CzlonekController {

    @FXML private VBox membersTab;
    @FXML private VBox passesTab;

    @FXML private TextField searchMembers;
    @FXML private TableView<Object> czlonekTable;
    @FXML private TableColumn<Object, String> colMId;
    @FXML private TableColumn<Object, String> colMNazwa;
    @FXML private TableColumn<Object, String> colMEmail;
    @FXML private TableColumn<Object, String> colMTel;
    @FXML private TableColumn<Object, String> colMKarnet;
    @FXML private TableColumn<Object, String> colMAkcje;

    @FXML private TextField searchPasses;
    @FXML private TableView<Object> karnetTable;
    @FXML private TableColumn<Object, String> colKCzlonek;
    @FXML private TableColumn<Object, String> colKRodzaj;
    @FXML private TableColumn<Object, String> colKOd;
    @FXML private TableColumn<Object, String> colKDo;
    @FXML private TableColumn<Object, String> colKStatus;
    @FXML private TableColumn<Object, String> colKAkcje;

    @FXML
    public void initialize() {
        setupMemberColumns();
        setupPassColumns();
        loadData();
    }

    private void setupMemberColumns() {
        colMAkcje.setCellFactory(col -> new TableCell<>() {
            private final Button btnEdytuj  = new Button("Edytuj");
            private final Button btnKarnet  = new Button("Karnet");
            private final HBox box          = new HBox(6, btnEdytuj, btnKarnet);
            {
                btnEdytuj.getStyleClass().addAll("btn", "btn-ghost", "btn-sm");
                btnKarnet.getStyleClass().addAll("btn", "btn-ghost", "btn-sm");
                btnEdytuj.setOnAction(e -> onEdytujCzlonka());
                btnKarnet.setOnAction(e -> onSprzedajKarnet());
            }
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });
        colMKarnet.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setGraphic(null); return; }
                Label badge = new Label(item);
                String cls = switch (item) {
                    case "Aktywny"         -> "badge-green";
                    case "Wygasa wkrótce"  -> "badge-amber";
                    case "Wygasły"         -> "badge-red";
                    default                -> "badge-gray";
                };
                badge.getStyleClass().addAll("badge", cls);
                setGraphic(badge);
            }
        });
    }

    private void setupPassColumns() {
        colKStatus.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setGraphic(null); return; }
                Label badge = new Label(item);
                String cls = switch (item) {
                    case "Aktywny"         -> "badge-green";
                    case "Wygasa wkrótce"  -> "badge-amber";
                    case "Wygasły"         -> "badge-red";
                    default                -> "badge-gray";
                };
                badge.getStyleClass().addAll("badge", cls);
                setGraphic(badge);
            }
        });
        colKAkcje.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("Przedłuż");
            {
                btn.getStyleClass().addAll("btn", "btn-ghost", "btn-sm");
                btn.setOnAction(e -> onPrzedluzKarnet());
            }
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });
    }

    private void loadData() { /* Wired to CzlonekRepository */ }

    @FXML private void onSearchMembers() { /* Filter czlonekTable */ }
    @FXML private void onSearchPasses()  { /* Filter karnetTable  */ }

    @FXML
    public void showMembersTab() {
        membersTab.setVisible(true);  membersTab.setManaged(true);
        passesTab.setVisible(false);  passesTab.setManaged(false);
    }

    @FXML
    public void showPassesTab() {
        membersTab.setVisible(false); membersTab.setManaged(false);
        passesTab.setVisible(true);   passesTab.setManaged(true);
    }

    @FXML
    public void onDodajCzlonka() {
        openDialog("/views/dialogs/AddEditCzlonekDialog.fxml", "Dodanie członka");
    }

    private void onEdytujCzlonka() {
        openDialog("/views/dialogs/AddEditCzlonekDialog.fxml", "Edycja członka");
    }

    @FXML
    public void onSprzedajKarnet() {
        openDialog("/views/dialogs/SprzedajKarnetDialog.fxml", "Sprzedaż karnetu");
    }

    private void onPrzedluzKarnet() {
        openDialog("/views/dialogs/SprzedajKarnetDialog.fxml", "Przedłużenie karnetu");
    }

    public void refresh() { loadData(); }

    private void openDialog(String path, String title) {
        try {
            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle(title);
            dialog.setScene(new Scene(FXMLLoader.load(getClass().getResource(path))));
            dialog.setResizable(false);
            dialog.showAndWait();
            refresh();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
