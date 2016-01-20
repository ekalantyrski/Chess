package com.Eric;

/**
 * Created by Felix on 1/3/2016.
 */
public class Score {

    private int score;
    private Save save;

    public Score(int score, Save save)
    {
        this.score = score;
        this.save = save;
    }

    public Save getSave() {
        return save;
    }

    public int getScore() {
        return score;
    }
    
    public Score copy()
    {
    	return new Score(score, save);
    }
}
