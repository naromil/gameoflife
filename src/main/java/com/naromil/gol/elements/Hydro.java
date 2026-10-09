package com.naromil.gol.elements;

import com.naromil.gol.main.Color;
import com.naromil.gol.main.Grid;

public class Hydro extends Tile {
    public static final Color DEFAULT_COLOR = new Color(76, 194, 241);
    public static final int MAX_TICKS_RAIN = 15;
    public static final double BASE_VAPORIZE_CHANCE = 0.50;
    public static final double BASE_VAPORIZE_STEAM_CHANCE = 0.30;

    private int tickRain = 0;

    public Hydro() {
        super(Color.similarColor(DEFAULT_COLOR));
    }

    public void globalAct(Grid grid, Grid newGrid, int width, int height) {
        // rain: spawn
        int rainX = (int) (Math.random() * width);
        int rainY = (int) (Math.random() * height);
        newGrid.setTile(rainY, rainX, new Hydro());
    }

    @Override
    public boolean act(Grid grid, Grid newGrid, int i, int j, int width, int height) {
        boolean flg = true;

        // rain: dry out
        if (Grids.doesExistWithinDistance(grid, i, j, width, height, Hydro.class, 1)) {
            tickRain = 0;
        } else {
            tickRain++;
            if (tickRain >= MAX_TICKS_RAIN) {
                return false;
            }
        }

        // vaporize
        for (Point2D adjacent : Grids.getNeighbors(i, j, width, height)) {
            int newI = i + adjacent.x();
            int newJ = j + adjacent.y();
            if (grid.getTile(newI, newJ) instanceof Pyro && Math.random() < BASE_VAPORIZE_CHANCE) {
                if (newGrid.getTile(newI, newJ) instanceof Pyro) {
                    newGrid.setTile(newI, newJ, new Empty());
                }
                if (Math.random() < BASE_VAPORIZE_STEAM_CHANCE) {
                    flg = false; // vaporize: steam
                }
            }
        }
        
        return flg;
    }
}
