package com.Eric;

import java.awt.image.BufferedImage;

public class Element {

	private BufferedImage image; // image to be displayed
	private int x; // x coordinate to be displayed at
	private int y; // y coordinate to be displayed at
	
	public Element(BufferedImage image, int x, int y)
	{
		this.image = image;
		this.x = x;
		this.y = y;
	}

	/**
	 * Returns the image
	 * @return Returns image
     */
	public BufferedImage getImage()
	{
		return image;
	}

	/**
	 * Returns x coordinate
	 * @return returns x
     */
	public int getX()
	{
		return x;
	}

	/**
	 * Returns y coordinate
	 * @return returns y
     */
	public int getY()
	{
		return y;
	}
	
}
