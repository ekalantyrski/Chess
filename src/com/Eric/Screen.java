package com.Eric;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.Buffer;
import java.util.ArrayList;


public class Screen extends JComponent{

    final int squareSize = 86;
    final int leftInset = 10; // was 450
    final int topInset = 15;
    private int x;
    private int y;
    private boolean drawImage = false;
    BufferedImage image;
    private ArrayList<Element> elementArray;
    Position clickPosition;
    private boolean clicked = false;
    public Screen(){

    }
    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        paintBackground(g);
        paintBoard(g);
        if(elementArray != null)
        {
        	drawElementArray(g);
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
    private void drawImage(Graphics g, Image image, int x, int y)
    {
        g.drawImage(image, leftInset + (squareSize * y), topInset + (squareSize * x), null);
        drawImage = false;
    }
    private Position calculateSquareCoordinate(int mouseX, int mouseY)
    {
        int row = (mouseY - topInset) / squareSize;
        int column = (mouseX - leftInset) / squareSize;
        Position pos = new Position(row, column);
        return pos;
    }
    
    public boolean wasClicked()
    {
        System.out.println(clicked);
        if(clicked)
    	{
            return true;
    	}
    	else
    	{
            return false;
    	}
    }
    
    public Position getClickPosition()
    {
    	return clickPosition;
    }
    private void setClickPosition(Position clickPosition)
    {
        this.clickPosition = clickPosition;
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
        paintRectangle(g, leftInset - 1, topInset - 1, (squareSize * 8) + 1, (squareSize * 8) + 1, Color.black);
    }
    private Color getTileColor(int x, int y)
    {
        Color color1 = new Color(160, 82, 45);
        Color color2 = new Color(245, 222, 179);
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

    public void drawImage(BufferedImage image, int x, int y)
    {
    	drawImage = true;
    	this.x = x;
    	this.y = y;
        this.image = image;
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
    		drawImage(g, e.getImage(), e.getX(), e.getY());    	
    	}
    	
    }
    public void update()
    {
    	repaint();
    }
    

}
