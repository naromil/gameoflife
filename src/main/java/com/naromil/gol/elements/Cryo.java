package com.naromil.gol.elements;

import com.naromil.gol.environment.SeasonType;
import com.naromil.gol.main.Color;
import com.naromil.gol.main.Grid;

public class Cryo extends Tile {
    public static final Color DEFAULT_COLOR = new Color(159, 214, 227);
    private static final int MAX_TICKS_WITHOUT_WINTER = 20;
    private static final double BASE_FROZEN_NORMAL_CHANCE = 0.05;
    private static final double BASE_FROZEN_WINTER_CHANCE = 0.40;

    private int tickWithoutWinter = 0;
    
    public Cryo() {
        super(Color.similarColor(DEFAULT_COLOR));
    }

    public Cryo(int tickWithoutWinter) {
        this();
        this.tickWithoutWinter = tickWithoutWinter;
    }

    @Override
    public int getRank() {
        return 40;
    }

    @Override
    public boolean isComplexType() {
        return false;
    }

    @Override 
    public boolean isStrongType() {
        return true;
    }

    @Override
    public void act(Grid grid, Grid newGrid, Point position) {
        int width = grid.getWidth();
        int height = grid.getHeight();
        // thaw
        SeasonType currentSeason = grid.getCurrentSeason();
        if (currentSeason != SeasonType.WINTER) {
            tickWithoutWinter++;
            if (tickWithoutWinter >= MAX_TICKS_WITHOUT_WINTER) {
                newGrid.replace(position, this, new Hydro());
                return;
            }
        } else {
            tickWithoutWinter = 0;
        }

        // frozen
        for (Point adjacent : Grids.getNeighbors(position, width, height)) {
            Tile adjHydro = grid.getTileOfType(adjacent, Hydro.class);
            if (!(adjHydro instanceof Empty)
                    && Grids.countTilesWithinDistance(grid, adjacent,
                    Cryo.class, 1) >= 2) {
                double frozenChance = (currentSeason == SeasonType.WINTER)
                        ? BASE_FROZEN_WINTER_CHANCE : BASE_FROZEN_NORMAL_CHANCE;
                if (Math.random() < frozenChance) {
                    newGrid.replace(adjacent, adjHydro, new Cryo(tickWithoutWinter + 1));
                }
            }
        }

    }
}
