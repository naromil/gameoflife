package com.naromil.gol.elements;

import com.naromil.gol.main.Color;
import com.naromil.gol.main.Grid;

public class Electro extends Tile {
    public static final Color DEFAULT_COLOR = new Color(211, 118, 240);

    public Electro() {
        super(Color.similarColor(DEFAULT_COLOR));
    }

    @Override
    public boolean act(Grid grid, Grid newGrid, int i, int j, int width, int height) {
        
        return true;
    }
}
