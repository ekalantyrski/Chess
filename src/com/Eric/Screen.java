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
    private ArrayList<Element> elementArray;
    private Color color1 = new Color(160, 82, 45);
    private Color color2 = new Color(245, 222, 179);
    private PieceColor pawnPromotionColor;
    public Screen(){
    
    }
    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        if(drawGameTypeScreen)
        {
            drawGameTypeScreen(g);
            
        }
        else 
        {
           
            if (elementArray != null) {
            	 paintBackground(g);
                 paintBoard(g);
                drawElementArray(g);
            }
            if (drawPawnPromotion) {
                drawPawnPromotion(g);
            }
        }
    }

    private void paintRectangleAndFill(Graphics g, int x, int y, int width, int height, Color color)
    {
        g.setColor(color);
        g.fillRect(x, y, width, height);
    }

    private void paintRectangle(Graphics g, int x, int y, int width, int height, Color color)
    {
        g.setColor(color);
        g.drawRect(x, y, width, height);
    }
    private void paintImage(Graphics g, Image image, int x, int y)
    {
        g.drawImage(image, leftInset + (squareSize * y), topInset + (squareSize * x), null);
    }
    private void paintImage(Graphics g, Image image, int x, int y, boolean manual)
    {
        if(manual)
            g.drawImage(image, x, y, null);
        else
            g.drawImage(image, leftInset + (squareSize * y), topInset + (squareSize * x), null);
    }
    private void paintString(Graphics g, String s, int x, int y)
    {
        g.setFont(new Font("Arial", Font.BOLD, 15));
        g.setColor(Color.black);
        g.drawString(s, x, y);
    }

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

    private void paintBackground(Graphics g)
    {
        // paints whole screen a certain color
        Color color = Color.white;
        paintRectangleAndFill(g, 0, 0, getWidth(), getHeight(), color);
    }
    public void setElementArray(ArrayList<Element> elementArray)
    {
    	this.elementArray = elementArray;
    }
    private void drawElementArray(Graphics g)
    {
    	for(int i = 0; i < elementArray.size(); i++)
    	{
    		Element e = elementArray.get(i);
    		paintImage(g, e.getImage(), e.getX(), e.getY());
    	}
    	
    }
    public void update()
    {
    	repaint();
    }
    public void setDrawPawnPromotion(boolean drawPawnPromotion)
    {
        this.drawPawnPromotion = drawPawnPromotion;
    }

    public void setDrawPawnPromotion(boolean drawPawnPromotion, PieceColor color)
    {
        this.drawPawnPromotion = drawPawnPromotion;
        this.pawnPromotionColor = color;
        update();
    }
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
    
    public void createGameTypeScreen(boolean value)
    {
    	this.drawGameTypeScreen = value;
    }
    
    private void drawGameTypeScreen(Graphics g)
    {
    	paintRectangleAndFill(g, leftInset, topInset, (squareSize * 8), (squareSize * 8), color2); // fills whole screen
    	paintImage(g, DAL.getTitleImage(), 105, 86, true);
        paintString(g, "Choose the way you want to play!", 225, 250);
        paintRectangle(g, 105, 345, 215, 155, Color.BLACK); // vs Friend Square
        paintRectangle(g, 370, 345, 215, 155, Color.BLACK); // vs Computer Square
        paintString(g, "Play vs a friend", 160, 425);
        paintString(g, "Play vs the computer", 410, 425);
    }
    
    

}
