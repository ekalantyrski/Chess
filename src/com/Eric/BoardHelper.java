package com.Eric;

import java.util.ArrayList;

import static com.Eric.PieceColor.BLACK;
import static com.Eric.PieceColor.WHITE;
import static com.Eric.PieceType.*;

public class BoardHelper {

    private Piece[][] board;
    private int globalModifier;
    private Position currentPosition;
    private Save save;
    private ArrayList<Position> validMoves;
    private boolean pawnPromotion;
    private Position newPosition;
    private int blackPieceCount;
    private int whitePieceCount;

    public BoardHelper(Piece[][] board, boolean whiteTurn, Save save) {
        this.board = board;
        globalModifier = (whiteTurn) ? -1 : 1;
        this.save = save;
        pawnPromotion = false;
        countPieces();
    }

    public ArrayList<Position> getValidMoves(Position currentPosition) {
        this.currentPosition = currentPosition;
        validMoves = getMoves(board[currentPosition.getX()][currentPosition.getY()].getPieceType());
        removeInvalidCastle();
        removeCheckmateMoves(validMoves);
        return validMoves;
    }

    private ArrayList<Position> getMoves(PieceType pt) {
        ArrayList<Position> validMoves = new ArrayList<>();
        switch (pt) {
            case BISHOP:
                validMoves = getBishopValidMoves();
                break;
            case KING:
                validMoves = getKingValidMoves();
                break;
            case KNIGHT:
                validMoves = getKnightValidMoves();
                break;
            case PAWN:
                validMoves = getPawnValidMoves();
                break;
            case QUEEN:
                validMoves = getQueenValidMoves();
                break;
            case ROOK:
                validMoves = getRookValidMoves();
                break;

        }
        return validMoves;
    }

    private ArrayList<Position> getPawnValidMoves() {
        Position tempPosition;
        ArrayList<Position> moves = new ArrayList<>();
        Piece currentPiece = board[currentPosition.getX()][currentPosition.getY()];
        //checks for moves going vertically
        if (currentPiece.amountOfMoves() == 0) {
            tempPosition = new Position(currentPosition.getX() + (1 * globalModifier), currentPosition.getY());
            if (!isOutOfBounds(tempPosition) && isNull(tempPosition)) {
                moves.add(tempPosition);
                tempPosition = new Position(currentPosition.getX() + (2 * globalModifier), currentPosition.getY());
                if (!isOutOfBounds(tempPosition) && isNull(tempPosition)) {
                    moves.add(tempPosition);
                }
            }
        } else {
            tempPosition = new Position(currentPosition.getX() + (1 * globalModifier), currentPosition.getY());
            if (!isOutOfBounds(tempPosition) && isNull(tempPosition)) {
                moves.add(tempPosition);
            }
        }
        int yModifier = 1;
        for (int i = 0; i < 2; i++) {
            tempPosition = new Position(currentPosition.getX() + (1 * globalModifier), currentPosition.getY() + (1 * yModifier * globalModifier));
            if (!isOutOfBounds(tempPosition) && !isNull(tempPosition)) {
                if (!isFriendlyPiece(tempPosition)) {
                    moves.add(tempPosition);
                }
            } else {
                tempPosition = new Position(currentPosition.getX(), currentPosition.getY() + (1 * yModifier * globalModifier));
                if (isEnPassant(tempPosition)) {
                    tempPosition = new Position(currentPosition.getX() + (1 * globalModifier), currentPosition.getY() + (1 * yModifier * globalModifier));
                    moves.add(tempPosition);
                }
            }
            yModifier = -1;

        }
        return moves;
    }


    private ArrayList<Position> getKnightValidMoves() {
        ArrayList<Position> moves = new ArrayList<>();
        int modifier = 1;
        int yModifier = 1;
        Position tempPosition;
        //moves with first 2 squares on columns
        for (int i = 0; i < 2; i++) {
            yModifier = 1;
            for (int j = 0; j < 2; j++) {
                tempPosition = new Position(currentPosition.getX() + (2 * modifier * globalModifier), currentPosition.getY() + (1 * yModifier * globalModifier));
                if (!isOutOfBounds(tempPosition) && (isNull(tempPosition) || !isFriendlyPiece(tempPosition))) {
                    moves.add(tempPosition);
                }
                yModifier = -1;
            }
            modifier = -1;

        }
        //moves with first 2 squares on row
        modifier = 1;
        for (int k = 0; k < 2; k++) {
            yModifier = 1;
            for (int l = 0; l < 2; l++) {
                tempPosition = new Position(currentPosition.getX() + (1 * modifier * globalModifier), currentPosition.getY() + (2 * yModifier * globalModifier));
                if (!isOutOfBounds(tempPosition) && (isNull(tempPosition) || !isFriendlyPiece(tempPosition))) {
                    moves.add(tempPosition);
                }
                yModifier = -1;
            }
            modifier = -1;
        }
        return moves;
    }

