package com.naromil.gol.environment;

// keeps track of the current season, its duration and seasonal effects
public class Season {
    private final int SEASON_DURATION = 100;

    private SeasonType currentSeason;
    private int currentSeasonTick;
    private double[] windDirection; // 2d vector length 1

    // default with SPRING and random wind direction
    public Season() {
        this(SeasonType.SPRING, 0, generateRandomWindDirection());
    }

    // comprehensive
    public Season(SeasonType currentSeason, int currentSeasonTick, double[] windDirection) {
        this.currentSeason = currentSeason;
        this.currentSeasonTick = currentSeasonTick;
        this.windDirection = windDirection;
    }

    // copy the state
    public Season(Season season) {
        this(season.currentSeason, season.currentSeasonTick, season.windDirection);
    }

    public SeasonType getCurrentSeason() {
        return currentSeason;
    }

    public int getCurrentSeasonTick() {
        return currentSeasonTick;
    }

    public double[] getWindDirection() {
        return windDirection;
    }

    public static double[] generateRandomWindDirection() {
        double angle = Math.random() * 2 * Math.PI;
        return new double[]{Math.cos(angle), Math.sin(angle)};
    }

    // reset the season to default values
    public void clear() {
        currentSeason = SeasonType.SPRING;
        currentSeasonTick = 0;
        windDirection = generateRandomWindDirection();
    }

    public Season nextState() {
        // season change
        Season nextSeason = new Season(this);

        if (nextSeason.currentSeasonTick >= SEASON_DURATION) {
            nextSeason.currentSeasonTick = 0;
            switch (nextSeason.currentSeason) {
                case SPRING:
                    nextSeason.currentSeason = SeasonType.SUMMER;
                    break;
                case SUMMER:
                    nextSeason.currentSeason = SeasonType.FALL;
                    break;
                case FALL:
                    nextSeason.currentSeason = SeasonType.WINTER;
                    break;
                case WINTER:
                    nextSeason.currentSeason = SeasonType.SPRING;
                    break;
            }
            // wind: change direction
            nextSeason.windDirection = Season.generateRandomWindDirection();
        }

        return nextSeason;
    }
}
