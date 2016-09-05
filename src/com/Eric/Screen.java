package com.Eric;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;


public class Screen extends JComponent{

    final int squareSize = 86;
    final int leftInset = 1; // was 450
    final int topInset = 1;
    final int pawnPromotionLeftInset = leftInset + 165;
    final int pawnPromotionTopInset = topInset + 302;
    private boolean drawPawnPromotion = false;
    private boolean drawGameTypeScreen = false;
    private boolean drawPlayerColorScreen = false;
    private ArrayList<Element> elementArray; // array to be displayed
    private Color color1 = new Color(160, 82, 45);
    private Color color2 = new Color(245, 222, 179);
    private PieceColor pawnPromotionColor;
    private int boardStatus; // current board status
    private PieceColor gameWinner; // who won
    private DrawData drawData;
    public Screen(DrawData drawData){
        this.drawData = drawData;
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        if(drawGameTypeScreen)
        {
            drawGameTypeScreen(g);
            
        }
        else if(drawPlayerColorScreen)
        {
            drawPlayerColorScreen(g);
            System.out.println("hello");
        }
        else 
        {
           
            if (elementArray != null) {
            	 paintBackground(g);
                 paintBoard(g);
                drawElementArray(g);
                if(boardStatus > 0)
                {
                    drawBoardStatus(g);
                }
            }
            if (drawPawnPromotion) {
                drawPawnPromotion(g);
            }

        }

    }

    /**
     * Paints a rectangle and fills it
     * @param g Graphics object
     * @param x Where to start on x coordinate
     * @param y Where to start on y coordinate
     * @param width Width of rectangle
     * @param height Height of rectangle
     * @param color Color rectangle
     */
    private void paintRectangleAndFill(Graphics g, int x, int y, int width, int height, Color color)
    {
        g.setColor(color);
        g.fillRect(x, y, width, height);
    }

    /**
     * Paints a rectangle on screen
     * @param g Graphics object
     * @param x Where to start on x coordinate
     * @param y Where to start on y coordinate
     * @param width Width of rectangle
     * @param height Height of rectangle
     * @param color Color of rectangle
     */
    private void paintRectangle(Graphics g, int x, int y, int width, int height, Color color)
    {
        g.setColor(color);
        g.drawRect(x, y, width, height);
    }

    /**
     * Paints an image at given x and y coordinate (coordiante of board (8x8), not the screen)
     * @param g Graphics object
     * @param image Image to be painted
     * @param x X coordinate to be printed at
     * @param y Y coordinate to be printed at
     */
    private void paintImage(Graphics g, Image image, int x, int y)
    {
        g.drawImage(image, leftInset + (squareSize * y), topInset + (squareSize * x), null);
    }

    /**
     * Paints an image at given x and y coordiante(manual tells it to use screen coordinate, not board coodrinates)
     * @param g Graphics object
     * @param image Image to be painted
     * @param x X coordinate to paint at
     * @param y Y coordiante to paint at
     * @param manual to print at screen coordinate or board coordinate
     */
    private void paintImage(Graphics g, Image image, int x, int y, boolean manual)
    {
        if(manual)
            g.drawImage(image, x, y, null);
        else
            g.drawImage(image, leftInset + (squareSize * y), topInset + (squareSize * x), null);
    }

    /**
     * Paints a string at given area
     * @param g Graphics object
     * @param s What string should be printed
     * @param x X coordiante to be printed at
     * @param y Y coordinate to be printed at
     */
    private void paintString(Graphics g, String s, int x, int y)
    {
        g.setFont(new Font("Arial", Font.BOLD, 15));
        g.setColor(Color.black);
        g.drawString(s, x, y);
    }

    /**
     * Paints a string at given area with a given font
     * @param g Graphics object
     * @param s String to be printed
     * @param x X coordinate to be printed at
     * @param y Y coordinate to be printed at
     * @param font The font to be used
     */
    private void paintString(Graphics g, String s, int x, int y, Font font)
    {
        g.setFont(font);
        g.setColor(Color.black);
        g.drawString(s, x, y);
    }

