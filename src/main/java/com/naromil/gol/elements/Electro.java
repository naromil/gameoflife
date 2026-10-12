package com.naromil.gol.elements;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import com.naromil.gol.environment.SeasonType;
import com.naromil.gol.main.Color;
import com.naromil.gol.main.Grid;

// only exists in storm form
public class Electro extends Tile {
    public static final Color DEFAULT_COLOR = new Color(211, 118, 240);
    public static final double BASE_STORM_CHANCE = 0.05;
    public static final int MAX_STORM_TICKS = 15;
    public static final int MAX_CHARGED_SPREAD = 30;

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

        // overloaded
        if (containedTile instanceof Pyro) {
            newGrid.softReplace(i, j, new Empty(), this);
            //overloaded: explosion
            for (Point2D adjacent : Grids.getNeighbors(i, j, width, height)) {
                int newI = adjacent.x();
                int newJ = adjacent.y();
                newGrid.softReplace(newI, newJ, new Empty(), newGrid.getTile(newI, newJ));
            }
            // overloaded: throw away
            List<Point2D> neighbors1 = Arrays.asList(Grids.getNeighbors(i, j, width, height));
            List<Point2D> neighbors2 = Arrays.asList(
                    Grids.getNeighbors(i, j, width, height, 2)).stream()
                    .filter(adjacent -> !neighbors1.contains(adjacent)).toList();
            int randomIndex = (int) (Math.random() * neighbors2.size());
            Point2D randomNeighbor = neighbors2.get(randomIndex);
            int newI = randomNeighbor.x();
            int newJ = randomNeighbor.y();
            newGrid.softReplace(newI, newJ, new Pyro(), newGrid.getTile(newI, newJ));
            return;
        }

        // hyperbloom
        if (containedTile instanceof Dendro && ((Dendro) containedTile).hasDendroCore()) {
            Dendro dendro = (Dendro) containedTile;
            dendro.setDendroCore(false);
            newGrid.softReplace(i, j, new Core(), this);
            return;
        }

        // charged
        if (containedTile instanceof Hydro) {
            Point2D[] connected = bfs(grid, i, j, width, height); // lake in the previous tick
            for (Point2D neighbor : connected) {
                int newI = neighbor.x();
                int newJ = neighbor.y();
                if (!newGrid.hasType(newI, newJ, Hydro.class)) {
                    continue; // already replaced by other tile this tick
                }
                Hydro lakeHydro = (Hydro) newGrid.getTileOfType(newI, newJ, Hydro.class);
                lakeHydro.setCharged(true);
            }
        }

        // quickened
        if (containedTile instanceof Dendro) {
            Dendro dendro = (Dendro) containedTile;
            dendro.setTickQuickened(true);
        }

        // superconduct
        if (containedTile instanceof Cryo) {
            newGrid.softReplace(i, j, new Empty(), this);
            // superconduct: brittle
            for (Point2D adjacent : Grids.getNeighbors(i, j, width, height)) {
                int newI = adjacent.x();
                int newJ = adjacent.y();
                if (newGrid.hasType(newI, newJ, Geo.class)) {
                    Geo geo = (Geo) newGrid.getTileOfType(newI, newJ, Geo.class);
                    geo.setBrittle(true);
                }
            }
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
                .stream().filter(p -> !TileFactory.isComplexTileType(newGrid.getTile(p.x(), p.y()))).toList();

        if (!neighbors.isEmpty()) {
            Point2D randomNeighbor = neighbors.get((int) (Math.random() * neighbors.size()));
            int newI = randomNeighbor.x();
            int newJ = randomNeighbor.y();
            // storm: absorb
            containedTile = newGrid.getTile(newI, newJ);
            newGrid.setTile(newI, newJ, this); // guaranteed replacing the newest state
        }
    }

    public static Point2D[] bfs(Grid grid, int startI, int startJ, int width, int height) {
        Set<Point2D> visited = new HashSet<>();
        Queue<Point2D> queue = new ArrayDeque<>();
        if (!grid.hasType(startI, startJ, Hydro.class)) {
            throw new IllegalArgumentException("Starting tile is not Hydro.");
        }
        queue.add(new Point2D(startI, startJ));
        visited.add(new Point2D(startI, startJ));

        while (!queue.isEmpty()) {
            Point2D current = queue.remove();
            int currentI = current.x();
            int currentJ = current.y();
            for (Point2D adjacent : Grids.getNeighbors(currentI, currentJ, width, height)) {
                int newI = adjacent.x();
                int newJ = adjacent.y();
                if (grid.hasType(newI, newJ, Hydro.class) && !visited.contains(adjacent)) {
                    queue.add(adjacent);
                    visited.add(adjacent);
                    if (visited.size() >= MAX_CHARGED_SPREAD) {
                        break;
                    }
                }
            }
            if (visited.size() >= MAX_CHARGED_SPREAD) {
                break;
            }
        }

        return visited.toArray(Point2D[]::new);
    }
}
