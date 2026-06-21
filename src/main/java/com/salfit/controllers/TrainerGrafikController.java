package com.salfit.controllers;

import com.salfit.SessionManager;
import com.salfit.model.Sala;
import com.salfit.model.Zajecia;
import com.salfit.repository.GrafikRepository;
import com.salfit.repository.SalaRepository;
import com.salfit.util.ZajeciaColor;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

// grafik tygodniowy ale dla panelu TRENERA. Tutaj filtrujemy tylko zajecia
// zalogowanego trenera i klikniecie w karte przenosi do listy uczestnikow (a nie do edycji jak u admina)
public class TrainerGrafikController {

    @FXML private Label weekLabel;
    @FXML private SplitPane grafikGrid;

    private LocalDate aktualnyTydzien;
    private static final Locale PL = Locale.forLanguageTag("pl");
    private static final int[] HOUR_VALUES = {8, 10, 12, 15, 17, 19};
    private static final String[] HOURS     = {"8:00", "10:00", "12:00", "15:00", "17:00", "19:00"};
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    @FXML
    public void initialize() {
        aktualnyTydzien = LocalDate.now();
        refresh();
    }

    // przyciski "poprzedni/nastepny tydzien"
    @FXML public void onPoprzedni() { aktualnyTydzien = aktualnyTydzien.minusWeeks(1); refresh(); }
    @FXML public void onNastepny()  { aktualnyTydzien = aktualnyTydzien.plusWeeks(1);  refresh(); }

    // odswieza caly widok - liczy poniedzialek/piatek tygodnia i rysuje siatke
    private void refresh() {
        LocalDate monday = aktualnyTydzien.with(DayOfWeek.MONDAY);
        LocalDate friday = monday.plusDays(4);
        weekLabel.setText("Grafik zajęć " + monday.getDayOfMonth() + "–"
                + friday.getDayOfMonth() + " "
                + monday.getMonth().getDisplayName(TextStyle.FULL, PL));
        renderGrid(monday);
    }

    // rysuje cala siatke grafika
    private void renderGrid(LocalDate monday) {
        grafikGrid.getItems().clear(); // czyscimy stary widok przed narysowaniem nowego

        // kolumna z godzinami po lewej stronie
        VBox timeCol = new VBox();
        timeCol.setMinWidth(55);
        timeCol.setPrefWidth(60);
        timeCol.getChildren().add(sizedLabel(makeHeaderCell("GODZ."), 36));
        for (String hour : HOURS) {
            Label time = new Label(hour);
            time.getStyleClass().add("cal-time-cell");
            time.setMaxWidth(Double.MAX_VALUE);
            timeCol.getChildren().add(sizedLabel(time, 72));
        }
        grafikGrid.getItems().add(timeCol);

        // tworzymy macierz "komorek"
        HBox[][] cells = new HBox[HOURS.length][5];
        for (int d = 0; d < 5; d++) {
            LocalDate day = monday.plusDays(d);
            String lbl = day.getDayOfWeek().getDisplayName(TextStyle.SHORT, PL).toUpperCase()
                    + " " + String.format("%d.%02d", day.getDayOfMonth(), day.getMonthValue());
            VBox dayCol = new VBox();
            dayCol.setMinWidth(80);
            dayCol.getChildren().add(sizedLabel(makeHeaderCell(lbl), 36));
            for (int r = 0; r < HOURS.length; r++) {
                HBox cell = new HBox(3);
                cell.getStyleClass().add("cal-cell");
                cell.setPadding(new Insets(4));
                // ustawiamy sztywna wysokosc komorki, zeby wszystkie wiersze byly rowne
                cell.setPrefHeight(72);
                cell.setMinHeight(72);
                cell.setMaxHeight(72);
                dayCol.getChildren().add(cell);
                cells[r][d] = cell;
            }
            grafikGrid.getItems().add(dayCol);
        }

        // bierzemy id zalogowanego trenera - jak go nie ma to nie ma sensu rysowac zadnych zajec
        String trainerId = SessionManager.getInstance().getLoggedInTrenerId();
        if (trainerId == null) return;

        // pobieramy WSZYSTKIE zajecia z tygodnia, ale filtrujemy tylko te tego konkretnego trenera
        List<Zajecia> weekAll = GrafikRepository.getInstance().findByTydzien(monday);
        for (Zajecia z : weekAll) {
            if (!trainerId.equals(z.getTrenerId())) continue; // nie jego zajecia - pomijamy
            int row = hourToRow(z.getTermin().getHour());
            int col = z.getTermin().getDayOfWeek().getValue() - 1; // poniedzialek=1
            if (row < 0 || col < 0 || col > 4) continue; // weekend albo dziwna godzina - pomijamy
            VBox card = makeCard(z);
            HBox.setHgrow(card, Priority.ALWAYS);
            cells[row][col].getChildren().add(card);
        }
    }

    // helper - ustawia sztywna wysokosc labelki, zeby siatka byla rowna
    private Label sizedLabel(Label l, double height) {
        l.setPrefHeight(height);
        l.setMinHeight(height);
        l.setMaxHeight(height);
        return l;
    }

    // zamienia konkretna godzine na numer wiersza w siatce
    private int hourToRow(int hour) {
        for (int i = 0; i < HOUR_VALUES.length; i++) {
            if (hour == HOUR_VALUES[i]) return i; // idealne trafienie w slot
        }
        if (hour < HOUR_VALUES[0]) return 0;
        for (int i = 0; i < HOUR_VALUES.length - 1; i++) {
            if (hour >= HOUR_VALUES[i] && hour < HOUR_VALUES[i + 1]) return i;
        }
        return HOUR_VALUES.length - 1;
    }

    // tworzy wizualna "karteczke" zajec do wstawienia w siatce
    private VBox makeCard(Zajecia z) {
        VBox card = new VBox(2);
        card.getStyleClass().add("cal-card");
        card.setPadding(new Insets(4, 6, 4, 6));
        card.setCursor(Cursor.HAND);
        // kolor karty generowany na podstawie id zajec (zeby kazde zajecia mialy inny kolor)
        card.setStyle("-fx-background-color: " + ZajeciaColor.colorFor(z.getId()) + ";");

        String salaNazwa = SalaRepository.getInstance().findById(z.getSalaId())
                .map(Sala::getNazwa).orElse("—");
        String czasRange = z.getTermin().format(TIME_FMT) + "–"
                + z.getTermin().plusMinutes(z.getCzasTrwaniaMinut()).format(TIME_FMT);

        Label lNazwa = new Label(z.getNazwa());
        lNazwa.getStyleClass().add("cal-card-title");
        Label lCzas = new Label(czasRange);
        lCzas.getStyleClass().add("cal-card-sub");
        //  pokazujemy liczbe zapisanych/limit
        Label lInfo = new Label(salaNazwa + " · "
                + z.getUczestnicyIds().size() + "/" + z.getLimitUczestnikow());
        lInfo.getStyleClass().add("cal-card-sub");
        card.getChildren().addAll(lNazwa, lCzas, lInfo);

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
        return l;
    }
}
