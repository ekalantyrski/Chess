package com.Eric;


import java.applet.AudioClip;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import static com.Eric.PieceType.*;
import static com.Eric.PieceColor.*;
import static com.Eric.ActionType.*;

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
	private int gameType;
	private AudioClip moveSound;
	private DrawData drawData;
	private boolean moveCompleted;
	private Action actionMade;
	private boolean canMove;
	private boolean boardFlipped;
	private PieceColor boardColor;

	/**
	 * Constructor for class
	 * @param save A save object can be empty or already have moves recorded
	 * @param gameType used to determine gameType
     */
    public Board(Save save, int gameType, boolean whiteTurn, DrawData drawData)

    {

        board = new Piece[8][8]; //initialization of board and variables
        createPieces();
		possibleMovesImage = DAL.getPossibleMovesImage();
		moveSound = DAL.getMoveSound();
        this.whiteTurn = whiteTurn;
		checkStatus = 0;
		this.save = save;
        bh = new BoardHelper(board, whiteTurn, this.save, true, !whiteTurn);
		this.drawData = drawData;
		moveCompleted = false;
		canMove = false;
		actionMade = null;

		this.gameType = gameType;

		if(!whiteTurn)
		{
			bh.flipBoard();
			boardFlipped = true;
			boardColor = BLACK;
		}
		else{
			boardColor = WHITE;
		}
		if(save.getSaveSize() > 0) //logic for if a save game is loaded
		{

			if(gameType == 2)
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
                board[6][i] = new Piece(PAWN, WHITE);
            }

            for (int j = 0; j < board.length; j++) {
                board[1][j] = new Piece(PAWN, BLACK);
            }
	}

	/**
	 * Creates kings on board
	 */
	private void createKings()
	{
			board[7][4] = new Piece(KING, WHITE);
			board[0][4] = new Piece(KING, BLACK);
	}

	/**
	 * Creates kings on board
	 */
	private void createQueens()
	{
			board[7][3] = new Piece(QUEEN, WHITE);
			board[0][3] = new Piece(QUEEN, BLACK);
	}

	/**
	 * Creates bishops on board
	 */
	private void createBishops()
	{
			board[7][2] = new Piece(BISHOP, WHITE);
			board[7][5] = new Piece(BISHOP, WHITE);
			board[0][2] = new Piece(BISHOP, BLACK);
			board[0][5] = new Piece(BISHOP, BLACK);
	}

	/**
	 * Creates rooks on board
	 */
	private void createRooks()
	{
			board[7][0] = new Piece(ROOK, WHITE);
			board[7][7] = new Piece(ROOK, WHITE);
			board[0][0] = new Piece(ROOK, BLACK);
			board[0][7] = new Piece(ROOK, BLACK);
		
	}

	/**
	 * Creates knights on board
	 */
	private void createKnights()
	{
			board[7][1] = new Piece(KNIGHT, WHITE);
			board[7][6] = new Piece(KNIGHT, WHITE);
			board[0][1] = new Piece(KNIGHT, BLACK);
			board[0][6] = new Piece(KNIGHT, BLACK);
		
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
    	drawData.setElementArray(elementArray);
    }

	/**
	 * Calculates what should be done and then displayed on the screen, based
	 * on user input
	 * @param clickedPosition position that was clicked
	 * @return returns the elementArray to display
     */
    public ArrayList<Element> calculate(Position clickedPosition) {
            if (isValidMove(clickedPosition)) {
				if(gameType == 2)
				{
					bh.move(currentPosition, clickedPosition, false);
					createElementArray();
					canMove = false;
				}
				else if(gameType == 1)
				{
					bh.move(currentPosition, clickedPosition, true);
					whiteTurn ^= true;
				}
				else if(gameType == 3)
				{
					bh.move(currentPosition, clickedPosition, false);
					PieceColor pc = (whiteTurn) ? WHITE : BLACK;
					Position oldPosition;
					Position newPosition;
					if(boardFlipped)
					{
						oldPosition = currentPosition;
						newPosition = clickedPosition;
					}
					else {
						oldPosition = currentPosition;
						newPosition = clickedPosition;
					}
					//String moveInformation = oldPosition.toString() + " " + newPosition.toString();
					actionMade = new Action(pc, MOVE, oldPosition, newPosition);
					canMove = false;

				}

				moveSound.play();
                validMoves = null;
                currentPosition = null;
				createElementArray();
				moveCompleted = true;
			} else {
				moveCompleted = false;
                if(isValidColor(clickedPosition, whiteTurn) && canMove) {
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
		int amountOfMoves = save.getSaveSize();
		PieceColor pieceColor;
		System.out.println(amountOfMoves);
		if (amountOfMoves % 2 == 0)
		{
			if(boardColor == WHITE)
			{
				pieceColor = BLACK;
			}
			else
			{
				pieceColor = WHITE;
			}
		}
		else
		{
			if(boardColor == WHITE)
			{
				pieceColor = WHITE;
			}
			else
			{
				pieceColor = BLACK;
			}
		}
		if(whiteTurn)
		bh.setPiece(pieceType, pieceColor);
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
	public void checkStatus(PieceColor pieceColor)
	{
		checkStatus = bh.checkCheck(pieceColor, boardColor);
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
			bh.move(savedMoves.get(2 * i), savedMoves.get((2 * i) + 1), gameType != 2);
		}
		createElementArray();
	}

	/**
	 * Only used in network component
	 * @param action the action that was made, that needs to be recreated
     */
	public void movePiece(Action action)
	{
		Position oldPosition;
		Position newPosition;
		if(boardFlipped){
			oldPosition = new Position(action.getMovesInformation()[0]);
			newPosition = new Position(action.getMovesInformation()[1]);
			oldPosition = new Position(7 - oldPosition.getX(), 7 - oldPosition.getY());
			newPosition = new Position(7 - newPosition.getX(), 7 - newPosition.getY());
		}
		else{
			oldPosition = new Position(action.getMovesInformation()[0]);
			newPosition = new Position(action.getMovesInformation()[1]);
			oldPosition = new Position(7 - oldPosition.getX(),7 - oldPosition.getY());
			newPosition = new Position(7 - newPosition.getX(),7 - newPosition.getY());
		}
		bh.move(oldPosition, newPosition, false);
		canMove = true;
		createElementArray();

	}

	public Piece[][] getBoard()
	{
		return board;
	}

	public boolean isMoveCompleted()
	{
		return moveCompleted;
	}

	public void updateElementArray()
	{
		createElementArray();
	}

	public void setCanMove(boolean canMove)
	{
		this.canMove = canMove;
	}

	public Action getActionMade()
	{
		Action action = actionMade;
		actionMade = null;
		return action;
	}



}


