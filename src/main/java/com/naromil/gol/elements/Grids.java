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
    public static boolean doesExistWithinDistance(Grid grid, Point position,
            Class<? extends Tile> tileType, int distance) {
        return countTilesWithinDistance(grid, position, tileType, distance) > 0;
    }

    public static int countTilesWithinDistance(Grid grid, Point position,
            Class<? extends Tile> tileType, int distance) {
        int count = 0;
        for (Point adjacent : getNeighbors(position, grid.getWidth(), grid.getHeight(), distance)) {
            if (grid.getTile(adjacent).hasType(tileType)) {
                count++;
            }
        }
        return count;
    }

    public static boolean isOutOfBounds(Point position, int width, int height) {
        return position.x() < 0 || position.x() >= height
                || position.y() < 0 || position.y() >= width;
    }

    // valid adjacent Point coordinates after boundary check
    public static Point[] getNeighbors(Point position, int width, int height) {
        return getNeighbors(position, width, height, 1);
    }

    public static Point[] getNeighbors(Point position, int width, int height, int distance) {
        List<Point> adjacentCoordinates = new ArrayList<>();
        for (int dx = -distance; dx <= distance; dx++) {
            for (int dy = -distance; dy <= distance; dy++) {
                Point adjacent = position.plus(dx, dy);
                if (!isOutOfBounds(adjacent, width, height)) {
                    adjacentCoordinates.add(adjacent);
                }
            }
        }

        return adjacentCoordinates.toArray(Point[]::new);
    }

    public static List<Point> bfs(Grid grid, Point start,
            Class<? extends Tile> targetType, int maxCount) {
        Set<Point> visited = new LinkedHashSet<>();
        Queue<Point> queue = new ArrayDeque<>();
        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            Point current = queue.remove();
            for (Point adjacent : Grids.getNeighbors(current,
                    grid.getWidth(), grid.getHeight())) {
                if (grid.hasType(adjacent, targetType) && !visited.contains(adjacent)) {
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

    public static List<Point> bfs(Grid grid, Point start,
            Class<? extends Tile> targetType) {
        return bfs(grid, start, targetType, Integer.MAX_VALUE);
    }

    private record DirectionStore(Point current, Point direction, int distance) {}

    public static Point directionToNearestType(Grid grid, Point start,
            Class<? extends Tile> targetType) {
        Set<Point> visited = new HashSet<>();
        Queue<DirectionStore> queue = new ArrayDeque<>();
        visited.add(start);
        for (Point adjacent : Grids.getNeighbors(start,
                grid.getWidth(), grid.getHeight())) {
            if (!adjacent.equals(start)) {
                queue.add(new DirectionStore(adjacent, adjacent, 1));
                visited.add(adjacent);
            }
        }

        while (!queue.isEmpty()) {
            DirectionStore current = queue.remove();
            if (grid.hasType(current.current(), targetType)) {
                return current.direction();
            }
            for (Point adjacent : Grids.getNeighbors(current.current(),
                    grid.getWidth(), grid.getHeight())) {
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
