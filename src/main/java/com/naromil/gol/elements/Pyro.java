package com.naromil.gol.elements;

import java.util.ArrayList;
import java.util.List;

import com.naromil.gol.environment.SeasonType;
import com.naromil.gol.main.Color;
import com.naromil.gol.main.Grid;

public class Pyro extends Tile {
    public static final Color DEFAULT_COLOR = new Color(239, 121, 56);
    private static final int MAX_TICKS_BURNOUT = 3;
    private static final double BASE_BURNING_CHANCE = 0.12;
    private static final double BASE_WILD_FIRE_CHANCE = 0.10;

    private int tickBurnout = 0;

    public Pyro() {
        super(Color.similarColor(DEFAULT_COLOR));
    }

    public static void globalAct(Grid grid, Grid newGrid, int width, int height) {
        // fire on random Dendro
        double chance = BASE_WILD_FIRE_CHANCE;
        if (newGrid.getCurrentSeason() == SeasonType.SUMMER) {
            chance *= 2;
        }
        double expextedTimes = height * width / 2000.0;
        int times = (Math.random() < expextedTimes - Math.floor(expextedTimes))
                ? (int) Math.ceil(expextedTimes) : (int) Math.floor(expextedTimes);
        for (int t = 0; t < times; t++) {
            if (Math.random() < chance) {
                List<Point2D> dendroTiles = new ArrayList<>();
                for (int i = 0; i < height; i++) {
                    for(int j = 0; j < width; j++) {
                        Tile newTile = newGrid.getTileOfType(i, j, Dendro.class);
                        if (newTile instanceof Dendro) {
                            dendroTiles.add(new Point2D(i, j));
                        }
                    }
                }
                if (dendroTiles.isEmpty()) {
                    return;
                }
                int randomIndex = (int) (Math.random() * dendroTiles.size());
                Point2D randomTile = dendroTiles.get(randomIndex);
                int newI = randomTile.x();
                int newJ = randomTile.y();
                newGrid.setTile(newI, newJ, new Pyro());
            }
        }
    }

    @Override
    public void act(Grid grid, Grid newGrid, int i, int j, int width, int height) {
        // burnout: become ash
        tickBurnout++;
        if (tickBurnout >= MAX_TICKS_BURNOUT) {
            newGrid.softReplace(i, j, new Ash(), this);
            return;
        }

        // burgeon before burning because it is stricter
        // burgeon
        for (Point2D adjacent : Grids.getNeighbors(i, j, width, height)) {
            int newI = adjacent.x();
            int newJ = adjacent.y();
            Tile adjDendro = newGrid.getTileOfType(newI, newJ, Dendro.class);
            if (!(adjDendro instanceof Empty)
                    && ((Dendro) adjDendro).hasDendroCore()) {
                ((Dendro) adjDendro).setDendroCore(false);
                // burgeon: ignite
                for (Point2D adjacent2 : Grids.getNeighbors(newI, newJ,
                        width, height, 2)) {
                    int newI2 = adjacent2.x();
                    int newJ2 = adjacent2.y();
                    Tile adjDendro2 = newGrid.getTileOfType(newI2, newJ2, Dendro.class);
                    if (!(adjDendro2 instanceof Empty)) {
                        newGrid.softReplace(newI2, newJ2, new Pyro(), adjDendro2);
                    }
                }
            }
        }

        // burning
        for (Point2D adjacent : Grids.getNeighbors(i, j, width, height)) {
            int newI = adjacent.x();
            int newJ = adjacent.y();
            Tile adjDendro = newGrid.getTileOfType(newI, newJ, Dendro.class);
            if (!(adjDendro instanceof Empty)
                    && Math.random() < BASE_BURNING_CHANCE) {
                newGrid.softReplace(newI, newJ, new Pyro(), adjDendro);
            }
        }

        // melt
        boolean hasGoneOut = false;
        for (Point2D adjacent : Grids.getNeighbors(i, j, width, height)) {
            int newI = adjacent.x();
            int newJ = adjacent.y();
            Tile cryoTile = newGrid.getTileOfType(newI, newJ, Cryo.class);
            if (cryoTile instanceof Cryo) {
                newGrid.softReplace(newI, newJ, new Hydro(), cryoTile);
                hasGoneOut = true; // melt: goes out
            }
        }

        if (hasGoneOut) {
            newGrid.softReplace(i, j, new Empty(), this);
        }
    }
}
