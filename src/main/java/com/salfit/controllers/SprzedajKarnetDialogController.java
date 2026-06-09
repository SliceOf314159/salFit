package com.salfit.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.Locale;

public class SprzedajKarnetDialogController {

    @FXML private ComboBox<String> fieldCzlonek;
    @FXML private ComboBox<String> fieldRodzaj;
    @FXML private ComboBox<String> fieldPlatnosc;
    @FXML private DatePicker fieldDataOd;
    @FXML private DatePicker fieldDataDo;
    @FXML private Label calMonthLabel;
    @FXML private GridPane calGrid;

    private YearMonth displayedMonth;

    @FXML
    public void initialize() {
        fieldRodzaj.setItems(FXCollections.observableArrayList(
                "Miesięczny", "Kwartalny", "Roczny"));
        fieldPlatnosc.setItems(FXCollections.observableArrayList(
                "Karta", "Gotówka", "Przelew"));
        fieldPlatnosc.getSelectionModel().selectFirst();

        LocalDate today = LocalDate.now();
        fieldDataOd.setValue(today);
        fieldDataDo.setValue(today.plusMonths(1));
        displayedMonth = YearMonth.from(today);
        renderCalendar();
    }

    @FXML
    private void onRodzajChanged() {
        String rodzaj = fieldRodzaj.getValue();
        if (rodzaj == null || fieldDataOd.getValue() == null) return;
        LocalDate od = fieldDataOd.getValue();
        LocalDate do_ = switch (rodzaj) {
            case "Miesięczny" -> od.plusDays(30);
            case "Kwartalny"  -> od.plusDays(90);
            case "Roczny"     -> od.plusDays(365);
            default           -> od.plusDays(30);
        };
        fieldDataDo.setValue(do_);
    }

    @FXML
    private void onDataOdChanged() {
        onRodzajChanged();
    }

    @FXML
    private void prevMonth() {
        displayedMonth = displayedMonth.minusMonths(1);
        renderCalendar();
    }

    @FXML
    private void nextMonth() {
        displayedMonth = displayedMonth.plusMonths(1);
        renderCalendar();
    }

    private void renderCalendar() {
        calGrid.getChildren().clear();
        String monthName = displayedMonth.getMonth()
                .getDisplayName(TextStyle.FULL_STANDALONE, Locale.forLanguageTag("pl"));
        calMonthLabel.setText(monthName + " " + displayedMonth.getYear());

        String[] days = {"Pn", "Wt", "Śr", "Cz", "Pt", "Sb", "Nd"};
        for (int i = 0; i < 7; i++) {

            Label lbl = new Label(days[i]);
            lbl.getStyleClass().add("mini-cal-weekday");
            calGrid.add(lbl, i, 0);
        }

        LocalDate first = displayedMonth.atDay(1);
        int startCol = first.getDayOfWeek().getValue() - 1;
        int daysInMonth = displayedMonth.lengthOfMonth();

        int col = startCol;
        int row = 1;
        for (int day = 1; day <= daysInMonth; day++) {
            final int d = day;
            Button btn = new Button(String.valueOf(day));
            btn.getStyleClass().add("mini-cal-day");
            btn.setOnAction(e -> {
                fieldDataOd.setValue(displayedMonth.atDay(d));
                onRodzajChanged();
            });
            calGrid.add(btn, col, row);
            col++;
            if (col == 7) { col = 0; row++; }
        }
    }

    @FXML private void onNowyCzlonek() { /* Navigate to add member */ }
    @FXML private void onSprzedaj()    { closeDialog(); }
    @FXML private void onPrzedluz()    { closeDialog(); }
    @FXML private void onCancel()      { closeDialog(); }

    private void closeDialog() {
        ((Stage) fieldRodzaj.getScene().getWindow()).close();
    }
}
