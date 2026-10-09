package com.naromil.gol.elements;

public class Grids {
    /* ---- Grid exploration section ---- */
    public static boolean doesExistWithinDistance(Tile[][] tiles, int i, int j, int width, int height, Class<? extends Tile> tileType, int distance) {
        for (int x = -distance; x <= distance; x++) {
            for (int y = -distance; y <= distance; y++) {
                int newI = i + x;
                int newJ = j + y;
                if (newI >= 0 && newI < height && newJ >= 0 && newJ < width) {
                    if (tileType.isInstance(tiles[newI][newJ])) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static int nearestTileDistance(Tile[][] tiles, int i, int j, int width, int height, Class<? extends Tile> tileType) {
        int minDistance = Integer.MAX_VALUE;
        for (int x = 0; x < height; x++) {
            for (int y = 0; y < width; y++) {
                if (tileType.isInstance(tiles[x][y])) {
                    int distance = Math.abs(i - x) + Math.abs(j - y);
                    if (distance < minDistance) {
                        minDistance = distance;
                    }
                }
            }
        }
        return minDistance == Integer.MAX_VALUE ? -1 : minDistance;
    }
}
