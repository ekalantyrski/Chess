package com.Eric;


import java.awt.image.BufferedImage;
import java.util.ArrayList;
import static com.Eric.PieceColor.BLACK;

public class Piece
{
    private BufferedImage image;
    private ArrayList<Position> validMoves;
    private PieceColor pieceColor;
    private PieceType pieceType;
    protected Piece[][] board;
    private Position position;
    private int moves = 0;

    public Piece(PieceType pieceType, PieceColor pieceColor, Position position, Piece[][] board)
    {
        this.pieceColor = pieceColor;
        this.pieceType = pieceType;
        this.image = DAL.getImage(pieceType, pieceColor);
        this.board = board;
        this.position = position;

    }

    public BufferedImage getImage()
    {
        return image;
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
    protected Position getPosition()
    {
    	return position;
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
    protected int getModifier()
    {
    	if(pieceColor == BLACK)
            return 1;
        else
            return -1;
    	
    }

    public void moved()
    {
        moves++;
    }
    
    protected boolean isOutOfBounds(Position position)
    {
    	if(position.getX() > 7 || position.getX() < 0 || position.getY() > 7 || position.getY() < 0)
    		return true;
    	else
    		return false;
    	
    }
}
