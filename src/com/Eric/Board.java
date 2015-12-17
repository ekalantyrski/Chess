package com.Eric;


import java.awt.image.BufferedImage;
import java.util.ArrayList;
import static com.Eric.PieceType.*;
import static com.Eric.PieceColor.*;

public class Board
{

	private ArrayList<Element> elementArray;
	private ArrayList<Position> validMoves;
    private Piece[][] board;
	private BufferedImage possibleMovesImage;
	private Position currentPosition;
	private int globalModifier;
	private boolean flipBoard;
    private boolean whiteTurn;
	private int checkStatus;
	private Save save;
    public Board(boolean whiteStart, Save save)
    {
        board = new Piece[8][8];
        createPieces(whiteStart);
		possibleMovesImage = DAL.getPossibleMovesImage();
		globalModifier = (whiteStart) ? -1 : 1;
        whiteTurn = whiteStart;
		checkStatus = 0;
		this.save = save;
    }

    private void createPieces(boolean whiteStart)
    {
    	//creates all pieces assuming white starts first
    	//if white does not start first, the board is flipped.
        createPawns();
        createKings();
        createQueens();
        createRooks();
        createBishops();
        createKnights();
        if(!whiteStart)
        {
        	flipBoard();
        }
        
    }

	private void createPawns() {
            for (int i = 0; i < board.length; i++) {
                board[6][i] = new Piece(PAWN, WHITE, new Position(6, i), board);
            }

            for (int j = 0; j < board.length; j++) {
                board[1][j] = new Piece(PAWN, BLACK, new Position(1, j), board);
            }
	}
	
	private void createKings()
	{
			board[7][4] = new Piece(KING, WHITE, new Position(7, 4), board);
			board[0][4] = new Piece(KING, BLACK, new Position(0 ,4), board);
	}
	
	private void createQueens()
	{
			board[7][3] = new Piece(QUEEN, WHITE, new Position(7 ,3), board);
			board[0][3] = new Piece(QUEEN, BLACK, new Position(0, 3), board);
	}
	
	private void createBishops()
	{
			board[7][2] = new Piece(BISHOP, WHITE, new Position(7, 2), board);
			board[7][5] = new Piece(BISHOP, WHITE, new Position(7, 5), board);
			board[0][2] = new Piece(BISHOP, BLACK, new Position(0, 2), board);
			board[0][5] = new Piece(BISHOP, BLACK, new Position(0, 5), board);
	}
	
	private void createRooks()
	{
			board[7][0] = new Piece(ROOK, WHITE, new Position(7, 0), board);
			board[7][7] = new Piece(ROOK, WHITE, new Position(7, 7), board);
			board[0][0] = new Piece(ROOK, BLACK, new Position(0, 0), board);
			board[0][7] = new Piece(ROOK, BLACK, new Position(0, 7), board);
		
	}
	
