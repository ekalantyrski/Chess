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
	private boolean flipBoard;
    private boolean whiteTurn;
	private int checkStatus;
	private Save save;
    private BoardHelper bh;
    private AI ai;
	private boolean usingAI;

    public Board(boolean whiteStart, Save save, boolean usingAI)
    {
        board = new Piece[8][8];
        createPieces(whiteStart);
		possibleMovesImage = DAL.getPossibleMovesImage();
        whiteTurn = whiteStart;
		checkStatus = 0;
		this.save = save;
        bh = new BoardHelper(board, whiteStart, this.save);
		this.usingAI = usingAI;
		if(usingAI)
		{
			createAI();
		}
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
        	//flipBoard();
        }
        
    }

	private void createPawns() {
            for (int i = 0; i < board.length; i++) {
                board[6][i] = new Piece(PAWN, WHITE, new Position(6, i));
            }

            for (int j = 0; j < board.length; j++) {
                board[1][j] = new Piece(PAWN, BLACK, new Position(1, j));
            }
	}
	
	private void createKings()
	{
			board[7][4] = new Piece(KING, WHITE, new Position(7, 4));
			board[0][4] = new Piece(KING, BLACK, new Position(0 ,4));
	}
	
	private void createQueens()
	{
			board[7][3] = new Piece(QUEEN, WHITE, new Position(7 ,3));
			board[0][3] = new Piece(QUEEN, BLACK, new Position(0, 3));
	}
	
	private void createBishops()
	{
			board[7][2] = new Piece(BISHOP, WHITE, new Position(7, 2));
			board[7][5] = new Piece(BISHOP, WHITE, new Position(7, 5));
			board[0][2] = new Piece(BISHOP, BLACK, new Position(0, 2));
			board[0][5] = new Piece(BISHOP, BLACK, new Position(0, 5));
	}
	
	private void createRooks()
	{
			board[7][0] = new Piece(ROOK, WHITE, new Position(7, 0));
			board[7][7] = new Piece(ROOK, WHITE, new Position(7, 7));
			board[0][0] = new Piece(ROOK, BLACK, new Position(0, 0));
			board[0][7] = new Piece(ROOK, BLACK, new Position(0, 7));
		
	}
	
	private void createKnights()
	{
			board[7][1] = new Piece(KNIGHT, WHITE, new Position(7, 1));
			board[7][6] = new Piece(KNIGHT, WHITE, new Position(7, 6));
			board[0][1] = new Piece(KNIGHT, BLACK, new Position(0, 1));
			board[0][6] = new Piece(KNIGHT, BLACK, new Position(0, 6));
		
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
    				elementArray.add(new Element(DAL.getImage(board[i][j].getPieceType(), board[i][j].getPieceColor()), i, j));
    			}
    		}
    	
    }
    
    public ArrayList<Element> calculate(Position clickedPosition) {
            if (isValidMove(clickedPosition)) {
				if(usingAI)
				{
					bh.move(currentPosition, clickedPosition, false);
					ai.move();
				}
				else
				{
					bh.move(currentPosition, clickedPosition, true);
					whiteTurn ^= true;
				}
				checkStatus();
                validMoves = null;
                currentPosition = null;
				createElementArray();
			} else {
                if(isValidColor(clickedPosition, whiteTurn)) {
                    currentPosition = clickedPosition;
                    createElementArray();
                    if (board[clickedPosition.getX()][clickedPosition.getY()] != null) {
                        validMoves = bh.getValidMoves(currentPosition);
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


	
	public int getCheckStatus()
	{
		return checkStatus;
	}

	public boolean getPawnPromotion()
	{
		return bh.getPawnPromotion();
	}
	//puts a piece on a specific part of the board
	//only happens if pawnPromotion is true
	public void setPiece(PieceType pieceType)
	{
		bh.setPiece(pieceType);
        createElementArray();

	}


    private boolean isValidMove(Position clickedPosition) {
		if (validMoves == null) {
			return false;
		} else {
			for (int i = 0; i < validMoves.size(); i++) {
				if (validMoves.get(i).equals(clickedPosition))
					return true;
			}
			return false;
		}

	}

	public void checkStatus()
	{
		PieceColor color = (whiteTurn) ? BLACK : WHITE;
		checkStatus = bh.checkCheck(color);
	}

	private void createAI()
	{
		this.ai = new AI(board, !whiteTurn, save);
	}
}


