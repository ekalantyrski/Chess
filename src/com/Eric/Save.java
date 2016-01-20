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
    
    private Save(ArrayList<Position> moves)
    {
    	this.moves = moves;
    }


    public Position getLastMove()
    {
    	if(moves.size() == 0)
    		return null;
    	else
    		return moves.get(moves.size() - 1);
    }

    public void addMove(Position oldPosition, Position newPosition)
    {
        moves.add(oldPosition);
        moves.add(newPosition);
    }

    public void save()
    {
    	
    }
    
    public static Save createNewSave()
    {
    	ArrayList<Position> moves = DAL.getMoves();
    	if(moves != null)
        {
            return new Save(moves);
        }
        else
        {
            return  null;
        }

    }
    public int getSaveSize()
    {
        return moves.size() / 2;
    }
    
    public ArrayList<Position> getAllMoves()
    {
    	return moves;
    }
}
