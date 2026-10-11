package com.naromil.gol.elements;

import java.util.ArrayList;
import java.util.List;

import com.naromil.gol.main.Color;
import com.naromil.gol.main.Grid;

public class Anemo extends Tile {
    public static final Color DEFAULT_COLOR = new Color(117, 194, 170);
    public static final double BASE_WIND_CHANCE = 0.20;

    // the tile that is currently being affected by the storm, Empty if none
    private Tile containedTile = new Empty();

    public Anemo() {
        super(Color.similarColor(DEFAULT_COLOR));
    }

    private Anemo(Tile containedTile) {
        this();
        this.containedTile = containedTile;
    }

    @Override
    public Color getStatusColor() {
        return containedTile.getColor(); // Empty has none
    }

    // the probability doesn't exclude complex tiles but will not replace them
    public void globalAct(Grid grid, Grid newGrid, int width, int height) {
        // winds: spawn
        int[] windDirections = newGrid.getWindDirection();
        int windI = windDirections[0];
        int windJ = windDirections[1];

        if (Math.random() < BASE_WIND_CHANCE) {
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
                if (!(TileFactory.isComplexTileType(oldTile))) {
                    newGrid.setTile(newY, newX, new Anemo(oldTile));
                }
            }
        }
    }

    @Override
    public void act(Grid grid, Grid newGrid, int i, int j, int width, int height) {
        // erosion
        if (containedTile instanceof Geo) {
            Geo geo = (Geo) containedTile;
            // shield: protection. brittle: erosion.
            if (!geo.hasShield() && (geo.isBrittle() || Math.random() < Geo.BASE_EROSION_CHANCE)) {
                newGrid.softReplace(i, j, new Empty(), containedTile);
            }
        }

        // contained tile act, Empty tile does nothing
        if (TileFactory.isComplexTileType(containedTile)) {
            throw new IllegalStateException("Anemo cannot contain another complex tile.");
        }
        containedTile.act(grid, newGrid, i, j, width, height);

        // wind: moves
        newGrid.softReplace(i, j, containedTile, this);

        // goes in the fixed direction of current seasonal wind
        int[] windDirections = newGrid.getWindDirection();
        int windI = windDirections[0];
        int windJ = windDirections[1];
        int newI = i + windI;
        int newJ = j + windJ;

        if (!Grids.isOutOfBounds(newI, newJ, width, height)
                && !(newGrid.getTile(newI, newJ).hasType(Electro.class)
                || newGrid.getTile(newI, newJ).hasType(Anemo.class))) {
            // wind: absorb
            containedTile = newGrid.getTile(newI, newJ);
            newGrid.setTile(newI, newJ, this); // guaranteed replacing the newest state
        }
    }
}
