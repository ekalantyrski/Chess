package com.Eric;

import java.util.ArrayList;

import static com.Eric.PieceColor.BLACK;
import static com.Eric.PieceColor.WHITE;
import static com.Eric.PieceType.*;

public class BoardHelper {

    /*
    This class is used to modify the board
    All methods that change the board go here
     */
    private Piece[][] board; // The board that is going to be modified
    private int globalModifier; // Used in calculation of pawns
    private Position currentPosition; // The currentPosition
    private Save save; // The save instance used
    private ArrayList<Position> validMoves;
    private boolean pawnPromotion;
    private Position newPosition;
    private int blackPieceCount; // count for black pieces
    private int whitePieceCount; // count for white pieces

    public BoardHelper(Piece[][] board, boolean whiteTurn, Save save, boolean isPlayer) {
        this.board = board;
        if(isPlayer)
        {
            globalModifier = -1;
        }else
        {
            globalModifier = (whiteTurn) ? -1 : 1;
        }
        this.save = save;
        pawnPromotion = false;
        countPieces();
    }

    /**
     * Entrance point for other classes to get validMoves
     * Calls other methods to do calculations
     *
     * @param currentPosition The position that was given by the player to get moves from
     * @return Returns arrayList of all moves
     */
    public ArrayList<Position> getValidMoves(Position currentPosition) {
        this.currentPosition = currentPosition;
        validMoves = getMoves(board[currentPosition.getX()][currentPosition.getY()].getPieceType());
        removeInvalidCastle(); // Removes invalid castles, has to be here or else indirect recursion crashes program
        removeCheckmateMoves(validMoves); // Removes all moves that would result in a checkmate the very next turn (illegal)
        return validMoves;
    }

    /**
     * Given a pieceType, switch statement chooses what moves are possible
     *
     * @param pt The pieceType of the move
     * @return The validMoves for that piece
     */
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

