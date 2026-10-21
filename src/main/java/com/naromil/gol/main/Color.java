package com.naromil.gol.main;

public record Color(int r, int g, int b) {
    public Color {
        if (r < 0) r = 0;
        else if (r > 255) r = 255;
        if (g < 0) g = 0;
        else if (g > 255) g = 255;
        if (b < 0) b = 0;
        else if (b > 255) b = 255;
    }

    public int max() {
        return (r > g) ? ((r > b) ? r : b) : ((g > b) ? g : b);
    }

    public int min() {
        return (r < g) ? ((r < b) ? r : b) : ((g < b) ? g : b);
    }

    /* ---- Utils ---- */
    private static final int DELTA = 10;

    private static int randomInt(int min, int max) {
        return (int) (Math.random() * (max - min + 1)) + min;
    }

    public static Color similarColor(Color c) {
        int newR = randomInt(c.r() - DELTA, c.r() + DELTA);
        int newG = randomInt(c.g() - DELTA, c.g() + DELTA);
        int newB = randomInt(c.b() - DELTA, c.b() + DELTA);
        return new Color(newR, newG, newB);
    }

    public static Color colorAdd(Color c, int delta) {
        int newR = c.r() + delta;
        int newG = c.g() + delta;
        int newB = c.b() + delta;
        return new Color(newR, newG, newB);
    }

    public static Color colorSaturate(Color c, int delta) {
        int l = (c.max() - c.min()) / 2; // grayscale
        int newR = l + (c.r() - l) * (100 + delta) / 100;
        int newG = l + (c.g() - l) * (100 + delta) / 100;
        int newB = l + (c.b() - l) * (100 + delta) / 100;
        return new Color(newR, newG, newB);
    }
}
