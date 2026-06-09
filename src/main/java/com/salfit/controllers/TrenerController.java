package com.salfit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class TrenerController {

    @FXML private TextField searchField;
    @FXML private TableView<Object> trenerTable;
    @FXML private TableColumn<Object, String> colId;
    @FXML private TableColumn<Object, String> colNazwa;
    @FXML private TableColumn<Object, String> colSpec;
    @FXML private TableColumn<Object, String> colEmail;
    @FXML private TableColumn<Object, String> colStatus;
    @FXML private TableColumn<Object, String> colAkcje;

    @FXML
    public void initialize() {
        setupColumns();
        loadData();
    }

    private void setupColumns() {
        colAkcje.setCellFactory(col -> new TableCell<>() {
            private final Button btnEdytuj     = new Button("Edytuj");
            private final Button btnToggle     = new Button("Dezaktywuj");
            private final HBox box             = new HBox(6, btnEdytuj, btnToggle);

            {
                btnEdytuj.getStyleClass().addAll("btn", "btn-ghost", "btn-sm");
                btnToggle.getStyleClass().addAll("btn", "btn-ghost", "btn-sm");
                btnEdytuj.setOnAction(e -> onEdytujTrener());
                btnToggle.setOnAction(e -> onToggleAktywny());
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
                badge.getStyleClass().addAll("badge",
                        item.equals("Aktywny") ? "badge-green" : "badge-gray");
                setGraphic(badge);
            }
        });
    }

    private void loadData() {
        /* Data loading will be wired to TrenerRepository. */
    }

    @FXML
    private void onSearch() {
        /* Filter rows in trenerTable based on searchField text. */
    }

    @FXML
    public void onDodajTrener() {
        openDialog("/views/dialogs/AddEditTrenerDialog.fxml", "Dodanie trenera");
    }

    private void onEdytujTrener() {
        openDialog("/views/dialogs/AddEditTrenerDialog.fxml", "Edycja trenera");
    }

    private void onToggleAktywny() {
        /* Toggle active/inactive status of selected trainer. */
    }

    public void refresh() {
        loadData();
    }

    private void openDialog(String fxmlPath, String title) {
        try {
            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle(title);
            dialog.setScene(new Scene(FXMLLoader.load(getClass().getResource(fxmlPath))));
            dialog.setResizable(false);
            dialog.showAndWait();
            refresh();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
