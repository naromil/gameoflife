package com.naromil.gol.elements;

import com.naromil.gol.main.Color;
import com.naromil.gol.main.Grid;

public class Anemo extends Tile {
    public static final Color DEFAULT_COLOR = new Color(117, 194, 170);

    public Anemo() {
        super(Color.similarColor(DEFAULT_COLOR));
    }

    @Override
    public boolean act(Grid grid, Grid newGrid, int i, int j, int width, int height) {
        
        return true;
    }
}
