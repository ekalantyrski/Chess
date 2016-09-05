package com.Eric;

import java.applet.AudioClip;
import java.util.ArrayList;
import static com.Eric.PieceColor.*;
import static com.Eric.PieceType.*;

/**
 * Created by Eric on 12/29/2015.
 */
public class AI implements Runnable{

    private boolean boardFlipped;
    private Piece[][] realBoard; //this board is not altered until final move is calculated
    private Save realSave;     //this is save is not altered until final move is calculated
    private boolean isWhite;    //what side is ai playing as
    private BoardHelper realBH; //not used until final move is calculated
    private int moveAmount;
    private Board board2;
    private AudioClip moveSound;
    private boolean moveFinished;
    private final int PAWN_VALUE = 10; //values given to calculate the score for a board
    private final int KNIGHT_VALUE = 30;
    private final int BISHOP_VALUE = 30;
    private final int ROOK_VALUE = 50;
    private final int QUEEN_VALUE = 90;
    private final int KING_VALUE = 5000;

public AI(Piece[][] board, boolean isWhite, Save save, Board board2, boolean boardFlipped)
{
    this.realBoard = board;
    this.isWhite = isWhite;
    this.realSave = save;
    moveSound = DAL.getMoveSound();
    this.board2 = board2;
    moveFinished = false;
    this.boardFlipped = boardFlipped;
}

    /**
     * Class called when AI is to make a move
     * Makes variables that are used in calculate method and calls it
     * Then does the move that is calculated
     */
    public void run()
    {
        int movesToLookAhead = 4;
        moveAmount = realSave.getSaveSize() + movesToLookAhead;
    	Save save = new Save(1);
    	Score alphaScore = new Score(Integer.MIN_VALUE, new Save(1));
    	Score betaScore = new Score(Integer.MAX_VALUE, new Save(1));
        Score score = calculate(realBoard, movesToLookAhead, true, save, alphaScore, betaScore);
        ArrayList<Position> moves = score.getSave().getAllMoves();
        realBH = new BoardHelper(realBoard, isWhite, realSave, true, boardFlipped);
        realBH.move(moves.get(moves.size() - 2), moves.get(moves.size() - 1), true);
        System.out.println(score.getScore());
        board2.updateElementArray();
        moveSound.play();
        moveFinished = true;

    }

