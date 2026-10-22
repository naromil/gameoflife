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
    public int getRank() {
        return 10;
    }

    @Override
    public Color getStatusColor() {
        return containedTile.getColor(); // Empty has none
    }

    @Override
    public boolean isComplexType() {
        return true;
    }

    @Override 
    public boolean isStrongType() {
        return true;
    }

    private boolean hasActed = false;

    // the probability doesn't exclude complex tiles but will not replace them
    public static void globalAct(Grid grid, Grid newGrid) {
        int width = grid.getWidth();
        int height = grid.getHeight();
        // storms: initialize
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                Tile tile = grid.getTile(new Point(i, j));
                if (tile instanceof Electro) {
                    // ignoring contained Electro that are for special purposes
                    ((Electro) tile).hasActed = false;
                }
            }
        }

        // storms: spawn
        double chance = BASE_STORM_CHANCE;
        if (grid.getCurrentSeason() == SeasonType.FALL) {
            chance *= 3;
        }
        double expextedTimes = height * width / 1000.0;
        int times = (Math.random() < expextedTimes - Math.floor(expextedTimes))
                ? (int) Math.ceil(expextedTimes) : (int) Math.floor(expextedTimes);
        for (int t = 0; t < times; t++) {
            if (Math.random() < chance) {
                Point spawn = new Point((int) (Math.random() * height),
                        (int) (Math.random() * width));
                double currentId = Math.random();
                Tile oldTile = grid.getTile(spawn);
                if (!oldTile.isComplexType()) {
                    newGrid.replace(spawn, new Electro(oldTile, currentId), oldTile);
                }
                while (Math.random() < BASE_MORE_STORM_CHANCE) {
                    Point[] neighbors = Grids.getNeighbors(spawn, width, height);
                    int randomIndex = (int) (Math.random() * neighbors.length);
                    Point next = neighbors[randomIndex];
                    Tile oldTile2 = grid.getTile(next);
                    if (!oldTile2.isComplexType()) {
                        newGrid.replace(next, new Electro(oldTile2, currentId),
                                oldTile2);
                        spawn = next;
                    }
                }
            }
        }
    }

    @Override
    public void act(Grid grid, Grid newGrid, Point position) {
        int directionX = (int) (Math.random() * 3) - 1;
        int directionY = (int) (Math.random() * 3) - 1;
        act(grid, newGrid, position, new Point(directionX, directionY));
    }

    private void act(Grid grid, Grid newGrid, Point position, Point direction) {
        int width = grid.getWidth();
        int height = grid.getHeight();
        if (hasActed) {
            return;
        }
        hasActed = true;

        // storm: disappear
        stormTicks++;
        if (stormTicks >= MAX_STORM_TICKS) {
            newGrid.replace(position, containedTile, this);
            return;
        }

        // overloaded
        if (containedTile instanceof Pyro) {
            newGrid.replace(position, new Empty(), this);
            //overloaded: explosion
            for (Point adjacent : Grids.getNeighbors(position, width, height, 2)) {
                Tile oldTile = grid.getTile(adjacent);
                if (oldTile instanceof Geo && ((Geo) oldTile).hasShield()) {
                    ((Geo) oldTile).setShield(false); // shield: protection
                    continue;
                }
                newGrid.replace(adjacent, new Empty(), oldTile);
            }
            // overloaded: throw away
            List<Point> neighbors2 = Arrays.asList(
                    Grids.getNeighbors(position, width, height, 2));
            List<Point> neighbors3 = Arrays.asList(
                    Grids.getNeighbors(position, width, height, 3)).stream()
                    .filter(adjacent -> !neighbors2.contains(adjacent)).toList();
            if (neighbors3.isEmpty()) {
                return;
            }
            int randomIndex = (int) (Math.random() * neighbors3.size());
            Point randomNeighbor = neighbors3.get(randomIndex);
            newGrid.replace(randomNeighbor, new Pyro(), grid.getTile(randomNeighbor));
            return;
        }

        // hyperbloom
        if (containedTile instanceof Dendro && ((Dendro) containedTile).hasDendroCore()) {
            Dendro dendro = (Dendro) containedTile;
            dendro.setDendroCore(false);
            newGrid.replace(position, new Core(), this);
            return;
        }

        // charged
        if (containedTile instanceof Hydro) {
            Point[] connected = bfs(grid, position); // lake in the previous tick
            for (Point neighbor : connected) {
                if (!grid.hasType(neighbor, Hydro.class)) {
                    continue; // already replaced by other tile this tick
                }
                Hydro lakeHydro = (Hydro) grid.getTileOfType(neighbor, Hydro.class);
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
            newGrid.replace(position, new Empty(), this);
            // superconduct: brittle
            for (Point adjacent : Grids.getNeighbors(position, width, height)) {
                if (grid.hasType(adjacent, Geo.class)) {
                    Geo geo = (Geo) grid.getTileOfType(adjacent, Geo.class);
                    geo.setBrittle(true);
                }
            }
            return;
        }

        // contained tile act, Empty does nothing
        if (containedTile.isComplexType()) {
            throw new IllegalStateException(
                    "Electro cannot contain another complex tile.");
        }
        containedTile.act(grid, newGrid, position);

        // storm: moves
        newGrid.replace(position, containedTile, this);
        Point next = position.plus(direction);
        if (!Grids.isOutOfBounds(next, width, height)) {
            if (grid.getTile(next).isComplexType()) {
                containedTile = grid.getTile(next).getContainedTile();
            } else {
                containedTile = grid.getTile(next);
            }
            newGrid.replace(next, this, grid.getTile(next));
        }

        // storm: move together
        for (Point adjacent : Grids.getNeighbors(position, width, height)) {
            Tile adjTile = grid.getTile(adjacent);
            if (adjTile instanceof Electro && ((Electro) adjTile).id == this.id) {
                ((Electro) adjTile).act(grid, newGrid, adjacent, direction);
            }
        }
    }

    private static Point[] bfs(Grid grid, Point start) {
        int width = grid.getWidth();
        int height = grid.getHeight();
        Set<Point> visited = new HashSet<>();
        Queue<Point> queue = new ArrayDeque<>();
        if (!grid.hasType(start, Hydro.class)) {
            throw new IllegalArgumentException("Starting tile is not Hydro.");
        }
        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            Point current = queue.remove();
            for (Point adjacent : Grids.getNeighbors(current, width, height)) {
                if (grid.hasType(adjacent, Hydro.class) && !visited.contains(adjacent)) {
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

        return visited.toArray(Point[]::new);
    }
}
