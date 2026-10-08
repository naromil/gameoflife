package com.naromil.gol.elements;

public class TileFactory {
    public static Tile createRandomTile() {
        int type = (int) (Math.random() * 7);
        switch (type) {
            case 0:
                return new Anemo();
            case 1:
                return new Geo();
            case 2:
                return new Electro();
            case 3:
                return new Dendro();
            case 4:
                return new Hydro();
            case 5:
                return new Pyro();
            case 6:
                return new Cryo();
            default:
                throw new IllegalStateException("Unexpected type number: " + type);
        }
    }
}
