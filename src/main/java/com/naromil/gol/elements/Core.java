package com.naromil.gol.elements;

import java.util.List;

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
    public Color getStatusColor() {
        return containedTile.getColor();
    }

    @Override
    protected boolean hasDendroCore() {
        return true;
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
            // extinguish all
            List<Point2D> connectedPyro = Grids.bfs(grid, newGrid, i, j, width, height, Pyro.class);
            for (Point2D pyro : connectedPyro) {
                int x = pyro.x();
                int y = pyro.y();
                newGrid.softReplace(x, y, new Empty(), newGrid.getTileOfType(x, y, Pyro.class));
                // this unconventionally removes the Pyro from the past as well so they can't spread
                Tile oldTile = grid.getTileOfType(x, y, Pyro.class);
                if(oldTile instanceof Pyro) {
                    grid.softReplace(x, y, new Empty(), oldTile);
                }
            }
            newGrid.softReplace(i, j, new Empty(), this);
            return;
        }

        // contained tile act
        if (TileFactory.isComplexTileType(containedTile)) {
            throw new IllegalStateException(
                    "Core cannot contain another complex tile.");
        }
        containedTile.act(grid, newGrid, i, j, width, height);

        // hyperbloom: hunt
        newGrid.softReplace(i, j, containedTile, this);

        // move one tile towards the nearest Pyro, otherwise self kill
        Point2D next = Grids.directionToNearestType(newGrid, i, j, width, height, Pyro.class);

        if (next != null) { // only move if pyro found
            int x = next.x();
            int y = next.y();
            Tile oldTile = newGrid.getTile(x, y);
            if (!(TileFactory.isComplexTileType(oldTile))) {
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
