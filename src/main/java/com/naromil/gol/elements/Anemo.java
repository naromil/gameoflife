package com.naromil.gol.elements;

import java.util.ArrayList;
import java.util.List;

import com.naromil.gol.main.Color;
import com.naromil.gol.main.Grid;

public class Anemo extends Tile {
    public static final Color DEFAULT_COLOR = new Color(117, 194, 170);
    public static final double BASE_WIND_CHANCE = 0.20;

    // the tile that is currently being affected by the storm
    private Tile containedTile = null;

    public Anemo() {
        super(Color.similarColor(DEFAULT_COLOR));
    }

    private Anemo(Tile containedTile) {
        this();
        this.containedTile = (containedTile == null
                || containedTile instanceof Empty) ? null : containedTile;
    }

    @Override
    public Color getStatusColor() {
        return containedTile != null ? containedTile.getColor() : null;
    }

    // the probability doesn't exclude anemo and electro but will not replace them
    public void globalAct(Grid grid, Grid newGrid, int width, int height) {
        // winds: spawn
        double[] windDirections = newGrid.getWindDirection();
        double windX = windDirections[0];
        double windY = windDirections[1];

        if (Math.random() < BASE_WIND_CHANCE) {
            // find the tiles that are the first in the current direction
            List<Point2D> firstTiles = new ArrayList<>();
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    int prevI = (int) (i + 0.5 - windX);
                    int prevJ = (int) (j + 0.5 - windY);
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
                if (!(oldTile instanceof Electro || oldTile instanceof Anemo)) {
                    newGrid.setTile(newY, newX, new Anemo(oldTile));
                }
            }
        }
    }

    @Override
    public void act(Grid grid, Grid newGrid, int i, int j, int width, int height) {
        Tile freedTile = containedTile != null ? containedTile : new Empty();

        // contained tile act
        if (containedTile instanceof Anemo || containedTile instanceof Electro) {
            throw new IllegalStateException("Anemo cannot contain another Anemo or Electro tile.");
        }
        if (containedTile != null) {
            containedTile.act(grid, newGrid, i, j, width, height);
        }

        // wind: moves
        newGrid.softReplace(i, j, freedTile, this);

        // goes in the fixed direction of current seasonal wind
        double[] windDirections = newGrid.getWindDirection();
        double windX = windDirections[0];
        double windY = windDirections[1];
        int newI = (int) (i + 0.5 + windX);
        int newJ = (int) (j + 0.5 + windY);

        if (!Grids.isOutOfBounds(newI, newJ, width, height)
                && !(newGrid.getTile(newI, newJ) instanceof Electro
                || newGrid.getTile(newI, newJ) instanceof Anemo)) {
            // wind: absorb
            Tile absorbedTile = newGrid.getTile(newI, newJ);
            containedTile = absorbedTile instanceof Empty ? null : absorbedTile;
            newGrid.setTile(newI, newJ, this); // guaranteed replacing the newest state
        }
    }
}
