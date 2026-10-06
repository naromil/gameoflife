package com.naromil.gol;

public class Sim {
    public static void main(int duration, int gridHeight, int gridWidth) {

        final Grid grid = new Grid(gridHeight, gridWidth);
        GridDisplay.put(grid.getColorGrid());

        while (duration > 0) {
            try {
                Thread.sleep(1000);
                duration--;

                grid.update();
                GridDisplay.put(grid.getColorGrid());

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
