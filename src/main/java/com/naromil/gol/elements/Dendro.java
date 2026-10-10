package com.naromil.gol.elements;

import com.naromil.gol.main.Color;
import com.naromil.gol.main.Grid;

public class Dendro extends Tile {
    public static final Color DEFAULT_COLOR = new Color(165, 200, 59);
    public static final int MAX_TICKS_WITHOUT_HYDRO = 30;
    public static final int MAX_TICKS_DENDRO_CORE = 6;
    public static final double BASE_GROWTH_CHANCE = 0.05;
    public static final double BASE_BLOOM_CHANCE = 0.03;

    public Dendro() {
        super(Color.similarColor(DEFAULT_COLOR));
    }

    @Override
    public Color getStatusColor() {
        return hasDendroCore() ? DEFAULT_COLOR : null;
    }

    private int tickWithoutHydro = 0;

    private boolean hasDendroCore;
    private int tickDendroCore = 0;

    public boolean hasDendroCore() {
        return hasDendroCore;
    }

    @Override
    public void act(Grid grid, Grid newGrid, int i, int j, int width, int height) {
        // wither
        boolean isNearHydro = Grids.doesExistWithinDistance(grid,
                i, j, width, height, Hydro.class, 1);
        if (!isNearHydro) {
            tickWithoutHydro++;
            if (tickWithoutHydro >= MAX_TICKS_WITHOUT_HYDRO) {
                newGrid.softReplace(i, j, new Empty(), this);
                return;
            }
        } else {
            tickWithoutHydro = 0;
        }

        // growth
        boolean isNearCryo = Grids.doesExistWithinDistance(grid,
                i, j, width, height, Cryo.class, 1);
        if (!isNearCryo) { // frost
            for (Point2D adjacent : Grids.getNeighbors(i, j, width, height)) {
                int newI = adjacent.x();
                int newJ = adjacent.y();
                // base chance and multipliers
                double chance = BASE_GROWTH_CHANCE;
                if (isNearHydro) {
                    chance *= 2;
                }
                if (newGrid.getTile(newI, newJ) instanceof Ash) {
                    chance *= 2; // burnout: fertile
                }
                if (Math.random() < chance) {
                    newGrid.setTile(newI, newJ, new Dendro());
                }
            }
        }

        // bloom
        if (Grids.doesExistWithinDistance(grid,
                i, j, width, height, Hydro.class, 1)) {
            if (Math.random() < BASE_BLOOM_CHANCE) {
                hasDendroCore = true;
                tickDendroCore = 0;
            }
        }
        // bloom: plant new Dendro
        if (hasDendroCore) {
            tickDendroCore++;
            if (tickDendroCore >= MAX_TICKS_DENDRO_CORE) {
                hasDendroCore = false;
                tickDendroCore = 0;
                for (Point2D adjacent : Grids.getNeighbors(i, j, width, height)) {
                    int newI = adjacent.x();
                    int newJ = adjacent.y();
                    newGrid.softReplace(newI, newJ, new Dendro());
                }
            }
        }
    }
}
