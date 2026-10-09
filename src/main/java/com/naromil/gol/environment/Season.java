package com.naromil.gol.environment;

public class Season {
    private final int SEASON_DURATION = 100;

    private SeasonType currentSeason;
    private int currentSeasonTick;

    public Season() {
        this.currentSeason = SeasonType.SPRING;
        currentSeasonTick = 0;
    }

    public SeasonType getCurrentSeason() {
        return currentSeason;
    }

    public int getCurrentSeasonTick() {
        return currentSeasonTick;
    }

    public void clear() {
        currentSeason = SeasonType.SPRING;
        currentSeasonTick = 0;
    }

    public Season nextState() {
        // season change
        Season nextSeason = new Season();
        nextSeason.currentSeason = currentSeason;
        nextSeason.currentSeasonTick = currentSeasonTick + 1;

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
        }

        return nextSeason;
    }
}