    private ArrayList<Position> getQueenValidMoves() {
        ArrayList<Position> moves = new ArrayList<>();
        moves.addAll(getStraightMoves());
        moves.addAll(getDiagonalMoves());
        return moves;
    }

    private ArrayList<Position> getRookValidMoves() {
        return getStraightMoves();
    }

    private ArrayList<Position> getBishopValidMoves() {
        return getDiagonalMoves();
    }


    private ArrayList<Position> getKingValidMoves() {
        ArrayList<Position> moves = new ArrayList<>();
        int modifier = 1;
        Position tempPosition;
        PieceColor pc = board[currentPosition.getX()][currentPosition.getY()].getPieceColor();
        for (int i = 0; i < 2; i++) {
            tempPosition = new Position(currentPosition.getX() + (modifier * globalModifier), currentPosition.getY());
            if (!isOutOfBounds(tempPosition) && (isNull(tempPosition) || !isFriendlyPiece(tempPosition)))
                moves.add(tempPosition);
            tempPosition = new Position(currentPosition.getX(), currentPosition.getY() + (modifier * globalModifier));
            if (!isOutOfBounds(tempPosition) && (isNull(tempPosition) || !isFriendlyPiece(tempPosition)))
                moves.add(tempPosition);
            tempPosition = new Position(currentPosition.getX() + (modifier * globalModifier), currentPosition.getY() + (modifier * globalModifier));
            if (!isOutOfBounds(tempPosition) && (isNull(tempPosition) || !isFriendlyPiece(tempPosition)))
                moves.add(tempPosition);
            modifier = -1;

        }

        tempPosition = new Position(currentPosition.getX() + (-1 * globalModifier), currentPosition.getY() + (1 * globalModifier));
        if (!isOutOfBounds(tempPosition) && (isNull(tempPosition) || !isFriendlyPiece(tempPosition)))
            moves.add(tempPosition);
        tempPosition = new Position(currentPosition.getX() + (1 * globalModifier), currentPosition.getY() + (-1 * globalModifier));
        if (!isOutOfBounds(tempPosition) && (isNull(tempPosition) || !isFriendlyPiece(tempPosition)))
            moves.add(tempPosition);

        //castling
        if (board[currentPosition.getX()][currentPosition.getY()].amountOfMoves() == 0) {
            //king side castle
            tempPosition = new Position(currentPosition.getX(), 7);
            if (!isNull(tempPosition) && board[tempPosition.getX()][tempPosition.getY()].amountOfMoves() == 0 && board[tempPosition.getX()][tempPosition.getY()].getPieceColor() == pc) {
                if (checkKingCastleable()) {
                    moves.add(new Position(currentPosition.getX(), currentPosition.getY() + 2));
                }
            }
            //queen side castle
            tempPosition = new Position(currentPosition.getX(), 0);
            if (!isNull(tempPosition) && board[tempPosition.getX()][tempPosition.getY()].amountOfMoves() == 0 && board[tempPosition.getX()][tempPosition.getY()].getPieceColor() == pc) {
                if (checkQueenCastleable()) {
                    moves.add(new Position(currentPosition.getX(), currentPosition.getY() - 2));
                }
            }
        }
        return moves;
    }


    private boolean checkKingCastleable() {
        Position tempPosition;
        for (int i = 1; i < 3; i++) {
            tempPosition = new Position(currentPosition.getX(), currentPosition.getY() + i);
            if (!isNull(tempPosition))
                return false;
        }
        return true;
    }

    private boolean checkQueenCastleable() {
        Position tempPosition = null;
        for (int i = 1; i < 4; i++) {
            tempPosition = new Position(currentPosition.getX(), currentPosition.getY() - i);
            if (!isNull(tempPosition)) {
                return false;
            }
        }
        return true;
    }


