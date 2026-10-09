package com.naromil.gol.elements;

import com.naromil.gol.main.Color;

public class Pyro extends Tile {
    public static final Color DEFAULT_COLOR = Color.similarColor(new Color(239, 121, 56));
    public static final int MAX_TICKS_BURNOUT = 3;
    public static final double BASE_BURNING_CHANCE = 0.20;

    private int tickBurnout = 0;

    public Pyro() {
        super(Color.similarColor(DEFAULT_COLOR));
    }

    @Override
    public boolean act(Tile[][] tiles, Tile[][] newTiles, int i, int j, int width, int height) {
        // burnout: become ash
        tickBurnout++;
        if (tickBurnout >= MAX_TICKS_BURNOUT) {
            newTiles[i][j] = new Ash();
            return false;
        }

        // burning
        for (Point2D adjacent : Grids.getAdjacentCoordinates(i, j, width, height)) {
            int newI = i + adjacent.x();
            int newJ = j + adjacent.y();
            if (Grids.isTileType(tiles, newI, newJ, width, height, Dendro.class) && Math.random() < BASE_BURNING_CHANCE) {
                newTiles[newI][newJ] = new Pyro();
            }
        }
        
        return true;
    }
}
