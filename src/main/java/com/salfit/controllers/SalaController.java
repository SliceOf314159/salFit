package com.salfit.controllers;

import com.salfit.model.Sala;
import com.salfit.model.StatusSali;
import com.salfit.repository.SalaRepository;
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

public class SalaController {

    @FXML private TextField searchField;
    @FXML private TableView<Sala> salaTable;
    @FXML private TableColumn<Sala, String> colId;
    @FXML private TableColumn<Sala, String> colNazwa;
    @FXML private TableColumn<Sala, String> colTyp;
    @FXML private TableColumn<Sala, String> colPoj;
    @FXML private TableColumn<Sala, String> colPrzerwa;
    @FXML private TableColumn<Sala, String> colStatus;
    @FXML private TableColumn<Sala, String> colAkcje;

    private ObservableList<Sala> allSale = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupColumns();
        loadData();
    }

    private void setupColumns() {
        colId.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getId() != null ? d.getValue().getId().substring(0, Math.min(8, d.getValue().getId().length())) + "…" : "—"));
        colNazwa.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getNazwa()));
        colTyp.setCellValueFactory(d -> new SimpleStringProperty("—"));
        colPoj.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getPojemnosc())));
        colPrzerwa.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getMinPrzerwaMinut() + " min"));

        colStatus.setCellValueFactory(d -> {
            String label = switch (d.getValue().getStatus()) {
                case DOSTEPNA  -> "Dostępna";
                case ZAJETA    -> "Zajęta";
                case W_REMONCIE-> "W remoncie";
            };
            return new SimpleStringProperty(label);
        });
        colStatus.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setGraphic(null); return; }
                Label badge = new Label(item);
                String cls = switch (item) {
                    case "Dostępna"   -> "badge-green";
                    case "Zajęta"     -> "badge-amber";
                    case "W remoncie" -> "badge-red";
                    default           -> "badge-gray";
                };
                badge.getStyleClass().addAll("badge", cls);
                setGraphic(badge);
            }
        });

        colAkcje.setCellFactory(col -> new TableCell<>() {
            private final Button btnStatus = new Button("Zmień status");
            private final Button btnEdytuj = new Button("Edytuj");
            private final HBox box = new HBox(6, btnStatus, btnEdytuj);

            {
                btnStatus.getStyleClass().addAll("btn", "btn-ghost", "btn-sm");
                btnEdytuj.getStyleClass().addAll("btn", "btn-ghost", "btn-sm");
                btnStatus.setOnAction(e -> {
                    int i = getIndex();
                    if (i >= 0 && i < getTableView().getItems().size())
                        onZmienStatus(getTableView().getItems().get(i));
                });
                btnEdytuj.setOnAction(e -> {
                    int i = getIndex();
                    if (i >= 0 && i < getTableView().getItems().size())
                        onEdytujSale(getTableView().getItems().get(i));
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });
    }

    private void loadData() {
        allSale.setAll(SalaRepository.getInstance().findAll());
        salaTable.setItems(allSale);
    }

    @FXML
    private void onSearch() {
        String text = searchField.getText().toLowerCase();
        if (text.isBlank()) { salaTable.setItems(allSale); return; }
        List<Sala> filtered = allSale.stream()
                .filter(s -> s.getNazwa().toLowerCase().contains(text))
                .collect(Collectors.toList());
        salaTable.setItems(FXCollections.observableArrayList(filtered));
    }

    @FXML
    public void onDodajSale() {
        openDialog("/views/dialogs/AddEditSalaDialog.fxml", "Dodanie sali", null);
    }

    private void onEdytujSale(Sala sala) {
        openDialog("/views/dialogs/AddEditSalaDialog.fxml", "Edycja sali", sala);
    }

    private void onZmienStatus(Sala sala) {
        StatusSali next = switch (sala.getStatus()) {
            case DOSTEPNA   -> StatusSali.ZAJETA;
            case ZAJETA     -> StatusSali.W_REMONCIE;
            case W_REMONCIE -> StatusSali.DOSTEPNA;
        };
        sala.setStatus(next);
        SalaRepository.getInstance().update(sala);
        salaTable.refresh();
    }

    public void refresh() { loadData(); }

    private void openDialog(String path, String title, Sala sala) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle(title);
            dialog.setScene(new Scene(loader.load()));
            dialog.setResizable(false);
            AddEditSalaDialogController ctrl = loader.getController();
            if (sala != null) {
                ctrl.setSala(sala);
                ctrl.setEditMode(true);
            }
            dialog.showAndWait();
            refresh();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
