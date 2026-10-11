package com.naromil.gol.elements;

import com.naromil.gol.main.Color;
import com.naromil.gol.main.Grid;

public class Hydro extends Tile {
    public static final Color DEFAULT_COLOR = new Color(76, 194, 241);
    public static final int MAX_TICKS_RAIN = 15;
    public static final double BASE_VAPORIZE_CHANCE = 0.50;
    public static final double BASE_VAPORIZE_STEAM_CHANCE = 0.30;

    private boolean isLake = false;
    private int tickRain = 0;

    public Hydro() {
        super(Color.similarColor(DEFAULT_COLOR));
    }

    // lake status
    public Color getStatusColor() {
        if (isLake) {
            return DEFAULT_COLOR;
        } else {
            return null;
        }
    }

    public void globalAct(Grid grid, Grid newGrid, int width, int height) {
        // rain: spawn
        int rainX = (int) (Math.random() * height);
        int rainY = (int) (Math.random() * width);
        newGrid.setTile(rainX, rainY, new Hydro());
    }

    @Override
    public void act(Grid grid, Grid newGrid, int i, int j, int width, int height) {
        // rain: dry out
        if (Grids.countTilesWithinDistance(grid,
                i, j, width, height, Hydro.class, 1) > 1) {
            isLake = true;
            tickRain = 0;
        } else {
            isLake = false;
            tickRain++;
            if (tickRain >= MAX_TICKS_RAIN) {
                newGrid.softReplace(i, j, new Empty(), this);
                return;
            }
        }

        // vaporize
        boolean vaporized = false;
        for (Point2D adjacent : Grids.getNeighbors(i, j, width, height)) {
            int newI = adjacent.x();
            int newJ = adjacent.y();
            Tile adjPyro = newGrid.getTileOfType(newI, newJ, Pyro.class);
            if (adjPyro != null
                    && Math.random() < BASE_VAPORIZE_CHANCE) {
                newGrid.softReplace(newI, newJ, new Empty(), adjPyro);
                if (Math.random() < BASE_VAPORIZE_STEAM_CHANCE) {
                    vaporized = true; // vaporize: steam
                }
            }
        }

        if (vaporized) {
            newGrid.softReplace(i, j, new Empty(), this);
        }
    }
}
