package com.naromil.gol.elements;

import java.util.List;

import com.naromil.gol.environment.SeasonType;
import com.naromil.gol.main.Color;
import com.naromil.gol.main.Grid;
import com.naromil.gol.main.Point;

public class Hydro extends Tile {
    public static final Color DEFAULT_COLOR = new Color(76, 194, 241);
    private static final int MAX_TICKS_RAIN = 15;
    private static final double BASE_VAPORIZE_CHANCE = 0.50;
    private static final double BASE_VAPORIZE_STEAM_CHANCE = 0.50;
    private static final double BASE_PLATEAU_CHANCE = 0.01;
    private static final int MAX_VAPORIZE_TILES = 5;
    private static final int MAX_AFFINITY_STEPS = 2;

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

    public static void globalAct(Grid grid, Grid newGrid) {
        int width = grid.getWidth();
        int height = grid.getHeight();
        // rain: spawn
        boolean isWinter = grid.getCurrentSeason() == SeasonType.WINTER;

        double expectedTimes = height * width / 400.0;
        int times = (Math.random() < expectedTimes - Math.floor(expectedTimes))
                ? (int) Math.ceil(expectedTimes) : (int) Math.floor(expectedTimes);

        for (int t = 0; t < times; t++) {
            Point rain = new Point((int) (Math.random() * height),
                    (int) (Math.random() * width));

            // rain: affinity (move towards nearest alike to form lake)
            Class<? extends Tile> affinity = isWinter ? Cryo.class : Hydro.class;
            Point target = rain;
            for (int step = 0; step < MAX_AFFINITY_STEPS; step++) {
                Point next = Grids.directionToNearestType(grid, target, affinity);
                if (next == null || grid.hasType(next, affinity)) {
                    break;
                }
                target = next;
            }

            // directly snows (spawn Cryo) in winter
            newGrid.replace(target, grid.getTile(target), isWinter ? new Cryo() : new Hydro());
        }
    }

    @Override
    public void act(Grid grid, Grid newGrid, Point position) {
        int width = grid.getWidth();
        int height = grid.getHeight();
        // plateau
        if (Math.random() < BASE_PLATEAU_CHANCE
                    * Grids.countTilesWithinDistance(grid, position, Geo.class, 1)) {
            newGrid.replace(position, this, new Cryo());
            return;
        }

        // rain: dry out
        if (Grids.countTilesWithinDistance(grid,
                position, Hydro.class, 1) > 1) {
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
                newGrid.replace(position, this, new Empty());
                return;
            }
        }

        // vaporize
        boolean vaporized = false;
        for (Point adjacent : Grids.getNeighbors(position, width, height)) {
            if (grid.hasType(adjacent, Pyro.class)
                    && Math.random() < BASE_VAPORIZE_CHANCE) {
                // vaporize connected fire
                List<Point> connectedPyro = Grids.bfs(grid, adjacent,
                        Pyro.class, MAX_VAPORIZE_TILES);
                for (Point pyro : connectedPyro) {
                    newGrid.replace(pyro, grid.getTileOfType(pyro, Pyro.class), new Empty());
                }
                if (Math.random() < BASE_VAPORIZE_STEAM_CHANCE) {
                    vaporized = true; // vaporize: steam
                    break;
                }
            }
        }

        if (vaporized) {
            newGrid.replace(position, this, new Empty());
        }
    }
}
