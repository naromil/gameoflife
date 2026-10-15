package com.naromil.gol.main;

import java.lang.reflect.Method;

import com.naromil.gol.elements.Ash;
import com.naromil.gol.elements.Empty;
import com.naromil.gol.elements.Tile;
import com.naromil.gol.elements.TileFactory;
import com.naromil.gol.environment.Season;
import com.naromil.gol.environment.SeasonType;

// responsible for storing the data of the grid state and logics for advancing to the next state
public class Grid {
    private static final int MAX_HEIGHT = 100;
    private static final int MAX_WIDTH = 100;

    /* ---- Properties ---- */
    private int height;
    private int width;
    private final Tile[][] tiles;

    private final Season season;

    /* ---- Grid operations ---- */
    // default constructor with fresh states
    public Grid() {
        this.height = MAX_HEIGHT;
        this.width = MAX_WIDTH;
        this.tiles = new Tile[MAX_HEIGHT][MAX_WIDTH];
        // Initialize the grid with some initial state
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                tiles[i][j] = new Empty();
            }
        }

        this.season = new Season();
    }

    // the copy used to advance a state
    private Grid(Grid old, Season season) {
        this.height = old.height;
        this.width = old.width;
        this.tiles = new Tile[MAX_HEIGHT][MAX_WIDTH];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                tiles[i][j] = old.tiles[i][j];
            }
        }

        this.season = season;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    protected void clear() {
        for (int i = 0; i < MAX_HEIGHT; i++) {
            for (int j = 0; j < MAX_WIDTH; j++) {
                tiles[i][j] = new Empty();
            }
        }
        season.clear();
    }

    private boolean isEmptyBoard(Tile[][] tiles) {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (!(tiles[i][j] instanceof Empty)) {
                    return false;
                }
            }
        }
        return true;
    }

    public void setSize(int gridWidth, int gridHeight) {
        if (gridWidth < 1 || gridWidth > MAX_WIDTH || gridHeight < 1 || gridHeight > MAX_HEIGHT) {
            throw new IllegalArgumentException("Grid size must be between 1 and " + MAX_WIDTH + " for width and 1 and " + MAX_HEIGHT + " for height.");
        }
        for (int i = this.width; i < gridWidth; i++) {
            for (int j = 0; j < this.height; j++) {
                tiles[j][i] = new Empty();
            }
        }
        for (int i = 0; i < this.width; i++) {
            for (int j = this.height; j < gridHeight; j++) {
                tiles[j][i] = new Empty();
            }
        }
        this.width = gridWidth;
        this.height = gridHeight;
    }

    /* ---- Tile operations ---- */
    public Tile[][] getTiles() {
        return tiles;
    }

    public Tile getTile(int i, int j) {
        return tiles[i][j];
    }

    public boolean hasType(int i, int j, Class<? extends Tile> tileType) {
        return tiles[i][j].hasType(tileType);
    }

    // get tile at a position if it or its contained tile is the specified type
    // otherwise return an Empty tile.
    public Tile getTileOfType(int i, int j, Class<? extends Tile> tileType) {
        Tile tile = tiles[i][j];
        if (tileType.isInstance(tile)) {
            return tile;
        } else if (tileType.isInstance(tile.getContainedTile())) {
            return tile.getContainedTile();
        }
        return new Empty();
    }

    public void setTile(int i, int j, Tile tile) {
        tiles[i][j] = tile;
    }

    /**
     * replace newTile but it can only overwrite oldTile.
     * can replace contained tiles as well, but only if non-empty before.
     * the preferred wrapper to replace a tile.
     * @param oldTile do not use Empty, omit it instead.
     */
    public void softReplace(int i, int j, Tile newTile, Tile oldTile) {
        if (tiles[i][j] instanceof Empty || tiles[i][j] instanceof Ash || tiles[i][j] == oldTile) {
            tiles[i][j] = newTile;
        } else if (tiles[i][j].getContainedTile() == oldTile) {
            tiles[i][j].setContainedTile(newTile);
        }
    }

    // cannot overwrite any element by default
    // cannot replace contained tiles
    public void softReplace(int i, int j, Tile newTile) {
        if (tiles[i][j] instanceof Empty || tiles[i][j] instanceof Ash) {
            tiles[i][j] = newTile;
        }
    }

    /* ---- Environment operations ---- */
    public int[] getWindDirection() {
        return season.getWindDirection();
    }

    public SeasonType getCurrentSeason() {
        return season.getCurrentSeason();
    }

    /* ---- Color Rendering ---- */
    public Color[][] getTilesColorGrid() {
        Color[][] colorGrid = new Color[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (!(tiles[i][j] instanceof Empty)) {
                    colorGrid[i][j] = tiles[i][j].getColor();
                } else {
                    colorGrid[i][j] = new Color(255, 255, 255); // Example: All tiles are white
                }
            }
        }
        return colorGrid;
    }

    public Color[][] getStatusColorGrid() {
        Color[][] statusGrid = new Color[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (!(tiles[i][j] instanceof Empty)) {
                    statusGrid[i][j] = tiles[i][j].getStatusColor(); // could be null
                }
            }
        }
        return statusGrid;
    }

    /* ---- Main Logic ---- */
    /**
     * Advances the grid to the next state based on the rules of the game.
     * First updates the environment and global actions since some tiles rely on them.
     * Each tile gets a chance to act without being affected by the actions of other tiles.
     * Each tile acts individually on a copy of the current state, so each sees the newest state.
     * @return a new Grid object representing the next state.
     */
    public Grid nextState() {
        // 1. Check if the board is empty and initialize it with random tiles if it is
        // If not empty, act with what is left on the board
        if(isEmptyBoard(tiles)) {
            TileFactory.initializeRandomBoard(tiles, height, width);
            return this;
        }

        // 2. Start from a copy of the current grid
        Grid newGrid = new Grid(this, season.nextState());

        // 3. Global actions
        // For each tile type, check if it has a globalAct method and invoke it
        for (Class<? extends Tile> tileClass : TileFactory.TYPES) {
            try {
                Method globalAct = tileClass.getMethod("globalAct", Grid.class, Grid.class, int.class, int.class);
                globalAct.invoke(null, this, newGrid, width, height);
            } catch (NoSuchMethodException e) {
                // element has no global action
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        // 4. Each tile act according to their own rules
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (!(tiles[i][j] instanceof Empty)) {
                    tiles[i][j].act(this, newGrid, i, j, width, height);
                }
            }
        }

        return newGrid;
    }
}
