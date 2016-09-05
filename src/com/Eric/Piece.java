package com.Eric;


import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.ArrayList;

public class Piece
{
    private PieceColor pieceColor; // Color of piece
    private PieceType pieceType; // Type of piece
    private Position position; //Position of piece
    private int moves = 0; // How many moves 
    /**
     * Constructor
     * @param pieceType The pieceType
     * @param pieceColor The pieceColor
     */
    public Piece(PieceType pieceType, PieceColor pieceColor)
    {
        this.pieceColor = pieceColor;
        this.pieceType = pieceType;

    }
    /**
     * Constructor used for deep copy
     * @param pieceType The pieceType
     * @param pieceColor THe piece Color
     * @param moves	How many moves the piece did
     */
    private Piece(PieceType pieceType, PieceColor pieceColor, int moves)
    {
        this.pieceColor = pieceColor;
        this.pieceType = pieceType;
        this.moves = moves;
    }
    /**
     * Returns a string with the file path to the proper image
     * Depending on type and color
     * @param pieceType The pieceType
     * @param pieceColor The pieceColor
     * @return	Returns a string with the file path
     */
    public static String getImageFilePath(PieceType pieceType, PieceColor pieceColor)
    {
        StringBuilder sb = new StringBuilder("Data/");
        switch(pieceType)
        {
            case PAWN:
                sb.append("PAWN");
                break;
            case QUEEN:
                sb.append("QUEEN");
                break;
            case KING:
                sb.append("KING");
                break;
            case ROOK:
                sb.append("ROOK");
                break;
            case BISHOP:
                sb.append("BISHOP");
                break;
            case KNIGHT:
                sb.append("KNIGHT");
                break;
        }
        if(pieceColor == PieceColor.BLACK)
            sb.append("_BLACK.png");
        else
            sb.append("_WHITE.png");

        return sb.toString();
    }
    /**
     * Returns the pieceColor
     * @return PieceColor
     */
    protected PieceColor getPieceColor()
    {
    	return pieceColor;
    }
    /**
     * Returns the pieceType
     * @return Return pieceType
     */
    protected PieceType getPieceType()
    {
        return pieceType;
    }
    /**
     * Returns amount of moves
     * @return amount of moves
     */
    protected int amountOfMoves()
    {
        return moves;
    }
    /**
     * Sets the position to given position
     * @param position the position to change to
     */
    protected void setPosition(Position position){
        this.position = position;
    }
    /**
     * Increments moves valuye
     */
    public void moved()
    {
        moves++;
    }

    /**
     * Creates a deep copy of the piece
     * @return The instance of the deep copy
     */
    public Piece copy()
    {
        return new Piece(pieceType, pieceColor, moves);
    }
}
