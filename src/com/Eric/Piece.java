package com.Eric;


import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.ArrayList;

public class Piece
{
    private PieceColor pieceColor;
    private PieceType pieceType;
    private Position position;
    private int moves = 0;

    public Piece(PieceType pieceType, PieceColor pieceColor, Position position)
    {
        this.pieceColor = pieceColor;
        this.pieceType = pieceType;
        this.position = position;

    }

    private Piece(PieceType pieceType, PieceColor pieceColor, Position position, int moves)
    {
        this.pieceColor = pieceColor;
        this.pieceType = pieceType;
        this.position = position;
        this.moves = moves;
    }

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
    
    protected PieceColor getPieceColor()
    {
    	return pieceColor;
    }
    protected PieceType getPieceType()
    {
        return pieceType;
    }
    protected int amountOfMoves()
    {
        return moves;
    }
    protected void setPosition(Position position){
        this.position = position;
    }
    public void moved()
    {
        moves++;
    }


    public Piece copy()
    {
        return new Piece(pieceType, pieceColor, position, moves);
    }
}