    /**
     * Draws 8x8 chess board
     * @param g Graphics object
     */
    private void paintBoard(Graphics g)
    {
        Color color;
        //loops run 8 * 8 times to create all chess board tiles.
        for(int i = 0; i < 8; i++)
        {
            for(int j = 0; j < 8; j++)
            {
                color = getTileColor(i, j);
                paintRectangleAndFill(g, leftInset + (squareSize * j), topInset + (squareSize * i), squareSize, squareSize, color);
            }
        }
        paintRectangle(g, leftInset - 1, topInset - 1, (squareSize * 8) + 1, (squareSize * 8) + 1, Color.black); // black border
    }

    /**
     * Given an x and y coordinate, method returns what color that tile should be
     * @param x X coordinate of tile
     * @param y Y coordinate ot tile
     * @return The color that should be used
     */
    private Color getTileColor(int x, int y)
    {
        // depending on what tile is chosen, a specific color wll be returned
        Color color;
        if(x % 2 == 0)
            if(y % 2 == 0)
                color = color2;
            else
                color = color1;
        else
            if(y % 2 == 0)
                color = color1;
            else
                color = color2;

        return color;
    }

    /**
     * Paints the background white
     * @param g Graphics object
     */
    private void paintBackground(Graphics g)
    {
        // paints whole screen a certain color
        Color color = Color.white;
        paintRectangleAndFill(g, 0, 0, getWidth(), getHeight(), color);
    }

    /**
     * Sets the elementArray to one given, this is used to print things on board
     * @param elementArray The elementArray to be printed
     */
    public void setElementArray(ArrayList<Element> elementArray)
    {
    	this.elementArray = elementArray;
    }

    /**
     * Draws the element array on the board
     * @param g Graphics object
     */
    private void drawElementArray(Graphics g)
    {
        elementArray = drawData.getElementArray();
    	for(int i = 0; i < elementArray.size(); i++)
    	{
    		Element e = elementArray.get(i);
    		paintImage(g, e.getImage(), e.getX(), e.getY());
    	}
    	
    }

    /**
     * Calls repaint
     */
    public void update()
    {
    	this.repaint();
    }

    /**
     * Sets a variable to draw the pawn promotion screen
     * @param drawPawnPromotion Sets drawPawnPromotion to given value
     */
    public void setDrawPawnPromotion(boolean drawPawnPromotion)
    {
        this.drawPawnPromotion = drawPawnPromotion;
    }

    public void setDrawPlayerColorScreen(boolean drawPlayerColorScreen)
    {
        this.drawPlayerColorScreen = drawPlayerColorScreen;
    }

    /**
     * Sets a variable to draw the pawn promotion screen, and the color of the pieces to be used
     * @param drawPawnPromotion Sets drawPawmPromotion to given value
     * @param color Sets the color to be used
     */
    public void setDrawPawnPromotion(boolean drawPawnPromotion, PieceColor color)
    {
        this.drawPawnPromotion = drawPawnPromotion;
        this.pawnPromotionColor = color;
        update();
    }

    /**
     * Draws the pawn promotion screen
     * @param g Graphics object
     */
    private void drawPawnPromotion(Graphics g)
    {
        paintRectangleAndFill(g, pawnPromotionLeftInset, pawnPromotionTopInset, (squareSize * 4) + (5 * 5),20 + squareSize, color2);
        paintRectangle(g, pawnPromotionLeftInset - 1, pawnPromotionTopInset - 1, (squareSize * 4) + (5*5) + 1, 20 + squareSize + 1, Color.black);
        paintString(g, "Select piece to promote to.", pawnPromotionLeftInset + 105, pawnPromotionTopInset + 11);
        paintImage(g, DAL.getImage(PieceType.QUEEN, pawnPromotionColor), pawnPromotionLeftInset + 4, pawnPromotionTopInset + 12, true);
        paintImage(g, DAL.getImage(PieceType.KNIGHT, pawnPromotionColor), pawnPromotionLeftInset + 4 + squareSize + 5, pawnPromotionTopInset + 12, true);
        paintImage(g, DAL.getImage(PieceType.ROOK, pawnPromotionColor), pawnPromotionLeftInset + 4 +(squareSize * 2) + 10, pawnPromotionTopInset + 12, true);
        paintImage(g, DAL.getImage(PieceType.BISHOP, pawnPromotionColor), pawnPromotionLeftInset + 4 + (squareSize * 3) + 15, pawnPromotionTopInset + 12, true);

    }

