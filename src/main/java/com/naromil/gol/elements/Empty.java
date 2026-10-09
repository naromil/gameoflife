package com.naromil.gol.elements;

import com.naromil.gol.main.Color;
import com.naromil.gol.main.Grid;

public class Empty extends Tile {
    public static final Color DEFAULT_COLOR = new Color(255, 255, 255);

    public Empty() {
        super(DEFAULT_COLOR);
    }

    // In this case, it returns whether it is not replaced by another tile.
    @Override
    public boolean act(Grid grid, Grid newGrid, int i, int j, int width, int height) {
        return true;
    }
}
