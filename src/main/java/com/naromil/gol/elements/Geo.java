package com.naromil.gol.elements;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import com.naromil.gol.main.Color;
import com.naromil.gol.main.Grid;
import com.naromil.gol.main.Point;

public class Geo extends Tile {
    public static final Color DEFAULT_COLOR = new Color(248, 186, 78);
    private static final double BASE_MOUNTAINS_CHANCE = 0.005;
    private static final double BASE_CRYSTALLIZE_CHANCE = 0.10;
    private static final double BASE_ABSORB_CHANCE = 0.10;
    public static final double BASE_EROSION_CHANCE = 0.002;
    private static final int INITIAL_SHIELD_DURATION = 20;
    private static final int INITIAL_BRITTLE_DURATION = 10;
    
    private int shieldDuration = 0;
    private int brittleDuration = 0;

    public Geo() {
        super(Color.similarColor(DEFAULT_COLOR));
    }

    @Override
    public int getRank() {
        return 60;
    }

    @Override
    public Color getStatusColor() {
        if (hasShield()) {
            return DEFAULT_COLOR;
        } else if (isBrittle()) {
            return Cryo.DEFAULT_COLOR;
        } else {
            return null;
        }
    }

    @Override
    public boolean isComplexType() {
        return false;
    }

    @Override 
    public boolean isStrongType() {
        return false;
    }

    public void setShield(boolean shield) {
        if (shield) {
            if (isBrittle()) {
                setBrittle(false);
            }
            else {
                shieldDuration = INITIAL_SHIELD_DURATION;
            }
        } else {
            shieldDuration = 0;
        }
    }

    public boolean hasShield() {
        return shieldDuration > 0;
    }

    public void setBrittle(boolean brittle) {
        if (brittle) {
            if (hasShield()) {
                // shield: protection
                setShield(false);
            } else {
                brittleDuration = INITIAL_BRITTLE_DURATION;
            }
        } else {
            brittleDuration = 0;
        }
    }

    public boolean isBrittle() {
        return brittleDuration > 0;
    }

    private static List<Point> bfs(Grid grid, Point start, Set<Point> visited) {
        Queue<Point> queue = new ArrayDeque<>();
        List<Point> peripheral = new ArrayList<Point>();
        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            Point current = queue.remove();
            for (Point adjacent : Grids.getNeighbors(current,
                    grid.getWidth(), grid.getHeight())) {
                boolean isGeo = grid.hasType(adjacent, Geo.class);
                if (isGeo && !visited.contains(adjacent)) {
                    queue.add(adjacent);
                    visited.add(adjacent);
                }
                else if (!isGeo) {
                    peripheral.add(adjacent);
                }
            }
        }

        return peripheral;
    }

    public static void globalAct(Grid grid, Grid newGrid) {
        int width = grid.getWidth();
        int height = grid.getHeight();
        // mountains
        Set<Point> visited = new HashSet<>();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                Point position = new Point(i, j);
                if (grid.hasType(position, Geo.class) && !visited.contains(position)) {

                    Set<Point> oldVisited = new HashSet<>(visited);
                    List<Point> peripheral = bfs(grid, position, visited);
                    List<Point> connected = visited.stream().
                            filter(p -> !oldVisited.contains(p)).toList();
                    if (connected.isEmpty()) {
                        throw new IllegalStateException(
                                "Geo: bfs explored connected component must not be empty.");
                    }

                    // mountains: tenacity
                    int size = connected.size();
                    int times = size <= 3 ? 5 - size : 1;

                    for (int t = 0; t < times; t++) {
                        double chance = BASE_MOUNTAINS_CHANCE;
                        if (Math.random() < chance) {
                            int randomIndex = (int) (Math.random() * peripheral.size());
                            Point target = peripheral.get(randomIndex);
                            newGrid.replace(target, grid.getTile(target), new Geo());
                        }
                    }

                }
            }
        }
    }

    @Override
    public void act(Grid grid, Grid newGrid, Point position) {
        int width = grid.getWidth();
        int height = grid.getHeight();
        // crystallize: countdown
        if (hasShield()) {
            shieldDuration--;
        } else if (isBrittle()) {
            brittleDuration--;
        }

        // crystallize
        for (Point adjacent : Grids.getNeighbors(position, width, height)) {
            double chance = BASE_CRYSTALLIZE_CHANCE;
            if (hasShield()) { chance = 0; } // already have shield
            if (Math.random() < chance
                    && grid.getTile(adjacent).isStrongType()) {
                setShield(true);
                // crystallize: absorb
                if (Math.random() < BASE_ABSORB_CHANCE) {
                    newGrid.replace(adjacent, grid.getTile(adjacent), new Empty());
                }
                break;
            }
        }
    }
}