	private void createKnights()
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
		globalModifier *= -1;

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
    	elementArray = new ArrayList<>();
    	for(int i = 0; i < board.length; i++)
    		for(int j = 0; j < board[i].length; j++)
    		{
    			if(board[i][j] != null)
    			{
    				elementArray.add(new Element(getImage(i, j), i, j));
    			}
    		}
    	
    }
    
    public ArrayList<Element> calculate(Position clickedPosition) {
            if (isValidMove(clickedPosition)) {
                if (isNull(clickedPosition)) {
					// en passant
                    if (board[currentPosition.getX()][currentPosition.getY()].getPieceType() == PAWN && currentPosition.getY() != clickedPosition.getY()) {
                        board[clickedPosition.getX() - (1  * globalModifier)][clickedPosition.getY()] = null;
						clickedPosition.setSpecialMove(1);
                    }
					else if(board[currentPosition.getX()][currentPosition.getY()].getPieceType() == KING && board[currentPosition.getX()][currentPosition.getY()].amountOfMoves() == 0)
					{
						//king side castle
						if(clickedPosition.getY() == 6)
						{
							board[currentPosition.getX()][5] = board[currentPosition.getX()][7];
							board[currentPosition.getX()][7] = null;
							clickedPosition.setSpecialMove(2);
						}
						//queen side castle
						else if(clickedPosition.getY() == 2)
						{
							board[currentPosition.getX()][3] = board[currentPosition.getX()][0];
							board[currentPosition.getX()][0] = null;
							clickedPosition.setSpecialMove(3);
						}
					}
                }
                move(clickedPosition);
                if(flipBoard)
                {
                    flipBoard();
                }
                validMoves = null;
                createElementArray();
                PieceColor color = (whiteTurn) ? WHITE : BLACK;
				checkStatus = checkCheck(color);
                whiteTurn ^= true;
                globalModifier *= -1;
                currentPosition = null;
			} else {
                if(isValidColor(clickedPosition, whiteTurn)) {
                    currentPosition = clickedPosition;
                    createElementArray();
                    if (board[clickedPosition.getX()][clickedPosition.getY()] != null) {
                        validMoves = getValidMoves(board[clickedPosition.getX()][clickedPosition.getY()].getPieceType());
						validMoves = removeCheckmateMoves(validMoves);
						//removes castling, if any position in castle is attackable
						//put here b/c it caused stack overflow
						if(board[currentPosition.getX()][currentPosition.getY()].getPieceType() == KING && board[currentPosition.getX()][currentPosition.getY()].amountOfMoves() == 0)
						{
							Position tempPosition = new Position(currentPosition.getX(), 5);
								if(isPositionAttackable(tempPosition))
								{
									removePosition(validMoves, new Position(currentPosition.getX(), 6));
								}
								tempPosition = new Position(currentPosition.getX(), 3);
								Position tempPosition2 = new Position(currentPosition.getX(), 1);
								if(isPositionAttackable(tempPosition) || isPositionAttackable(tempPosition2))
								{
									removePosition(validMoves, new Position(currentPosition.getX(), 2));
								}
						}
						// adds all valid moves to elementArray
                        for (int i = 0; i < validMoves.size(); i++) {
                            elementArray.add(new Element(possibleMovesImage, validMoves.get(i).getX(), validMoves.get(i).getY()));
                        }
						return elementArray;
                    }
                    validMoves = null;
                }
                else
                {
                    createElementArray();
                }
            }

        return elementArray;
    }
    //removes a position from array of moves
    private void removePosition(ArrayList<Position> moves, Position position)
    {
    	for(int i = 0; i < moves.size(); i++)
    	{
    		if(moves.get(i).equals(position))
    		{
    			moves.remove(i);
    		}
    	}
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
	//does all moving functionality
    private void move(Position newPosition)
    {
		board[newPosition.getX()][newPosition.getY()] = board[currentPosition.getX()][currentPosition.getY()];
		board[currentPosition.getX()][currentPosition.getY()] = null;
		board[newPosition.getX()][newPosition.getY()].moved();
		board[newPosition.getX()][newPosition.getY()].setPosition(newPosition);
		save.addMove(newPosition);
    }
    //gets valid moves
	private ArrayList<Position> getValidMoves(PieceType pieceType)
	{
		ArrayList<Position> validMoves = new ArrayList<>();
		switch(pieceType) {
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


	private ArrayList<Position> getPawnValidMoves()
	{
		Position tempPosition;
		ArrayList<Position> moves = new ArrayList<>();
		Piece currentPiece = board[currentPosition.getX()][currentPosition.getY()];
		//checks for moves going vertically
		if(currentPiece.amountOfMoves() == 0)
		{
			tempPosition = new Position(currentPosition.getX() + (1 * globalModifier), currentPosition.getY());
			if(!isOutOfBounds(tempPosition) && isNull(tempPosition))
			{
				moves.add(tempPosition);
				tempPosition = new Position(currentPosition.getX() + (2 * globalModifier), currentPosition.getY());
				if(!isOutOfBounds(tempPosition) && isNull(tempPosition))
				{
					moves.add(tempPosition);
				}
			}
		}
		else
		{
			tempPosition = new Position(currentPosition.getX() + (1 * globalModifier), currentPosition.getY());
			if(!isOutOfBounds(tempPosition) && isNull(tempPosition))
			{
				moves.add(tempPosition);
			}
		}
		int yModifier = 1;
		for(int i = 0; i < 2; i++) {
			tempPosition = new Position(currentPosition.getX() + (1 * globalModifier), currentPosition.getY() + (1 * yModifier * globalModifier));
			if (!isOutOfBounds(tempPosition) && !isNull(tempPosition)){
				if(!isFriendlyPiece(tempPosition)) {
					moves.add(tempPosition);
				}
			}
			else
			{
				tempPosition = new Position(currentPosition.getX(), currentPosition.getY() + (1 * yModifier * globalModifier));
				if(isEnPassant(tempPosition))
				{
					tempPosition = new Position(currentPosition.getX() + (1 * globalModifier), currentPosition.getY() + (1 * yModifier * globalModifier));
					moves.add(tempPosition);
				}
			}
			yModifier = -1;

		}
		return moves;
	}
	
	private ArrayList<Position> getKnightValidMoves()
	{
		ArrayList<Position> moves = new ArrayList<>();
		int modifier = 1;
		int yModifier = 1;
		Position tempPosition;
		//moves with first 2 squares on columns
		for(int i = 0; i < 2; i++)
		{
			yModifier = 1;
			for(int j = 0; j < 2; j++)
			{
				tempPosition = new Position(currentPosition.getX() + (2 * modifier * globalModifier), currentPosition.getY() + (1 * yModifier * globalModifier));
				if(!isOutOfBounds(tempPosition) && (isNull(tempPosition) || !isFriendlyPiece(tempPosition)))
				{
					moves.add(tempPosition);
				}
					yModifier = -1;
			}
			modifier = -1;

		}
		//moves with first 2 squares on row
		modifier = 1;
		for(int k = 0; k < 2; k++)
		{
			yModifier = 1;
			for(int l = 0; l < 2; l++)
			{
				tempPosition = new Position(currentPosition.getX() + (1 * modifier * globalModifier), currentPosition.getY() + (2 * yModifier * globalModifier));
				if(!isOutOfBounds(tempPosition) && (isNull(tempPosition) || !isFriendlyPiece(tempPosition)))
				{
					moves.add(tempPosition);
				}
				yModifier = -1;
			}
			modifier = -1;
		}
		return moves;
	}

	private ArrayList<Position> getQueenValidMoves()
	{
		ArrayList<Position> moves = new ArrayList<>();
		moves.addAll(getStraightMoves());
		moves.addAll(getDiagonalMoves());
		return moves;
	}

	private ArrayList<Position> getRookValidMoves()
	{
		return getStraightMoves();
	}

	private ArrayList<Position> getBishopValidMoves()
	{
		return getDiagonalMoves();
	}
	private ArrayList<Position> getKingValidMoves()
	{
		ArrayList<Position> moves = new ArrayList<>();
		int modifier = 1;
		Position tempPosition;
		for(int i = 0; i < 2; i++)
		{
			tempPosition = new Position(currentPosition.getX() + (modifier * globalModifier), currentPosition.getY());
			if(!isOutOfBounds(tempPosition) && (isNull(tempPosition) || !isFriendlyPiece(tempPosition)))
				moves.add(tempPosition);
			tempPosition = new Position(currentPosition.getX(), currentPosition.getY() + (modifier * globalModifier));
			if(!isOutOfBounds(tempPosition) && (isNull(tempPosition) || !isFriendlyPiece(tempPosition)))
				moves.add(tempPosition);
			tempPosition = new Position(currentPosition.getX() + (modifier * globalModifier), currentPosition.getY() + (modifier * globalModifier));
			if(!isOutOfBounds(tempPosition) && (isNull(tempPosition) || !isFriendlyPiece(tempPosition)))
				moves.add(tempPosition);
			modifier = -1;

		}

		tempPosition = new Position(currentPosition.getX() + (-1 * globalModifier), currentPosition.getY() + (1 * globalModifier));
		if(!isOutOfBounds(tempPosition) && (isNull(tempPosition) || !isFriendlyPiece(tempPosition)))
			moves.add(tempPosition);
		tempPosition = new Position(currentPosition.getX() + (1 * globalModifier), currentPosition.getY() + (-1 * globalModifier));
		if(!isOutOfBounds(tempPosition) && (isNull(tempPosition) || !isFriendlyPiece(tempPosition)))
			moves.add(tempPosition);
		//castling
		if(board[currentPosition.getX()][currentPosition.getY()].amountOfMoves() == 0)
		{
			//king side castle
			tempPosition = new Position(currentPosition.getX(), 7);
			if(!isNull(tempPosition) && board[tempPosition.getX()][tempPosition.getY()].amountOfMoves() == 0)
			{
				if(checkKingCastleable())
				{
					moves.add(new Position(currentPosition.getX(), currentPosition.getY() + 2));
				}
			}
			//queen side castle
			tempPosition = new Position(currentPosition.getX(), 0);
			if(!isNull(tempPosition) && board[tempPosition.getX()][tempPosition.getY()].amountOfMoves() == 0)
			{
				if(checkQueenCastleable())
				{
					moves.add(new Position(currentPosition.getX(), currentPosition.getY() - 2));
				}
			}
		}
		return moves;
	}
	
	private boolean checkKingCastleable()
	{
		Position tempPosition = null;
		for(int i = 1; i < 3; i++)
		{
			tempPosition = new Position(currentPosition.getX(), currentPosition.getY() + i);
			if(!isNull(tempPosition))
				return false;
		}
		return true;
	}
	
	public boolean checkQueenCastleable()
	{
		Position tempPosition = null;
		for(int i = 1; i < 4; i++)
		{
			tempPosition = new Position(currentPosition.getX(), currentPosition.getY() - i);
			if(!isNull(tempPosition))
			{
				return false;
			}
		}
		return true;
	}
	private ArrayList<Position> getStraightMoves()
	{
		ArrayList<Position> moves = new ArrayList<>();
		Position tempPosition;
		int modifier = 1;
		int count;
		//vertical moves
		for(int i = 0; i < 2; i++)
		{
			count = 1;
			tempPosition = new Position(currentPosition.getX() + (count * modifier), currentPosition.getY());
			while(!isOutOfBounds(tempPosition))
			{
				if(isNull(tempPosition))
				{
					moves.add(tempPosition);
				}
				else if(!isFriendlyPiece(tempPosition))
				{
					moves.add(tempPosition);
					break;
				}
				else
				{
					break;
				}
				count++;
				tempPosition = new Position(currentPosition.getX() + (count * modifier), currentPosition.getY());
			}
			modifier = -1;
		}
		modifier = 1;
		//get horizontal moves
		for(int j = 0; j < 2; j++)
		{
			count = 1;
			tempPosition = new Position(currentPosition.getX(), currentPosition.getY() + (count * modifier));
			while(!isOutOfBounds(tempPosition))
			{
				if (isNull(tempPosition))
				{
					moves.add(tempPosition);
				}
				else if (!isFriendlyPiece(tempPosition))
				{
					moves.add(tempPosition);
					break;
				}
				else
				{
					break;
				}
				count++;
				tempPosition = new Position(currentPosition.getX(), currentPosition.getY() + (count * modifier));
			}
			modifier = -1;

		}
		return moves;
	}

	private ArrayList<Position> getDiagonalMoves()
	{
		ArrayList<Position> moves = new ArrayList<>();
		int xModifier = 1;
		int yModifier = 1;
		Position tempPosition;
		for(int i = 0; i < 2; i++)
		{
			// top left to bottom right
			tempPosition = new Position(currentPosition.getX() + xModifier, currentPosition.getY() + yModifier);
			while(!isOutOfBounds(tempPosition))
			{
				if(isNull(tempPosition))
				{
					moves.add(tempPosition);
				}
				else if(!isFriendlyPiece(tempPosition))
				{
					moves.add(tempPosition);
					break;
				}
				else
				{
					break;
				}
				if(i == 0)
				{
					xModifier++;
					yModifier++;
				}
				else
				{
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
		for(int j = 0; j < 2; j++)
		{
			tempPosition = new Position(currentPosition.getX() + xModifier, currentPosition.getY() + yModifier);
			while(!isOutOfBounds(tempPosition))
			{
				if(isNull(tempPosition))
				{
					moves.add(tempPosition);
				}
				else if(!isFriendlyPiece(tempPosition))
				{
					moves.add(tempPosition);
					break;
				}
				else
				{
					break;
				}
				if(j == 0)
				{
					xModifier++;
					yModifier--;
				}
				else
				{
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
	private boolean isEnPassant(Position position)
	{
		if((save.getLastMove() != null && save.getLastMove().equals(position)) &&!isOutOfBounds(position) && !isNull(position) &&
				board[position.getX()][position.getY()].getPieceType() == PAWN && board[position.getX()][position.getY()].amountOfMoves() == 1)
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
		//checks if a
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

	private boolean isOutOfBounds(Position position)
	{
		//checks if a specific position is out of bounds
		//if x or y is bigger than 7 or smaller than 0
		if(position.getX() > 7 || position.getX() < 0 || position.getY() > 7 || position.getY() < 0)
			return true;
		else
			return false;

	}

    private boolean isValidColor(Position clickedPosition, boolean whiteTurn)
    {
        if(board[clickedPosition.getX()][clickedPosition.getY()] == null)
            return true;
        else if((board[clickedPosition.getX()][clickedPosition.getY()].getPieceColor() == WHITE) == whiteTurn)
            return true;
        else
            return false;

    }

    public void setFlipBoard(boolean flipBoard)
    {
        this.flipBoard = flipBoard;
    }

	private ArrayList<Position> removeCheckmateMoves(ArrayList<Position> moves)
	{
		PieceColor pc = board[currentPosition.getX()][currentPosition.getY()].getPieceColor();
		Position actualPosition = new Position(currentPosition.getX(), currentPosition.getY());
		Position tempPosition;
		Piece tempPiece;
		globalModifier *= -1;
		int count;
		for(int moveLoop = 0; moveLoop < moves.size(); moveLoop++)
		{
			count = 0;
			tempPosition  = moves.get(moveLoop);
			tempPiece = board[tempPosition.getX()][tempPosition.getY()];
			board[tempPosition.getX()][tempPosition.getY()] = board[actualPosition.getX()][actualPosition.getY()];
			board[actualPosition.getX()][actualPosition.getY()] = null;
			for(int i = 0; i < board.length && count < 16; i++)
			{
				for(int j = 0; j < board[i].length && count < 16;j++)
				{
					if(board[i][j] != null && board[i][j].getPieceColor() != pc)
					{
						count++;
						currentPosition = new Position(i, j);
						if(isKingAttackable(getValidMoves(board[i][j].getPieceType())))
						{
							moves.remove(moveLoop);
							moveLoop--;
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
	
	private boolean isKingAttackable(ArrayList<Position> moves)
	{
		Position pos;
		for(int i = 0; i < moves.size(); i++)
		{
			pos = moves.get(i);
			if(board[pos.getX()][pos.getY()] != null && board[pos.getX()][pos.getY()].getPieceType() == KING)
				return true;
		}
		return false;
	}
	
	private int checkCheck(PieceColor pc)
	{
		globalModifier *= -1;
		ArrayList<Position> validMoves;
		Position tempPosition = currentPosition;
		//gets all possible moves for other team
		validMoves = new ArrayList<>();
			for(int i = 0; i < board.length; i++)
			{
				for(int j = 0; j < board[i].length;j++)
				{
					if(board[i][j] != null && board[i][j].getPieceColor() != pc)
					{
						currentPosition = new Position(i, j);
						validMoves.addAll(removeCheckmateMoves(getValidMoves(board[i][j].getPieceType())));
					}
					
				}
			}
		globalModifier *= -1;
		// if amount of moves is 0, checks if the king can be attacked by current team
			if(validMoves.size() == 0)
			{
				validMoves = new ArrayList<>();
				for(int k = 0; k < board.length; k++)
				{
					for(int l = 0; l < board[k].length; l++)
					{
						if(board[k][l] != null && board[k][l].getPieceColor() == pc)
						{
							currentPosition = new Position(k, l);
							validMoves.addAll(removeCheckmateMoves(getValidMoves(board[k][l].getPieceType())));
						}
					}
				}
				if(isKingAttackable(validMoves))
					return 2; //2 for check mate
				else
				{
					// if its not a check mate, this checks for check
					// finds king, then sees if he can move anywhere
					globalModifier *= -1;
					boolean foundKing = false;
					for(int i = 0; i < board.length; i++)
					{
						for(int j = 0; j < board[i].length; j++)
						{
							if(board[i][j]!= null && board[i][j].getPieceType() == KING)
							{
								currentPosition = new Position(i, j);
								validMoves = getValidMoves(KING);
								validMoves = removeCheckmateMoves(validMoves);
								if(validMoves.size() == 0)
								{
									globalModifier *= 1;
									return 1;
								}
								foundKing = true;
								break;

							}
						}
						if(foundKing)
							break;
					}
				}

			}
		globalModifier *= 1;
		currentPosition = tempPosition;
		return 0;
	}

	private boolean isPositionAttackable(Position position)
	{
		globalModifier *= -1;
		PieceColor pc = board[currentPosition.getX()][currentPosition.getY()].getPieceColor();
		Position tempPosition = currentPosition;
		ArrayList<Position> moves = new ArrayList<>();
		//gets all moves of opposite side
		for(int i = 0; i < board.length; i++)
		{
			for(int j = 0; j < board[i].length; j++)
			{
				if(board[i][j] != null && board[i][j].getPieceColor() != pc)
				{
					currentPosition = new Position(i, j);
					moves = getValidMoves(board[i][j].getPieceType());
					for(int l = 0; l < moves.size(); l++)
					{
						if(moves.get(l).equals(position))
						{
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

	
	public int getCheckStatus()
	{
		return checkStatus;
	}
	
	public void setSave(Save save)
	{
		this.save = save;
		createElementArray();
	}
}


