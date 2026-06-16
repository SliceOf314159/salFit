package com.salfit.controllers;

import com.salfit.SessionManager;
import com.salfit.model.Sala;
import com.salfit.model.Zajecia;
import com.salfit.repository.GrafikRepository;
import com.salfit.repository.SalaRepository;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class TrainerAktywnoscController {

    @FXML private ComboBox<String> monthSelector;
    @FXML private Label statWeek;
    @FXML private Label statMonth;
    @FXML private Label statTotal;
    @FXML private Label statObloz;
    @FXML private TableView<Zajecia> aktywnoscTable;
    @FXML private TableColumn<Zajecia, String> colData;
    @FXML private TableColumn<Zajecia, String> colTyp;
    @FXML private TableColumn<Zajecia, String> colSala;
    @FXML private TableColumn<Zajecia, String> colUczestnicy;
    @FXML private TableColumn<Zajecia, String> colGodz;

    private final List<YearMonth> months = new ArrayList<>();
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("EEE d MMM", Locale.forLanguageTag("pl"));

    @FXML
    public void initialize() {
        setupColumns();
        populateMonthSelector();
        loadData();
    }

    private void setupColumns() {
        colData.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getTermin() != null
                        ? d.getValue().getTermin().format(DATE_FMT).toUpperCase()
                        : "—"));
        colTyp.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getNazwa()));
        colSala.setCellValueFactory(d -> {
            String name = SalaRepository.getInstance().findById(d.getValue().getSalaId())
                    .map(Sala::getNazwa).orElse("—");
            return new SimpleStringProperty(name);
        });
        colUczestnicy.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getUczestnicyIds().size() + "/" + d.getValue().getLimitUczestnikow()));
        colGodz.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getCzasTrwaniaMinut() + " min"));
    }

    private void populateMonthSelector() {
        Locale pl = Locale.forLanguageTag("pl");
        List<String> labels = new ArrayList<>();
        YearMonth current = YearMonth.now();
        for (int i = 0; i < 6; i++) {
            YearMonth ym = current.minusMonths(i);
            months.add(ym);
            String name = ym.getMonth().getDisplayName(TextStyle.FULL_STANDALONE, pl);
            labels.add(name.substring(0, 1).toUpperCase() + name.substring(1) + " " + ym.getYear());
        }
        monthSelector.setItems(FXCollections.observableArrayList(labels));
        monthSelector.getSelectionModel().selectFirst();
    }

    @FXML private void onMonthChanged() { loadData(); }

    private void loadData() {
        String trainerId = SessionManager.getInstance().getLoggedInTrenerId();
        if (trainerId == null) return;

        int selectedIndex = monthSelector.getSelectionModel().getSelectedIndex();
        YearMonth ym = (selectedIndex >= 0 && selectedIndex < months.size())
                ? months.get(selectedIndex) : YearMonth.now();

        List<Zajecia> byTrener = GrafikRepository.getInstance().findByTrener(trainerId);

        List<Zajecia> byMonth = byTrener.stream()
                .filter(z -> z.getTermin() != null
                        && YearMonth.from(z.getTermin()).equals(ym))
                .collect(Collectors.toList());

        LocalDate monday = LocalDate.now().with(DayOfWeek.MONDAY);
        List<Zajecia> byWeek = GrafikRepository.getInstance().findByTydzien(monday)
                .stream().filter(z -> trainerId.equals(z.getTrenerId())).collect(Collectors.toList());

        statWeek.setText(String.valueOf(byWeek.size()));
        statMonth.setText(String.valueOf(byMonth.size()));
        statTotal.setText(String.valueOf(byTrener.size()));

        double avgObloz = byTrener.isEmpty() ? 0.0
                : byTrener.stream()
                        .mapToDouble(z -> z.getLimitUczestnikow() == 0 ? 0.0
                                : (double) z.getUczestnicyIds().size() / z.getLimitUczestnikow())
                        .average().orElse(0.0);
        statObloz.setText(String.format("%.0f%%", avgObloz * 100));

        ObservableList<Zajecia> items = FXCollections.observableArrayList(byMonth);
        aktywnoscTable.setItems(items);
    }
}
