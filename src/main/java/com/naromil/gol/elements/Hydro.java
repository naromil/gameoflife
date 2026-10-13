package com.naromil.gol.elements;

import com.naromil.gol.environment.SeasonType;
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
        if (isCharged()) {
            return Electro.DEFAULT_COLOR;
        } else if (isLake) {
            return DEFAULT_COLOR;
        } else {
            return null;
        }
    }

    // allow other tiles to interact with electro if it is charged
	public void setCharged(boolean charged) {
        if (charged) {
            containedTile = new Electro();
        } else {
            containedTile = new Empty();
        }
	}

    public boolean isCharged() {
        return containedTile instanceof Electro;
    }

    public void globalAct(Grid grid, Grid newGrid, int width, int height) {
        // rain: spawn
        int rainX = (int) (Math.random() * height);
        int rainY = (int) (Math.random() * width);
        boolean isWinter = newGrid.getCurrentSeason() == SeasonType.WINTER;
        // directly snows (spawn Cryo) in winter
        newGrid.setTile(rainX, rainY, isWinter ? new Cryo() : new Hydro());
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
            int maxTicks = MAX_TICKS_RAIN;
            if (newGrid.getCurrentSeason() == SeasonType.SUMMER) {
                maxTicks /= 2;
            }
            if (tickRain >= maxTicks) {
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
            if (!(adjPyro instanceof Empty)
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
