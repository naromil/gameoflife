package com.naromil.gol.elements;

import java.util.ArrayList;
import java.util.List;

public class Grids {
    /* ---- Grid exploration section ---- */
    public static boolean doesExistWithinDistance(Tile[][] tiles, int i, int j, int width, int height, Class<? extends Tile> tileType, int distance) {
        return nearestTileDistance(tiles, i, j, width, height, tileType) <= distance;
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

    public static boolean isTileType(Tile[][] tiles, int i, int j, int width, int height, Class<? extends Tile> tileType) {
        if (i >= 0 && i < height && j >= 0 && j < width) {
            return tileType.isInstance(tiles[i][j]);
        }
        return false;
    }

    // valid adjacent Point2Ds after boundary check
    public static Point2D[] getAdjacentCoordinates(int i, int j, int width, int height) {
        List<Point2D> adjacentCoordinates = new ArrayList<>();
        for (int[] dir : Tile.DIRECTIONS) {
            int newI = i + dir[0];
            int newJ = j + dir[1];
            if (newI >= 0 && newI < height && newJ >= 0 && newJ < width) {
                adjacentCoordinates.add(new Point2D(newI, newJ));
            }
        }

        Point2D[] result = new Point2D[adjacentCoordinates.size()];
        for (int k = 0; k < adjacentCoordinates.size(); k++) {
            result[k] = adjacentCoordinates.get(k);
        }
        return result;
    }
}
