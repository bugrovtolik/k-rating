package com.abuhrov;

/**
 * Represents the result of a match between two players.
 */
public class Result {
    private static final double POINTS_FOR_WIN = 1.0;
    private static final double POINTS_FOR_LOSS = 0.0;
    private static final double POINTS_FOR_DRAW = 0.5;
    private final Rating winner;
    private final Rating loser;
    private boolean isDraw = false;


    /**
     * Record a new result from a match between two players.
     */
    public Result(Rating winner, Rating loser) {
        if (!validPlayers(winner, loser)) {
            throw new IllegalArgumentException();
        }

        this.winner = winner;
        this.loser = loser;
    }


    /**
     * Record a draw between two players.
     */
    public Result(Rating player1, Rating player2, boolean isDraw) {
        if (!isDraw || !validPlayers(player1, player2)) {
            throw new IllegalArgumentException();
        }

        this.winner = player1;
        this.loser = player2;
        this.isDraw = true;
    }


    /**
     * Check that we're not doing anything silly like recording a match with only one player.
     */
    private boolean validPlayers(Rating player1, Rating player2) {
        return !player1.equals(player2);
    }


    /**
     * Test whether a particular player participated in the match represented by this result.
     */
    public boolean participated(Rating player) {
        return winner.equals(player) || loser.equals(player);
    }


    /**
     * Returns the "score" for a match.
     *
     * @return 1 for a win, 0.5 for a draw and 0 for a loss
     */
    public double getScore(Rating player) throws IllegalArgumentException {
        double score;

        if (winner.equals(player)) {
            score = POINTS_FOR_WIN;
        } else if (loser.equals(player)) {
            score = POINTS_FOR_LOSS;
        } else {
            throw new IllegalArgumentException("Player " + player.getUid() + " did not participate in match");
        }

        if (isDraw) {
            score = POINTS_FOR_DRAW;
        }

        return score;
    }


    /**
     * Given a particular player, returns the opponent.
     */
    public Rating getOpponent(Rating player) {
        Rating opponent;

        if (winner.equals(player)) {
            opponent = loser;
        } else if (loser.equals(player)) {
            opponent = winner;
        } else {
            throw new IllegalArgumentException("Player " + player.getUid() + " did not participate in match");
        }

        return opponent;
    }


    public Rating getWinner() {
        return this.winner;
    }


    public Rating getLoser() {
        return this.loser;
    }
}