    private ArrayList<Position> getStraightMoves() {
        ArrayList<Position> moves = new ArrayList<>();
        Position tempPosition;
        int modifier = 1;
        int count;
        //vertical moves
        for (int i = 0; i < 2; i++) {
            count = 1;
            tempPosition = new Position(currentPosition.getX() + (count * modifier), currentPosition.getY());
            while (!isOutOfBounds(tempPosition)) {
                if (isNull(tempPosition)) {
                    moves.add(tempPosition);
                } else if (!isFriendlyPiece(tempPosition)) {
                    moves.add(tempPosition);
                    break;
                } else {
                    break;
                }
                count++;
                tempPosition = new Position(currentPosition.getX() + (count * modifier), currentPosition.getY());
            }
            modifier = -1;
        }
        modifier = 1;
        //get horizontal moves
        for (int j = 0; j < 2; j++) {
            count = 1;
            tempPosition = new Position(currentPosition.getX(), currentPosition.getY() + (count * modifier));
            while (!isOutOfBounds(tempPosition)) {
                if (isNull(tempPosition)) {
                    moves.add(tempPosition);
                } else if (!isFriendlyPiece(tempPosition)) {
                    moves.add(tempPosition);
                    break;
                } else {
                    break;
                }
                count++;
                tempPosition = new Position(currentPosition.getX(), currentPosition.getY() + (count * modifier));
            }
            modifier = -1;

        }
        return moves;
    }


    private ArrayList<Position> getDiagonalMoves() {
        ArrayList<Position> moves = new ArrayList<>();
        int xModifier = 1;
        int yModifier = 1;
        Position tempPosition;
        for (int i = 0; i < 2; i++) {
            // top left to bottom right
            tempPosition = new Position(currentPosition.getX() + xModifier, currentPosition.getY() + yModifier);
            while (!isOutOfBounds(tempPosition)) {
                if (isNull(tempPosition)) {
                    moves.add(tempPosition);
                } else if (!isFriendlyPiece(tempPosition)) {
                    moves.add(tempPosition);
                    break;
                } else {
                    break;
                }
                if (i == 0) {
                    xModifier++;
                    yModifier++;
                } else {
                    xModifier--;
                    yModifier--;
                }
                tempPosition = new Position(currentPosition.getX() + xModifier, currentPosition.getY() + yModifier);
            }
            xModifier = -1;
            yModifier = -1;

        }
        //top right to bottom left
        xModifier = 1;
        yModifier = -1;
        for (int j = 0; j < 2; j++) {
            tempPosition = new Position(currentPosition.getX() + xModifier, currentPosition.getY() + yModifier);
            while (!isOutOfBounds(tempPosition)) {
                if (isNull(tempPosition)) {
                    moves.add(tempPosition);
                } else if (!isFriendlyPiece(tempPosition)) {
                    moves.add(tempPosition);
                    break;
                } else {
                    break;
                }
                if (j == 0) {
                    xModifier++;
                    yModifier--;
                } else {
                    xModifier--;
                    yModifier++;
                }
                tempPosition = new Position(currentPosition.getX() + xModifier, currentPosition.getY() + yModifier);
            }
            xModifier = -1;
            yModifier = 1;
        }
        return moves;
    }

    private boolean isEnPassant(Position position) {
        if ((save.getLastMove() != null && save.getLastMove().equals(position)) && !isOutOfBounds(position) && !isNull(position) &&
                board[position.getX()][position.getY()].getPieceType() == PAWN && board[position.getX()][position.getY()].amountOfMoves() == 1)
            return true;
        else
            return false;
    }


    private boolean isNull(Position position) {
        //checks if a
        if (board[position.getX()][position.getY()] == null) {
            return true;
        } else {
            return false;
        }
    }


    private boolean isFriendlyPiece(Position position) {
        //returns true if the color of piece at position is same as the color of piece in currentPosition
        //called with isNull
        if (board[currentPosition.getX()][currentPosition.getY()].getPieceColor() == board[position.getX()][position.getY()].getPieceColor())
            return true;
        else
            return false;

    }

    private boolean isOutOfBounds(Position position) {
        //checks if a specific position is out of bounds
        //if x or y is bigger than 7 or smaller than 0
        if (position.getX() > 7 || position.getX() < 0 || position.getY() > 7 || position.getY() < 0)
            return true;
        else
            return false;

    }

