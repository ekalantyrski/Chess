package com.Eric;

import java.util.ArrayList;

/**
 * Created by Eric on 11/23/2015.
 */
public class Save {

    ArrayList<Position> moves;

    public Save()
    {
        moves = new ArrayList<>();

    }


    public Position getLastMove()
    {
        if(moves.size() == 0)
            return null;
        else
            return moves.get(moves.size() - 1);
    }

    public void addMove(Position move)
    {
        moves.add(move);
    }


}
