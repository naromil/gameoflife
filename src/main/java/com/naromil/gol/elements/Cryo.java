package com.naromil.gol.elements;

import com.naromil.gol.environment.SeasonType;
import com.naromil.gol.main.Color;
import com.naromil.gol.main.Grid;

public class Cryo extends Tile {
    public static final Color DEFAULT_COLOR = Color.similarColor(new Color(159, 214, 227));
    public static final int MAX_TICKS_WITHOUT_WINTER = 20;

    private int tickWithoutWinter = 0;
    
    public Cryo() {
        super(Color.similarColor(DEFAULT_COLOR));
    }

    @Override
    public boolean act(Grid grid, Grid newGrid, int i, int j, int width, int height) {
        // thaw
        SeasonType currentSeason = newGrid.getCurrentSeason();
        if (currentSeason != SeasonType.WINTER) {
            tickWithoutWinter++;
            if (tickWithoutWinter >= MAX_TICKS_WITHOUT_WINTER) {
                return false;
            }
        }

        return true;
    }
}
