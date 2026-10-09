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

    public void act() {
        // season change
        currentSeasonTick++;
        if (currentSeasonTick >= SEASON_DURATION) {
            currentSeasonTick = 0;
            switch (currentSeason) {
                case SPRING:
                    currentSeason = SeasonType.SUMMER;
                    break;
                case SUMMER:
                    currentSeason = SeasonType.FALL;
                    break;
                case FALL:
                    currentSeason = SeasonType.WINTER;
                    break;
                case WINTER:
                    currentSeason = SeasonType.SPRING;
                    break;
            }
        }
    }

    public void clear() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'clear'");
    }
}
