package com.naromil.gol.elements;

import com.naromil.gol.environment.SeasonType;
import com.naromil.gol.main.Color;
import com.naromil.gol.main.Grid;

public class Cryo extends Tile {
    public static final Color DEFAULT_COLOR = new Color(159, 214, 227);
    public static final int MAX_TICKS_WITHOUT_WINTER = 20;
    public static final double BASE_FROZEN_NORMAL_CHANCE = 0.05;
    public static final double BASE_FROZEN_WINTER_CHANCE = 0.40;

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

        // frozen
        for (Point2D adjacent : Grids.getNeighbors(i, j, width, height)) {
            int newI = i + adjacent.x();
            int newJ = j + adjacent.y();
            if (grid.getTile(newI, newJ) instanceof Hydro && Grids.countTilesWithinDistance(grid, newI, newJ, width, height, Cryo.class, 1) >= 2) {
                double frozenChance = (currentSeason == SeasonType.WINTER) ? BASE_FROZEN_WINTER_CHANCE : BASE_FROZEN_NORMAL_CHANCE;
                if (Math.random() < frozenChance && newGrid.getTile(newI, newJ) instanceof Hydro) {
                    newGrid.setTile(newI, newJ, new Cryo());
                }
            }
        }

        return true;
    }
}
