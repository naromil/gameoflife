package com.naromil.gol.elements;

import com.naromil.gol.main.Color;

public class Dendro extends Tile {
    public static final Color DEFAULT_COLOR = Color.similarColor(new Color(165, 200, 59));
    public static final int MAX_TICKS_WITHOUT_HYDRO = 30;
    public static final int MAX_TICKS_DENDRO_CORE = 6;
    public static final double BASE_GROWTH_CHANCE = 0.05;
    public static final double BASE_BLOOM_CHANCE = 0.03;

    public Dendro() {
        super(Color.similarColor(DEFAULT_COLOR));
    }

    private int tickWithoutHydro = 0;

    private boolean hasDendroCore;
    private int tickDendroCore = 0;

    public boolean hasDendroCore() {
        return hasDendroCore;
    }

    @Override
    public boolean act(Tile[][] tiles, Tile[][] newTiles, int i, int j, int width, int height) {
        // wither
        boolean isNearHydro = Grids.doesExistWithinDistance(tiles, i, j, width, height, Hydro.class, 1);
        if (!isNearHydro) {
            tickWithoutHydro++;
            if (tickWithoutHydro >= MAX_TICKS_WITHOUT_HYDRO) {
                return false;
            }
        } else {
            tickWithoutHydro = 0;
        }

        // growth
        for (Point2D adjacent : Grids.getNeighbors(i, j, width, height)) {
            int newI = adjacent.x();
            int newJ = adjacent.y();
            // base chance and multipliers
            double chance = BASE_GROWTH_CHANCE;
            if (isNearHydro) {
                chance *= 2;
            }
            if (tiles[newI][newJ] instanceof Ash) {
                chance *= 2; // burnout: fertile
            }
            boolean success = Math.random() < chance;
            if (success) {
                newTiles[newI][newJ] = new Dendro();
            }
        }

        // bloom
        if (Grids.doesExistWithinDistance(tiles, i, j, width, height, Hydro.class, 1)) {
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
                    if (newTiles[newI][newJ] == null) {
                        newTiles[newI][newJ] = new Dendro();
                    }
                }
            }
        }

        return true;
    }
}
