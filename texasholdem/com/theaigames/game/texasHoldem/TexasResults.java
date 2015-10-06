package com.theaigames.game.texasHoldem;

/**
 *
 *
 */
public class TexasResults {

    private static final TexasResults INSTANCE = new TexasResults();

    private void TexasResults(){}
    public static TexasResults getInstance(){return INSTANCE;}

    private volatile static int score;
    private volatile static int winner;

    public static int getWinner() {
        return winner;
    }

    public static void setWinner(int winner) {
        TexasResults.winner = winner;
    }

    public static int getScore() {
        return score;
    }

    public static void setScore(int score) {
        TexasResults.score = score;
    }
}