    /**
     * Sets variable to create game type screen
     * @param value Sets drawGameTypeScreen to given value
     */
    public void createGameTypeScreen(boolean value)
    {
    	this.drawGameTypeScreen = value;
    }

    /**
     * Draws the gameType screen
     * @param g Graphics object
     */
    private void drawGameTypeScreen(Graphics g)
    {
    	paintRectangleAndFill(g, leftInset, topInset, (squareSize * 8), (squareSize * 8), color2); // fills whole screen
    	paintImage(g, DAL.getTitleImage(), 105, 86, true);
        paintString(g, "Choose the way you want to play!", 225, 250);
        paintRectangle(g, 105, 345, 215, 155, Color.BLACK); // vs Friend Square
        paintRectangle(g, 370, 345, 215, 155, Color.BLACK); // vs Computer Square
        paintRectangle(g, 235, 520, 215, 155, Color.BLACK); // Online square
        paintString(g, "Play vs a friend", 160, 425);
        paintString(g, "Play vs the computer", 410, 425);
        paintString(g, "Play Online", 302, 602);
    }

    /**
     * Sets boardStatus variable to value given
     * @param boardStatus The value to be changed to
     */
    public void setBoardStatus(int boardStatus)
    {
        this.boardStatus = boardStatus;
    }

    /**
     * Sets boardStatus variable to value given, and what color won
     * @param boardStatus The value to be changed to
     * @param pieceColor Color of winning team
     */
    public void setBoardStatus(int boardStatus, PieceColor pieceColor) {
        this.boardStatus = boardStatus;
        this.gameWinner = pieceColor;
        update();
    }

    /**
     * Draws the boardStatus screen
     * @param g Graphics object
     */
    private void drawBoardStatus(Graphics g)
    {
        paintRectangle(g, 104, 104, 481, 481, Color.BLACK); // creates the rectangles
        paintRectangleAndFill(g, 105, 105, 480, 480, color2);
        switch(boardStatus)
        {
            case 1:
                paintString(g, "Stalemate!", 280, 180, new Font("Arial", Font.BOLD, 25)); //game status
                break;
            case 2:
                paintString(g, "Checkmate!", 280, 180, new Font("Arial", Font.BOLD, 25)); // game status
                paintString(g, gameWinner.toString() + " Wins!", 270, 240, new Font("Arial", Font.BOLD, 25)); // who won
                break;
        }
        paintRectangle(g, 140, 350, 175, 175, Color.BLACK); // new game box
        paintRectangle(g, 370, 350, 175 ,175, Color.BLACK); // exit box
        paintString(g, "Play again?", 190, 440);
        paintString(g, "Exit Game", 425, 440);
    }

    private void drawPlayerColorScreen(Graphics g)
    {
        paintRectangleAndFill(g, leftInset, topInset, (squareSize * 8), (squareSize * 8), color2); // fills whole screen
        paintRectangle(g, 105, 345, 215, 155, Color.BLACK); // BLACK square
        paintRectangle(g, 370, 345, 215, 155, Color.BLACK); // WHITE square
        paintString(g, "BLACK", 177, 425, new Font("Arial", Font.BOLD, 20)); // BLACK button
        paintString(g, "WHITE", 440, 425, new Font("Arial", Font.BOLD, 20));



    }
}
