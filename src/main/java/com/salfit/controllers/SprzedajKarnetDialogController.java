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

// Kontroler dialogu sprzedazy/przedluzenia karnetu. Ma wlasny, recznie zrobiony
public class SprzedajKarnetDialogController {

    @FXML private ComboBox<Czlonek> fieldCzlonek;
    @FXML private ComboBox<String> fieldRodzaj;
    @FXML private ComboBox<String> fieldPlatnosc;
    @FXML private DatePicker fieldDataOd;
    @FXML private DatePicker fieldDataDo;
    @FXML private Label calMonthLabel;
    @FXML private GridPane calGrid; // tutaj rysujemy wlasny mini-kalendarz
    @FXML private Label formError;

    private static final Gson GSON = Repository.createGson();
    private YearMonth displayedMonth; // ktory miesiac jest aktualnie wyswietlany w mini-kalendarzu

    @FXML
    public void initialize() {
        List<Czlonek> czlonkowie = CzlonekRepository.getInstance().findAll();
        fieldCzlonek.setItems(FXCollections.observableArrayList(czlonkowie));
        fieldCzlonek.setConverter(new StringConverter<>() {
            @Override public String toString(Czlonek c) { return c != null ? c.getImieNazwisko() : ""; }
            @Override public Czlonek fromString(String s) { return null; }
        });
        if (!czlonkowie.isEmpty()) fieldCzlonek.getSelectionModel().selectFirst(); // domyslnie pierwszy z listy

        fieldRodzaj.setItems(FXCollections.observableArrayList("Miesięczny", "Kwartalny", "Roczny"));
        fieldPlatnosc.setItems(FXCollections.observableArrayList("Karta", "Gotówka", "Przelew"));
        fieldPlatnosc.getSelectionModel().selectFirst();

        LocalDate today = LocalDate.now();
        fieldDataOd.setValue(today);
        fieldDataDo.setValue(today.plusMonths(1)); // domyslnie zakladamy karnet miesieczny
        displayedMonth = YearMonth.from(today);
        renderCalendar();
    }

    // wywolywane z zewnatrz - jak otwieramy dialog z juz "zaznaczonym" czlonkiem
    public void setCzlonek(Czlonek c) {
        fieldCzlonek.getSelectionModel().select(c);
    }

    // przelicza "dataDo" na podstawie wybranego rodzaju karnetu i aktualnej "dataOd"
    @FXML
    private void onRodzajChanged() {
        String rodzaj = fieldRodzaj.getValue();
        if (rodzaj == null || fieldDataOd.getValue() == null) return;
        LocalDate od = fieldDataOd.getValue();
        // proste przyblizenie - miesiac=30 dni, kwartal=90, rok=365
        fieldDataDo.setValue(switch (rodzaj) {
            case "Miesięczny" -> od.plusDays(30);
            case "Kwartalny"  -> od.plusDays(90);
            case "Roczny"     -> od.plusDays(365);
            default           -> od.plusDays(30);
        });
    }

    // jak zmieni sie data "od", to przeliczamy "do" na nowo
    @FXML private void onDataOdChanged() { onRodzajChanged(); }

    @FXML private void prevMonth() { displayedMonth = displayedMonth.minusMonths(1); renderCalendar(); }
    @FXML private void nextMonth() { displayedMonth = displayedMonth.plusMonths(1);  renderCalendar(); }

    // rysuje od zera caly mini-kalendarz dla aktualnie wybranego miesiaca
    private void renderCalendar() {
        calGrid.getChildren().clear(); // czyscimy stary kalendarz przed narysowaniem nowego
        String monthName = displayedMonth.getMonth()
                .getDisplayName(TextStyle.FULL_STANDALONE, Locale.forLanguageTag("pl"));
        calMonthLabel.setText(monthName + " " + displayedMonth.getYear());

        // wiersz naglowkowy z nazwami dni tygodnia
        String[] days = {"Pn", "Wt", "Śr", "Cz", "Pt", "Sb", "Nd"};
        for (int i = 0; i < 7; i++) {
            Label lbl = new Label(days[i]);
            lbl.getStyleClass().add("mini-cal-weekday");
            calGrid.add(lbl, i, 0); // kolumna i, wiersz 0
        }
        LocalDate first = displayedMonth.atDay(1);
        int col = first.getDayOfWeek().getValue() - 1;
        int row = 1;
        for (int day = 1; day <= displayedMonth.lengthOfMonth(); day++) {
            final int d = day;
            Button btn = new Button(String.valueOf(day));
            btn.getStyleClass().add("mini-cal-day");
            // klikniecie na dzien - ustawia "dataOd" na ten dzien i przelicza "dataDo"
            btn.setOnAction(e -> { fieldDataOd.setValue(displayedMonth.atDay(d)); onRodzajChanged(); });
            calGrid.add(btn, col, row);
            col++;
            if (col == 7) { col = 0; row++; } //koniec tygodnia - przechodzimy do nowego wiersza
        }
    }


    @FXML private void onNowyCzlonek() {}

    // glowna metoda zapisu - tworzy nowy karnet dla wybranego czlonka
    @FXML
    private void onSprzedaj() {
        Czlonek czlonek = fieldCzlonek.getValue();
        if (czlonek == null || fieldRodzaj.getValue() == null
                || fieldDataOd.getValue() == null || fieldDataDo.getValue() == null) {
            showError("Wypełnij wszystkie wymagane pola.");
            return;
        }
        hideError();

        // zamieniamy polska etykiete na wartosc enuma RodzajKarnetu
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

    // "Przedluz" robi DOKLADNIE to samo co "Sprzedaj" (tworzy nowy karnet) - to jest jedna
    // i ta sama metoda pod dwoma nazwami przyciskow
    @FXML private void onPrzedluz() { onSprzedaj(); }
    @FXML private void onCancel()   { closeDialog(); }

    private void closeDialog() {
        ((Stage) fieldRodzaj.getScene().getWindow()).close();
    }

    private void showError(String msg) { formError.setText(msg); formError.setVisible(true); }
    private void hideError() { formError.setVisible(false); }
}
