package com.naromil.gol.elements;

import java.util.List;

import com.naromil.gol.main.Color;

// responsible for Tile type management
public class TileFactory {

    public final static List<Class<? extends Tile>> TYPES = List.of(
        Anemo.class,
        Geo.class,
        Electro.class,
        Dendro.class,
        Hydro.class,
        Pyro.class,
        Cryo.class,
        Ash.class,
        Core.class
    );

    // the list of tile types that can contain another one
    public static boolean isComplexTileType(Tile tile) {
        return tile instanceof Electro || tile instanceof Anemo || tile instanceof Core;
    }

    public static int getTypeSize() {
        return TYPES.size();
    }

    public static Class<? extends Tile> getTypeAt(int index) {
        if (index < 0 || index >= TYPES.size()) {
            throw new IndexOutOfBoundsException("Index must be between 0 and " + (TYPES.size() - 1));
        }
        return TYPES.get(index);
    }

    public static Color getDefaultColor(Class<? extends Tile> type) {
        try {
            return (Color) type.getField("DEFAULT_COLOR").get(null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Tile createRandomTile() {
        int type = (int) (Math.random() * getTypeSize());
        try {
            return (Tile) getTypeAt(type).getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return new Empty();
        }
    }
}
