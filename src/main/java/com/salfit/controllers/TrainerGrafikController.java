package com.salfit.controllers;

import com.salfit.SessionManager;
import com.salfit.model.Sala;
import com.salfit.model.Zajecia;
import com.salfit.repository.GrafikRepository;
import com.salfit.repository.SalaRepository;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

public class TrainerGrafikController {

    @FXML private Label weekLabel;
    @FXML private GridPane grafikGrid;

    private LocalDate aktualnyTydzien;
    private static final Locale PL = Locale.forLanguageTag("pl");
    private static final int[] HOUR_VALUES = {8, 10, 12, 15, 17, 19};
    private static final String[] HOURS     = {"8:00", "10:00", "12:00", "15:00", "17:00", "19:00"};

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
        weekLabel.setText("Grafik zajęć " + monday.getDayOfMonth() + "–"
                + friday.getDayOfMonth() + " "
                + monday.getMonth().getDisplayName(TextStyle.FULL, PL));
        renderGrid(monday);
    }

    private void renderGrid(LocalDate monday) {
        grafikGrid.getChildren().clear();
        grafikGrid.getRowConstraints().clear();

        grafikGrid.getRowConstraints().add(rowConst(36));
        grafikGrid.add(makeHeaderCell("GODZ."), 0, 0);
        for (int d = 0; d < 5; d++) {
            LocalDate day = monday.plusDays(d);
            String lbl = day.getDayOfWeek().getDisplayName(TextStyle.SHORT, PL).toUpperCase()
                    + " " + String.format("%d.%02d", day.getDayOfMonth(), day.getMonthValue());
            grafikGrid.add(makeHeaderCell(lbl), d + 1, 0);
        }

        VBox[][] cells = new VBox[HOURS.length][5];
        for (int r = 0; r < HOURS.length; r++) {
            grafikGrid.getRowConstraints().add(rowConst(72));
            Label time = new Label(HOURS[r]);
            time.getStyleClass().add("cal-time-cell");
            time.setMaxWidth(Double.MAX_VALUE);
            time.setMaxHeight(Double.MAX_VALUE);
            grafikGrid.add(time, 0, r + 1);
            for (int d = 0; d < 5; d++) {
                VBox cell = new VBox(3);
                cell.getStyleClass().add("cal-cell");
                cell.setPadding(new Insets(4));
                grafikGrid.add(cell, d + 1, r + 1);
                cells[r][d] = cell;
            }
        }

        String trainerId = SessionManager.getInstance().getLoggedInTrenerId();
        if (trainerId == null) return;

        List<Zajecia> weekAll = GrafikRepository.getInstance().findByTydzien(monday);
        for (Zajecia z : weekAll) {
            if (!trainerId.equals(z.getTrenerId())) continue;
            int row = hourToRow(z.getTermin().getHour());
            int col = z.getTermin().getDayOfWeek().getValue() - 1;
            if (row < 0 || col < 0 || col > 4) continue;
            cells[row][col].getChildren().add(makeCard(z));
        }
    }

    private int hourToRow(int hour) {
        for (int i = 0; i < HOUR_VALUES.length; i++) {
            if (hour == HOUR_VALUES[i]) return i;
        }
        if (hour < HOUR_VALUES[0]) return 0;
        for (int i = 0; i < HOUR_VALUES.length - 1; i++) {
            if (hour >= HOUR_VALUES[i] && hour < HOUR_VALUES[i + 1]) return i;
        }
        return HOUR_VALUES.length - 1;
    }

    private VBox makeCard(Zajecia z) {
        VBox card = new VBox(2);
        card.getStyleClass().add("cal-card");
        card.setPadding(new Insets(4, 6, 4, 6));
        card.setCursor(Cursor.HAND);

        String salaNazwa = SalaRepository.getInstance().findById(z.getSalaId())
                .map(Sala::getNazwa).orElse("—");

        Label lNazwa = new Label(z.getNazwa());
        lNazwa.getStyleClass().add("cal-card-title");
        Label lInfo = new Label(salaNazwa + " · "
                + z.getUczestnicyIds().size() + "/" + z.getLimitUczestnikow());
        lInfo.getStyleClass().add("cal-card-sub");
        card.getChildren().addAll(lNazwa, lInfo);

        card.setOnMouseClicked(e -> {
            SessionManager.getInstance().setSelectedZajecia(z);
            TrainerShellController.getInstance().showUczestnicy();
        });
        return card;
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
