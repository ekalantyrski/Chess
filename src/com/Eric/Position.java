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
    public Position(String move)
    {
        if(isPawnMove(move))
        {
            this.x = (int)move.charAt(1) - 65;
            this.y = (int)move.charAt(0) - 65;
        }
        else
        {
            this.x = (int)move.charAt(2) - 65;
            this.y = (int)move.charAt(1) - 65;
        }
    }
    public int getY()
    {
        return y;
    }
    public int getX()
    {
        return x;
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

    private boolean isPawnMove(String move)
    {
        char x = move.charAt(0);
        if(x == 'a' || x == 'b' || x == 'c' || x == 'd' || x == 'e' || x == 'f' || x == 'g' || x == 'h')
            return true;
        else
            return false;
    }

    public int getSpecialMove()
    {
        return specialMove;
    }
}