    /**
     * Calculates possible moves for a pawn
     *
     * @return The possible moves for a pawn
     */
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
        //checks for diagonal moves
        for (int i = 0; i < 2; i++) {
            tempPosition = new Position(currentPosition.getX() + (1 * globalModifier), currentPosition.getY() + (1 * yModifier * globalModifier));
            if (!isOutOfBounds(tempPosition) && !isNull(tempPosition)) {
                if (!isFriendlyPiece(tempPosition)) {
                    moves.add(tempPosition);
                }
            } else {
                tempPosition = new Position(currentPosition.getX(), currentPosition.getY() + (1 * yModifier * globalModifier));
                if (isEnPassant(tempPosition)) { //checks if a move is enPassant
                    tempPosition = new Position(currentPosition.getX() + (1 * globalModifier), currentPosition.getY() + (1 * yModifier * globalModifier));
                    moves.add(tempPosition);
                }
            }
            yModifier = -1;

        }
        return moves;
    }

    /**
     * Calculates all moves possible for a knight
     *
     * @return The moves possible
     */
    private ArrayList<Position> getKnightValidMoves() {
        ArrayList<Position> moves = new ArrayList<>();
        int modifier = 1;
        int yModifier = 1;
        Position tempPosition;
        //moves with first 2 squares on columns
        for (int i = 0; i < 2; i++) {
            yModifier = 1;
            for (int j = 0; j < 2; j++) {
                tempPosition = new Position(currentPosition.getX() + (2 * modifier), currentPosition.getY() + (1 * yModifier));
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
                tempPosition = new Position(currentPosition.getX() + (1 * modifier), currentPosition.getY() + (2 * yModifier));
                if (!isOutOfBounds(tempPosition) && (isNull(tempPosition) || !isFriendlyPiece(tempPosition))) {
                    moves.add(tempPosition);
                }
                yModifier = -1;
            }
            modifier = -1;
        }
        return moves;
    }

    /**
     * Gets all moves that a queen can do, calls other functions
     * And adds those moves
     *
     * @return Returns the moves possible
     */
    private ArrayList<Position> getQueenValidMoves() {
        ArrayList<Position> moves = new ArrayList<>();
        moves.addAll(getStraightMoves()); // gets all moves possible horizontally and vertically
        moves.addAll(getDiagonalMoves()); // gets all moves possible diagonally
        return moves;
    }

    /**
     * Gets all moves possible by a rook
     *
     * @return The moves possible
     */
    private ArrayList<Position> getRookValidMoves() {
        return getStraightMoves();
    }

    /**
     * Gets all moves possible by a bishop
     *
     * @return Returns the moves possible
     */
    private ArrayList<Position> getBishopValidMoves() {
        return getDiagonalMoves();
    }

    /**
     * Gets all moves possible by a king
     *
     * @return Returns moves possible
     */
    private ArrayList<Position> getKingValidMoves() {
        ArrayList<Position> moves = new ArrayList<>();
        int modifier = 1;
        Position tempPosition;
        // Calculates the tiles adjacent to him
        PieceColor pc = board[currentPosition.getX()][currentPosition.getY()].getPieceColor();
        for (int i = 0; i < 2; i++) {
            tempPosition = new Position(currentPosition.getX() + (modifier), currentPosition.getY());
            if (!isOutOfBounds(tempPosition) && (isNull(tempPosition) || !isFriendlyPiece(tempPosition)))
                moves.add(tempPosition);
            tempPosition = new Position(currentPosition.getX(), currentPosition.getY() + (modifier));
            if (!isOutOfBounds(tempPosition) && (isNull(tempPosition) || !isFriendlyPiece(tempPosition)))
                moves.add(tempPosition);
            tempPosition = new Position(currentPosition.getX() + (modifier), currentPosition.getY() + (modifier));
            if (!isOutOfBounds(tempPosition) && (isNull(tempPosition) || !isFriendlyPiece(tempPosition)))
                moves.add(tempPosition);
            modifier = -1;

        }
        tempPosition = new Position(currentPosition.getX() + (-1), currentPosition.getY() + (1));
        if (!isOutOfBounds(tempPosition) && (isNull(tempPosition) || !isFriendlyPiece(tempPosition)))
            moves.add(tempPosition);
        tempPosition = new Position(currentPosition.getX() + (1), currentPosition.getY() + (-1));
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

    /**
     * Checks if the King can do a king side castle legally
     * (not fully, more calculations in getValidMoves method)
     * @return Returns boolean based on results
     */
    private boolean checkKingCastleable() {
        Position tempPosition;
        for (int i = 1; i < 3; i++) {
            tempPosition = new Position(currentPosition.getX(), currentPosition.getY() + i);
            if (!isNull(tempPosition))
                return false;
        }
        return true;
    }

    /**
     * Checks if a king can do a queen side castle legally
     * (not fully, more calculations in getValidMoves method)
      * @return Returns boolean based on results
     */
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

    /**
     * Calculates all moves that are possible on horizontal or vertical path
     *
     * @return The moves possible
     */
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

    /**
     * Calculates all diagonal moves
     *
     * @return Returns moves calculated
     */
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

    /**
     * Checks if a specific position just did an enPassant
     * (pawn just moved to that position, and it was a double move)
     *
     * @param position position to check
     * @return returns boolean based on calculation
     */
    private boolean isEnPassant(Position position) {
        ArrayList<Position> moves = save.getAllMoves();
        globalModifier *= -1;
        if ((moves.size() > 0 && moves.get(moves.size() - 1).equals(position) && moves.get(moves.size() - 2).equals(new Position(position.getX()- (2 * globalModifier), position.getY())))
                && !isOutOfBounds(position) && !isNull(position) && board[position.getX()][position.getY()].getPieceType() == PAWN && board[position.getX()][position.getY()].amountOfMoves() == 1)
        {
            globalModifier *= -1;
            return true;
        }
        else
        {
            globalModifier *= -1;
            return false;
        }
    }

    /**
     * Checks if a given position is null on the board
     * @param position the position to check
     * @return returns boolean based on calculation
     */
    private boolean isNull(Position position) {
        //checks if a
        if (board[position.getX()][position.getY()] == null) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Checks if a piece is friendly to the piece at currentPosition
     *
     * @param position position that is checked
     * @return Returns boolean based on result
     */
    private boolean isFriendlyPiece(Position position) {
        //returns true if the color of piece at position is same as the color of piece in currentPosition
        //called with isNull
        if (board[currentPosition.getX()][currentPosition.getY()].getPieceColor() == board[position.getX()][position.getY()].getPieceColor())
            return true;
        else
            return false;

    }

    /**
     * Checks if a position would be out of bounds
     * Called before checking if something is on the board
     * @param position The position to check
     * @return Returns boolean based on result
     */
    private boolean isOutOfBounds(Position position) {
        //checks if a specific position is out of bounds
        //if x or y is bigger than 7 or smaller than 0
        if (position.getX() > 7 || position.getX() < 0 || position.getY() > 7 || position.getY() < 0)
            return true;
        else
            return false;

    }

    /**
     * Removes all moves that would result in a checkmate the following turn
     * @param moves Moves to check if they are illegal
     * @return Returns arraylist of moves that are legal
     */
    private ArrayList<Position> removeCheckmateMoves(ArrayList<Position> moves) {
        PieceColor pc = board[currentPosition.getX()][currentPosition.getY()].getPieceColor();
        Position actualPosition = new Position(currentPosition.getX(), currentPosition.getY());
        Position tempPosition;
        Piece tempPiece;
        boolean positionRemoved;
        globalModifier *= -1;
        int count;
        for (int moveLoop = 0; moveLoop < moves.size(); moveLoop++) { //loops through all moves
            positionRemoved = false;
            count = 0;
            tempPosition = moves.get(moveLoop);
            tempPiece = board[tempPosition.getX()][tempPosition.getY()]; // saves the piece info at that position
            board[tempPosition.getX()][tempPosition.getY()] = board[actualPosition.getX()][actualPosition.getY()]; // moves piece to move position
            board[actualPosition.getX()][actualPosition.getY()] = null;
            int pieceCount = (globalModifier == 1) ? blackPieceCount : whitePieceCount;
            for (int i = 0; i < board.length && count < pieceCount && !positionRemoved; i++) { // loops through all pieces on opposite team
                for (int j = 0; j < board[i].length && count < pieceCount && !positionRemoved; j++) {
                    if (board[i][j] != null && board[i][j].getPieceColor() != pc) {
                        count++;
                        currentPosition = new Position(i, j);
                        if (isKingAttackable(getMoves(board[i][j].getPieceType()))) { // gets moves for any piece found, calculates if a king is under attack by one of those moves
                            moves.remove(moveLoop);  //removes move if one is found
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

    /**
     * Checks if a king is under attack by one of the given moves
     *
     * @param moves Given moves to check
     * @return Boolean result based on calculations
     */
    private boolean isKingAttackable(ArrayList<Position> moves) {
        Position pos;
        for (int i = 0; i < moves.size(); i++) {
            pos = moves.get(i);
            if (board[pos.getX()][pos.getY()] != null && board[pos.getX()][pos.getY()].getPieceType() == KING)
                return true;
        }
        return false;
    }

    /**
     * Calculates what kind of status the game is in (checkmate or stalemate)
     * @param pc What color to check for
     * @return Returns integer value, 1 for stalemate, 2 for checkmate
     */
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

    /**
     * Calculates if a position is attackable by the opposite team
     * @param position The position to check
     * @return Returns boolean value based on result
     */
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

    /**
     * Toggles the globalModdifier
     */
    private void toggleGlobalModifier() {
        globalModifier *= -1;
    }

    /**
     * Removes all illegal castles that are remaining.
     */
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


    /**
     * Removes a position from a given arraylist of positions
     * @param moves ArrayList of positions
     * @param position Position to remove
     */
    private void removePosition(ArrayList<Position> moves, Position position) {
        for (int i = 0; i < moves.size(); i++) {
            if (moves.get(i).equals(position)) {
                moves.remove(i);
            }
        }


    }

    /**
     * Moves a piece one oldPosition to newPosition
     * @param oldPosition position to move from
     * @param newPosition position to move to
     * @param toggle determines if to toggle globalModifier (do not when playing against AI)
     */
    public void move(Position oldPosition, Position newPosition, boolean toggle)
    {
    	this.newPosition = newPosition;
    	if(board[oldPosition.getX()][oldPosition.getY()].getPieceType() == PAWN)
    	{

    		if(board[newPosition.getX()][newPosition.getX()] == null && newPosition.getY() != oldPosition.getY() && isEnPassant(new Position(newPosition.getX() - (1 * globalModifier), newPosition.getY())))
    		{
    			if(board[newPosition.getX() - (1 * globalModifier)][newPosition.getY()].getPieceColor() == BLACK) //remove piece count if doing en passant
    			{
    				blackPieceCount--;
    			}
    			else
    			{
    				whitePieceCount--;
    			}
    			board[newPosition.getX() - (1 * globalModifier)][newPosition.getY()] = null; // remove position if enpassant
            }
    		if(newPosition.getX() == 0 || newPosition.getX() == 7)
    		{
    			pawnPromotion = true; //Do pawnpromotion if pawn is on last row
    		}
    	}
    	else if(board[oldPosition.getX()][oldPosition.getY()].getPieceType() == KING && board[oldPosition.getX()][oldPosition.getY()].amountOfMoves() == 0)
    	{
    		//king side castle
    		if(newPosition.getY() == 6)
    		{
    			board[newPosition.getX()][5] = board[newPosition.getX()][7]; //moves rook
    			board[newPosition.getX()][7] = null;
    		}
    		else if(newPosition.getY() == 2) // queen side castle
    		{
    			board[newPosition.getX()][3] = board[newPosition.getX()][0]; // moves rook
    			board[newPosition.getX()][0] = null;
    		}
    	}
    	if(board[newPosition.getX()][newPosition.getY()] != null)
    	{
    		if(board[newPosition.getX()][newPosition.getY()].getPieceColor() == BLACK) // if piece is where newPosition is, decrease piececount
    		{
    			blackPieceCount--;
    		}
    		else
    		{
    			whitePieceCount--;
    		}
    	}
    	board[newPosition.getX()][newPosition.getY()] = board[oldPosition.getX()][oldPosition.getY()]; //does the move
    	board[oldPosition.getX()][oldPosition.getY()] = null;
    	board[newPosition.getX()][newPosition.getY()].moved();
    	board[newPosition.getX()][newPosition.getY()].setPosition(newPosition);
    	save.addMove(oldPosition, newPosition);
        if(toggle)
    	    toggleGlobalModifier();
    	
    
    }

    /**
     * Returnts current pawnPromotion status
     * @return pawnPromotion boolean
     */
    public boolean getPawnPromotion()
    {
    	return pawnPromotion;
    }

    /**
     * Sets a pieceType at currentPosition, used for pawnPromotion
     *
     * @param pt the piece type that will be set
     */
    public void setPiece(PieceType pt)
    {
    	
    	PieceColor pc = (globalModifier == 1) ? WHITE : BLACK;
		board[newPosition.getX()][newPosition.getY()] = new Piece(pt, pc, newPosition);
		pawnPromotion = false;
    	
    }

    /**
     * Counts the amount of pieces for both teams
     */
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

    public void flipBoard()
    {
        Piece temp;
        for(int i = 0; i < 2; i++)
        {
            for(int j = 0; j < board[i].length; j++)
            {
                temp = board[i][j];
                board[i][j] = board[board.length - i - 1][j];
                board[board.length - i - 1][j] = temp;
            }
        }
    }
}
