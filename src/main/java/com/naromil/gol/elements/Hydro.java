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

    @Override
    public int getRank() {
        return 50;
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

    @Override
    public boolean isComplexType() {
        return false;
    }

    @Override 
    public boolean isStrongType() {
        return true;
    }

    // allow other tiles to interact with electro if it is charged
	public void setCharged(boolean charged) {
        if (charged) {
            // they will never act() because they are not storm. only passive reaction.
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
        boolean isWinter = grid.getCurrentSeason() == SeasonType.WINTER;

        double expectedTimes = height * width / 400.0;
        int times = (Math.random() < expectedTimes - Math.floor(expectedTimes))
                ? (int) Math.ceil(expectedTimes) : (int) Math.floor(expectedTimes);

        for (int t = 0; t < times; t++) {
            int rainX = (int) (Math.random() * height);
            int rainY = (int) (Math.random() * width);

            // rain: affinity (move towards nearest alike to form lake)
            Point next = Grids.directionToNearestType(grid, rainX, rainY, width, height,
                    isWinter ? Cryo.class : Hydro.class);
            int x = next != null ? next.x() : rainX;
            int y = next != null ? next.y() : rainY;

            // directly snows (spawn Cryo) in winter
            newGrid.replace(x, y, isWinter ? new Cryo() : new Hydro(), grid.getTile(x, y));
        }
    }

    @Override
    public void act(Grid grid, Grid newGrid, int i, int j, int width, int height) {
        // TODO: plateau (Geo)

        // rain: dry out
        if (Grids.countTilesWithinDistance(grid,
                i, j, width, height, Hydro.class, 1) > 1) {
            isLake = true;
            tickRain = 0;
        } else {
            isLake = false;
            tickRain++;
            int maxTicks = MAX_TICKS_RAIN;
            if (grid.getCurrentSeason() == SeasonType.SUMMER) {
                maxTicks /= 2;
            }
            if (tickRain >= maxTicks) {
                newGrid.replace(i, j, new Empty(), this);
                return;
            }
        }

        // vaporize
        boolean vaporized = false;
        for (Point adjacent : Grids.getNeighbors(i, j, width, height)) {
            int newI = adjacent.x();
            int newJ = adjacent.y();
            if (grid.hasType(newI, newJ, Pyro.class)
                    && Math.random() < BASE_VAPORIZE_CHANCE) {
                // vaporize connected fire
                List<Point> connectedPyro = Grids.bfs(grid, newI, newJ, width, height, Pyro.class, MAX_VAPORIZE_TILES);
                for (Point pyro : connectedPyro) {
                    int x = pyro.x();
                    int y = pyro.y();
                    newGrid.replace(x, y, new Empty(), grid.getTileOfType(x, y, Pyro.class));
                }
                if (Math.random() < BASE_VAPORIZE_STEAM_CHANCE) {
                    vaporized = true; // vaporize: steam
                    break;
                }
            }
        }

        if (vaporized) {
            newGrid.replace(i, j, new Empty(), this);
        }
    }
}
