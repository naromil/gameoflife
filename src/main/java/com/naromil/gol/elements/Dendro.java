package com.naromil.gol.elements;

import com.naromil.gol.main.Color;

public class Dendro extends Tile {
    public static final Color DEFAULT_COLOR = Color.similarColor(new Color(165, 200, 59));
    public static final int MAX_TICKS_WITHOUT_HYDRO = 30;
    public static final double BASE_GROWTH_CHANCE = 0.05;

    public Dendro() {
        super(Color.similarColor(DEFAULT_COLOR));
    }

    private int tickWithoutHydro = 0;
  
    @Override
    public void act(Tile[][] tiles, Tile[][] newTiles, int i, int j, int width, int height) {
        // wither
        boolean isNearHydro = Grids.doesExistWithinDistance(tiles, i, j, width, height, Hydro.class, 1);
        if (!isNearHydro) {
            tickWithoutHydro++;
            if (tickWithoutHydro >= MAX_TICKS_WITHOUT_HYDRO) {
                newTiles[i][j] = null;
                return;
            }
        } else {
            tickWithoutHydro = 0;
        }

        // growth
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        for (int[] dir : directions) {
            int newI = i + dir[0];
            int newJ = j + dir[1];
            boolean success = Math.random() < (BASE_GROWTH_CHANCE * (isNearHydro ? 2 : 1));
            if (success && newI >= 0 && newI < height && newJ >= 0 && newJ < width) {
                newTiles[newI][newJ] = new Dendro();
            }
        }
    }
}
