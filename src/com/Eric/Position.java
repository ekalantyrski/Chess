package com.Eric;

public class Position {
    private int x;
    private int y;

    public Position(int x, int y)
    {
        this.x = x;
        this.y = y;
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
        if(getX() == position.getX() && getY() == position.getY())
            return true;
        else
            return false;
    }

    private char getColumnLetter(int column)
    {
        return (char)(97 + column);
    }

}
