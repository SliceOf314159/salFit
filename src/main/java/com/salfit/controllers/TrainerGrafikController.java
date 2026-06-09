package com.salfit.controllers;

import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

public class TrainerGrafikController {

    @FXML private Label weekLabel;
    @FXML private GridPane grafikGrid;

    private LocalDate aktualnyTydzien;
    private static final String[] HOURS = {"8:00", "10:00", "12:00", "15:00", "17:00", "19:00"};

    @FXML
    public void initialize() {
        aktualnyTydzien = LocalDate.now();
        refresh();
    }

    @FXML public void onPoprzedni() { aktualnyTydzien = aktualnyTydzien.minusWeeks(1); refresh(); }
    @FXML public void onNastepny()  { aktualnyTydzien = aktualnyTydzien.plusWeeks(1);  refresh(); }

    @FXML
    public void showUczestnicy() {
        TrainerShellController.getInstance().showUczestnicy();
    }

    private void refresh() {
        LocalDate monday = aktualnyTydzien.with(DayOfWeek.MONDAY);
        LocalDate friday = monday.plusDays(4);
        Locale pl = Locale.forLanguageTag("pl");
        weekLabel.setText("Grafik zajęć " + monday.getDayOfMonth() + "–"
                + friday.getDayOfMonth() + " "
                + monday.getMonth().getDisplayName(TextStyle.FULL, pl));
        renderGrid(monday);
    }

    private void renderGrid(LocalDate monday) {
        grafikGrid.getChildren().clear();
        grafikGrid.getRowConstraints().clear();

        grafikGrid.getRowConstraints().add(rowConst(36));
        grafikGrid.add(makeHeaderCell("GODZ."), 0, 0);
        Locale pl = Locale.forLanguageTag("pl");
        for (int d = 0; d < 5; d++) {
            LocalDate day = monday.plusDays(d);
            String label = day.getDayOfWeek().getDisplayName(TextStyle.SHORT, pl).toUpperCase()
                    + " " + String.format("%d.%02d", day.getDayOfMonth(), day.getMonthValue());
            grafikGrid.add(makeHeaderCell(label), d + 1, 0);
        }
        for (int r = 0; r < HOURS.length; r++) {
            grafikGrid.getRowConstraints().add(rowConst(68));
            Label time = new Label(HOURS[r]);
            time.getStyleClass().add("cal-time-cell");
            time.setMaxWidth(Double.MAX_VALUE);
            time.setMaxHeight(Double.MAX_VALUE);
            grafikGrid.add(time, 0, r + 1);
            for (int d = 1; d <= 5; d++) {
                VBox cell = new VBox(3);
                cell.getStyleClass().add("cal-cell");
                cell.setPadding(new Insets(4));
                grafikGrid.add(cell, d, r + 1);
            }
        }
    }

    private Label makeHeaderCell(String text) {
        Label l = new Label(text);
        l.getStyleClass().add("cal-header-cell");
        l.setMaxWidth(Double.MAX_VALUE);
        GridPane.setHgrow(l, Priority.ALWAYS);
        GridPane.setHalignment(l, HPos.CENTER);
        return l;
    }

    private RowConstraints rowConst(double h) {
        RowConstraints rc = new RowConstraints();
        rc.setMinHeight(h);
        rc.setPrefHeight(h);
        return rc;
    }
}
