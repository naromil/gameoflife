package com.naromil.gol.main;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;

public class MainController {

  // All the UI elements that we need.
  @FXML
  private Canvas gridDisplay;
  @FXML
  private Canvas legendDisplay;
  @FXML
  private Button startButton;
  @FXML
  private ScrollBar intervalScroll;
  @FXML
  private Label intervalLabel;

  @FXML
  public void initialize() {
    intervalScroll.valueProperty().addListener(
        (observable, oldValue, newValue) -> updateInterval(newValue.doubleValue()));
    updateInterval(intervalScroll.getValue());
  }

  private void updateInterval(double value) {
    intervalLabel.setText("Interval: " + (int) value + " ms");
  }

  @FXML
  private void onStartButtonPressed() {

    System.out.println("Start button pressed.");
    if (startButton.getText().equals("Start")) {

      startButton.setText("Stop");

    } else if (startButton.getText().equals("Stop")) {

      startButton.setText("Start");

    }
    else {
      throw new IllegalStateException("Start button has unexpected text: " + startButton.getText());
    }
  }

  @FXML
  private void onResetButtonPressed() {
    System.out.println("Reset button pressed.");
  }
}
