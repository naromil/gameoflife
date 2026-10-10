package com.naromil.gol.elements;

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
    public boolean act(Grid grid, Grid newGrid, int i, int j, int width, int height) {
        // storm: disappear
        stormTicks++;
        if (stormTicks >= MAX_STORM_TICKS) {
            newGrid.setTile(i, j, containedTile != null ? containedTile : new Empty());
            return false;
        }

        // contained tile act
        if (containedTile != null) {
            containedTile.act(grid, newGrid, i, j, width, height);
        }

        // storm moves
        if (grid.getTile(i, j) == this) {
            newGrid.setTile(i, j, containedTile != null ? containedTile : new Empty());
            Point2D randomNeighbor = Grids.getRandomNeighbor(i, j, width, height);
            int newI = randomNeighbor.x();
            int newJ = randomNeighbor.y();
            containedTile = (newGrid.doesTileExist(newI, newJ) ? newGrid : grid).getTile(newI, newJ); // absorb the newer tile
            newGrid.setTile(newI, newJ, this);
        }

        return false;
    }
}
