package com.salfit.controllers;

import com.salfit.model.Sala;
import com.salfit.model.Trener;
import com.salfit.model.Zajecia;
import com.salfit.repository.GrafikRepository;
import com.salfit.repository.SalaRepository;
import com.salfit.repository.TrenerRepository;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

public class GrafikController {

    @FXML private Label weekLabel;
    @FXML private GridPane grafikGrid;

    private LocalDate aktualnyTydzien;
    private static final Locale PL = Locale.forLanguageTag("pl");
    private static final DateTimeFormatter DAY_FMT = DateTimeFormatter.ofPattern("EEE d.MM").withLocale(PL);
    private static final int[] HOUR_VALUES = {8, 10, 12, 14, 17, 19};
    private static final String[] HOURS = {"8:00", "10:00", "12:00", "14:00", "17:00", "19:00"};

    @FXML
    public void initialize() {
        aktualnyTydzien = LocalDate.now();
        refresh();
    }

    @FXML public void onPoprzedniTydzien() { aktualnyTydzien = aktualnyTydzien.minusWeeks(1); refresh(); }
    @FXML public void onNastepnyTydzien()  { aktualnyTydzien = aktualnyTydzien.plusWeeks(1);  refresh(); }

    @FXML
    public void onDodajZajecia() {
        openZajeciaDialog(null);
    }

    public void refresh() {
        LocalDate monday = aktualnyTydzien.with(DayOfWeek.MONDAY);
        LocalDate friday = monday.plusDays(4);
        weekLabel.setText(String.format("Tydzień %d–%d %s %d",
                monday.getDayOfMonth(), friday.getDayOfMonth(),
                monday.getMonth().getDisplayName(TextStyle.FULL, PL),
                monday.getYear()));
        renderTydzien(monday);
    }

    private void renderTydzien(LocalDate monday) {
        grafikGrid.getChildren().clear();
        grafikGrid.getRowConstraints().clear();

        grafikGrid.getRowConstraints().add(rowConst(36));
        grafikGrid.add(makeHeaderCell("GODZ."), 0, 0);
        for (int d = 0; d < 5; d++) {
            LocalDate day = monday.plusDays(d);
            grafikGrid.add(makeHeaderCell(day.format(DAY_FMT).toUpperCase(PL)), d + 1, 0);
        }

        VBox[][] cells = new VBox[HOURS.length][5];
        for (int r = 0; r < HOURS.length; r++) {
            grafikGrid.getRowConstraints().add(rowConst(72));
            int row = r + 1;
            Label timeCell = new Label(HOURS[r]);
            timeCell.getStyleClass().add("cal-time-cell");
            timeCell.setMaxWidth(Double.MAX_VALUE);
            timeCell.setMaxHeight(Double.MAX_VALUE);
            grafikGrid.add(timeCell, 0, row);
            for (int d = 0; d < 5; d++) {
                VBox cell = new VBox(3);
                cell.getStyleClass().add("cal-cell");
                cell.setPadding(new Insets(4));
                grafikGrid.add(cell, d + 1, row);
                cells[r][d] = cell;
            }
        }

        List<Zajecia> zajecia = GrafikRepository.getInstance().findByTydzien(monday);
        for (Zajecia z : zajecia) {
            int row = hourToRow(z.getTermin().getHour());
            int col = z.getTermin().getDayOfWeek().getValue() - 1; // Mon=0, ..., Fri=4
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

        String trenerName = TrenerRepository.getInstance().findById(z.getTrenerId())
                .map(Trener::getImieNazwisko).orElse("—");
        String salaNazwa = SalaRepository.getInstance().findById(z.getSalaId())
                .map(Sala::getNazwa).orElse("—");

        Label lNazwa = new Label(z.getNazwa());
        lNazwa.getStyleClass().add("cal-card-title");
        Label lInfo = new Label(trenerName + " · " + salaNazwa);
        lInfo.getStyleClass().add("cal-card-sub");
        lInfo.setWrapText(true);
        card.getChildren().addAll(lNazwa, lInfo);

        card.setOnMouseClicked(e -> openZajeciaDialog(z));
        return card;
    }

    private void openZajeciaDialog(Zajecia zajecia) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/views/dialogs/AddEditZajeciaDialog.fxml"));
            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle(zajecia != null ? "Edycja zajęć" : "Dodanie zajęć");
            dialog.setScene(new Scene(loader.load()));
            AddEditZajeciaDialogController ctrl = loader.getController();
            if (zajecia != null) {
                ctrl.setZajecia(zajecia);
                ctrl.setEditMode(true);
            }
            dialog.showAndWait();
            refresh();
        } catch (IOException e) {
            e.printStackTrace();
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

    private RowConstraints rowConst(double height) {
        RowConstraints rc = new RowConstraints();
        rc.setMinHeight(height);
        rc.setPrefHeight(height);
        return rc;
    }
}
