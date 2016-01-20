package com.Eric;

import java.util.ArrayList;
import static com.Eric.PieceColor.*;
import static com.Eric.PieceType.*;

/**
 * Created by Eric on 12/29/2015.
 */
public class AI{

    private Piece[][] realBoard;
    private Save realSave;
    private boolean isWhite;
    private BoardHelper realBH;
    private final int PAWN_VALUE = 10;
    private final int KNIGHT_VALUE = 30;
    private final int BISHOP_VALUE = 30;
    private final int ROOK_VALUE = 50;
    private final int QUEEN_VALUE = 90;
    private final int KING_VALUE = 5000;

public AI(Piece[][] board, boolean isWhite, Save save)
{
    this.realBoard = board;
    this.isWhite = isWhite;
    this.realSave = save;
}


    public void move()
    {
        long start = System.currentTimeMillis();
    	Save save = new Save();
    	Score alphaScore = new Score(Integer.MIN_VALUE, new Save());
    	Score betaScore = new Score(Integer.MAX_VALUE, new Save());
        Score score = calculate(realBoard, 4, true, save, alphaScore, betaScore);
        System.out.println("Score attained: " + score.getScore());
        System.out.println("Time taken: " + (System.currentTimeMillis() - start));
        ArrayList<Position> moves = score.getSave().getAllMoves();
        realBH = new BoardHelper(realBoard, isWhite, realSave);
        realBH.move(moves.get(moves.size() - 2), moves.get(moves.size() - 1), true);
        System.gc();

    }


    private Score calculate(Piece[][] board, int depth, boolean maximizingPlayer, Save save, Score alphaScore, Score betaScore)
    {
        if(depth == 0)
        {
            return new Score(getScore(board, save), save);
        }
        ArrayList<Position> validMoves;
        PieceColor pc;
        BoardHelper bh;
        if(maximizingPlayer)
        {
            Piece[][] newBoard;
        	Save maxSave = new Save();
        	Score maxScore = new Score(Integer.MIN_VALUE, maxSave);
        	Score newScore;
            pc = (isWhite) ? WHITE : BLACK;
            bh = new BoardHelper(board, isWhite, save);
            for(int i = 0; i < board.length; i++)
            {
                for(int j = 0; j < board[i].length; j++)
                {
                    if(board[i][j] != null && board[i][j].getPieceColor() == pc)
                    {
                        validMoves = bh.getValidMoves(new Position(i, j));
                        Save newSave = new Save();
                        Position oldPosition = new Position(i,j);
                        for(int moves = 0; moves < validMoves.size(); moves++)
                        {
                            newSave.addMove(new Position(i,j), validMoves.get(moves));
                            newBoard = copyArray(board);
                            BoardHelper newBH = new BoardHelper(newBoard, isWhite, newSave);
                            newBH.move(oldPosition, validMoves.get(moves), true);
                            newScore = calculate(newBoard, depth - 1, false, newSave, alphaScore.copy(), betaScore.copy());
                            
                            if(newScore.getScore() > alphaScore.getScore())
                            {
                            	maxSave = new Save();
                            	maxSave.addMove(new Position(i, j), newSave.getLastMove());
                            	alphaScore = new Score(newScore.getScore(), maxSave);
                            }
                            
                            if(betaScore.getScore() <= alphaScore.getScore())
                            {
                            	return alphaScore;
                            }
                            	
                        }
                    }
                }
            }
            return alphaScore;

        }
        else
        {
            Piece[][] newBoard;
        	Save minSave = new Save();
        	Score minScore = new Score(Integer.MAX_VALUE, minSave);
        	Score newScore;
            pc = (!isWhite) ? WHITE : BLACK;
            bh = new BoardHelper(board, !isWhite, save);
            for(int i = 0; i < board.length; i++)
            {
                for(int j = 0; j < board[i].length; j++)
                {
                    if(board[i][j] != null && board[i][j].getPieceColor() == pc)
                    {
                        validMoves = bh.getValidMoves(new Position(i, j));
                        Save newSave = new Save();
                        Position oldPosition = new Position(i,j);
                        for(int moves = 0; moves < validMoves.size(); moves++)
                        {
                            newBoard = copyArray(board);
                            BoardHelper newBH = new BoardHelper(newBoard, !isWhite, newSave);
                            newBH.move(oldPosition, validMoves.get(moves), true);
                            newScore = calculate(newBoard, depth - 1, true, newSave, alphaScore.copy(),  betaScore.copy());
                            
                            if(newScore.getScore() < betaScore.getScore())
                            {
                            	minSave = new Save();
                            	minSave.addMove(new Position(i, j), newSave.getLastMove());
                            	betaScore = new Score(newScore.getScore(), minSave);
                            }
                            
                            if(betaScore.getScore() <= alphaScore.getScore())
                            {
                            	return betaScore;
                            }
                            	
                        }
                    }
                }
            }
            return betaScore;	
        }
    }

    private int getScore(Piece[][] board, Save save)
    {
        int score = 0;
        score += (1 * getAllMoves(isWhite, save, board).size());
        score += getPieceDifference(board);
        //middle control
        //pawn structure

        return score;
    }


    private ArrayList<Position> getAllMoves(boolean whiteTurn, Save save, Piece[][] board)
    {
        ArrayList<Position> validMoves = new ArrayList<>();
        BoardHelper bh = new BoardHelper(board, whiteTurn, save);
        PieceColor pc = (whiteTurn) ? WHITE : BLACK;
        for(int i = 0; i < board.length; i++)
        {
            for(int j = 0; j < board[i].length; j++)
            {
                if(board[i][j] != null && board[i][j].getPieceColor() == pc)
                {
                    Position position = new Position(i, j);
                    validMoves.addAll(bh.getValidMoves(position));
                }
            }
        }
        return validMoves;
    }

    private int getPieceDifference(Piece[][] board)
    {
        int difference = 0;
        difference += (QUEEN_VALUE * calculateDifference(QUEEN, board));
        difference += (ROOK_VALUE * calculateDifference(ROOK, board));
        difference += (BISHOP_VALUE * calculateDifference(BISHOP, board));
        difference += (KNIGHT_VALUE * calculateDifference(KNIGHT, board));
        difference += (PAWN_VALUE * calculateDifference(PAWN, board));
        difference += (KING_VALUE * calculateDifference(KING, board));
        return difference;

    }

    private int calculateDifference(PieceType pt, Piece[][] board)
    {
        PieceColor pc = (isWhite) ? WHITE : BLACK;
        int amount = 0;
        for(int loop = 0; loop < 2; loop++)
        {
            for(int i = 0; i < board.length;i++)
            {
                for(int j = 0; j < board[i].length; j++)
                {
                    if(board[i][j] != null && board[i][j].getPieceColor() == pc && board[i][j].getPieceType() == pt)
                    {
                        amount++;
                    }
                }
            }
            pc = (isWhite) ? BLACK : WHITE;
            amount *= -1;

        }
        return amount;

    }

    private Piece[][] copyArray(Piece[][] original)
    {
        Piece[][] newArray = new Piece[original.length][original.length];
        for(int i = 0; i < original.length; i++)
        {
            for(int j = 0; j < original.length; j++)
            {
                if(original[i][j] != null)
                    newArray[i][j] = original[i][j].copy();
            }
        }
        return newArray;
    }
    
    //private int calculateMiddleControl(boolean isWhite)
    //{
    	//PieceColor pc = (isWhite) ? WHITE : BLACK;
    	
    //}

}
