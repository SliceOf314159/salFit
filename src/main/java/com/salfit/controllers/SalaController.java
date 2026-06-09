package com.salfit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class SalaController {

    @FXML private TextField searchField;
    @FXML private TableView<Object> salaTable;
    @FXML private TableColumn<Object, String> colId;
    @FXML private TableColumn<Object, String> colNazwa;
    @FXML private TableColumn<Object, String> colTyp;
    @FXML private TableColumn<Object, String> colPoj;
    @FXML private TableColumn<Object, String> colPrzerwa;
    @FXML private TableColumn<Object, String> colStatus;
    @FXML private TableColumn<Object, String> colAkcje;

    @FXML
    public void initialize() {
        setupColumns();
        loadData();
    }

    private void setupColumns() {
        colAkcje.setCellFactory(col -> new TableCell<>() {
            private final Button btnStatus = new Button("Zmień status");
            private final Button btnEdytuj = new Button("Edytuj");
            private final HBox box         = new HBox(6, btnStatus, btnEdytuj);

            {
                btnStatus.getStyleClass().addAll("btn", "btn-ghost", "btn-sm");
                btnEdytuj.getStyleClass().addAll("btn", "btn-ghost", "btn-sm");
                btnStatus.setOnAction(e -> onZmienStatus());
                btnEdytuj.setOnAction(e -> onEdytujSale());
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });

        colStatus.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setGraphic(null); return; }
                Label badge = new Label(item);
                String cls = switch (item) {
                    case "Dostępna"  -> "badge-green";
                    case "Zajęta"    -> "badge-amber";
                    case "W remoncie"-> "badge-red";
                    default          -> "badge-gray";
                };
                badge.getStyleClass().addAll("badge", cls);
                setGraphic(badge);
            }
        });
    }

    private void loadData() { /* Wired to SalaRepository */ }

    @FXML private void onSearch() { /* Filter salaTable */ }

    @FXML
    public void onDodajSale() {
        openDialog("/views/dialogs/AddEditSalaDialog.fxml", "Dodanie sali");
    }

    private void onEdytujSale() {
        openDialog("/views/dialogs/AddEditSalaDialog.fxml", "Edycja sali");
    }

    private void onZmienStatus() { /* Toggle room status */ }

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
