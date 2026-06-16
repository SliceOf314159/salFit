package com.salfit.controllers;

import com.salfit.model.Sala;
import com.salfit.model.Trener;
import com.salfit.model.Zajecia;
import com.salfit.repository.CzlonekRepository;
import com.salfit.repository.GrafikRepository;
import com.salfit.repository.SalaRepository;
import com.salfit.repository.TrenerRepository;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

public class RaportController {

    @FXML private Label statZajecia;
    @FXML private Label statCzlonkowie;
    @FXML private Label statWygasajace;

    @FXML private ProgressBar barSalaA;
    @FXML private ProgressBar barSalaB;
    @FXML private ProgressBar barSalaC;
    @FXML private ProgressBar barSalaD;

    @FXML private ProgressBar barT1;
    @FXML private ProgressBar barT2;
    @FXML private ProgressBar barT3;
    @FXML private ProgressBar barT4;

    @FXML
    public void initialize() {
        refresh();
    }

    public void refresh() {
        LocalDate monday = LocalDate.now().with(DayOfWeek.MONDAY);

        List<Zajecia> weekZajecia = GrafikRepository.getInstance().findByTydzien(monday);
        List<Zajecia> allZajecia  = GrafikRepository.getInstance().findAll();

        statZajecia.setText(String.valueOf(weekZajecia.size()));
        statCzlonkowie.setText(String.valueOf(CzlonekRepository.getInstance().findAll().size()));
        statWygasajace.setText(String.valueOf(
                CzlonekRepository.getInstance().findKarnetyWygasajace(14).size()));

        int total = Math.max(allZajecia.size(), 1);

        List<Sala> sale = SalaRepository.getInstance().findAll();
        ProgressBar[] salaBars = {barSalaA, barSalaB, barSalaC, barSalaD};
        for (int i = 0; i < salaBars.length && i < sale.size(); i++) {
            if (salaBars[i] == null) continue;
            final String salaId = sale.get(i).getId();
            long count = allZajecia.stream().filter(z -> salaId.equals(z.getSalaId())).count();
            salaBars[i].setProgress((double) count / total);
        }

        List<Trener> trenerzy = TrenerRepository.getInstance().findAll();
        ProgressBar[] trenerBars = {barT1, barT2, barT3, barT4};
        for (int i = 0; i < trenerBars.length && i < trenerzy.size(); i++) {
            if (trenerBars[i] == null) continue;
            final String trenerId = trenerzy.get(i).getId();
            long count = allZajecia.stream().filter(z -> trenerId.equals(z.getTrenerId())).count();
            trenerBars[i].setProgress((double) count / total);
        }
    }

    @FXML public void onEksportCSV() { /* Serialize report data to CSV */ }
    @FXML public void onEksportTXT() { /* Serialize report data to TXT */ }
}
