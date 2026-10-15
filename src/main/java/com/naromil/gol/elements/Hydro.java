package com.naromil.gol.elements;

import java.util.List;

import com.naromil.gol.environment.SeasonType;
import com.naromil.gol.main.Color;
import com.naromil.gol.main.Grid;

public class Hydro extends Tile {
    public static final Color DEFAULT_COLOR = new Color(76, 194, 241);
    private static final int MAX_TICKS_RAIN = 15;
    private static final double BASE_VAPORIZE_CHANCE = 0.50;
    private static final double BASE_VAPORIZE_STEAM_CHANCE = 0.50;
    private static final int MAX_VAPORIZE_TILES = 5;

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

    public static void globalAct(Grid grid, Grid newGrid, int width, int height) {
        // rain: spawn
        boolean isWinter = newGrid.getCurrentSeason() == SeasonType.WINTER;

        double expextedTimes = height * width / 500.0;
        int times = (Math.random() < expextedTimes - Math.floor(expextedTimes))
                ? (int) Math.ceil(expextedTimes) : (int) Math.floor(expextedTimes);

        for (int t = 0; t < times; t++) {
            int rainX = (int) (Math.random() * height);
            int rainY = (int) (Math.random() * width);

            // rain: affinity (move towards nearest alike to form lake)
            Point2D next = Grids.directionToNearestType(newGrid, rainX, rainY, width, height,
                    isWinter ? Cryo.class : Hydro.class);
            int x = next != null ? next.x() : rainX;
            int y = next != null ? next.y() : rainY;

            // directly snows (spawn Cryo) in winter
            newGrid.setTile(x, y, isWinter ? new Cryo() : new Hydro());
        }
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
            if (newGrid.hasType(newI, newJ, Pyro.class)
                    && Math.random() < BASE_VAPORIZE_CHANCE) {
                // vaporize connected fire
                List<Point2D> connectedPyro = Grids.bfs(grid, newGrid, newI, newJ, width, height, Pyro.class, MAX_VAPORIZE_TILES);
                for (Point2D pyro : connectedPyro) {
                    int x = pyro.x();
                    int y = pyro.y();
                    newGrid.softReplace(x, y, new Empty(), newGrid.getTileOfType(x, y, Pyro.class));
                }
                if (Math.random() < BASE_VAPORIZE_STEAM_CHANCE) {
                    vaporized = true; // vaporize: steam
                    break;
                }
            }
        }

        if (vaporized) {
            newGrid.softReplace(i, j, new Empty(), this);
        }
    }
}
