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
    public void act(Grid grid, Grid newGrid, int i, int j, int width, int height) {
        // thaw
        SeasonType currentSeason = newGrid.getCurrentSeason();
        if (currentSeason != SeasonType.WINTER) {
            tickWithoutWinter++;
            if (tickWithoutWinter >= MAX_TICKS_WITHOUT_WINTER) {
                newGrid.softReplace(i, j, new Empty(), this);
                return;
            }
        }

        // frozen
        for (Point2D adjacent : Grids.getNeighbors(i, j, width, height)) {
            int newI = adjacent.x();
            int newJ = adjacent.y();
            if (newGrid.getTile(newI, newJ) instanceof Hydro
                    && Grids.countTilesWithinDistance(grid, newI, newJ,
                    width, height, Cryo.class, 1) >= 2) {
                double frozenChance = (currentSeason == SeasonType.WINTER)
                        ? BASE_FROZEN_WINTER_CHANCE : BASE_FROZEN_NORMAL_CHANCE;
                if (Math.random() < frozenChance) {
                    newGrid.setTile(newI, newJ, new Cryo());
                }
            }
        }

    }
}
