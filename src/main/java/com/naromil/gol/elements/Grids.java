package com.naromil.gol.elements;

import java.util.ArrayList;
import java.util.List;

public class Grids {
    /* ---- Grid exploration section ---- */
    public static boolean doesExistWithinDistance(Tile[][] tiles, int i, int j, int width, int height, Class<? extends Tile> tileType, int distance) {
        for (Point2D adjacent : getNeighbors(i, j, width, height, distance)) {
            int newI = i + adjacent.x();
            int newJ = j + adjacent.y();
            if (tileType.isInstance(tiles[newI][newJ])) {
                return true;
            }
        }
        return false;
    }

    public static int nearestTileDistance(Tile[][] tiles, int i, int j, int width, int height, Class<? extends Tile> tileType) {
        int minDistance = Integer.MAX_VALUE;
        for (int x = 0; x < height; x++) {
            for (int y = 0; y < width; y++) {
                if (tileType.isInstance(tiles[x][y])) {
                    int distance = Math.max(Math.abs(i - x), Math.abs(j - y));
                    if (distance < minDistance) {
                        minDistance = distance;
                    }
                }
            }
        }
        return minDistance == Integer.MAX_VALUE ? -1 : minDistance;
    }

    // valid adjacent Point2Ds after boundary check
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
