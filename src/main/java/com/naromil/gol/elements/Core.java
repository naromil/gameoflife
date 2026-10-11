package com.naromil.gol.elements;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

import com.naromil.gol.main.Color;
import com.naromil.gol.main.Grid;

// standalone protectile caused by hyperbloom that hunts down a Pyro
public class Core extends Dendro {

    private Tile containedTile = new Empty();

    public Core() {
        super();
    }

    @Override
    public Color getStatusColor() {
        return containedTile.getColor();
    }

    @Override
    public boolean hasDendroCore() {
        return true;
    }
    
    /**
     * other can react with this since this is Dendro, but Dendro-side reactions won't happen
     * has useless quickened state, won't wither, won't grow, won't bloom
     */
    @Override
    public void act(Grid grid, Grid newGrid, int i, int j, int width, int height) {
        // quicken: timer
        if (isQuickened()) {
            tickQuickened--;
        }

        // hyperbloom: extinguish
        if (containedTile instanceof Pyro) {
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
        Point2D next = directionToPyro(newGrid, i, j, width, height);

        newGrid.softReplace(i, j, containedTile, this);
        if (next != null) { // only move if pyro found
            int newI = next.x();
            int newJ = next.y();
            Tile oldTile = newGrid.getTile(newI, newJ);
            if (!(TileFactory.isComplexTileType(oldTile))) {
                containedTile = oldTile;
            }
            // top priority among complex tile types
            newGrid.softReplace(newI, newJ, this, oldTile);
        }
    }

    private record DirectionStore(Point2D current, Point2D direction, int distance) {}

    private Point2D directionToPyro(Grid newGrid, int startI, int startJ, int width, int height) {
        Set<Point2D> visited = new HashSet<>();
        Queue<DirectionStore> queue = new ArrayDeque<>();
        visited.add(new Point2D(startI, startJ));
        for (Point2D adjacent : Grids.getNeighbors(startI, startJ, width, height)) {
            int newI = adjacent.x();
            int newJ = adjacent.y();
            if (!newGrid.hasType(newI, newJ, Core.class)) {
                queue.add(new DirectionStore(adjacent, adjacent, 1));
                visited.add(adjacent);
            }
        }

        while (!queue.isEmpty()) {
            DirectionStore current = queue.remove();
            int currentI = current.current().x();
            int currentJ = current.current().y();
            if (newGrid.hasType(currentI, currentJ, Pyro.class)) {
                return current.direction();
            }
            for (Point2D adjacent : Grids.getNeighbors(currentI, currentJ, width, height)) {
                if (!visited.contains(adjacent)) {
                    queue.add(new DirectionStore(adjacent, current.direction(), current.distance() + 1));
                    visited.add(adjacent);
                }
            }
        }

        return null; // no pyro found 
    }
}
