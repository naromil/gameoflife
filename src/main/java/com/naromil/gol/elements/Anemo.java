package com.naromil.gol.elements;

import java.util.ArrayList;
import java.util.List;

import com.naromil.gol.environment.SeasonType;
import com.naromil.gol.main.Color;
import com.naromil.gol.main.Grid;
import com.naromil.gol.main.Point;

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
    public static void globalAct(Grid grid, Grid newGrid) {
        int width = grid.getWidth();
        int height = grid.getHeight();
        // winds: spawn
        Point wind = grid.getWindDirection();

        double chance = BASE_WIND_CHANCE;
        if (grid.getCurrentSeason() == SeasonType.FALL) {
            chance *= 2;
        }
        double expextedTimes = height * width / 1000.0;
        int times = (Math.random() < expextedTimes - Math.floor(expextedTimes))
                ? (int) Math.ceil(expextedTimes) : (int) Math.floor(expextedTimes);
        for (int t = 0; t < times; t++) {
            if (Math.random() < chance) {
                // find the tiles that are the first in the current direction
                List<Point> firstTiles = new ArrayList<>();
                for (int i = 0; i < height; i++) {
                    for (int j = 0; j < width; j++) {
                        Point position = new Point(i, j);
                        if (Grids.isOutOfBounds(position.minus(wind), width, height)) {
                            firstTiles.add(position);
                        }
                    }
                }

                if (!firstTiles.isEmpty()) {
                    int randomIndex = (int) (Math.random() * firstTiles.size());
                    Point randomTile = firstTiles.get(randomIndex);
                    Tile oldTile = grid.getTile(randomTile);
                    if (!oldTile.isComplexType()) {
                        newGrid.replace(randomTile, oldTile, new Anemo(oldTile));
                    }
                }
            }
        }
    }

    @Override
    public void act(Grid grid, Grid newGrid, Point position) {
        int width = grid.getWidth();
        int height = grid.getHeight();
        // erosion
        if (containedTile instanceof Geo) {
            Geo geo = (Geo) containedTile;
            // shield: protection; brittle: erosion.
            if (!geo.hasShield() && (geo.isBrittle() || Math.random() < Geo.BASE_EROSION_CHANCE)) {
                newGrid.replace(position, containedTile, new Empty());
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
        containedTile.act(grid, newGrid, position);

        // swirl
        if (containedTile.isStrongType()) {
            Point[] neighbors = Grids.getNeighbors(position, width, height);
            int randomIndex = (int) (Math.random() * neighbors.length);
            Point randomNeighbor = neighbors[randomIndex];
            Tile tmp;
            if (grid.getTile(randomNeighbor).isComplexType()) {
                // swap with inner tile if complex
                tmp = grid.getTile(randomNeighbor).getContainedTile();
            }
            else {
                tmp = grid.getTile(randomNeighbor);
            }
            newGrid.replace(randomNeighbor, tmp, containedTile);
            if (Math.random() < BASE_SWIRL_COPY_CHANCE) {
                try {
                    containedTile = containedTile.getClass()
                            .getDeclaredConstructor().newInstance();
                } catch (Exception e) {
                    containedTile = tmp;
                }

            } else { // copy failure, swap
                containedTile = tmp;
            }
        }

        // goes in the fixed direction of current seasonal wind
        Point next = position.plus(grid.getWindDirection());

        if (Grids.isOutOfBounds(next, width, height)) {
            newGrid.replace(position, this, containedTile);
            return;
        }

        // newGrid since the wind ahead may have moved this tick
        Tile nextTile = newGrid.getTile(next);
        if (nextTile.isComplexType()) {
            return;
        }

        // wind: moves
        newGrid.replace(position, this, containedTile);
        // wind: absorb
        containedTile = nextTile;
        newGrid.replace(next, nextTile, this);
    }
}
