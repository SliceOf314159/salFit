package com.salfit;

import javafx.application.Application;
import javafx.stage.Stage;

//  klasa startowa  aplikacji - tutaj wszystko sie zaczyna
//  Application to klasa z JavaFX
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        // primaryStage to glowne okno aplikacji
        SceneManager.getInstance().init(primaryStage);
        // po starcie aplikacji zawsze najpierw pokazujemy ekran logowania
        SceneManager.getInstance().showLogin();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
