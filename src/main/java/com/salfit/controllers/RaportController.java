package com.salfit.controllers;

import com.salfit.model.Sala;
import com.salfit.model.Trener;
import com.salfit.model.Zajecia;
import com.salfit.repository.CzlonekRepository;
import com.salfit.repository.GrafikRepository;
import com.salfit.repository.SalaRepository;
import com.salfit.repository.TrenerRepository;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.FileWriter;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class RaportController {

    @FXML private Label statZajecia;
    @FXML private Label statCzlonkowie;
    @FXML private Label statWygasajace;

    @FXML private Label labelSalaA;
    @FXML private Label labelSalaB;
    @FXML private Label labelSalaC;
    @FXML private Label labelSalaD;
    @FXML private ProgressBar barSalaA;
    @FXML private ProgressBar barSalaB;
    @FXML private ProgressBar barSalaC;
    @FXML private ProgressBar barSalaD;
    @FXML private Label pctSalaA;
    @FXML private Label pctSalaB;
    @FXML private Label pctSalaC;
    @FXML private Label pctSalaD;

    @FXML private Label labelT1;
    @FXML private Label labelT2;
    @FXML private Label labelT3;
    @FXML private Label labelT4;
    @FXML private ProgressBar barT1;
    @FXML private ProgressBar barT2;
    @FXML private ProgressBar barT3;
    @FXML private ProgressBar barT4;
    @FXML private Label pctT1;
    @FXML private Label pctT2;
    @FXML private Label pctT3;
    @FXML private Label pctT4;

    @FXML private TableView<MonthStats> monthlyTable;
    @FXML private TableColumn<MonthStats, String> colMiesiac;
    @FXML private TableColumn<MonthStats, String> colLiczbaZajec;
    @FXML private TableColumn<MonthStats, String> colUczestnicy;
    @FXML private TableColumn<MonthStats, String> colObloz;

    private static final Locale PL = Locale.forLanguageTag("pl");
    private static final int MAX_MONTHS = 12;

    private List<MonthStats> monthlyStats = List.of();

    public record MonthStats(YearMonth month, int liczbaZajec, int uczestnicy, double obloz) {
        String label() {
            String name = month.getMonth().getDisplayName(TextStyle.FULL_STANDALONE, PL);
            return name.substring(0, 1).toUpperCase() + name.substring(1) + " " + month.getYear();
        }
    }

    @FXML
    public void initialize() {
        setupMonthlyColumns();
        refresh();
    }

    private void setupMonthlyColumns() {
        colMiesiac.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().label()));
        colLiczbaZajec.setCellValueFactory(d -> new SimpleStringProperty(
                String.valueOf(d.getValue().liczbaZajec())));
        colUczestnicy.setCellValueFactory(d -> new SimpleStringProperty(
                String.valueOf(d.getValue().uczestnicy())));
        colObloz.setCellValueFactory(d -> new SimpleStringProperty(
                String.format("%.0f%%", d.getValue().obloz() * 100)));
    }

    public void refresh() {
        LocalDate monday = LocalDate.now().with(DayOfWeek.MONDAY);

        List<Zajecia> weekZajecia = GrafikRepository.getInstance().findByTydzien(monday);
        List<Zajecia> allZajecia  = GrafikRepository.getInstance().findAll();

        statZajecia.setText(String.valueOf(weekZajecia.size()));
        statCzlonkowie.setText(String.valueOf(CzlonekRepository.getInstance().findAll().size()));
        statWygasajace.setText(String.valueOf(
                CzlonekRepository.getInstance().findKarnetyWygasajace(14).size()));

        List<Sala> sale = SalaRepository.getInstance().findAll();
        Label[] salaLabels = {labelSalaA, labelSalaB, labelSalaC, labelSalaD};
        ProgressBar[] salaBars = {barSalaA, barSalaB, barSalaC, barSalaD};
        Label[] salaPcts = {pctSalaA, pctSalaB, pctSalaC, pctSalaD};
        long[] salaCounts = new long[salaBars.length];
        for (int i = 0; i < salaBars.length && i < sale.size(); i++) {
            final String salaId = sale.get(i).getId();
            salaCounts[i] = allZajecia.stream().filter(z -> salaId.equals(z.getSalaId())).count();
        }
        long maxSala = Math.max(1, java.util.Arrays.stream(salaCounts).max().orElse(1));
        for (int i = 0; i < salaBars.length; i++) {
            boolean has = i < sale.size();
            setRowVisible(salaLabels[i], salaBars[i], salaPcts[i], has);
            if (!has) continue;
            double progress = (double) salaCounts[i] / maxSala;
            salaLabels[i].setText(sale.get(i).getNazwa());
            salaBars[i].setProgress(progress);
            salaPcts[i].setText(String.format("%.0f%%", progress * 100));
        }

        List<Trener> trenerzy = TrenerRepository.getInstance().findAll();
        Label[] trenerLabels = {labelT1, labelT2, labelT3, labelT4};
        ProgressBar[] trenerBars = {barT1, barT2, barT3, barT4};
        Label[] trenerPcts = {pctT1, pctT2, pctT3, pctT4};
        long[] trenerCounts = new long[trenerBars.length];
        for (int i = 0; i < trenerBars.length && i < trenerzy.size(); i++) {
            final String trenerId = trenerzy.get(i).getId();
            trenerCounts[i] = allZajecia.stream().filter(z -> trenerId.equals(z.getTrenerId())).count();
        }
        long maxTrener = Math.max(1, java.util.Arrays.stream(trenerCounts).max().orElse(1));
        for (int i = 0; i < trenerBars.length; i++) {
            boolean has = i < trenerzy.size();
            setRowVisible(trenerLabels[i], trenerBars[i], trenerPcts[i], has);
            if (!has) continue;
            trenerLabels[i].setText(trenerzy.get(i).getImieNazwisko());
            trenerBars[i].setProgress((double) trenerCounts[i] / maxTrener);
            trenerPcts[i].setText(trenerCounts[i] + " zaj.");
        }

        List<YearMonth> activeMonths = allZajecia.stream()
                .filter(z -> z.getTermin() != null)
                .map(z -> YearMonth.from(z.getTermin()))
                .distinct()
                .sorted(java.util.Comparator.reverseOrder())
                .limit(MAX_MONTHS)
                .collect(Collectors.toList());
        if (activeMonths.isEmpty()) activeMonths = List.of(YearMonth.now());

        List<MonthStats> rows = new ArrayList<>();
        for (YearMonth ym : activeMonths) {
            List<Zajecia> byMonth = allZajecia.stream()
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
        monthlyStats = rows;
        monthlyTable.setItems(FXCollections.observableArrayList(rows));
    }

    private void setRowVisible(Label label, ProgressBar bar, Label pct, boolean visible) {
        for (var node : new javafx.scene.Node[]{label, bar, pct}) {
            node.setVisible(visible);
            node.setManaged(visible);
        }
    }

    @FXML
    public void onEksportCSV() {
        export("csv", this::writeCsv);
    }

    @FXML
    public void onEksportTXT() {
        export("txt", this::writeTxt);
    }

    private void export(String extension, ExportWriter writer) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Eksportuj raport");
        chooser.setInitialFileName("raport_" + YearMonth.now() + "." + extension);
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(
                extension.toUpperCase() + " (*." + extension + ")", "*." + extension));
        Window owner = statZajecia.getScene().getWindow();
        java.io.File file = chooser.showSaveDialog(owner);
        if (file == null) return;
        try (FileWriter fw = new FileWriter(file)) {
            writer.write(fw);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private interface ExportWriter {
        void write(FileWriter fw) throws IOException;
    }

    private void writeCsv(FileWriter fw) throws IOException {
        fw.write("Zajec w tym tygodniu," + statZajecia.getText() + "\n");
        fw.write("Aktywni czlonkowie," + statCzlonkowie.getText() + "\n");
        fw.write("Karnety wygasajace,"  + statWygasajace.getText() + "\n\n");
        fw.write("Miesiac,Liczba zajec,Uczestnicy lacznie,Obloznienie\n");
        for (MonthStats m : monthlyStats) {
            fw.write(m.label() + "," + m.liczbaZajec() + "," + m.uczestnicy()
                    + "," + String.format("%.0f%%", m.obloz() * 100) + "\n");
        }
    }

    private void writeTxt(FileWriter fw) throws IOException {
        fw.write("RAPORT SALFIT\n");
        fw.write("=============\n\n");
        fw.write("Zajec w tym tygodniu: " + statZajecia.getText() + "\n");
        fw.write("Aktywni czlonkowie: "   + statCzlonkowie.getText() + "\n");
        fw.write("Karnety wygasajace: "   + statWygasajace.getText() + "\n\n");
        fw.write("Statystyki miesieczne:\n");
        for (MonthStats m : monthlyStats) {
            fw.write(String.format("  %-20s zajec: %-4d uczestnicy: %-4d obloznienie: %.0f%%%n",
                    m.label(), m.liczbaZajec(), m.uczestnicy(), m.obloz() * 100));
        }
    }
}
