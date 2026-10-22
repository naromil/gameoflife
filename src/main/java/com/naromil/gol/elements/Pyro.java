package com.naromil.gol.elements;

import java.util.ArrayList;
import java.util.List;

import com.naromil.gol.environment.SeasonType;
import com.naromil.gol.main.Color;
import com.naromil.gol.main.Grid;

public class Pyro extends Tile {
    public static final Color DEFAULT_COLOR = new Color(239, 121, 56);
    private static final int MAX_TICKS_BURNOUT = 3;
    private static final double BASE_BURNING_CHANCE = 0.10;
    private static final double BASE_WILD_FIRE_CHANCE = 0.10;

    private int tickBurnout = 0;

    public Pyro() {
        super(Color.similarColor(DEFAULT_COLOR));
    }

    @Override
    public int getRank() {
        return 30;
    }

    @Override
    public boolean isComplexType() {
        return false;
    }

    @Override 
    public boolean isStrongType() {
        return true;
    }

    public static void globalAct(Grid grid, Grid newGrid) {
        int width = grid.getWidth();
        int height = grid.getHeight();
        // fire on random Dendro
        double chance = BASE_WILD_FIRE_CHANCE;
        if (grid.getCurrentSeason() == SeasonType.SUMMER) {
            chance *= 2;
        }
        double expextedTimes = height * width / 2000.0;
        int times = (Math.random() < expextedTimes - Math.floor(expextedTimes))
                ? (int) Math.ceil(expextedTimes) : (int) Math.floor(expextedTimes);
        for (int t = 0; t < times; t++) {
            if (Math.random() < chance) {
                List<Point> dendroTiles = new ArrayList<>();
                for (int i = 0; i < height; i++) {
                    for(int j = 0; j < width; j++) {
                        Point position = new Point(i, j);
                        Tile newTile = grid.getTileOfType(position, Dendro.class);
                        if (newTile instanceof Dendro) {
                            dendroTiles.add(position);
                        }
                    }
                }
                if (dendroTiles.isEmpty()) {
                    return;
                }
                int randomIndex = (int) (Math.random() * dendroTiles.size());
                Point randomTile = dendroTiles.get(randomIndex);
                newGrid.replace(randomTile, new Pyro(), grid.getTile(randomTile));
            }
        }
    }

    @Override
    public void act(Grid grid, Grid newGrid, Point position) {
        int width = grid.getWidth();
        int height = grid.getHeight();
        // burnout: become ash
        tickBurnout++;
        if (tickBurnout >= MAX_TICKS_BURNOUT) {
            newGrid.replace(position, new Ash(), this);
            return;
        }

        // burgeon before burning because it is stricter
        // burgeon
        for (Point adjacent : Grids.getNeighbors(position, width, height)) {
            Tile adjDendro = grid.getTileOfType(adjacent, Dendro.class);
            if (!(adjDendro instanceof Empty)
                    && ((Dendro) adjDendro).hasDendroCore()) {
                ((Dendro) adjDendro).setDendroCore(false);
                // burgeon: ignite
                for (Point adjacent2 : Grids.getNeighbors(adjacent,
                        width, height, 2)) {
                    Tile adjDendro2 = grid.getTileOfType(adjacent2, Dendro.class);
                    if (!(adjDendro2 instanceof Empty)) {
                        newGrid.replace(adjacent2, new Pyro(), adjDendro2);
                    }
                }
            }
        }

        // burning
        for (Point adjacent : Grids.getNeighbors(position, width, height)) {
            Tile adjDendro = grid.getTileOfType(adjacent, Dendro.class);
            if (!(adjDendro instanceof Empty)
                    && Math.random() < BASE_BURNING_CHANCE) {
                newGrid.replace(adjacent, new Pyro(), adjDendro);
            }
        }

        // melt
        boolean hasGoneOut = false;
        for (Point adjacent : Grids.getNeighbors(position, width, height)) {
            Tile cryoTile = grid.getTileOfType(adjacent, Cryo.class);
            if (cryoTile instanceof Cryo) {
                newGrid.replace(adjacent, new Hydro(), cryoTile);
                hasGoneOut = true; // melt: goes out
            }
        }

        if (hasGoneOut) {
            newGrid.replace(position, new Empty(), this);
        }
    }
}
