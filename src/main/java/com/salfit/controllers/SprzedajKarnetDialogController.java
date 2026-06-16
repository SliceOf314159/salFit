package com.salfit.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.salfit.model.Czlonek;
import com.salfit.model.Karnet;
import com.salfit.model.RodzajKarnetu;
import com.salfit.repository.CzlonekRepository;
import com.salfit.repository.Repository;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

public class SprzedajKarnetDialogController {

    @FXML private ComboBox<Czlonek> fieldCzlonek;
    @FXML private ComboBox<String> fieldRodzaj;
    @FXML private ComboBox<String> fieldPlatnosc;
    @FXML private DatePicker fieldDataOd;
    @FXML private DatePicker fieldDataDo;
    @FXML private Label calMonthLabel;
    @FXML private GridPane calGrid;

    private static final Gson GSON = Repository.createGson();
    private YearMonth displayedMonth;

    @FXML
    public void initialize() {
        List<Czlonek> czlonkowie = CzlonekRepository.getInstance().findAll();
        fieldCzlonek.setItems(FXCollections.observableArrayList(czlonkowie));
        fieldCzlonek.setConverter(new StringConverter<>() {
            @Override public String toString(Czlonek c) { return c != null ? c.getImieNazwisko() : ""; }
            @Override public Czlonek fromString(String s) { return null; }
        });
        if (!czlonkowie.isEmpty()) fieldCzlonek.getSelectionModel().selectFirst();

        fieldRodzaj.setItems(FXCollections.observableArrayList("Miesięczny", "Kwartalny", "Roczny"));
        fieldPlatnosc.setItems(FXCollections.observableArrayList("Karta", "Gotówka", "Przelew"));
        fieldPlatnosc.getSelectionModel().selectFirst();

        LocalDate today = LocalDate.now();
        fieldDataOd.setValue(today);
        fieldDataDo.setValue(today.plusMonths(1));
        displayedMonth = YearMonth.from(today);
        renderCalendar();
    }

    public void setCzlonek(Czlonek c) {
        fieldCzlonek.getSelectionModel().select(c);
    }

    @FXML
    private void onRodzajChanged() {
        String rodzaj = fieldRodzaj.getValue();
        if (rodzaj == null || fieldDataOd.getValue() == null) return;
        LocalDate od = fieldDataOd.getValue();
        fieldDataDo.setValue(switch (rodzaj) {
            case "Miesięczny" -> od.plusDays(30);
            case "Kwartalny"  -> od.plusDays(90);
            case "Roczny"     -> od.plusDays(365);
            default           -> od.plusDays(30);
        });
    }

    @FXML private void onDataOdChanged() { onRodzajChanged(); }

    @FXML private void prevMonth() { displayedMonth = displayedMonth.minusMonths(1); renderCalendar(); }
    @FXML private void nextMonth() { displayedMonth = displayedMonth.plusMonths(1);  renderCalendar(); }

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
        int col = first.getDayOfWeek().getValue() - 1;
        int row = 1;
        for (int day = 1; day <= displayedMonth.lengthOfMonth(); day++) {
            final int d = day;
            Button btn = new Button(String.valueOf(day));
            btn.getStyleClass().add("mini-cal-day");
            btn.setOnAction(e -> { fieldDataOd.setValue(displayedMonth.atDay(d)); onRodzajChanged(); });
            calGrid.add(btn, col, row);
            col++;
            if (col == 7) { col = 0; row++; }
        }
    }

    @FXML private void onNowyCzlonek() { /* Navigate to add member — handled by parent controller */ }

    @FXML
    private void onSprzedaj() {
        Czlonek czlonek = fieldCzlonek.getValue();
        if (czlonek == null || fieldRodzaj.getValue() == null
                || fieldDataOd.getValue() == null || fieldDataDo.getValue() == null) return;

        RodzajKarnetu rodzaj = switch (fieldRodzaj.getValue()) {
            case "Kwartalny" -> RodzajKarnetu.KWARTALNY;
            case "Roczny"    -> RodzajKarnetu.ROCZNY;
            default          -> RodzajKarnetu.MIESIECZNY;
        };

        JsonObject obj = new JsonObject();
        obj.addProperty("czlonekId", czlonek.getId());
        obj.addProperty("rodzaj",    rodzaj.name());
        obj.addProperty("dataOd",    fieldDataOd.getValue().toString());
        obj.addProperty("dataDo",    fieldDataDo.getValue().toString());

        Karnet karnet = GSON.fromJson(obj, Karnet.class);
        CzlonekRepository.getInstance().saveKarnet(karnet);
        closeDialog();
    }

    @FXML private void onPrzedluz() { onSprzedaj(); }
    @FXML private void onCancel()   { closeDialog(); }

    private void closeDialog() {
        ((Stage) fieldRodzaj.getScene().getWindow()).close();
    }
}
