package com.naromil.gol.main;

import java.lang.reflect.Method;
import com.naromil.gol.elements.Tile;
import com.naromil.gol.elements.TileFactory;
import com.naromil.gol.environment.Season;
import com.naromil.gol.environment.SeasonType;

// responsible for storing the data of the grid state and logics for advancing to the next state
public class Grid {
    private static final int MAX_HEIGHT = 100;
    private static final int MAX_WIDTH = 100;

    /* ---- Grid Properties ---- */
    private int height;
    private int width;
    private final Tile[][] tiles;

    private final Season season;

    // default constructor; starts with fresh state
    public Grid() {
        this(MAX_HEIGHT, MAX_WIDTH, new Season());
    }

    // the comprehensive constructor used to create the next state of the current
    private Grid(int height, int width, Season season) {
        this.height = MAX_HEIGHT;
        this.width = MAX_WIDTH;
        this.tiles = new Tile[MAX_HEIGHT][MAX_WIDTH];
        // Initialize the grid with some initial state
        for (int i = 0; i < MAX_HEIGHT; i++) {
            for (int j = 0; j < MAX_WIDTH; j++) {
                tiles[i][j] = null;
            }
        }

        this.season = season;
    }

    public Tile[][] getTiles() {
        return tiles;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public Object getTile(int i, int j) {
        return tiles[i][j];
    }

    public void setTile(int i, int j, Tile tile) {
        tiles[i][j] = tile;
    }

    public SeasonType getCurrentSeason() {
        return season.getCurrentSeason();
    }

    /* ---- Grid Management ---- */
    protected void clear() {
        for (int i = 0; i < MAX_HEIGHT; i++) {
            for (int j = 0; j < MAX_WIDTH; j++) {
                tiles[i][j] = null;
            }
        }
        season.clear();
    }

    private boolean emptyBoard(Tile[][] tiles) {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (tiles[i][j] != null) {
                    return false;
                }
            }
        }
        return true;
    }

    private void initializeRandomBoard() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                tiles[i][j] = TileFactory.createRandomTile();
            }
        }
    }

    public void setSize(int gridWidth, int gridHeight) {
        if (gridWidth < 1 || gridWidth > MAX_WIDTH || gridHeight < 1 || gridHeight > MAX_HEIGHT) {
            throw new IllegalArgumentException("Grid size must be between 1 and " + MAX_WIDTH + " for width and 1 and " + MAX_HEIGHT + " for height.");
        }
        for (int i = this.width; i < gridWidth; i++) {
            for (int j = 0; j < this.height; j++) {
                tiles[j][i] = null;
            }
        }
        for (int i = 0; i < this.width; i++) {
            for (int j = this.height; j < gridHeight; j++) {
                tiles[j][i] = null;
            }
        }
        this.width = gridWidth;
        this.height = gridHeight;
    }

    /* ---- Color Rendering ---- */
    public Color[][] getTilesColorGrid() {
        Color[][] colorGrid = new Color[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (tiles[i][j] != null) {
                    colorGrid[i][j] = tiles[i][j].getColor();
                } else {
                    colorGrid[i][j] = new Color(255, 255, 255); // Example: All tiles are white
                }
            }
        }
        return colorGrid;
    }

    /* ---- Main Logic ---- */
    public Grid nextState() {
        // 1. Check if the board is empty and initialize it with random tiles if it is
        // If not empty, act with what is left on the board
        if(emptyBoard(tiles)) {
            initializeRandomBoard();
            return this;
        }

        Grid newGrid = new Grid(height, width, season.nextState());

        // 2. Global actions
        for (Class<? extends Tile> tileClass : TileFactory.TYPES) {
            try {
                Method globalAct = tileClass.getMethod("globalAct", Grid.class, Grid.class, int.class, int.class);
                Tile tile = tileClass.getDeclaredConstructor().newInstance();
                globalAct.invoke(tile, this, newGrid, width, height);
            } catch (NoSuchMethodException e) {
                // element has no global action
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        // 3. Each tile act according to their own rules
        boolean[][] aliveState = new boolean[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (tiles[i][j] != null) {
                    boolean isAlive = tiles[i][j].act(this, newGrid, i, j, width, height);
                    aliveState[i][j] = isAlive;
                }
            }
        }

        // 4. Alive ones can continue if not covered already
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (aliveState[i][j] && newGrid.getTile(i, j) == null) {
                    newGrid.setTile(i, j, tiles[i][j]);
                }
            }
        }

        return newGrid;
    }
}
