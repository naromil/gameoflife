package com.naromil.gol.elements;

import com.naromil.gol.main.Color;
import com.naromil.gol.main.Grid;

public class Geo extends Tile {
    public static final Color DEFAULT_COLOR = new Color(248, 186, 78);
    public static final double BASE_MOUNTAINS_CHANCE = 0.005;
    public static final double BASE_CRYSTALLIZE_CHANCE = 0.10;
    public static final int INITIAL_SHIELD_DURATION = 20;
    
    private int shieldDuration = 0;

    public Geo() {
        super(Color.similarColor(DEFAULT_COLOR));
    }

    @Override
    public boolean act(Grid grid, Grid newGrid, int i, int j, int width, int height) {
        // crystallize: countdown
        boolean hasShield = shieldDuration > 0;
        if (hasShield) {
            shieldDuration--;
        }

        // mountains
        int adjacentGeoCount = Grids.countTilesWithinDistance(grid, i, j, width, height, Geo.class, 1);
        if (adjacentGeoCount < 2 && !hasShield) { // crystallize: protection from erosion
            return false; // erode
        }
        for (Point2D adjacent : Grids.getNeighbors(i, j, width, height)) {
            int newI = i + adjacent.x();
            int newJ = j + adjacent.y();
            int neighborAdjacentGeoCount = Grids.countTilesWithinDistance(grid, newI, newJ, width, height, Geo.class, 1);
            if (neighborAdjacentGeoCount >= 5 && Math.random() < BASE_MOUNTAINS_CHANCE) {
                newGrid.setTile(newI, newJ, new Geo()); // grow
            }
        }

        // crystallize
        for (Point2D adjacent : Grids.getNeighbors(i, j, width, height)) {
            int newI = i + adjacent.x();
            int newJ = j + adjacent.y();
            Tile adjacentTile = grid.getTile(newI, newJ);
            if (adjacentTile instanceof Pyro || adjacentTile instanceof Hydro || adjacentTile instanceof Cryo || adjacentTile instanceof Electro) {
                shieldDuration = INITIAL_SHIELD_DURATION;
                break;
            }
        }

        return true;
    }
}
