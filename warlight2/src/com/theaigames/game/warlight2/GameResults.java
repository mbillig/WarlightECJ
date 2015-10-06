package com.theaigames.game.warlight2;

/**
 * Created by Jonatan on 28-Sep-15.
 */
public class GameResults {

    private volatile static int winner;
    private volatile static int score;
    private volatile static double landControlledRatio1;
    private volatile static double landControlledRatio2;
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

    public static void setLandControlledRatioPlayer1(double landControlledRatio1) {
        GameResults.landControlledRatio1 = landControlledRatio1;
    }

    public double getLandControlledRatioPlayer1() {
        return this.landControlledRatio1;
    }

    public static void setLandControlledRatioPlayer2(double landControlledRatio2) {
        GameResults.landControlledRatio2 = landControlledRatio2;
    }

    public double getLandControlledRatioPlayer2() {
        return this.landControlledRatio2;
    }


    public static void setReinforcementSize(double reinforcementSize) {
        GameResults.reinforcementSize = reinforcementSize;
    }

    public static double getReinforcementSize() {
        return reinforcementSize;
    }
}
