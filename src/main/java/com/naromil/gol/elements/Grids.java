package com.naromil.gol.elements;

import java.util.ArrayList;
import java.util.List;

import com.naromil.gol.main.Grid;

public class Grids {
    /* ---- Grid exploration section ---- */
    public static boolean doesExistWithinDistance(Grid grid, int i, int j, int width, int height, Class<? extends Tile> tileType, int distance) {
        return countTilesWithinDistance(grid, i, j, width, height, tileType, distance) > 0;
    }

    public static int countTilesWithinDistance(Grid grid, int i, int j, int width, int height, Class<? extends Tile> tileType, int distance) {
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
}
