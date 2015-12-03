package com.Eric;


import javax.swing.*;
import java.awt.image.BufferedImage;
import java.lang.reflect.Array;
import java.util.ArrayList;
import static com.Eric.PieceType.*;
import static com.Eric.PieceColor.*;

public class Board{

	private ArrayList<Element> elementArray;
	private ArrayList<Position> validMoves;
    private Piece[][] board;
	private BufferedImage possibleMovesImage;
	private Position currentPosition;
    public Board(boolean whiteStart)
    {
        board = new Piece[8][8];
        createPieces(whiteStart);
		possibleMovesImage = DAL.getPossibleMovesImage();
    }

    private void createPieces(boolean whiteStart)
    {
    	//creates all pieces assuming white starts first
    	//if white does not start first, the board is flipped.
        createPawns(whiteStart);
        createKings(whiteStart);
        createQueens(whiteStart);
        createRooks(whiteStart);
        createBishops(whiteStart);
        createKnights(whiteStart);
        if(!whiteStart)
        {
        	flipBoard();
        }
        
    }

	private void createPawns(boolean whiteStart) {
            for (int i = 0; i < board.length; i++) {
                board[6][i] = new Piece(PAWN, WHITE, new Position(6, i), board);
            }

            for (int j = 0; j < board.length; j++) {
                board[1][j] = new Piece(PAWN, BLACK, new Position(1, j), board);
            }
	}
	
	private void createKings(boolean whiteStart)
	{
			board[7][4] = new Piece(KING, WHITE, new Position(7, 4), board);
			board[0][3] = new Piece(KING, BLACK, new Position(0 ,3), board);
	}
	
	private void createQueens(boolean whiteStart)
	{
			board[7][3] = new Piece(QUEEN, WHITE, new Position(7 ,3), board);
			board[0][4] = new Piece(QUEEN, BLACK, new Position(0, 4), board);
	}
	
	private void createBishops(boolean whiteStart)
	{
			board[7][2] = new Piece(BISHOP, WHITE, new Position(7, 2), board);
			board[7][5] = new Piece(BISHOP, WHITE, new Position(7, 5), board);
			board[0][2] = new Piece(BISHOP, BLACK, new Position(0, 2), board);
			board[0][5] = new Piece(BISHOP, BLACK, new Position(0, 5), board);
	}
	
	private void createRooks(boolean whiteStart)
	{
			board[7][0] = new Piece(ROOK, WHITE, new Position(7, 0), board);
			board[7][7] = new Piece(ROOK, WHITE, new Position(7, 7), board);
			board[0][0] = new Piece(ROOK, BLACK, new Position(0, 0), board);
			board[0][7] = new Piece(ROOK, BLACK, new Position(0, 7), board);
		
	}
	
	private void createKnights(boolean whiteStart)
	{
			board[7][1] = new Piece(KNIGHT, WHITE, new Position(7, 1), board);
			board[7][6] = new Piece(KNIGHT, WHITE, new Position(7, 6), board);
			board[0][1] = new Piece(KNIGHT, BLACK, new Position(0, 1), board);
			board[0][6] = new Piece(KNIGHT, BLACK, new Position(0, 6), board);
		
	}
	private void flipBoard()
	{
		Piece temp;
		for(int i = 0; i < board.length / 2; i++)
		{
			for(int j = 0; j < board[i].length; j++)
			{
				temp = board[i][j];
				board[i][j] = board[board[i].length - i - 1][j];
				board[board[i].length - i - 1][j] = temp;
			}
		}
	}
	//returns image of piece at x and y
    public BufferedImage getImage(int x, int y)
    {
        return board[x][y].getImage();
    }

	//creates a new element array
	//iterates through board array finding all pieces
	//if one is found,
	//makes a new element with corresponding picture to the piece, x and y.
    private void createElementArray()
    {
    	elementArray = new ArrayList<Element>();
    	for(int i = 0; i < board.length; i++)
    		for(int j = 0; j < board[i].length; j++)
    		{
    			if(board[i][j] != null)
    			{
    				elementArray.add(new Element(getImage(i, j), i, j));
    			}
    		}
    	
    }
    
    public ArrayList<Element> calculate(Position clickedPosition)
    {
    	if(isValidMove(clickedPosition))
		{
            if(isNull(clickedPosition))
            {
				if(board[currentPosition.getX()][currentPosition.getY()].getPieceType() == PAWN && currentPosition.getY() != clickedPosition.getY())
				{
					int modifier  = board[currentPosition.getX()][currentPosition.getY()].getModifier();
					board[clickedPosition.getX() - (1 * modifier)][clickedPosition.getY()] = null;
				}
            }
			move(clickedPosition);
			currentPosition = null;
			validMoves = null;
			createElementArray();
		}
		else {
			currentPosition = clickedPosition;
			createElementArray();
			if (board[clickedPosition.getX()][clickedPosition.getY()] != null) {
				validMoves = getValidMoves(board[clickedPosition.getX()][clickedPosition.getY()].getPieceType());
				for (int i = 0; i < validMoves.size(); i++) {
					elementArray.add(new Element(possibleMovesImage, validMoves.get(i).getX(), validMoves.get(i).getY()));
				}
				System.out.println(elementArray.size());
				return elementArray;
			}
			validMoves = null;
		}

			return elementArray;
    }


