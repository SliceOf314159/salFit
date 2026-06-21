package com.salfit.controllers;

import com.salfit.SessionManager;
import com.salfit.model.Zajecia;
import com.salfit.repository.GrafikRepository;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

// Kontroler widoku "aktywnosc" w panelu trenera - statystyki dotyczace tylko TEGO trenera
// (ile mial zajec w tym tygodniu, jakie obloznienie, statystyki miesiac po miesiacu)
public class TrainerAktywnoscController {

    @FXML private Label statWeek;
    @FXML private Label statTotal;
    @FXML private Label statObloz;
    @FXML private TableView<MonthStats> aktywnoscTable;
    @FXML private TableColumn<MonthStats, String> colMiesiac;
    @FXML private TableColumn<MonthStats, String> colLiczbaZajec;
    @FXML private TableColumn<MonthStats, String> colUczestnicy;
    @FXML private TableColumn<MonthStats, String> colObloz;

    private static final Locale PL = Locale.forLanguageTag("pl");
    private static final int MAX_MONTHS = 12; // nie pokazujemy wiecej niz ostatnie 12 miesiecy w tabeli

    // record mala klasa danych do trzymania statystyk jednego miesiaca
    public record MonthStats(YearMonth month, int liczbaZajec, int uczestnicy, double obloz) {
        // metoda formatujaca nazwe miesiaca z duzej litery
        String label() {
            String name = month.getMonth().getDisplayName(TextStyle.FULL_STANDALONE, PL);
            return name.substring(0, 1).toUpperCase() + name.substring(1) + " " + month.getYear();
        }
    }

    @FXML
    public void initialize() {
        setupColumns();
        loadData();
    }

    private void setupColumns() {
        colMiesiac.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().label()));
        colLiczbaZajec.setCellValueFactory(d -> new SimpleStringProperty(
                String.valueOf(d.getValue().liczbaZajec())));
        colUczestnicy.setCellValueFactory(d -> new SimpleStringProperty(
                String.valueOf(d.getValue().uczestnicy())));
        colObloz.setCellValueFactory(d -> new SimpleStringProperty(
                String.format("%.0f%%", d.getValue().obloz() * 100))); // format jako procent, np "75%"
    }

    private void loadData() {
        // bierzemy id zalogowanego trenera - jak go nie ma, to po prostu nie robimy nic (brak danych do pokazania)
        String trainerId = SessionManager.getInstance().getLoggedInTrenerId();
        if (trainerId == null) return;

        // wszystkie zajecia tego trenera (bez wzgledu na date - cala historia)
        List<Zajecia> byTrener = GrafikRepository.getInstance().findByTrener(trainerId);

        // zajecia tego trenera, ale tylko w aktualnym tygodniu
        LocalDate monday = LocalDate.now().with(DayOfWeek.MONDAY);
        List<Zajecia> byWeek = GrafikRepository.getInstance().findByTydzien(monday)
                .stream().filter(z -> trainerId.equals(z.getTrenerId())).collect(Collectors.toList());

        statWeek.setText(String.valueOf(byWeek.size()));
        statTotal.setText(String.valueOf(byTrener.size()));

        // srednie obloznienie - usrednione "wypelnienie" wszystkich zajec tego trenera
        // (np jak miejsc bylo 20 a przyszlo 10 ludzi to obloznienie tych zajec = 0.5)
        double avgObloz = byTrener.isEmpty() ? 0.0
                : byTrener.stream()
                        .mapToDouble(z -> z.getLimitUczestnikow() == 0 ? 0.0
                                : (double) z.getUczestnicyIds().size() / z.getLimitUczestnikow())
                        .average().orElse(0.0);
        statObloz.setText(String.format("%.0f%%", avgObloz * 100));

        // zbieramy liste miesiecy w ktorych trener mial jakies zajecia
        List<YearMonth> activeMonths = byTrener.stream()
                .filter(z -> z.getTermin() != null)
                .map(z -> YearMonth.from(z.getTermin()))
                .distinct()
                .sorted(java.util.Comparator.reverseOrder())
                .limit(MAX_MONTHS)
                .collect(Collectors.toList());
        // jak trener nie ma jeszcze zadnych zajec, pokazujemy chociaz aktualny miesiac
        if (activeMonths.isEmpty()) activeMonths = List.of(YearMonth.now());

        // dla kazdego miesiaca liczymy statystyki: liczba zajec, suma uczestnikow, srednie obloznienie
        List<MonthStats> rows = new java.util.ArrayList<>();
        for (YearMonth ym : activeMonths) {
            List<Zajecia> byMonth = byTrener.stream()
                    .filter(z -> z.getTermin() != null && YearMonth.from(z.getTermin()).equals(ym))
                    .collect(Collectors.toList());
            int uczestnicy = byMonth.stream().mapToInt(z -> z.getUczestnicyIds().size()).sum();
            double obloz = byMonth.isEmpty() ? 0.0
                    : byMonth.stream()
                            .mapToDouble(z -> z.getLimitUczestnikow() == 0 ? 0.0
                                    : (double) z.getUczestnicyIds().size() / z.getLimitUczestnikow())
                            .average().orElse(0.0);
            rows.add(new MonthStats(ym, byMonth.size(), uczestnicy, obloz));
        }

        ObservableList<MonthStats> items = FXCollections.observableArrayList(rows);
        aktywnoscTable.setItems(items);
    }
}
