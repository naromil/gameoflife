package com.naromil.gol.elements;

import com.naromil.gol.environment.SeasonType;
import com.naromil.gol.main.Color;
import com.naromil.gol.main.Grid;

public class Dendro extends Tile {
    public static final Color DEFAULT_COLOR = new Color(165, 200, 59);
    public static final int MAX_TICKS_WITHOUT_HYDRO = 30;
    public static final int INITIAL_TICKS_DENDRO_CORE = 6;
    public static final int MAX_TICKS_QUICKENED = 5;
    public static final double BASE_GROWTH_CHANCE = 0.05;
    public static final double BASE_BLOOM_CHANCE = 0.03;

    public Dendro() {
        super(Color.similarColor(DEFAULT_COLOR));
    }

    @Override
    public Color getStatusColor() {
        if (hasDendroCore()) {
            return DEFAULT_COLOR;
        } else if (isFrost()) {
            return Cryo.DEFAULT_COLOR;
        } else if (isQuickened()) {
            return Electro.DEFAULT_COLOR;
        } else {
            return null;
        }
    }

    private int tickWithoutHydro = 0;
    private int tickDendroCore = 0;

    public void setDendroCore(boolean dendroCore) {
        if (dendroCore) {
            tickDendroCore = INITIAL_TICKS_DENDRO_CORE;
        } else {
            tickDendroCore = 0;
        }

    }

    public boolean hasDendroCore() {
        return tickDendroCore > 0;
    }

    protected int tickQuickened = 0;
    private boolean isFrost = false;

    public void setQuickened(boolean quickened) {
        if (isFrost()) { return; } // frost: inhibit
        if (quickened) {
            tickQuickened = MAX_TICKS_QUICKENED;
        } else {
            tickQuickened = 0;
        }
    }

    public boolean isQuickened() {
        return tickQuickened > 0;
    }

    public void setFrost(boolean frost) {
        if (frost) {
            setQuickened(false); // frost: inhibit
        }
        isFrost = frost;
    }

    public boolean isFrost() {
        return isFrost;
    }

    @Override
    public void act(Grid grid, Grid newGrid, int i, int j, int width, int height) {
        // quicken: timer
        if (isQuickened()) {
            tickQuickened--;
        }

        // wither
        boolean isNearHydro = Grids.doesExistWithinDistance(grid,
                i, j, width, height, Hydro.class, 1)
                || Grids.doesExistWithinDistance(grid, i, j, width, height, Cryo.class, 1);
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
        setFrost(newGrid.getCurrentSeason() == SeasonType.WINTER
                || Grids.doesExistWithinDistance(grid, i, j, width, height, Cryo.class, 1));

        if (!isFrost()) { // frost
            for (Point2D adjacent : Grids.getNeighbors(i, j, width, height)) {
                int newI = adjacent.x();
                int newJ = adjacent.y();
                // base chance and multipliers
                double chance = BASE_GROWTH_CHANCE;
                if (isNearHydro) { chance *= 2; }
                if (newGrid.hasType(newI, newJ, Ash.class)) { chance *= 2; } // burnout: fertile
                if (newGrid.getCurrentSeason() == SeasonType.SPRING) { chance *= 1.5; }
                if (isQuickened()) { chance *= 3; }
                if (Math.random() < chance) {
                    newGrid.softReplace(newI, newJ, new Dendro());
                }
            }
        }

        // bloom: plant new Dendro
        if (hasDendroCore()) {
            tickDendroCore--;
            if (tickDendroCore == 0) {
                for (Point2D adjacent : Grids.getNeighbors(i, j, width, height)) {
                    int newI = adjacent.x();
                    int newJ = adjacent.y();
                    newGrid.softReplace(newI, newJ, new Dendro());
                }
            }
        }

        // bloom. after plant new Dendro because it should start next tick
        if (Grids.doesExistWithinDistance(grid,
                i, j, width, height, Hydro.class, 1)) {
            double chance = BASE_BLOOM_CHANCE;
            if (newGrid.getCurrentSeason() == SeasonType.SPRING) {
                chance *= 2;
            }
            if (Math.random() < chance) {
                setDendroCore(true);
            }
        }
    }
}
