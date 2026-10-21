package com.naromil.gol.elements;

import com.naromil.gol.main.Color;
import com.naromil.gol.main.Grid;

public class Empty extends Tile {
    public static final Color DEFAULT_COLOR = null;

    public Empty() {
        super(DEFAULT_COLOR);
    }

    @Override
    public int getRank() {
        return 90;
    }

    @Override
    public boolean isComplexType() {
        return false;
    }

    @Override 
    public boolean isStrongType() {
        return false;
    }

    // do nothing
    @Override
    public void act(Grid grid, Grid newGrid, int i, int j, int width, int height) {
        // TODO: Animation
    }
}
