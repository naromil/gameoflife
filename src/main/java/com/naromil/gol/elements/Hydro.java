package com.naromil.gol.elements;

import com.naromil.gol.main.Color;

public class Hydro extends Tile {
    public static final Color DEFAULT_COLOR = Color.similarColor(new Color(76, 194, 241));
    public static final int MAX_TICKS_RAIN = 15;
    public static final double BASE_VAPORIZE_CHANCE = 0.50;

    private int tickRain = 0;

    public Hydro() {
        super(Color.similarColor(DEFAULT_COLOR));
    }

    public void globalAct(Tile[][] tiles, Tile[][] newTiles, int width, int height) {
        // rain: spawn
        int rainX = (int) (Math.random() * width);
        int rainY = (int) (Math.random() * height);
        newTiles[rainY][rainX] = new Hydro();
    }

    @Override
    public void act(Tile[][] tiles, Tile[][] newTiles, int i, int j, int width, int height) {
        // rain: dry out
        if (Grids.doesExistWithinDistance(tiles, i, j, width, height, Hydro.class, 1)) {
            tickRain = 0;
        } else {
            tickRain++;
            if (tickRain >= MAX_TICKS_RAIN) {
                newTiles[i][j] = null;
                return;
            }
        }

        // vaporize
        for (Point2D adjacent : Grids.getAdjacentCoordinates(i, j, width, height)) {
            int newI = i + adjacent.x();
            int newJ = j + adjacent.y();
            if (Grids.isTileType(tiles, newI, newJ, width, height, Pyro.class) && Math.random() < BASE_VAPORIZE_CHANCE) {
                newTiles[newI][newJ] = null;
            }
        }
    }
}
