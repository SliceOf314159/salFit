package com.salfit.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;

public class TrainerUczestnicyController {

    @FXML private Label pageTitle;
    @FXML private Label countLabel;
    @FXML private Label limitLabel;
    @FXML private TableView<Object> uczestnicyTable;
    @FXML private TableColumn<Object, String> colId;
    @FXML private TableColumn<Object, String> colNazwa;
    @FXML private TableColumn<Object, String> colKarnet;
    @FXML private TableColumn<Object, String> colStatus;
    @FXML private TableColumn<Object, String> colObec;

    @FXML
    public void initialize() {
        setupColumns();
        loadData();
    }

    private void setupColumns() {
        colStatus.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setGraphic(null); return; }
                Label badge = new Label(item);
                String cls = item.startsWith("Wygasa") ? "badge-amber" : "badge-green";
                badge.getStyleClass().addAll("badge", cls);
                setGraphic(badge);
            }
        });
    }

    private void loadData() {
        /* Load participants from GrafikRepository for the selected session. */
    }

    @FXML
    private void backToGrafik() {
        TrainerShellController.getInstance().showGrafik();
    }
}
