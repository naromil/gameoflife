package com.naromil.gol.elements;

import com.naromil.gol.main.Color;
import com.naromil.gol.main.Grid;
import com.naromil.gol.main.Point;

// standalone protectile caused by hyperbloom that hunts down a Pyro
public class Core extends Dendro {
    public static final Color DEFAULT_COLOR = new Color(255, 255, 255);
    private static final int CORE_SPEED_MULTIPLIER = 3;

    public Core() {
        // no variation
        super(DEFAULT_COLOR);
    }

    @Override
    public int getRank() {
        return 0;
    }

    @Override
    public Color getStatusColor() {
        return containedTile.getColor();
    }

    @Override
    protected boolean hasDendroCore() {
        return true;
    }

    @Override
    public boolean isComplexType() {
        return true;
    }

    @Override 
    public boolean isStrongType() {
        return false;
    }
    
    /**
     * other can react with this since this is Dendro, but Dendro-side reactions won't happen
     * has useless quickened state, won't wither, won't grow, won't bloom
     */
    @Override
    public void act(Grid grid, Grid newGrid, Point position) {
        act(grid, newGrid, position, CORE_SPEED_MULTIPLIER);
    }

    public void act(Grid grid, Grid newGrid, Point position, int times) {
        int width = grid.getWidth();
        int height = grid.getHeight();
        // quicken: timer
        if (tickQuickened > 0) {
            tickQuickened--;
        }

        // hyperbloom: extinguish
        if (containedTile instanceof Pyro) {
            // extinguish all on the grid
            for (int x = 0; x < height; x++) {
                for (int y = 0; y < width; y++) {
                    Point tilePosition = new Point(x, y);
                    if (grid.hasType(tilePosition, Pyro.class)) {
                        newGrid.replace(tilePosition,
                                grid.getTileOfType(tilePosition, Pyro.class),
                                new Empty());
                    }
                }
            }
            
            newGrid.replace(position, this, new Empty());
            return;
        }

        // contained tile act
        if (containedTile.isComplexType()) {
            throw new IllegalStateException(
                    "Core cannot contain another complex tile.");
        }
        containedTile.act(grid, newGrid, position);

        // hyperbloom: hunt
        newGrid.replace(position, this, containedTile);

        // move one tile towards the nearest Pyro, otherwise self kill
        Point next = Grids.directionToNearestType(grid, position, Pyro.class);

        if (next != null) { // only move if pyro found
            Tile oldTile = grid.getTile(next);
            if (!oldTile.isComplexType()) {
                containedTile = oldTile;
            }
            // top priority among complex tile types
            newGrid.replace(next, oldTile, this);

            if (times > 1) {
                // recursion to act multiple times a tick
                act(grid, newGrid, next, times - 1);
            }
        }
    }

}
