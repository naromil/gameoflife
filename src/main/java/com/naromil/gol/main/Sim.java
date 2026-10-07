package com.naromil.gol.main;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class Sim extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        double screenWidth = screenBounds.getWidth();
        double screenHeight = screenBounds.getHeight();

        double calculatedWidth = screenWidth * (960.0 / 1920.0);
        double calculatedHeight = screenHeight * (640.0 / 1080.0);

        FXMLLoader fxmlLoader = new FXMLLoader(Sim.class.getResource("main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), calculatedWidth, calculatedHeight);

        // MainController mainController = fxmlLoader.getController();

        stage.setTitle("Game Of Life - naromil");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
