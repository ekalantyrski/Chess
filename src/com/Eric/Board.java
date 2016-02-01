package com.Eric;


import java.applet.AudioClip;
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
    private boolean whiteTurn;
	private int checkStatus;
	private Save save;
    private BoardHelper bh;
    private AI ai;
	private boolean usingAI;
	private AudioClip moveSound;

	/**
	 * Constructor for class
	 * @param save A save object can be empty or already have moves recorded
	 * @param usingAI used to determine if AI is to be used.
     */
    public Board(Save save, boolean usingAI)
    {

        board = new Piece[8][8]; //initialization of board and variables
        createPieces();
		possibleMovesImage = DAL.getPossibleMovesImage();
		moveSound = DAL.getMoveSound();
        whiteTurn = true;
		checkStatus = 0;
		this.save = save;
        bh = new BoardHelper(board, whiteTurn, this.save);
		this.usingAI = usingAI;
		if(usingAI)
		{
			createAI();
		}
		if(save.getSaveSize() > 0) //logic for if a save game is loaded
		{

			if(usingAI)
			{
				whiteTurn = true;
			}
			else
			{
				int size = this.save.getSaveSize();
				whiteTurn = (size % 2 == 0) ? true : false;
			}
			recreateBoard();
		}
    }

    private void createPieces()
    {
    	//creates all pieces assuming white starts first
    	//if white does not start first, the board is flipped.
        createPawns();
        createKings();
        createQueens();
        createRooks();
        createBishops();
        createKnights();
        
    }

	/**
	 * Creates pawns on board
	 */
	private void createPawns() {
            for (int i = 0; i < board.length; i++) {
                board[6][i] = new Piece(PAWN, WHITE, new Position(6, i));
            }

            for (int j = 0; j < board.length; j++) {
                board[1][j] = new Piece(PAWN, BLACK, new Position(1, j));
            }
	}

	/**
	 * Creates kings on board
	 */
	private void createKings()
	{
			board[7][4] = new Piece(KING, WHITE, new Position(7, 4));
			board[0][4] = new Piece(KING, BLACK, new Position(0 ,4));
	}

	/**
	 * Creates kings on board
	 */
	private void createQueens()
	{
			board[7][3] = new Piece(QUEEN, WHITE, new Position(7 ,3));
			board[0][3] = new Piece(QUEEN, BLACK, new Position(0, 3));
	}

	/**
	 * Creates bishops on board
	 */
	private void createBishops()
	{
			board[7][2] = new Piece(BISHOP, WHITE, new Position(7, 2));
			board[7][5] = new Piece(BISHOP, WHITE, new Position(7, 5));
			board[0][2] = new Piece(BISHOP, BLACK, new Position(0, 2));
			board[0][5] = new Piece(BISHOP, BLACK, new Position(0, 5));
	}

	/**
	 * Creates rooks on board
	 */
	private void createRooks()
	{
			board[7][0] = new Piece(ROOK, WHITE, new Position(7, 0));
			board[7][7] = new Piece(ROOK, WHITE, new Position(7, 7));
			board[0][0] = new Piece(ROOK, BLACK, new Position(0, 0));
			board[0][7] = new Piece(ROOK, BLACK, new Position(0, 7));
		
	}

	/**
	 * Creates knights on board
	 */
	private void createKnights()
	{
			board[7][1] = new Piece(KNIGHT, WHITE, new Position(7, 1));
			board[7][6] = new Piece(KNIGHT, WHITE, new Position(7, 6));
			board[0][1] = new Piece(KNIGHT, BLACK, new Position(0, 1));
			board[0][6] = new Piece(KNIGHT, BLACK, new Position(0, 6));
		
	}

	/**
	 * Creates a new elementArray based on the positions
	 * on the board
	 */
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

	/**
	 * Calculates what should be done and then displayed on the screen, based
	 * on user input
	 * @param clickedPosition position that was clicked
	 * @return returns the elementArray to display
     */
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
				moveSound.play();
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

	/**Description : returns the elementArray, that is used for displaying pieces and valid moves
	 * New one is made, if there is current elementArray is null
	 *
	 * @return the current element array
     */
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

	/**Description : Checks if the position a person clicked on, is their proper piece
	 *
	 * @param clickedPosition position that they clicked
	 * @param whiteTurn variable storing whose turn it's supposed to be
     * @return returns a boolean value
     */
    private boolean isValidColor(Position clickedPosition, boolean whiteTurn)
    {
        if(board[clickedPosition.getX()][clickedPosition.getY()] == null)
            return true;
        else if((board[clickedPosition.getX()][clickedPosition.getY()].getPieceColor() == WHITE) == whiteTurn)
            return true;
        else
            return false;

    }

	/**
	 * Returns check status
	 * @return current checkstatus
     */
	public int getCheckStatus()
	{
		return checkStatus;
	}

	/**
	 * Calls pawnPromotion from boardhelper
	 * @return returns result given
     */
	public boolean getPawnPromotion()
	{
		return bh.getPawnPromotion();
	}

	/**
	 * Called if pawnpromotion is true, gives piece that was chosen
	 * to promote to.
	 * @param pieceType the piece that was chosen for pawn promotion
     */
	public void setPiece(PieceType pieceType)
	{
		bh.setPiece(pieceType);
        createElementArray();

	}

	/**
	 * Checks if where the person clicked is a position that they can move to
	 * @param clickedPosition The position that was clicked
	 * @return boolean result based on calculations
     */
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

	/**
	 * Checks if there is a checkmate or stalemate, and records it.
	 */
	public void checkStatus()
	{
		PieceColor color = (whiteTurn) ? BLACK : WHITE;
		checkStatus = bh.checkCheck(color);
	}

	/**
	 * Creates AI object
	 */
	private void createAI()
	{
		this.ai = new AI(board, !whiteTurn, save);
	}

	/**
	 * Called if there is a save loaded
	 * Loops through moves that happened, and calls move method in boardhelper
	 */
	private void recreateBoard()
	{
		ArrayList<Position> savedMoves = save.getAllMoves();
		int size = savedMoves.size() / 2;
		for(int i = 0; i < size; i++)
		{
			bh.move(savedMoves.get(2 * i), savedMoves.get((2 * i) + 1), !usingAI);
		}
		createElementArray();
	}
}


