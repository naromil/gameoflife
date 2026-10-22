package com.naromil.gol.elements;

import com.naromil.gol.main.Color;
import com.naromil.gol.main.Grid;

public class Ash extends Tile {
    public static final Color DEFAULT_COLOR = new Color(155, 155, 155);
    private static final int MAX_TICKS = 10;

    private int tick = 0;

    public Ash() {
        super(Color.similarColor(DEFAULT_COLOR));
    }

    @Override
    public int getRank() {
        return 80;
    }

    @Override
    public boolean isComplexType() {
        return false;
    }

    @Override 
    public boolean isStrongType() {
        return false;
    }

    @Override
    public void act(Grid grid, Grid newGrid, Point position) {
        // burnout: die
        // tick++;
        if (tick >= MAX_TICKS) {
            newGrid.replace(position, new Empty(), this);
        }
    }
}
