package com.salfit.controllers;

import com.salfit.SessionManager;
import com.salfit.model.Czlonek;
import com.salfit.model.Karnet;
import com.salfit.model.Zajecia;
import com.salfit.repository.CzlonekRepository;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TrainerUczestnicyController {

    @FXML private Label pageTitle;
    @FXML private Label countLabel;
    @FXML private Label limitLabel;
    @FXML private TableView<Czlonek> uczestnicyTable;
    @FXML private TableColumn<Czlonek, String> colId;
    @FXML private TableColumn<Czlonek, String> colNazwa;
    @FXML private TableColumn<Czlonek, String> colKarnet;
    @FXML private TableColumn<Czlonek, String> colStatus;
    @FXML private TableColumn<Czlonek, String> colObec;

    @FXML
    public void initialize() {
        setupColumns();
        loadData();
    }

    private void setupColumns() {
        colId.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getId() != null
                        ? d.getValue().getId().substring(0, Math.min(8, d.getValue().getId().length())) + "…"
                        : "—"));
        colNazwa.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getImieNazwisko()));
        colKarnet.setCellValueFactory(d -> {
            Optional<Karnet> k = CzlonekRepository.getInstance().findAktywnyKarnet(d.getValue().getId());
            return new SimpleStringProperty(k.map(karnet -> karnet.getRodzaj().name()).orElse("Brak"));
        });
        colStatus.setCellValueFactory(d -> {
            Optional<Karnet> k = CzlonekRepository.getInstance().findAktywnyKarnet(d.getValue().getId());
            String label = k.map(karnet -> switch (karnet.getStatus()) {
                case AKTYWNY        -> "Aktywny";
                case WYGASA_WKROTCE -> "Wygasa wkrótce";
                case WYGASL         -> "Wygasły";
            }).orElse("Brak");
            return new SimpleStringProperty(label);
        });
        colStatus.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setGraphic(null); return; }
                Label badge = new Label(item);
                String cls = switch (item) {
                    case "Aktywny"        -> "badge-green";
                    case "Wygasa wkrótce" -> "badge-amber";
                    default               -> "badge-gray";
                };
                badge.getStyleClass().addAll("badge", cls);
                setGraphic(badge);
            }
        });
        colObec.setCellValueFactory(d -> {
            Zajecia zajecia = SessionManager.getInstance().getSelectedZajecia();
            boolean potwierdzony = zajecia != null && zajecia.czyPotwierdzony(d.getValue().getId());
            return new SimpleStringProperty(potwierdzony ? "Potwierdzony" : "Niepotwierdzony");
        });
        colObec.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setGraphic(null); return; }
                Label badge = new Label(item);
                badge.getStyleClass().addAll("badge",
                        item.equals("Potwierdzony") ? "badge-green" : "badge-gray");
                setGraphic(badge);
            }
        });
    }

    private void loadData() {
        Zajecia zajecia = SessionManager.getInstance().getSelectedZajecia();
        if (zajecia == null) {
            uczestnicyTable.setItems(FXCollections.emptyObservableList());
            if (pageTitle != null) pageTitle.setText("Uczestnicy zajęć");
            return;
        }

        if (pageTitle != null) pageTitle.setText("Uczestnicy: " + zajecia.getNazwa());
        if (countLabel != null) countLabel.setText(String.valueOf(zajecia.getUczestnicyIds().size()));
        if (limitLabel != null) limitLabel.setText(String.valueOf(zajecia.getLimitUczestnikow()));

        List<Czlonek> uczestnicy = new ArrayList<>();
        for (String id : zajecia.getUczestnicyIds()) {
            CzlonekRepository.getInstance().findById(id).ifPresent(uczestnicy::add);
        }
        uczestnicyTable.setItems(FXCollections.observableArrayList(uczestnicy));
    }

    @FXML
    private void backToGrafik() {
        TrainerShellController.getInstance().showGrafik();
    }
}
