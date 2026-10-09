package com.naromil.gol.elements;

import com.naromil.gol.main.Color;

public class Pyro extends Tile {
    public static final Color DEFAULT_COLOR = Color.similarColor(new Color(239, 121, 56));
    public static final int MAX_TICKS_BURNOUT = 3;

    private int tickBurnout = 0;

    public Pyro() {
        super(Color.similarColor(DEFAULT_COLOR));
    }

    @Override
    public void act(Tile[][] tiles, Tile[][] newTiles, int i, int j, int width, int height) {
        // burnout: become ash
        tickBurnout++;
        if (tickBurnout >= MAX_TICKS_BURNOUT) {
            newTiles[i][j] = new Ash();
            return;
        }
    }
}