    /**
     * A recursive method that calculates what is the best move to do
     * This method uses the minimax algorithm
     * Alpha-Beta pruning is implemented ontop of minimax
     * 
     * @param board Stores all piece positions
     * @param depth Determines what depth to go to, decreased each time this method is called recursively
     * @param maximizingPlayer Keeps track if a specific node is maximizing score, or minimizing score
     * @param save  The moves that happened in previous itteration
     * @param alphaScore The alpha score, used for hard cut offs
     * @param betaScore The beta score, used for hard cut offs.
     * @return Returns a Score, containing the score chosen and the position to move to.
     */
    private Score calculate(Piece[][] board, int depth, boolean maximizingPlayer, Save save, Score alphaScore, Score betaScore)
    {
        if(depth == 0) //stopper
        {
            return new Score(getScore(board, save), save); //gets a board score
        }
        ArrayList<Position> validMoves;
        PieceColor pc;
        BoardHelper bh;
        if(maximizingPlayer) // if this player is maximizing the value
        {
            Piece[][] newBoard;
        	Save maxSave = new Save(1);
        	Score newScore;
            pc = (isWhite) ? WHITE : BLACK;
            bh = new BoardHelper(board, isWhite, save, false, boardFlipped);
            for(int i = 0; i < board.length; i++) // loops through board and finds all pieces of the maximizing players color
            {
                for(int j = 0; j < board[i].length; j++)
                {
                    if(board[i][j] != null && board[i][j].getPieceColor() == pc)
                    {
                        validMoves = bh.getValidMoves(new Position(i, j)); // gets all valid moves for this piece
                        Save newSave = new Save(1);
                        Position oldPosition = new Position(i,j);
                        for(int moves = 0; moves < validMoves.size(); moves++) //iterates through moves and makes a move
                        {
                            newSave.addMove(new Position(i,j), validMoves.get(moves));
                            newBoard = copyArray(board);    //hard copy
                            BoardHelper newBH = new BoardHelper(newBoard, isWhite, newSave, false, boardFlipped); //object to move the piece
                            newBH.move(oldPosition, validMoves.get(moves), true);   //makes the move
                            newScore = calculate(newBoard, depth - 1, false, newSave, alphaScore.copy(), betaScore.copy()); // recursive call
                            
                            if(newScore.getScore() > alphaScore.getScore()) // saves the best score that it got
                            {
                            	maxSave = new Save(1);
                            	maxSave.addMove(new Position(i, j), newSave.getLastMove());
                            	alphaScore = new Score(newScore.getScore(), maxSave);
                            }
                            
                            if(betaScore.getScore() <= alphaScore.getScore()) // hard alpha cut off
                            {
                            	return alphaScore;
                            }
                            	
                        }
                    }
                }
            }
            return alphaScore;

        }
        else // minmizing player decisions
        {
            Piece[][] newBoard;
        	Save minSave = new Save(1);
        	Score minScore = new Score(Integer.MAX_VALUE, minSave);
        	Score newScore;
            pc = (!isWhite) ? WHITE : BLACK;
            bh = new BoardHelper(board, !isWhite, save, false, boardFlipped);
            for(int i = 0; i < board.length; i++) //loops through board finding pieces of minimizing player
            {
                for(int j = 0; j < board[i].length; j++)
                {
                    if(board[i][j] != null && board[i][j].getPieceColor() == pc)
                    {
                        validMoves = bh.getValidMoves(new Position(i, j)); //  generates all moves for piece found
                        Save newSave = new Save(1);
                        Position oldPosition = new Position(i,j);
                        for(int moves = 0; moves < validMoves.size(); moves++) // iterates through all moves, and makes that move
                        {
                            newBoard = copyArray(board);
                            BoardHelper newBH = new BoardHelper(newBoard, !isWhite, newSave, false, boardFlipped);
                            newBH.move(oldPosition, validMoves.get(moves), true);
                            newScore = calculate(newBoard, depth - 1, true, newSave, alphaScore.copy(),  betaScore.copy()); // calls calculate with new piece positions
                            
                            if(newScore.getScore() < betaScore.getScore()) // saves the worst move it got
                            {
                            	minSave = new Save(1);
                            	minSave.addMove(new Position(i, j), newSave.getLastMove());
                            	betaScore = new Score(newScore.getScore(), minSave);
                            }
                            
                            if(betaScore.getScore() <= alphaScore.getScore()) // hard beta cut off
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

    /**
     * Determines a heuristic score from the view of the maximizing player
     *
     * This is quite weak right now
     *
     * @param board The given board to calculate
     * @param save The previous moves that happened
     * @return Returns a score that was caclulated
     */
    private double getScore(Piece[][] board, Save save)
    {
        double score = 0;
        if(save.getSaveSize() < 10)
        {
            score += getMoveScore(save, board) * 0.75;
            score += getMiddlePiecesScore(board, isWhite) * 1.25;
        }
        else
        {
            score += getMoveScore(save, board) * 1.25;
            score += getMiddlePiecesScore(board, isWhite) * 0.75;
        }
        score += getPieceDifference(board);
        score += getKingSafetyScore(board);
        score += getPiecePositionScore(board);

        return score;
    }

    /**
     * Calculates a score depending on positions of the pieces
     * and the moves that can be made
     * @param save The current save of the game
     * @param board The current board
     * @return The score calculated
     */
    private double getMoveScore(Save save, Piece[][] board)
    {
        ArrayList<Position> moves = getAllMoves(isWhite, save, board);
        int score = moves.size();
        for(int i = 0; i < moves.size(); i++) // if a move is on middle square, increment score
        {
            score++;
            if(moves.get(i).isCenterSquare())
                score++;
        }

        return score;
    }

    /**
     *  Returns a score based on how many pieces are in the middle
     * @param board The current board
     * @return Saves
     */
    private double getMiddlePiecesScore(Piece[][] board, boolean isWhite)
    {
        double score = 0;
        PieceColor pc = (isWhite) ? WHITE : BLACK;
        for(int i = 2; i < board.length - 2; i++)
        {
            for(int j = 0; j < board[i].length; j++)
            {
                Position position = new Position(i, j);
                if(position.isCenterSquare() && board[i][j] != null && board[i][j].getPieceColor() == pc)
                    score++;

            }
        }
        return score;
    }

    private double getKingSafetyScore(Piece[][] board)
    {
        double score = 0;
        if(moveAmount > 10 && moveAmount < 25) {
            if (board[0][6] != null && board[0][6].getPieceType() == KING)
            {
                if(board[1][6] != null && board[1][6].getPieceType() == PAWN && ((board[1][5] != null && board[1][5].getPieceType() == PAWN)
                        || (board[1][7] != null && board[1][7].getPieceType() == PAWN)))
                {
                        score += 10;
                }
            }
            else if (board[0][2] != null && board[0][2].getPieceType() == KING)
            {
                if(board[1][2] != null && board[1][2].getPieceType() == PAWN && ((board[1][1] != null && board[1][1].getPieceType() == PAWN)
                        || (board[1][3] != null && board[1][3].getPieceType() == PAWN)))
                {
                    score += 10;
                }

            }
        }

        return score;

    }

    private double getPiecePositionScore(Piece[][] board)
    {
        double score = 0;
        if(moveAmount < 15)
        {
            //checks if queen has moved
            if(board[0][3] == null)
            {
                score -= 25;
            }

            if(board[0][1] == null)
            {
                score += 10;
                if(board[0][6] == null)
                {
                    score += 5;
                }

            }
            else if(board[0][6] == null)
            {
                score += 10;
            }

            //checks if bishop has moved
            if(board[0][2] == null)
            {
                score += 10;
                if(board[0][5] == null)
                {
                    score += 5;
                }

            }
            else if(board[0][5] == null)
            {
                score += 10;
            }




        }




        return score;
    }


    /**Returns all moves possible by maximizing player
     *
     * @param whiteTurn Used to determine who is maximizing player
     * @param save The previous moves that happened
     * @param board Position of all pieces
     * @return Returns the score calculated
     */
    private ArrayList<Position> getAllMoves(boolean whiteTurn, Save save, Piece[][] board)
    {
        ArrayList<Position> validMoves = new ArrayList<>();
        BoardHelper bh = new BoardHelper(board, whiteTurn, save, false, boardFlipped);
        PieceColor pc = (isWhite) ? WHITE : BLACK;
        for(int i = 0; i < board.length; i++) // Loops through whole board
        {
            for(int j = 0; j < board[i].length; j++)
            {
                if(board[i][j] != null && board[i][j].getPieceColor() == pc) // Piece found
                {
                    Position position = new Position(i, j);
                    validMoves.addAll(bh.getValidMoves(position)); // Adds all moves to current moves
                }
            }
        }
        return validMoves;
    }

    /**
     * Gets the score difference of all pieces
     *
     * @param board All piece positions
     * @return Returns score calculated
     */
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

    /**
     *Calculates how many pieces the maximizing player has vs the minimizing player
     * ex. Max player has 5 pawns, min player has 4 pawns. Method would return 1
     *
     * LOOK THIS ONLY USES ONE VARIABLE TO STORE AND CALCULATE DIFFERENCE OMG OMG OMG
     *
     * @param pt Type of piece to find
     * @param board All piece positions
     * @return The difference in piece numbers
     */
    private int calculateDifference(PieceType pt, Piece[][] board)
    {
        PieceColor pc = (isWhite) ? WHITE : BLACK;
        int difference = 0;
        for(int loop = 0; loop < 2; loop++)
        {
            for(int i = 0; i < board.length;i++)
            {
                for(int j = 0; j < board[i].length; j++)
                {
                    if(board[i][j] != null && board[i][j].getPieceColor() == pc && board[i][j].getPieceType() == pt)
                    {
                        difference++;
                    }
                }
            }
            pc = (isWhite) ? BLACK : WHITE;
            difference *= -1;

        }
        return difference;

    }

    /**
     * Creates a deep copy of a given array
     *
     * @param original The original array to be copied
     * @return Returns a new array
     */
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

    public boolean moveFinished()
    {
        return moveFinished;
    }

    public void setMoveFinished(boolean moveFinished)
    {
        this.moveFinished = moveFinished;
    }
}
