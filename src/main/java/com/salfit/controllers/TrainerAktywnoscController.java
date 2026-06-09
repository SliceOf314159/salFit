package com.salfit.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TrainerAktywnoscController {

    @FXML private ComboBox<String> monthSelector;
    @FXML private Label statWeek;
    @FXML private Label statMonth;
    @FXML private Label statTotal;
    @FXML private Label statObloz;
    @FXML private TableView<Object> aktywnoscTable;
    @FXML private TableColumn<Object, String> colData;
    @FXML private TableColumn<Object, String> colTyp;
    @FXML private TableColumn<Object, String> colSala;
    @FXML private TableColumn<Object, String> colUczestnicy;
    @FXML private TableColumn<Object, String> colGodz;

    @FXML
    public void initialize() {
        populateMonthSelector();
        loadData();
    }

    private void populateMonthSelector() {
        Locale pl = Locale.forLanguageTag("pl");
        List<String> months = new ArrayList<>();
        YearMonth current = YearMonth.now();
        for (int i = 0; i < 6; i++) {
            YearMonth ym = current.minusMonths(i);
            String name = ym.getMonth().getDisplayName(TextStyle.FULL_STANDALONE, pl);
            months.add(name.substring(0, 1).toUpperCase() + name.substring(1)
                    + " " + ym.getYear());
        }
        monthSelector.setItems(FXCollections.observableArrayList(months));
        monthSelector.getSelectionModel().selectFirst();
    }

    @FXML
    private void onMonthChanged() {
        loadData();
    }

    private void loadData() {
        /* Statistics will be computed from GrafikRepository for the logged-in trainer. */
    }
}