    private ArrayList<Position> removeCheckmateMoves(ArrayList<Position> moves) {
        PieceColor pc = board[currentPosition.getX()][currentPosition.getY()].getPieceColor();
        Position actualPosition = new Position(currentPosition.getX(), currentPosition.getY());
        Position tempPosition;
        Piece tempPiece;
        boolean positionRemoved;
        globalModifier *= -1;
        int count;
        for (int moveLoop = 0; moveLoop < moves.size(); moveLoop++) {
            positionRemoved = false;
            count = 0;
            tempPosition = moves.get(moveLoop);
            tempPiece = board[tempPosition.getX()][tempPosition.getY()];
            board[tempPosition.getX()][tempPosition.getY()] = board[actualPosition.getX()][actualPosition.getY()];
            board[actualPosition.getX()][actualPosition.getY()] = null;
            int pieceCount = (globalModifier == 1) ? blackPieceCount : whitePieceCount;
            for (int i = 0; i < board.length && count < pieceCount && !positionRemoved; i++) {
                for (int j = 0; j < board[i].length && count < pieceCount && !positionRemoved; j++) {
                    if (board[i][j] != null && board[i][j].getPieceColor() != pc) {
                        count++;
                        currentPosition = new Position(i, j);
                        if (isKingAttackable(getMoves(board[i][j].getPieceType()))) {
                            moves.remove(moveLoop);
                            moveLoop--;
                            positionRemoved = true;
                        }

                    }

                }
            }
            board[actualPosition.getX()][actualPosition.getY()] = board[tempPosition.getX()][tempPosition.getY()];
            board[tempPosition.getX()][tempPosition.getY()] = tempPiece;

        }
        currentPosition = actualPosition;
        globalModifier *= -1;
        return moves;
    }


    private boolean isKingAttackable(ArrayList<Position> moves) {
        Position pos;
        for (int i = 0; i < moves.size(); i++) {
            pos = moves.get(i);
            if (board[pos.getX()][pos.getY()] != null && board[pos.getX()][pos.getY()].getPieceType() == KING)
                return true;
        }
        return false;
    }


