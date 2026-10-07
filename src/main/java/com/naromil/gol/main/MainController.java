package com.naromil.gol.main;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;

public class MainController {

    private final Grid grid = new Grid();

    @FXML
    private Canvas gridDisplay;
    @FXML
    private Canvas legendDisplay;

    @FXML
    public void initialize() {
        // Listeners
        intervalScroll.valueProperty().addListener(
            (observable, oldValue, newValue) -> updateInterval(newValue.doubleValue()));
        updateInterval(intervalScroll.getValue());
        gridWidthSpinner.valueProperty().addListener((observable, oldValue, newValue) -> onGridSizeChanged());
        gridHeightSpinner.valueProperty().addListener((observable, oldValue, newValue) -> onGridSizeChanged());

        // Initialize grid
        gridWidthSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 16));
        gridHeightSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 10));

        grid.setSize(getGridWidth(), getGridHeight());
    }

    /* ---- Grid Settings ---- */
    @FXML
    private Spinner<Integer> gridWidthSpinner;
    @FXML
    private Spinner<Integer> gridHeightSpinner;

    public int getGridWidth() {
        return gridWidthSpinner.getValue();
    }
    public int getGridHeight() {
        return gridHeightSpinner.getValue();
    }

    @FXML
    private void onGridSizeChanged() {
        int newWidth = getGridWidth();
        int newHeight = getGridHeight();
        System.out.println("Grid size changed to: " + newWidth + "x" + newHeight);
        grid.setSize(newWidth, newHeight);
    }

    /* ---- Interval ---- */
    @FXML
    private ScrollBar intervalScroll;
    @FXML
    private Label intervalLabel;

    private void updateInterval(double value) {
        intervalLabel.setText("Interval: " + (int) value + " ms");
    }

    /* ---- Start and stop ---- */
    @FXML
    private Button startButton;
    boolean isRunning;

    public void setRunning(boolean running) {
        isRunning = running;
        startButton.setText(running ? "Stop" : "Start");
    }
    public boolean isRunning() {
        return startButton.getText().equals("Stop");
    }

    @FXML
    private void onStartButtonPressed() {
        System.out.println("Start button pressed.");
        if (startButton.getText().equals("Start")) {
            setRunning(true);
        } else if (startButton.getText().equals("Stop")) {
            setRunning(false);
        } else {
            throw new IllegalStateException("Start button has unexpected text: " + startButton.getText());
        }
    }

    /* ---- Reset ---- */
    @FXML
    private void onResetButtonPressed() {
        System.out.println("Reset button pressed.");
        grid.clear();
    }
}
