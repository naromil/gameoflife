package com.naromil.gol;

import com.naromil.gol.display.GridDisplay;

public class Sim {
    public static void main(int duration, int gridHeight, int gridWidth) {
        // Initialize the grid with some initial state
        final Grid grid = new Grid(gridHeight, gridWidth);
        GridDisplay.put(grid.toColorGrid());

        while (duration > 0) {
            try {
                Thread.sleep(1000);
                duration--;

                grid.update();
                GridDisplay.put(grid.toColorGrid());

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
