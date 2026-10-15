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
    private static final double BASE_STORM_CHANCE = 0.05;
    private static final double BASE_MORE_STORM_CHANCE = 0.80;
    private static final int MAX_STORM_TICKS = 15;
    private static final int MAX_CHARGED_SPREAD = 5;

    private int stormTicks = 0;
    private double id;

    public Electro() {
        super(Color.similarColor(DEFAULT_COLOR));
        id = Math.random();
    }

    private Electro(Tile containedTile, double id) {
        this();
        this.containedTile = containedTile;
        this.id = id;
    }

    @Override
    public Color getStatusColor() {
        return containedTile.getColor(); // Empty has none
    }

    private boolean hasActed = false;

    // the probability doesn't exclude complex tiles but will not replace them
    public static void globalAct(Grid grid, Grid newGrid, int width, int height) {
        // storms: initialize
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                Tile tile = newGrid.getTile(i, j);
                if (tile instanceof Electro) {
                    ((Electro) tile).hasActed = false;
                }
            }
        }

        // storms: spawn
        double chance = BASE_STORM_CHANCE;
        if (newGrid.getCurrentSeason() == SeasonType.FALL) {
            chance *= 3;
        }
        double expextedTimes = height * width / 1000.0;
        int times = (Math.random() < expextedTimes - Math.floor(expextedTimes))
                ? (int) Math.ceil(expextedTimes) : (int) Math.floor(expextedTimes);
        for (int t = 0; t < times; t++) {
            if (Math.random() < chance) {
                int newI = (int) (Math.random() * height);
                int newJ = (int) (Math.random() * width);
                double currentId = Math.random();
                Tile oldTile = newGrid.getTile(newI, newJ);
                if (!TileFactory.isComplexTileType(oldTile)) {
                    newGrid.setTile(newI, newJ, new Electro(oldTile, currentId));
                }
                while (Math.random() < BASE_MORE_STORM_CHANCE) {
                    Point2D[] neighbors = Grids.getNeighbors(newI, newJ, width, height);
                    int randomIndex = (int) (Math.random() * neighbors.length);
                    int newI2 = neighbors[randomIndex].x();
                    int newJ2 = neighbors[randomIndex].y();
                    Tile oldTile2 = newGrid.getTile(newI2, newJ2);
                    if (!TileFactory.isComplexTileType(oldTile2)) {
                        newGrid.setTile(newI2, newJ2, new Electro(oldTile2, currentId));
                        newI = newI2;
                        newJ = newJ2;
                    }
                }
            }
    }
    }

    @Override
    public void act(Grid grid, Grid newGrid, int i, int j, int width, int height) {
        int directionX = (int) (Math.random() * 3) - 1;
        int directionY = (int) (Math.random() * 3) - 1;
        act(grid, newGrid, i, j, width, height, new Point2D(directionX, directionY));
    }

    private void act(Grid grid, Grid newGrid, int i, int j, int width, int height, Point2D direction) {
        if (hasActed) {
            return;
        }
        hasActed = true;

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
            for (Point2D adjacent : Grids.getNeighbors(i, j, width, height, 2)) {
                int newI = adjacent.x();
                int newJ = adjacent.y();
                Tile oldTile = newGrid.getTile(newI, newJ);
                if (oldTile instanceof Geo && ((Geo) oldTile).hasShield()) {
                    ((Geo) oldTile).setShield(false); // shield: protection
                    continue;
                }
                newGrid.softReplace(newI, newJ, new Empty(), oldTile);
            }
            // overloaded: throw away
            List<Point2D> neighbors2 = Arrays.asList(Grids.getNeighbors(i, j, width, height, 2));
            List<Point2D> neighbors3 = Arrays.asList(
                    Grids.getNeighbors(i, j, width, height, 3)).stream()
                    .filter(adjacent -> !neighbors2.contains(adjacent)).toList();
            if (neighbors3.isEmpty()) {
                return;
            }
            int randomIndex = (int) (Math.random() * neighbors3.size());
            Point2D randomNeighbor = neighbors3.get(randomIndex);
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
            dendro.setQuickened(true);
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
        int newI = i + direction.x();
        int newJ = j + direction.y();
        if (!Grids.isOutOfBounds(newI, newJ, width, height)) {
            if (TileFactory.isComplexTileType(newGrid.getTile(newI, newJ))) {
                containedTile = newGrid.getTile(newI, newJ).getContainedTile();
            } else {
                containedTile = newGrid.getTile(newI, newJ);
            }
            newGrid.setTile(newI, newJ, this); // strongest of the complex tiles
        }

        // storm: move together
        for (Point2D adjacent : Grids.getNeighbors(i, j, width, height)) {
            int adjI = adjacent.x();
            int adjJ = adjacent.y();
            Tile adjTile = newGrid.getTile(adjI, adjJ);
            if (adjTile instanceof Electro && ((Electro) adjTile).id == this.id) {
                ((Electro) adjTile).act(grid, newGrid, adjI, adjJ, width, height, direction);
            }
        }
    }

    private static Point2D[] bfs(Grid grid, int startI, int startJ, int width, int height) {
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