	//returns an elementArray
	//if null, makes a new one.
    public ArrayList<Element> getElementArray()
    {
    	if(elementArray == null)
    	{
    		createElementArray();
    		return elementArray;
    	}
    	else
    	{
    		return elementArray;
    	}
    }
    private void move(Position newPosition)
    {
		board[newPosition.getX()][newPosition.getY()] = board[currentPosition.getX()][currentPosition.getY()];
		board[currentPosition.getX()][currentPosition.getY()] = null;
		board[newPosition.getX()][newPosition.getY()].moved();
		board[newPosition.getX()][newPosition.getY()].setPosition(newPosition);
    }

	private ArrayList<Position> getValidMoves(PieceType pieceType)
	{
		ArrayList<Position> validMoves = new ArrayList<>();
		switch(pieceType) {
			case BISHOP:
				break;
			case KING:
				break;
			case KNIGHT:
				validMoves = getKnightValidMoves();
				break;
			case PAWN:
				int modifier = board[currentPosition.getX()][currentPosition.getY()].getModifier();
				validMoves = getPawnValidMoves(modifier);
				break;
			case QUEEN:
				break;
			case ROOK:

				break;

		}
		return validMoves;
	}


	private ArrayList<Position> getPawnValidMoves(int modifier)
	{
		Position positionToDelete;
		Position tempPosition;
		ArrayList<Position> moves = new ArrayList<>();
		Piece currentPiece = board[currentPosition.getX()][currentPosition.getY()];
		//checks for moves going vertically
		if(currentPiece.amountOfMoves() == 0)
		{
			tempPosition = new Position(currentPosition.getX() + (1 * modifier), currentPosition.getY());
			if(isNull(tempPosition))
			{
				moves.add(tempPosition);
				tempPosition = new Position(currentPosition.getX() + (2 * modifier), currentPosition.getY());
				if(isNull(tempPosition))
				{
					moves.add(tempPosition);
				}
			}
		}
		else
		{
			tempPosition = new Position(currentPosition.getX() + (1 * modifier), currentPosition.getY());
			if(isNull(tempPosition))
			{
				moves.add(tempPosition);
			}
		}
		int yModifier = 1;
		for(int i = 0; i < 2; i++)
		{
			tempPosition = new Position(currentPosition.getX() + (1 * modifier), currentPosition.getY() + (1 * yModifier));
			if(!isNull(tempPosition) && !isFriendlyPiece(tempPosition))
			{
				moves.add(tempPosition);
			}

			yModifier = -1;
		}
		return moves;
	}
	
	private ArrayList<Position> getKnightValidMoves()
	{
//		for(int i = 0; i < moves.size(); i++)
//		{
//			if(!isNull(moves.get(i)) && isFriendlyPiece(moves.get(i)))
//			{
//				removePositionFromList(moves, moves.get(i));
//				i--;
//			}
//		}
		return null;
	}

	private ArrayList<Position> getQueenValidMoves(ArrayList<Position> moves)
	{
		return moves;
	}

	private ArrayList<Position> removeStraightMoves(ArrayList<Position> moves)
	{
		//vertical
		int modifier = 1;
		Position tempPosition;
		for(int i = 0; i < moves.size(); i++)
		{


		}
		return null;
	}

	private ArrayList<Position> removePositionFromList(ArrayList<Position> moves, Position position)
	{
		for(int i = 0; i < moves.size(); i++)
		{
			if(moves.get(i).equals(position))
			{
				moves.remove(i);
				break;
			}
		}
		return moves;
		
	}
	private boolean isEnPassant(Position position)
	{
		if(!isNull(position) && board[position.getX()][position.getY()].getPieceType() == PAWN && Save.getLastMove().equals(position) && board[position.getX()][position.getY()].amountOfMoves() == 1)
			return true;
		else
			return false;
	}

	private boolean isValidMove(Position clickedPosition)
	{
		if(validMoves == null) {
			return false;
		}
		else
		{
			for(int i = 0; i < validMoves.size(); i++)
			{
				if(validMoves.get(i).equals(clickedPosition))
					return true;
			}
			return false;
		}

	}

	private boolean isNull(Position position) {
		if (board[position.getX()][position.getY()] == null) {
			return true;
		}
		else
		{
			return false;
		}
	}

	private boolean isFriendlyPiece(Position position)
	{
        //returns true if the color of piece at position is same as the color of piece in currentPosition
        //called with isNull
        if(board[currentPosition.getX()][currentPosition.getY()].getPieceColor() == board[position.getX()][position.getY()].getPieceColor())
            return true;
        else
            return false;

	}
	
}


