package com.Eric;

import java.util.ArrayList;

/**
 * Created by Eric on 11/23/2015.
 */
public class Save {

    private ArrayList<Position> moves; // the moves saved
    private int gameType; //game type
    /**
     * Constructor
     * @param gameType The gametype
     */
    public Save(int gameType)
    {
        moves = new ArrayList<>();
        this.gameType = gameType;

    }
    /**
     * Constructor
     * @param moves The moves already made
     * @param gameType the gametype
     */
    public Save(ArrayList<Position> moves, int gameType)
    {
    	this.moves = moves;
    	this.gameType = gameType;
    }

    /**
     * Returns the last move made
     * @return The Position value of last move
     */
    public Position getLastMove()
    {
    	if(moves.size() == 0)
    		return null;
    	else
    		return moves.get(moves.size() - 1);
    }
    /**
     * Adds a move, contains oldPosition and newPosition of piece
     * @param oldPosition The old position
     * @param newPosition The new position
     */
    public void addMove(Position oldPosition, Position newPosition)
    {
        moves.add(oldPosition);
        moves.add(newPosition);
    }
    /**
     * Saves the game using this instance of save
     * @param gameType The gametype of the current game
     */
    public void saveGame()
    {
        DAL.saveGame(moves, gameType);
    }
    /**
     * Creates a new Save using DAL, then returns that save instance
     * @return The save instance
     */
    public static Save createNewSave()
    {
    	return DAL.getSave();

    }
    /**
     * Returns the save size, divide by two because two additions per move
     * @return The save size
     */
    public int getSaveSize()
    {
        return (moves.size() / 2);
    }
    /**
     * Returns ArrayList of positions that is used
     * @return Arraylist os position
     */
    public ArrayList<Position> getAllMoves()
    {
    	return moves;
    }
    /**
     * Returns the gametype of the save
     * @return gametype
     */
    public int getGameType()
    {
    	return gameType;
    }
}
