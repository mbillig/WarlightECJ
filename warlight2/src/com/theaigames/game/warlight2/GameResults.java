package com.theaigames.game.warlight2;

/**
 * Created by Jonatan on 28-Sep-15.
 */
public class GameResults {

    private volatile static int winner;
    private volatile static int score;
    private volatile static double landControlledRatio;
    public volatile static double reinforcementSize;


    private static final GameResults INSTANCE = new GameResults();

    private void GameResults() {
    }

    public static GameResults getInstance() {
        return INSTANCE;
    }


    public void setScore(int score) {
        this.score = score;
    }

    public int getScore() {
        return this.score;
    }

    public void setWinner(int winner) {
        this.winner = winner;
    }

    public int getWinner() {
        return this.winner;
    }

    public static void setLandControlledRatio(double landControlledRatio) {
        GameResults.landControlledRatio = landControlledRatio;
    }

    public double getLandControlledRatio() {
        return this.landControlledRatio;
    }

    public static void setReinforcementSize(double reinforcementSize) {
        GameResults.reinforcementSize = reinforcementSize;
    }

    public static double getReinforcementSize() {
        return reinforcementSize;
    }
}
