package com.naromil.gol.elements;

import com.naromil.gol.main.Color;
import com.naromil.gol.main.Grid;

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
    public void act(Grid grid, Grid newGrid, int i, int j, int width, int height) {
        act(grid, newGrid, i, j, width, height, CORE_SPEED_MULTIPLIER);
    }

    public void act(Grid grid, Grid newGrid, int i, int j, int width, int height, int times) {
        // quicken: timer
        if (tickQuickened > 0) {
            tickQuickened--;
        }

        // hyperbloom: extinguish
        if (containedTile instanceof Pyro) {
            // extinguish all on the grid
            for (int x = 0; x < height; x++) {
                for (int y = 0; y < width; y++) {
                    if (grid.hasType(x, y, Pyro.class)) {
                        newGrid.softReplace(x, y, new Empty(), grid.getTileOfType(x, y, Pyro.class));
                    }
                }
            }
            
            newGrid.softReplace(i, j, new Empty(), this);
            return;
        }

        // contained tile act
        if (containedTile.isComplexType()) {
            throw new IllegalStateException(
                    "Core cannot contain another complex tile.");
        }
        containedTile.act(grid, newGrid, i, j, width, height);

        // hyperbloom: hunt
        newGrid.softReplace(i, j, containedTile, this);

        // move one tile towards the nearest Pyro, otherwise self kill
        Point next = Grids.directionToNearestType(grid, i, j, width, height, Pyro.class);

        if (next != null) { // only move if pyro found
            int x = next.x();
            int y = next.y();
            Tile oldTile = grid.getTile(x, y);
            if (!oldTile.isComplexType()) {
                containedTile = oldTile;
            }
            // top priority among complex tile types
            newGrid.softReplace(x, y, this, oldTile);

            if (times > 1) {
                // recursion to act multiple times a tick
                act(grid, newGrid, next.x(), next.y(), width, height, times - 1);
            }
        }
    }

}
