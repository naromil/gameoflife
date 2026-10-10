package com.naromil.gol.elements;

import java.util.Arrays;
import java.util.List;

import com.naromil.gol.environment.SeasonType;
import com.naromil.gol.main.Color;
import com.naromil.gol.main.Grid;

public class Electro extends Tile {
    public static final Color DEFAULT_COLOR = new Color(211, 118, 240);
    public static final double BASE_STORM_CHANCE = 0.05;
    public static final int MAX_STORM_TICKS = 15;

    private Tile containedTile = null; // the tile that is currently being affected by the storm
    private int stormTicks = 0;

    public Electro() {
        super(Color.similarColor(DEFAULT_COLOR));
    }

    @Override
    public Color getStatusColor() {
        return containedTile != null ? containedTile.getColor() : null;
    }

    public void globalAct(Grid grid, Grid newGrid, int width, int height) {
        // storms: spawn
        double chance = BASE_STORM_CHANCE;
        if (newGrid.getCurrentSeason() == SeasonType.FALL) {
            chance *= 1.5;
        }
        if (Math.random() < chance) {
            int rainX = (int) (Math.random() * width);
            int rainY = (int) (Math.random() * height);
            newGrid.setTile(rainY, rainX, new Hydro());
        }
    }

    @Override
    public void act(Grid grid, Grid newGrid, int i, int j, int width, int height) {
        // storm: disappear
        Tile freedTile = containedTile != null ? containedTile : new Empty();
        stormTicks++;
        if (stormTicks >= MAX_STORM_TICKS) {
            newGrid.softReplace(i, j, freedTile, this);
            return;
        }

        // contained tile act
        if (containedTile != null) {
            containedTile.act(grid, newGrid, i, j, width, height);
        }

        // storm moves
        newGrid.softReplace(i, j, freedTile, this);
        // random neighbor excluding electro
        List<Point2D> neighbors = Arrays.asList(Grids.getNeighbors(i, j, width, height)).stream()
                .filter(p -> !(newGrid.getTile(p.x(), p.y()) instanceof Electro)).toList();
        if (neighbors.isEmpty()) {
            return;
        }
        Point2D randomNeighbor = neighbors.get((int) (Math.random() * neighbors.size()));
        int newI = randomNeighbor.x();
        int newJ = randomNeighbor.y();
        // storm: absorb
        Tile absorbedTile = newGrid.getTile(newI, newJ);
        containedTile = absorbedTile instanceof Empty ? null : absorbedTile;
        newGrid.setTile(newI, newJ, this); // guaranteed replacing the newest state
    }
}
