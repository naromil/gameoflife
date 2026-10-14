package com.naromil.gol.elements;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import com.naromil.gol.main.Grid;

public class Grids {
    /* ---- Grid exploration section ---- */
    public static boolean doesExistWithinDistance(Grid grid, int i, int j,
            int width, int height, Class<? extends Tile> tileType, int distance) {
        return countTilesWithinDistance(grid, i, j, width, height, tileType, distance) > 0;
    }

    public static int countTilesWithinDistance(Grid grid, int i, int j,
            int width, int height, Class<? extends Tile> tileType, int distance) {
        int count = 0;
        for (Point2D adjacent : getNeighbors(i, j, width, height, distance)) {
            int newI = adjacent.x();
            int newJ = adjacent.y();
            if (grid.getTile(newI, newJ).hasType(tileType)) {
                count++;
            }
        }
        return count;
    }

    public static boolean isOutOfBounds(int i, int j, int width, int height) {
        return i < 0 || i >= height || j < 0 || j >= width;
    }

    // valid adjacent Point2D coordinates after boundary check
    public static Point2D[] getNeighbors(int i, int j, int width, int height) {
        return getNeighbors(i, j, width, height, 1);
    }

    public static Point2D[] getNeighbors(int i, int j, int width, int height, int distance) {
        List<Point2D> adjacentCoordinates = new ArrayList<>();
        for (int x = -distance; x <= distance; x++) {
            for (int y = -distance; y <= distance; y++) {
                int newI = i + x;
                int newJ = j + y;
                if (newI >= 0 && newI < height && newJ >= 0 && newJ < width) {
                    adjacentCoordinates.add(new Point2D(newI, newJ));
                }
            }
        }

        Point2D[] result = new Point2D[adjacentCoordinates.size()];
        for (int k = 0; k < adjacentCoordinates.size(); k++) {
            result[k] = adjacentCoordinates.get(k);
        }
        return result;
    }

    public static List<Point2D> bfs(Grid grid, Grid newGrid, int startI, int startJ,
            int width, int height, Class<? extends Tile> targetType, int maxCount) {
        Set<Point2D> visited = new LinkedHashSet<>();
        Queue<Point2D> queue = new ArrayDeque<>();
        queue.add(new Point2D(startI, startJ));
        visited.add(new Point2D(startI, startJ));

        while (!queue.isEmpty()) {
            Point2D current = queue.remove();
            int currentI = current.x();
            int currentJ = current.y();
            for (Point2D adjacent : Grids.getNeighbors(currentI, currentJ, width, height)) {
                int newI = adjacent.x();
                int newJ = adjacent.y();
                if (grid.hasType(newI, newJ, targetType) && !visited.contains(adjacent)) {
                    queue.add(adjacent);
                    visited.add(adjacent);
                }
                if (visited.size() >= maxCount) {
                    return new ArrayList<>(visited);
                }
            }
        }

        return new ArrayList<>(visited);
    }

    public static List<Point2D> bfs(Grid grid, Grid newGrid, int startI, int startJ,
            int width, int height, Class<? extends Tile> targetType) {
        return bfs(grid, newGrid, startI, startJ, width, height, targetType, Integer.MAX_VALUE);
    }

    private record DirectionStore(Point2D current, Point2D direction, int distance) {}

    public static Point2D directionToNearestType(Grid newGrid, int startI, int startJ, int width,
            int height, Class<? extends Tile> targetType) {
        Set<Point2D> visited = new HashSet<>();
        Queue<DirectionStore> queue = new ArrayDeque<>();
        visited.add(new Point2D(startI, startJ));
        for (Point2D adjacent : Grids.getNeighbors(startI, startJ, width, height)) {
            int newI = adjacent.x();
            int newJ = adjacent.y();
            if (newI != startI || newJ != startJ) {
                queue.add(new DirectionStore(adjacent, adjacent, 1));
                visited.add(adjacent);
            }
        }

        while (!queue.isEmpty()) {
            DirectionStore current = queue.remove();
            int currentI = current.current().x();
            int currentJ = current.current().y();
            if (newGrid.hasType(currentI, currentJ, targetType)) {
                return current.direction();
            }
            for (Point2D adjacent : Grids.getNeighbors(currentI, currentJ, width, height)) {
                if (!visited.contains(adjacent)) {
                    queue.add(new DirectionStore(adjacent,
                            current.direction(), current.distance() + 1));
                    visited.add(adjacent);
                }
            }
        }

        return null; // no target found 
    }
}
