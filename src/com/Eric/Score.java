package com.Eric;

/**
 * Created by Eric on 1/3/2016.
 */
public class Score {

    private double score; // score attained
    private Save save; // moves that happened

    /**
     * Constructor
     * @param score The score given
     * @param save The Save of the moves
     */
    public Score(double score, Save save)
    {
        this.score = score;
        this.save = save;
    }

    /**
     * Returns the save
     * @return save
     */
    public Save getSave() {
        return save;
    }

    /**
     * Returns the score
     * @return score
     */
    public double getScore() {
        return score;
    }

    /**
     * Creates a deep copy of this instance and returns it
     * @return a deep copy of Score
     */
    public Score copy()
    {
    	return new Score(score, save);
    }
}
