package com.Eric;

public class Position {
    private int x; // x coordinate
    private int y; // y coordinate
    /**
     * Constructor
     * @param x x coordinate
     * @param y y coordinate
     */
    public Position(int x, int y)
    {
        this.x = x;
        this.y = y;
    }
    /**
     * Constructor to create piece from String value
     * @param move The value given
     */
    public Position(String move)
    {
        this.x = ((int)move.charAt(1)) - 48;
        this.y = ((int)move.charAt(0)) - 97;
    }
    /**
     * Returns y value
     * @return y
     */
    public int getY()
    {
        return y;
    }
    /**
     * Returns x value
     * @return x
     */
    public int getX()
    {
        return x;
    }
    /**
     * Checks if a position equals another position
     * @param position The position to check
     * @return Returns boolean value based on calculations
     */
    public boolean equals(Position position)
    {
        if(position == null)
            return false;
        else if(getX() == position.getX() && getY() == position.getY())
            return true;
        else
            return false;
    }
    /**
     * Gets the char of the column letter for a given column
     * @param column The column given
     * @return The char letter attained
     */
    private char getColumnLetter(int column)
    {
        return (char)(97 + column);
    }
    /**
     * Creates a string from the x and y coordinate
     * @return Return the string
     */
    public String toString()
    {
        return getColumnLetter(y) + "" + x;
    }

    public boolean isCenterSquare()
    {
        if(y == 2)
        {
            if(x == 3 || x == 4)
                return true;
        }
        else if(y == 3)
        {
            if(x == 2 || x == 3 || x == 4 || x == 5)
                return true;
        }
        else if(y == 4)
        {
            if(x == 2 || x == 3 || x == 4 || x == 5)
                return true;
        }
        else if(y == 5)
        {
            if(x == 3 || x == 4)
                return true;
        }
        return false;
    }
}
