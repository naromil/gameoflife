package com.naromil.gol.elements;

import java.util.Arrays;
import java.util.List;

import com.naromil.gol.environment.SeasonType;
import com.naromil.gol.main.Color;
import com.naromil.gol.main.Grid;

// only exists in storm form
public class Electro extends Tile {
    public static final Color DEFAULT_COLOR = new Color(211, 118, 240);
    public static final double BASE_STORM_CHANCE = 0.05;
    public static final int MAX_STORM_TICKS = 15;

    // the tile that is currently being affected by the storm, Empty if none
    private Tile containedTile = new Empty();

    private int stormTicks = 0;

    public Electro() {
        super(Color.similarColor(DEFAULT_COLOR));
    }

    private Electro(Tile containedTile) {
        this();
        this.containedTile = containedTile;
    }

    @Override
    public Color getStatusColor() {
        return containedTile.getColor(); // Empty has none
    }

    // the probability doesn't exclude complex tiles but will not replace them
    public void globalAct(Grid grid, Grid newGrid, int width, int height) {
        // storms: spawn
        double chance = BASE_STORM_CHANCE;
        if (newGrid.getCurrentSeason() == SeasonType.FALL) {
            chance *= 1.5;
        }
        if (Math.random() < chance) {
            int newX = (int) (Math.random() * height);
            int newY = (int) (Math.random() * width);
            Tile oldTile = newGrid.getTile(newX, newY);
            if (!TileFactory.isComplexTileType(oldTile)) {
                newGrid.setTile(newX, newY, new Electro(oldTile));
            }
        }
    }

    @Override
    public void act(Grid grid, Grid newGrid, int i, int j, int width, int height) {
        // storm: disappear
        stormTicks++;
        if (stormTicks >= MAX_STORM_TICKS) {
            newGrid.softReplace(i, j, containedTile, this);
            return;
        }

        // contained tile act, Empty does nothing
        if (TileFactory.isComplexTileType(containedTile)) {
            throw new IllegalStateException(
                    "Electro cannot contain another complex tile.");
        }
        containedTile.act(grid, newGrid, i, j, width, height);

        // storm: moves
        newGrid.softReplace(i, j, containedTile, this);

        // random neighbor excluding electro and anemo
        List<Point2D> neighbors = Arrays.asList(Grids.getNeighbors(i, j, width, height))
                .stream().filter(p -> !newGrid.hasType(p.x(), p.y(), Electro.class)
                && !newGrid.hasType(p.x(), p.y(), Anemo.class)).toList();

        if (!neighbors.isEmpty()) {
            Point2D randomNeighbor = neighbors.get((int) (Math.random() * neighbors.size()));
            int newI = randomNeighbor.x();
            int newJ = randomNeighbor.y();
            // storm: absorb
            containedTile = newGrid.getTile(newI, newJ);
            newGrid.setTile(newI, newJ, this); // guaranteed replacing the newest state
        }
    }
}
