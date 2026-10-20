package com.naromil.gol.elements;

import java.util.ArrayList;
import java.util.List;

import com.naromil.gol.environment.SeasonType;
import com.naromil.gol.main.Color;
import com.naromil.gol.main.Grid;

public class Anemo extends Tile {
    public static final Color DEFAULT_COLOR = new Color(117, 194, 170);
    private static final double BASE_WIND_CHANCE = 0.20;
    private static final double BASE_SWIRL_COPY_CHANCE = 0.20;

    public Anemo() {
        super(Color.similarColor(DEFAULT_COLOR));
    }

    private Anemo(Tile containedTile) {
        this();
        this.containedTile = containedTile;
    }

    @Override
    public int getRank() {
        return 20;
    }

    @Override
    public Color getStatusColor() {
        return containedTile.getColor(); // Empty has none
    }

    @Override
    public boolean isComplexType() {
        return true;
    }

    @Override 
    public boolean isStrongType() {
        return false;
    }

    private boolean pollinationState = false;

    // the probability doesn't exclude complex tiles but will not replace them
    public static void globalAct(Grid grid, Grid newGrid, int width, int height) {
        // winds: spawn
        int[] windDirections = newGrid.getWindDirection();
        int windI = windDirections[0];
        int windJ = windDirections[1];

        double chance = BASE_WIND_CHANCE;
        if (newGrid.getCurrentSeason() == SeasonType.FALL) {
            chance *= 2;
        }
        double expextedTimes = height * width / 1000.0;
        int times = (Math.random() < expextedTimes - Math.floor(expextedTimes))
                ? (int) Math.ceil(expextedTimes) : (int) Math.floor(expextedTimes);
        for (int t = 0; t < times; t++) {
            if (Math.random() < chance) {
                // find the tiles that are the first in the current direction
                List<Point2D> firstTiles = new ArrayList<>();
                for (int i = 0; i < height; i++) {
                    for (int j = 0; j < width; j++) {
                        int prevI = i - windI;
                        int prevJ = j - windJ;
                        if (Grids.isOutOfBounds(prevI, prevJ, width, height)) {
                            firstTiles.add(new Point2D(i, j));
                        }
                    }
                }

                if (!firstTiles.isEmpty()) {
                    int randomIndex = (int) (Math.random() * firstTiles.size());
                    Point2D randomTile = firstTiles.get(randomIndex);
                    int newX = randomTile.y();
                    int newY = randomTile.x();
                    Tile oldTile = newGrid.getTile(newY, newX);
                    if (!oldTile.isComplexType()) {
                        newGrid.softReplace(newY, newX, new Anemo(oldTile), oldTile);
                    }
                }
            }
        }
    }

    @Override
    public void act(Grid grid, Grid newGrid, int i, int j, int width, int height) {
        // erosion
        if (containedTile instanceof Geo) {
            Geo geo = (Geo) containedTile;
            // shield: protection; brittle: erosion.
            if (!geo.hasShield() && (geo.isBrittle() || Math.random() < Geo.BASE_EROSION_CHANCE)) {
                newGrid.softReplace(i, j, new Empty(), containedTile);
            }
        }

        // pollination
        if (containedTile instanceof Dendro) {
            pollinationState = true;
        }

        // pollination: plant
        // only one can happen at a time
        else if (pollinationState == true && containedTile instanceof Empty) {
            containedTile = new Dendro();
            pollinationState = false;
        }

        // contained tile act, Empty tile does nothing
        if (containedTile.isComplexType()) {
            throw new IllegalStateException("Anemo cannot contain another complex tile.");
        }
        containedTile.act(grid, newGrid, i, j, width, height);

        // swirl
        if (containedTile.isStrongType()) {
            Point2D[] neighbors = Grids.getNeighbors(i, j, width, height);
            int randomIndex = (int) (Math.random() * neighbors.length);
            Point2D randomNeighbor = neighbors[randomIndex];
            int newI = randomNeighbor.x();
            int newJ = randomNeighbor.y();
            Tile tmp;
            if (newGrid.getTile(newI, newJ).isComplexType()) {
                // swap with inner tile if complex
                tmp = newGrid.getTile(newI, newJ).getContainedTile();
            }
            else {
                tmp = newGrid.getTile(newI, newJ);
            }
            newGrid.softReplace(newI, newJ, containedTile, tmp);
            if (Math.random() < BASE_SWIRL_COPY_CHANCE) {
                try {
                    containedTile = containedTile.getClass().getDeclaredConstructor(containedTile.getClass()).newInstance();
                } catch (Exception e) {
                    containedTile = tmp;
                }

            } else { // copy failure, swap
                containedTile = tmp;
            }
        }

        // wind: moves
        newGrid.softReplace(i, j, containedTile, this);

        // goes in the fixed direction of current seasonal wind
        int[] windDirections = newGrid.getWindDirection();
        int windI = windDirections[0];
        int windJ = windDirections[1];
        int newI = i + windI;
        int newJ = j + windJ;

        if (!Grids.isOutOfBounds(newI, newJ, width, height)
                && !newGrid.getTile(newI, newJ).isComplexType()) {
            // wind: absorb
            containedTile = newGrid.getTile(newI, newJ);
            newGrid.softReplace(newI, newJ, this, newGrid.getTile(newI, newJ));
        }
    }
}
