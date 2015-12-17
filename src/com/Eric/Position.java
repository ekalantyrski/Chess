package com.Eric;

public class Position {
    private int x;
    private int y;
    private int specialMove;

    public Position(int x, int y)
    {
        this.x = x;
        this.y = y;
        specialMove = 0;
    }
    public int getY()
    {
        return y;
    }
    public int getX()
    {
        return x;
    }
    public String getAlgebraRepresentation()
    {
        StringBuilder s = new StringBuilder();
        s.append(getColumnLetter(y));
        s.append(x + 1);
        return s.toString();
    }

    public boolean equals(Position position)
    {
        if(position == null)
            return false;
        else if(getX() == position.getX() && getY() == position.getY())
            return true;
        else
            return false;
    }

    private char getColumnLetter(int column)
    {
        return (char)(97 + column);
    }

    public void setSpecialMove(int move)
    {
        //0 = no special moves
        //1 = en passant
        //2 = king side castle
        //3 = queen side castle
        this.specialMove = move;
    }

    public int getSpecialMove()
    {
        return specialMove;
    }
}
