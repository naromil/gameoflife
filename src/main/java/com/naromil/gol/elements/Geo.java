package com.naromil.gol.elements;

import com.naromil.gol.main.Color;
import com.naromil.gol.main.Grid;

public class Geo extends Tile {
    public static final Color DEFAULT_COLOR = new Color(248, 186, 78);
    public static final double BASE_GEO_MOUNTAIN_CHANCE = 0.005;

    public Geo() {
        super(Color.similarColor(DEFAULT_COLOR));
    }

    @Override
    public boolean act(Grid grid, Grid newGrid, int i, int j, int width, int height) {
        // mountains
        // erode
        int adjacentGeoCount = Grids.countTilesWithinDistance(grid, i, j, width, height, Geo.class, 1);
        if (adjacentGeoCount < 2) {
            return false;
        }
        // grow
        for (Point2D adjacent : Grids.getNeighbors(i, j, width, height)) {
            int newI = i + adjacent.x();
            int newJ = j + adjacent.y();
            int neighborAdjacentGeoCount = Grids.countTilesWithinDistance(grid, newI, newJ, width, height, Geo.class, 1);
            if (neighborAdjacentGeoCount >= 5 && Math.random() < BASE_GEO_MOUNTAIN_CHANCE) {
                newGrid.setTile(newI, newJ, new Geo());
            }
        }

        return true;
    }
}
