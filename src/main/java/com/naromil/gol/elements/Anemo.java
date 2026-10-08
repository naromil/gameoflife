package com.naromil.gol.elements;

import com.naromil.gol.main.Color;

public class Anemo extends Tile {
    public static final Color DEFAULT_COLOR = Color.similarColor(new Color(117, 194, 170));

    public Anemo() {
        super(Color.similarColor(DEFAULT_COLOR));
    }

    @Override
    public void act(Tile[][] tiles, Tile[][] newTiles, int i, int j, int width, int height) {
        // Example: spread to random direction
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        for (int[] dir : directions) {
            int newI = i + dir[0];
            int newJ = j + dir[1];
            boolean success = Math.random() < 0.25;
            if (success && newI >= 0 && newI < height && newJ >= 0 && newJ < width) {
                newTiles[newI][newJ] = new Anemo();
            }
        }
    }
}
