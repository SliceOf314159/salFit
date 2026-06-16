package com.salfit.controllers;

import com.salfit.model.Trener;
import com.salfit.repository.TrenerRepository;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class TrenerController {

    @FXML private TextField searchField;
    @FXML private TableView<Trener> trenerTable;
    @FXML private TableColumn<Trener, String> colId;
    @FXML private TableColumn<Trener, String> colNazwa;
    @FXML private TableColumn<Trener, String> colSpec;
    @FXML private TableColumn<Trener, String> colEmail;
    @FXML private TableColumn<Trener, String> colStatus;
    @FXML private TableColumn<Trener, String> colAkcje;

    private ObservableList<Trener> allTrenerzy = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupColumns();
        loadData();
    }

    private void setupColumns() {
        colId.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getId() != null ? d.getValue().getId().substring(0, 8) + "…" : "—"));
        colNazwa.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getImieNazwisko()));
        colSpec.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getSpecjalizacja()));
        colEmail.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getEmail()));

        colStatus.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().isAktywny() ? "Aktywny" : "Nieaktywny"));
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

        colAkcje.setCellFactory(col -> new TableCell<>() {
            private final Button btnEdytuj = new Button("Edytuj");
            private final Button btnToggle = new Button("Dezaktywuj");
            private final HBox box = new HBox(6, btnEdytuj, btnToggle);

            {
                btnEdytuj.getStyleClass().addAll("btn", "btn-ghost", "btn-sm");
                btnToggle.getStyleClass().addAll("btn", "btn-ghost", "btn-sm");
                btnEdytuj.setOnAction(e -> {
                    int i = getIndex();
                    if (i >= 0 && i < getTableView().getItems().size())
                        onEdytujTrener(getTableView().getItems().get(i));
                });
                btnToggle.setOnAction(e -> {
                    int i = getIndex();
                    if (i >= 0 && i < getTableView().getItems().size())
                        onToggleAktywny(getTableView().getItems().get(i));
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setGraphic(null); return; }
                int i = getIndex();
                if (i >= 0 && i < getTableView().getItems().size()) {
                    btnToggle.setText(getTableView().getItems().get(i).isAktywny()
                            ? "Dezaktywuj" : "Aktywuj");
                }
                setGraphic(box);
            }
        });
    }

    private void loadData() {
        allTrenerzy.setAll(TrenerRepository.getInstance().findAll());
        trenerTable.setItems(allTrenerzy);
    }

    @FXML
    private void onSearch() {
        String text = searchField.getText().toLowerCase();
        if (text.isBlank()) {
            trenerTable.setItems(allTrenerzy);
            return;
        }
        List<Trener> filtered = allTrenerzy.stream()
                .filter(t -> t.getImieNazwisko().toLowerCase().contains(text)
                          || t.getEmail().toLowerCase().contains(text)
                          || t.getSpecjalizacja().toLowerCase().contains(text))
                .collect(Collectors.toList());
        trenerTable.setItems(FXCollections.observableArrayList(filtered));
    }

    @FXML
    public void onDodajTrener() {
        openDialog("/views/dialogs/AddEditTrenerDialog.fxml", "Dodanie trenera", null);
    }

    private void onEdytujTrener(Trener trener) {
        openDialog("/views/dialogs/AddEditTrenerDialog.fxml", "Edycja trenera", trener);
    }

    private void onToggleAktywny(Trener trener) {
        trener.setAktywny(!trener.isAktywny());
        TrenerRepository.getInstance().update(trener);
        trenerTable.refresh();
    }

    public void refresh() {
        loadData();
    }

    private void openDialog(String fxmlPath, String title, Trener trener) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle(title);
            dialog.setScene(new Scene(loader.load()));
            dialog.setResizable(false);
            AddEditTrenerDialogController ctrl = loader.getController();
            if (trener != null) {
                ctrl.setTrener(trener);
                ctrl.setEditMode(true);
            }
            dialog.showAndWait();
            refresh();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