    public int checkCheck(PieceColor pc) {
        ArrayList<Position> validMoves;
        Position tempPosition = currentPosition;
        //gets all possible moves for other team
        validMoves = new ArrayList<>();
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j] != null && board[i][j].getPieceColor() != pc) {
                    currentPosition = new Position(i, j);
                    validMoves.addAll(removeCheckmateMoves(getMoves(board[i][j].getPieceType())));
                }

            }
        }
        // if amount of moves is 0, checks if the king can be attacked by current team
        if (validMoves.size() == 0) {
            validMoves = new ArrayList<>();
            for (int k = 0; k < board.length; k++) {
                for (int l = 0; l < board[k].length; l++) {
                    if (board[k][l] != null && board[k][l].getPieceColor() == pc) {
                        currentPosition = new Position(k, l);
                        validMoves.addAll(removeCheckmateMoves(getMoves(board[k][l].getPieceType())));
                    }
                }
            }
            if (isKingAttackable(validMoves))
                return 2; //2 for check mate
            else {
                // if its not a check mate, this checks for check
                // finds king, then sees if he can move anywhere
                globalModifier *= -1;
                boolean foundKing = false;
                for (int i = 0; i < board.length; i++) {
                    for (int j = 0; j < board[i].length; j++) {
                        if (board[i][j] != null && board[i][j].getPieceType() == KING) {
                            currentPosition = new Position(i, j);
                            validMoves = getMoves(KING);
                            validMoves = removeCheckmateMoves(validMoves);
                            if (validMoves.size() == 0) {
                                globalModifier *= 1;
                                return 1;
                            }
                            foundKing = true;
                            break;

                        }
                    }
                    if (foundKing)
                        break;
                }
            }

        }
        globalModifier *= 1;
        currentPosition = tempPosition;
        return 0;
    }


    public boolean isPositionAttackable(Position position) {
        globalModifier *= -1;
        PieceColor pc = board[currentPosition.getX()][currentPosition.getY()].getPieceColor();
        Position tempPosition = currentPosition;
        ArrayList<Position> moves = new ArrayList<>();
        //gets all moves of opposite side
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j] != null && board[i][j].getPieceColor() != pc) {
                    currentPosition = new Position(i, j);
                    moves = getMoves(board[i][j].getPieceType());
                    for (int l = 0; l < moves.size(); l++) {
                        if (moves.get(l).equals(position)) {
                            globalModifier *= -1;
                            currentPosition = tempPosition;
                            return true;
                        }
                    }
                }
            }
        }
        globalModifier *= -1;
        currentPosition = tempPosition;
        return false;
    }

    private void toggleGlobalModifier() {
        globalModifier *= -1;
    }

    private void removeInvalidCastle() {
        if (board[currentPosition.getX()][currentPosition.getY()].getPieceType() == KING && board[currentPosition.getX()][currentPosition.getY()].amountOfMoves() == 0) {
            Position tempPosition = new Position(currentPosition.getX(), 5);
            //king side
            if (isPositionAttackable(tempPosition)) {
                removePosition(validMoves, new Position(currentPosition.getX(), 6));
            }
            //queen side
            tempPosition = new Position(currentPosition.getX(), 3);
            Position tempPosition2 = new Position(currentPosition.getX(), 1);
            if (isPositionAttackable(tempPosition) || isPositionAttackable(tempPosition2)) {
                removePosition(validMoves, new Position(currentPosition.getX(), 2));
            }

            //if king is in check
            tempPosition = new Position(currentPosition.getX(), 4);
            if(isPositionAttackable(tempPosition))
            {
                removePosition(validMoves, new Position(currentPosition.getX(), 6));
                removePosition(validMoves, new Position(currentPosition.getX(), 2));
            }
        }
    }

    

    private void removePosition(ArrayList<Position> moves, Position position) {
        for (int i = 0; i < moves.size(); i++) {
            if (moves.get(i).equals(position)) {
                moves.remove(i);
            }
        }


    }
    
    public void move(Position oldPosition, Position newPosition, boolean toggle)
    {
    	this.newPosition = newPosition;
    	if(board[oldPosition.getX()][oldPosition.getY()].getPieceType() == PAWN)
    	{

    		if(board[newPosition.getX()][newPosition.getX()] == null && newPosition.getY() != oldPosition.getY() && isEnPassant(new Position(newPosition.getX() - (1 * globalModifier), newPosition.getY()))) // enpassant
    		{
    			if(board[newPosition.getX() - (1 * globalModifier)][newPosition.getY()].getPieceColor() == BLACK)
    			{
    				blackPieceCount--;
    			}
    			else
    			{
    				whitePieceCount--;
    			}
    			board[newPosition.getX() - (1 * globalModifier)][newPosition.getY()] = null;
    		}
    		if(newPosition.getX() == 0 || newPosition.getX() == 7)
    		{
    			pawnPromotion = true;
    		}
    	}
    	else if(board[oldPosition.getX()][oldPosition.getY()].getPieceType() == KING && board[oldPosition.getX()][oldPosition.getY()].amountOfMoves() == 0)
    	{
    		//king side castle
    		if(newPosition.getY() == 6)
    		{
    			board[newPosition.getX()][5] = board[newPosition.getX()][7];
    			board[newPosition.getX()][7] = null;
    		}
    		else if(newPosition.getY() == 2) // queen side castle
    		{
    			board[newPosition.getX()][3] = board[newPosition.getX()][0];
    			board[newPosition.getX()][0] = null;
    		}
    	}
    	if(board[newPosition.getX()][newPosition.getY()] != null)
    	{
    		if(board[newPosition.getX()][newPosition.getY()].getPieceColor() == BLACK)
    		{
    			blackPieceCount--;
    		}
    		else
    		{
    			whitePieceCount--;
    		}
    	}
    	board[newPosition.getX()][newPosition.getY()] = board[oldPosition.getX()][oldPosition.getY()];
    	board[oldPosition.getX()][oldPosition.getY()] = null;
    	board[newPosition.getX()][newPosition.getY()].moved();
    	board[newPosition.getX()][newPosition.getY()].setPosition(newPosition);
    	save.addMove(oldPosition, newPosition);
        if(toggle)
    	    toggleGlobalModifier();
    	
    
    }
    
    public boolean getPawnPromotion()
    {
    	return pawnPromotion;
    }
    
    public void setPiece(PieceType pt)
    {
    	
    	PieceColor pc = (globalModifier == 1) ? WHITE : BLACK;
		board[newPosition.getX()][newPosition.getY()] = new Piece(pt, pc, newPosition);
		pawnPromotion = false;
    	
    }
    
    private void countPieces()
    {
    	blackPieceCount = 0;
    	whitePieceCount = 0;
    	for(int i = 0; i < board.length; i++)
    	{
    		for(int j = 0; j < board[i].length; j++)
    		{
    			if(board[i][j] != null)
    			{
    				if(board[i][j].getPieceColor() == BLACK)
    				{
    					blackPieceCount++; 
    				}
    				else
    				{
    					whitePieceCount++;
    				}
    			}
    		}
    	}
    	
    }
}
