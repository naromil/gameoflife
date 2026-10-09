package com.naromil.gol.elements;

import com.naromil.gol.main.Color;
import com.naromil.gol.main.Grid;

public class Pyro extends Tile {
    public static final Color DEFAULT_COLOR = new Color(239, 121, 56);
    public static final int MAX_TICKS_BURNOUT = 3;
    public static final double BASE_BURNING_CHANCE = 0.20;

    private int tickBurnout = 0;

    public Pyro() {
        super(Color.similarColor(DEFAULT_COLOR));
    }

    @Override
    public boolean act(Grid grid, Grid newGrid, int i, int j, int width, int height) {
        boolean flg = true;

        // burnout: become ash
        tickBurnout++;
        if (tickBurnout >= MAX_TICKS_BURNOUT) {
            newGrid.setTile(i, j, new Ash());
            return false;
        }

        // burning
        for (Point2D adjacent : Grids.getNeighbors(i, j, width, height)) {
            int newI = adjacent.x();
            int newJ = adjacent.y();
            if (grid.getTile(newI, newJ) instanceof Dendro && Math.random() < BASE_BURNING_CHANCE) {
                newGrid.setTile(newI, newJ, new Pyro());
            }
        }

        // burgeon
        for (Point2D adjacent : Grids.getNeighbors(i, j, width, height)) {
            int newI = adjacent.x();
            int newJ = adjacent.y();
            if (grid.getTile(newI, newJ) instanceof Dendro && ((Dendro) grid.getTile(newI, newJ)).hasDendroCore()) {
                // burgeon: ignite
                for (Point2D adjacent2 : Grids.getNeighbors(newI, newJ, width, height, 2)) {
                    int newI2 = adjacent2.x();
                    int newJ2 = adjacent2.y();
                    if (grid.getTile(newI2, newJ2) instanceof Dendro) {
                        newGrid.setTile(newI2, newJ2, new Pyro());
                    }
                }
            }
        }

        // melt
        for (Point2D adjacent : Grids.getNeighbors(i, j, width, height)) {
            int newI = adjacent.x();
            int newJ = adjacent.y();
            if (grid.getTile(newI, newJ) instanceof Cryo) {
                if(newGrid.getTile(newI, newJ) instanceof Cryo) {
                    newGrid.setTile(newI, newJ, new Hydro());
                }
                flg = false; // melt: goes out
            }
        }

        return flg;
    }
}
