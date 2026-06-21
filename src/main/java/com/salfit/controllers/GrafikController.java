package com.salfit.controllers;

import com.salfit.model.Sala;
import com.salfit.model.Trener;
import com.salfit.model.Zajecia;
import com.salfit.repository.GrafikRepository;
import com.salfit.repository.SalaRepository;
import com.salfit.repository.TrenerRepository;
import com.salfit.util.ZajeciaColor;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
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

// Glowny kontroler grafika tygodniowego dla PANELU ADMINA - widzi WSZYSTKICH trenerow
// i WSZYSTKIE zajecia (w odroznieniu od TrainerGrafikController, ktory widzi tylko swoje zajecia).
// Klik na karte zajec otwiera dialog edycji (a nie liste uczestnikow jak u trenera)
public class GrafikController {

    @FXML private Label weekLabel;
    @FXML private SplitPane grafikGrid;

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

    // przycisk "dodaj zajecia" - otwiera dialog ale bez przekazanych zajec (null = tworzymy nowe)
    @FXML
    public void onDodajZajecia() {
        openZajeciaDialog(null);
    }

    // odswieza naglowek tygodnia i przerysowuje cala siatke
    public void refresh() {
        LocalDate monday = aktualnyTydzien.with(DayOfWeek.MONDAY);
        LocalDate friday = monday.plusDays(4);
        weekLabel.setText(String.format("Tydzień %d–%d %s %d",
                monday.getDayOfMonth(), friday.getDayOfMonth(),
                monday.getMonth().getDisplayName(TextStyle.FULL, PL),
                monday.getYear()));
        renderTydzien(monday);
    }

    // rysuje cala siatke grafika - kolumna godzin po lewej + 5 kolumn dni roboczych
    private void renderTydzien(LocalDate monday) {
        grafikGrid.getItems().clear();

        VBox timeCol = new VBox();
        timeCol.setMinWidth(55);
        timeCol.setPrefWidth(60);
        timeCol.getChildren().add(sizedLabel(makeHeaderCell("GODZ."), 36));
        for (String hour : HOURS) {
            Label timeCell = new Label(hour);
            timeCell.getStyleClass().add("cal-time-cell");
            timeCell.setMaxWidth(Double.MAX_VALUE);
            timeCol.getChildren().add(sizedLabel(timeCell, 72));
        }
        grafikGrid.getItems().add(timeCol);

        // macierz komorek (wiersz=godzina, kolumna=dzien) - tu wstawimy karty zajec
        HBox[][] cells = new HBox[HOURS.length][5];
        for (int d = 0; d < 5; d++) {
            LocalDate day = monday.plusDays(d);
            VBox dayCol = new VBox();
            dayCol.setMinWidth(80);
            dayCol.getChildren().add(sizedLabel(makeHeaderCell(day.format(DAY_FMT).toUpperCase(PL)), 36));
            for (int r = 0; r < HOURS.length; r++) {
                HBox cell = new HBox(3);
                cell.getStyleClass().add("cal-cell");
                cell.setPadding(new Insets(4));
                cell.setPrefHeight(72);
                cell.setMinHeight(72);
                cell.setMaxHeight(72);
                dayCol.getChildren().add(cell);
                cells[r][d] = cell;
            }
            grafikGrid.getItems().add(dayCol);
        }

        // bierzemy WSZYSTKIE zajecia z tygodnia
        List<Zajecia> zajecia = GrafikRepository.getInstance().findByTydzien(monday);
        for (Zajecia z : zajecia) {
            int row = hourToRow(z.getTermin().getHour());
            int col = z.getTermin().getDayOfWeek().getValue() - 1; // Mon=0, ..., Fri=4
            if (row < 0 || col < 0 || col > 4) continue; // poza siatka (np weekend) - ignorujemy
            VBox card = makeCard(z);
            HBox.setHgrow(card, Priority.ALWAYS);
            cells[row][col].getChildren().add(card);
        }
    }

    private Label sizedLabel(Label l, double height) {
        l.setPrefHeight(height);
        l.setMinHeight(height);
        l.setMaxHeight(height);
        return l;
    }

    // zamienia konkretna godzine na numer wiersza siatki
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

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    // tworzy karteczke zajec do wstawienia w siatce -  pokazujemy imie trenera + nazwe sali,
    // bo admin musi widziec KTO i GDZIE prowadzi te zajecia
    private VBox makeCard(Zajecia z) {
        VBox card = new VBox(2);
        card.getStyleClass().add("cal-card");
        card.setPadding(new Insets(4, 6, 4, 6));
        card.setStyle("-fx-background-color: " + ZajeciaColor.colorFor(z.getId()) + ";");

        String trenerName = TrenerRepository.getInstance().findById(z.getTrenerId())
                .map(Trener::getImieNazwisko).orElse("—");
        String salaNazwa = SalaRepository.getInstance().findById(z.getSalaId())
                .map(Sala::getNazwa).orElse("—");
        String czasRange = z.getTermin().format(TIME_FMT) + "–"
                + z.getTermin().plusMinutes(z.getCzasTrwaniaMinut()).format(TIME_FMT);

        Label lNazwa = new Label(z.getNazwa());
        lNazwa.getStyleClass().add("cal-card-title");
        Label lCzas = new Label(czasRange);
        lCzas.getStyleClass().add("cal-card-sub");
        Label lInfo = new Label(trenerName + " · " + salaNazwa);
        lInfo.getStyleClass().add("cal-card-sub");
        lInfo.setWrapText(true); // imie trenera + sala moze byc dlugie, zawijamy tekst zeby sie zmiescilo
        card.getChildren().addAll(lNazwa, lCzas, lInfo);

        // klik na karte - otwiera dialog EDYCJI tych zajec
        card.setOnMouseClicked(e -> openZajeciaDialog(z));
        return card;
    }

    // otwiera modalny dialog dodania/edycji zajec
    private void openZajeciaDialog(Zajecia zajecia) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/views/dialogs/AddEditZajeciaDialog.fxml"));
            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle(zajecia != null ? "Edycja zajęć" : "Dodanie zajęć");
            dialog.setScene(new Scene(loader.load()));
            double maxHeight = javafx.stage.Screen.getPrimary().getVisualBounds().getHeight() - 80;
            dialog.setMaxHeight(maxHeight);
            AddEditZajeciaDialogController ctrl = loader.getController();
            if (zajecia != null) {
                ctrl.setZajecia(zajecia);
                ctrl.setEditMode(true);
            }
            dialog.showAndWait();
            refresh(); // po zamknieciu dialogu odswiezamy grafik
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Label makeHeaderCell(String text) {
        Label l = new Label(text);
        l.getStyleClass().add("cal-header-cell");
        l.setMaxWidth(Double.MAX_VALUE);
        return l;
    }
}
