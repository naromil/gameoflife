package com.naromil.gol.elements;

import com.naromil.gol.main.Color;
import com.naromil.gol.main.Grid;

public class Ash extends Tile {
    public static final Color DEFAULT_COLOR = new Color(105, 105, 105);
    public static final int MAX_TICKS = 10;

    private int tick = 0;

    public Ash() {
        super(Color.similarColor(DEFAULT_COLOR));
    }

    @Override
    public boolean act(Grid grid, Grid newGrid, int i, int j, int width, int height) {
        // burnout: die
        tick++;
        if (tick >= MAX_TICKS) {
            return false;
        }

        return true;
    }
}
