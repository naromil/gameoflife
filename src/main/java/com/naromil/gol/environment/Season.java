package com.naromil.gol.environment;

// keeps track of the current season, its duration and seasonal effects
public class Season {
    private final int SEASON_DURATION = 100;

    // wind blows in one of the 8 integer directions {di, dj}
    private static final int[][] WIND_DIRECTIONS = {
        {-1, 0}, {1, 0}, {0, -1}, {0, 1}, {-1, -1}, {-1, 1}, {1, -1}, {1, 1}
    };

    private SeasonType currentSeason;
    private int currentSeasonTick;
    private int[] windDirection;

    // default with SPRING and random wind direction
    public Season() {
        this(SeasonType.SPRING, 0, generateRandomWindDirection());
    }

    // comprehensive
    public Season(SeasonType currentSeason, int currentSeasonTick, int[] windDirection) {
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

    public int[] getWindDirection() {
        return windDirection;
    }

    public static int[] generateRandomWindDirection() {
        return WIND_DIRECTIONS[(int) (Math.random() * WIND_DIRECTIONS.length)];
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
        nextSeason.currentSeasonTick++;

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
