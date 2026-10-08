package com.naromil.gol.main;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

public class MainController {

    private final Grid grid = new Grid();

    @FXML
    private Canvas gridDisplay;
    @FXML
    private Canvas legendDisplay;
    @FXML
    private Pane gridDisplayPane;

    // Initialize the application
    @FXML
    public void initialize() {
        // Initialize grid
        gridWidthSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 16));
        gridHeightSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 10));

        // Listeners
        intervalScroll.valueProperty().addListener(
            (observable, oldValue, newValue) -> updateInterval(newValue.doubleValue()));
        updateInterval(intervalScroll.getValue());
        gridWidthSpinner.valueProperty().addListener((observable, oldValue, newValue) -> onGridSizeChanged());
        gridHeightSpinner.valueProperty().addListener((observable, oldValue, newValue) -> onGridSizeChanged());

        // The grid canvas takes all the space left over by the controls
        gridDisplay.widthProperty().bind(gridDisplayPane.widthProperty());
        gridDisplay.heightProperty().bind(gridDisplayPane.heightProperty());
        gridDisplay.widthProperty().addListener((observable, oldValue, newValue) -> displayGrid());
        gridDisplay.heightProperty().addListener((observable, oldValue, newValue) -> displayGrid());

        grid.setSize(getGridWidth(), getGridHeight());
        displayGrid();
    }

    // Put the grid on the canvas
    private void displayGrid() {
        Color[][] colorGrid = grid.toColorGrid();
        GraphicsContext gc = gridDisplay.getGraphicsContext2D();
        int rows = colorGrid.length;
        int cols = rows == 0 ? 0 : colorGrid[0].length;
        if (rows == 0 || cols == 0) {
            return;
        }
        double tileWidth = gridDisplay.getWidth() / cols;
        double tileHeight = gridDisplay.getHeight() / rows;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Color color = colorGrid[i][j];
                gc.setFill(javafx.scene.paint.Color.rgb(color.r(), color.g(), color.b()));
                gc.fillRect(j * tileWidth, i * tileHeight, tileWidth, tileHeight);
            }
        }
        drawTileOutlines(gc, rows, cols, tileWidth, tileHeight);
    }

    private void drawTileOutlines(GraphicsContext gc, int rows, int cols, double tileWidth, double tileHeight) {
        double width = gridDisplay.getWidth();
        double height = gridDisplay.getHeight();
        gc.setStroke(javafx.scene.paint.Color.BLACK);
        gc.setLineWidth(1);
        // Half-pixel offsets keep the 1px outline crisp, and the clips keep the outer border inside the canvas.
        for (int i = 0; i <= rows; i++) {
            double y = Math.min(Math.round(i * tileHeight) + 0.5, height - 0.5);
            gc.strokeLine(0, y, width, y);
        }
        for (int j = 0; j <= cols; j++) {
            double x = Math.min(Math.round(j * tileWidth) + 0.5, width - 0.5);
            gc.strokeLine(x, 0, x, height);
        }
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
        displayGrid();
    }

    /* ---- Interval ---- */
    @FXML
    private ScrollBar intervalScroll;
    @FXML
    private Label intervalLabel;
    
    private void updateInterval(double value) {
        intervalLabel.setText("Interval: " + (int) value + " ms");
        if (isRunning()) {
            startSimulation();
        }
    }

    private int getInterval() {
        return (int) intervalScroll.getValue();
    }

    /* ---- Simulation Control ---- */
    boolean isRunning;

    public void setRunning(boolean running) {
        isRunning = running;
        startButton.setText(running ? "Stop" : "Start");
        if (running) {
            startSimulation();
        } else if (simulation != null) {
            simulation.stop();
            simulation = null;
        }
    }

    public boolean isRunning() {
        return isRunning;
    }

    private Animation simulation;

    private Animation createSimulation() {
        // A zero-length frame would stop the timeline instead of running it.
        Duration period = Duration.millis(Math.max(1, getInterval()));
        Timeline timeline = new Timeline(new KeyFrame(period, event -> {
            grid.nextState();
            displayGrid();
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        return timeline;
    }

    private void startSimulation() {
        if (simulation != null) {
            simulation.stop();
        }
        simulation = createSimulation();
        simulation.play();
    }

    /* ---- Buttons ---- */
    @FXML
    private Button startButton;

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

    @FXML
    private void onResetButtonPressed() {
        System.out.println("Reset button pressed.");
        setRunning(false);
        grid.clear();
        displayGrid();
    }
}
