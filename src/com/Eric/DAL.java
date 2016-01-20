package com.Eric;

import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

import com.sun.media.jfxmedia.Media;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;

public class DAL {
    static BufferedImage[] icons;
    static BufferedImage possibleMovesImage;
    static JFileChooser fc = new JFileChooser();
    static BufferedImage titleImage;
    static AudioClip moveSound;

    public DAL()
    {
        load();
    }
    public void load()
    {
        //loads black Icons
        PieceType[] array = PieceType.values();
        icons = new BufferedImage[array.length * 2];
        //gets black
        for(int i = 0; i < array.length; i++)
        {
            try {
                File file = new File(Piece.getImageFilePath(array[i], PieceColor.BLACK));
                icons[i] = ImageIO.read(file);
                //icons[i] = ImageIO.read(getClass().getResourceAsStream(Piece.getImageFilePath(array[i], PieceColor.BLACK)));
            }catch (IOException e)
            {
                e.printStackTrace();
            }

        }
        for(int i = 0; i < array.length; i++)
        {
            try {
                File file = new File(Piece.getImageFilePath(array[i], PieceColor.WHITE));
                icons[array.length + i] = ImageIO.read(file);
                //icons[array.length + i] = ImageIO.read(getClass().getResource(Piece.getImageFilePath(array[i], PieceColor.WHITE)));
            }catch (IOException e)
            {
                e.printStackTrace();
            }

        }
        try
        {
            File file = new File("Data/CIRCLE_RED.png");
            possibleMovesImage = ImageIO.read(file);
            //possibleMovesImage = ImageIO.read(getClass().getResource("Data/CIRCLE_RED.png"));
        }catch(IOException e)
        {
            e.printStackTrace();
        }
        try
        {
        	File file = new File("Data/Title.png");
            titleImage = ImageIO.read(file);
        	//titleImage = ImageIO.read(getClass().getResource("Data/Title.png"));
        }catch(IOException ioe)
        {
        	ioe.printStackTrace();
        }
       
    }
    
    public static ArrayList<Position> getMoves()
    {
    	int val = fc.showOpenDialog(new JFrame());
    	if(val == fc.APPROVE_OPTION)
    	{
    		File file = fc.getSelectedFile();
            ArrayList<Position> moves = new ArrayList<>();
            String[] turn;
            String tmp;
            try
            {
                BufferedReader br = new BufferedReader(new FileReader(file));
                tmp = br.readLine();
                while(tmp != null)
                {
                    turn = tmp.split(" ");
                    //moves.add()

                }
            }catch(IOException ioe)
            {
                ioe.printStackTrace();
            }

            return moves;
    	}
    	else
    	{
    		return null;
    	}
    }

    public static BufferedImage getPossibleMovesImage()
    {
        return possibleMovesImage;
    }
    public static BufferedImage getImage(PieceType pieceType, PieceColor pieceColor)
    {
        int num = pieceType.ordinal() + (PieceType.values().length * pieceColor.ordinal());
        return icons[num];
    }
    public static BufferedImage getTitleImage()
    {
    	return titleImage;
    }
    public static AudioClip getMoveAudio()
    {
    	return moveSound;
    }
}
