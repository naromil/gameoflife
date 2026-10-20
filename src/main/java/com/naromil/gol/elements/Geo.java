package com.naromil.gol.elements;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import com.naromil.gol.main.Color;
import com.naromil.gol.main.Grid;

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
            shieldDuration = INITIAL_SHIELD_DURATION;
            setBrittle(false); // shield: protection
        } else {
            shieldDuration = 0;
        }
    }

    public boolean hasShield() {
        return shieldDuration > 0;
    }

    public void setBrittle(boolean brittle) {
        if (hasShield()) {
            // shield: protection
            setShield(false);
        } else if (brittle) {
            brittleDuration = INITIAL_BRITTLE_DURATION;
        } else {
            brittleDuration = 0;
        }
    }

    public boolean isBrittle() {
        return brittleDuration > 0;
    }

    private static List<Point> bfs(Grid grid, Grid newGrid, int startI, int startJ,
            int width, int height, Set<Point> visited) {
        Queue<Point> queue = new ArrayDeque<>();
        List<Point> peripheral = new ArrayList<Point>();
        queue.add(new Point(startI, startJ));
        visited.add(new Point(startI, startJ));

        while (!queue.isEmpty()) {
            Point current = queue.remove();
            int currentI = current.x();
            int currentJ = current.y();
            for (Point adjacent : Grids.getNeighbors(currentI, currentJ, width, height)) {
                int newI = adjacent.x();
                int newJ = adjacent.y();
                if (grid.hasType(newI, newJ, Geo.class) && !visited.contains(adjacent)) {
                    queue.add(adjacent);
                    visited.add(adjacent);
                }
                else if (!grid.hasType(newI, newJ, Geo.class)) {
                    peripheral.add(adjacent);
                }
            }
        }

        return peripheral;
    }

    public static void globalAct(Grid grid, Grid newGrid, int width, int height) {
        // mountains
        Set<Point> visited = new HashSet<>();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (newGrid.hasType(i, j, Geo.class) && !visited.contains(new Point(i, j))) {

                    Set<Point> oldVisited = new HashSet<>(visited);
                    List<Point> peripheral = bfs(grid, newGrid, i, j, width, height, visited);
                    List<Point> connected = visited.stream().
                            filter(p -> !oldVisited.contains(p)).toList();
                    if (connected.isEmpty()) {
                        throw new IllegalStateException("Geo: bfs explored connected component must not be empty.");
                    }

                    // mountains: tenacity
                    int size = connected.size();
                    int times = size <= 3 ? 5 - size : 1;

                    for (int t = 0; t < times; t++) {
                        double chance = BASE_MOUNTAINS_CHANCE;
                        if (Math.random() < chance) {
                            int randomIndex = (int) (Math.random() * peripheral.size());
                            int newI = peripheral.get(randomIndex).x();
                            int newJ = peripheral.get(randomIndex).y();
                            newGrid.softReplace(newI, newJ, new Geo(), newGrid.getTile(newI, newJ));
                        }
                    }

                }
            }
        }
    }

    @Override
    public void act(Grid grid, Grid newGrid, int i, int j, int width, int height) {
        // crystallize: countdown
        if (hasShield()) {
            shieldDuration--;
        } else if (isBrittle()) {
            brittleDuration--;
        }

        // crystallize
        for (Point adjacent : Grids.getNeighbors(i, j, width, height)) {
            int newI = adjacent.x();
            int newJ = adjacent.y();
            double chance = BASE_CRYSTALLIZE_CHANCE;
            if (hasShield()) { chance = 0; } // already have shield
            if (Math.random() < chance
                    && newGrid.getTile(newI, newJ).isStrongType()) {
                setShield(true);
                // crystallize: absorb
                if (Math.random() < BASE_ABSORB_CHANCE) {
                    newGrid.softReplace(newI, newJ, new Empty(), newGrid.getTile(newI, newJ));
                }
                break;
            }
        }
    }
}
