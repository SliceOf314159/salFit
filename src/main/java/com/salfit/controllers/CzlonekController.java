package com.salfit.controllers;

import com.salfit.model.Czlonek;
import com.salfit.model.Karnet;
import com.salfit.repository.CzlonekRepository;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CzlonekController {

    @FXML private VBox membersTab;
    @FXML private VBox passesTab;

    @FXML private TextField searchMembers;
    @FXML private TableView<Czlonek> czlonekTable;
    @FXML private TableColumn<Czlonek, String> colMId;
    @FXML private TableColumn<Czlonek, String> colMNazwa;
    @FXML private TableColumn<Czlonek, String> colMEmail;
    @FXML private TableColumn<Czlonek, String> colMTel;
    @FXML private TableColumn<Czlonek, String> colMKarnet;
    @FXML private TableColumn<Czlonek, String> colMAkcje;

    @FXML private TextField searchPasses;
    @FXML private TableView<Karnet> karnetTable;
    @FXML private TableColumn<Karnet, String> colKCzlonek;
    @FXML private TableColumn<Karnet, String> colKRodzaj;
    @FXML private TableColumn<Karnet, String> colKOd;
    @FXML private TableColumn<Karnet, String> colKDo;
    @FXML private TableColumn<Karnet, String> colKStatus;
    @FXML private TableColumn<Karnet, String> colKAkcje;

    private ObservableList<Czlonek> allCzlonkowie = FXCollections.observableArrayList();
    private ObservableList<Karnet> allKarnety = FXCollections.observableArrayList();
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    @FXML
    public void initialize() {
        setupMemberColumns();
        setupPassColumns();
        loadData();
    }

    private void setupMemberColumns() {
        colMId.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getId() != null
                        ? d.getValue().getId().substring(0, Math.min(8, d.getValue().getId().length())) + "…"
                        : "—"));
        colMNazwa.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getImieNazwisko()));
        colMEmail.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getEmail()));
        colMTel.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getTelefon()));
        colMKarnet.setCellValueFactory(d -> {
            Optional<Karnet> karnet = CzlonekRepository.getInstance().findAktywnyKarnet(d.getValue().getId());
            String label = karnet.map(k -> switch (k.getStatus()) {
                case AKTYWNY        -> "Aktywny";
                case WYGASA_WKROTCE -> "Wygasa wkrótce";
                case WYGASL         -> "Wygasły";
            }).orElse("Brak");
            return new SimpleStringProperty(label);
        });
        colMKarnet.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setGraphic(null); return; }
                Label badge = new Label(item);
                String cls = switch (item) {
                    case "Aktywny"        -> "badge-green";
                    case "Wygasa wkrótce" -> "badge-amber";
                    case "Wygasły"        -> "badge-red";
                    default               -> "badge-gray";
                };
                badge.getStyleClass().addAll("badge", cls);
                setGraphic(badge);
            }
        });
        colMAkcje.setCellFactory(col -> new TableCell<>() {
            private final Button btnEdytuj = new Button("Edytuj");
            private final Button btnKarnet = new Button("Karnet");
            private final HBox box = new HBox(6, btnEdytuj, btnKarnet);
            {
                btnEdytuj.getStyleClass().addAll("btn", "btn-ghost", "btn-sm");
                btnKarnet.getStyleClass().addAll("btn", "btn-ghost", "btn-sm");
                btnEdytuj.setOnAction(e -> {
                    int i = getIndex();
                    if (i >= 0 && i < getTableView().getItems().size())
                        onEdytujCzlonka(getTableView().getItems().get(i));
                });
                btnKarnet.setOnAction(e -> {
                    int i = getIndex();
                    if (i >= 0 && i < getTableView().getItems().size())
                        openSprzedajDialog(getTableView().getItems().get(i));
                });
            }
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });
    }

    private void setupPassColumns() {
        colKCzlonek.setCellValueFactory(d -> {
            String name = CzlonekRepository.getInstance().findById(d.getValue().getCzlonekId())
                    .map(Czlonek::getImieNazwisko).orElse("—");
            return new SimpleStringProperty(name);
        });
        colKRodzaj.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getRodzaj() != null ? d.getValue().getRodzaj().name() : "—"));
        colKOd.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getDataOd() != null ? d.getValue().getDataOd().format(DATE_FMT) : "—"));
        colKDo.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getDataDo() != null ? d.getValue().getDataDo().format(DATE_FMT) : "—"));
        colKStatus.setCellValueFactory(d -> {
            String label = switch (d.getValue().getStatus()) {
                case AKTYWNY        -> "Aktywny";
                case WYGASA_WKROTCE -> "Wygasa wkrótce";
                case WYGASL         -> "Wygasły";
            };
            return new SimpleStringProperty(label);
        });
        colKStatus.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setGraphic(null); return; }
                Label badge = new Label(item);
                String cls = switch (item) {
                    case "Aktywny"        -> "badge-green";
                    case "Wygasa wkrótce" -> "badge-amber";
                    case "Wygasły"        -> "badge-red";
                    default               -> "badge-gray";
                };
                badge.getStyleClass().addAll("badge", cls);
                setGraphic(badge);
            }
        });
        colKAkcje.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("Przedłuż");
            {
                btn.getStyleClass().addAll("btn", "btn-ghost", "btn-sm");
                btn.setOnAction(e -> {
                    int i = getIndex();
                    if (i >= 0 && i < getTableView().getItems().size())
                        openSprzedajDialog(null);
                });
            }
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });
    }

    private void loadData() {
        allCzlonkowie.setAll(CzlonekRepository.getInstance().findAll());
        czlonekTable.setItems(allCzlonkowie);
        allKarnety.setAll(CzlonekRepository.getInstance().findAllKarnety());
        karnetTable.setItems(allKarnety);
    }

    @FXML
    private void onSearchMembers() {
        String text = searchMembers.getText().toLowerCase();
        if (text.isBlank()) { czlonekTable.setItems(allCzlonkowie); return; }
        List<Czlonek> filtered = allCzlonkowie.stream()
                .filter(c -> c.getImieNazwisko().toLowerCase().contains(text)
                          || c.getEmail().toLowerCase().contains(text)
                          || c.getTelefon().toLowerCase().contains(text))
                .collect(Collectors.toList());
        czlonekTable.setItems(FXCollections.observableArrayList(filtered));
    }

    @FXML
    private void onSearchPasses() {
        String text = searchPasses.getText().toLowerCase();
        if (text.isBlank()) { karnetTable.setItems(allKarnety); return; }
        List<Karnet> filtered = allKarnety.stream()
                .filter(k -> {
                    String czlonekName = CzlonekRepository.getInstance().findById(k.getCzlonekId())
                            .map(Czlonek::getImieNazwisko).orElse("").toLowerCase();
                    return czlonekName.contains(text);
                })
                .collect(Collectors.toList());
        karnetTable.setItems(FXCollections.observableArrayList(filtered));
    }

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
        openCzlonekDialog(null);
    }

    private void onEdytujCzlonka(Czlonek czlonek) {
        openCzlonekDialog(czlonek);
    }

    @FXML
    public void onSprzedajKarnet() {
        openSprzedajDialog(null);
    }

    private void openCzlonekDialog(Czlonek czlonek) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/views/dialogs/AddEditCzlonekDialog.fxml"));
            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle(czlonek != null ? "Edycja członka" : "Dodanie członka");
            dialog.setScene(new Scene(loader.load()));
            dialog.setResizable(false);
            AddEditCzlonekDialogController ctrl = loader.getController();
            if (czlonek != null) {
                ctrl.setCzlonek(czlonek);
                ctrl.setEditMode(true);
            }
            dialog.showAndWait();
            refresh();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openSprzedajDialog(Czlonek preselected) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/views/dialogs/SprzedajKarnetDialog.fxml"));
            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle("Sprzedaż karnetu");
            dialog.setScene(new Scene(loader.load()));
            dialog.setResizable(false);
            SprzedajKarnetDialogController ctrl = loader.getController();
            if (preselected != null) ctrl.setCzlonek(preselected);
            dialog.showAndWait();
            refresh();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void refresh() { loadData(); }
}
