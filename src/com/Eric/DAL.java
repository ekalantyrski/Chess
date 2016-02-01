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
    private static BufferedImage[] icons;
    private static BufferedImage possibleMovesImage;
    private static JFileChooser fc = new JFileChooser();
    private static BufferedImage titleImage;
    private static AudioClip moveSound;

    public DAL()
    {
        load();
    }
    /**
     * Loads all resources that
     * are used for displaying
     */
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
                //icons[i] = ImageIO.read(getClass().getResourceAsStream(Piece.getImageFilePath(array[i], PieceColor.BLACK))); used to make jar
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
                //icons[array.length + i] = ImageIO.read(getClass().getResource(Piece.getImageFilePath(array[i], PieceColor.WHITE))); used to make jar
            }catch (IOException e)
            {
                e.printStackTrace();
            }

        }
        try // possible moves image
        {
            File file = new File("Data/CIRCLE_RED.png");
            possibleMovesImage = ImageIO.read(file);
            //possibleMovesImage = ImageIO.read(getClass().getResource("/Data/CIRCLE_RED.png")); used to make jar
        }catch(IOException e)
        {
            e.printStackTrace();
        }
        try // the title page image
        {
        	File file = new File("Data/Title.png");
            titleImage = ImageIO.read(file);
        	//titleImage = ImageIO.read(getClass().getResource("/Data/Title.png")); used to make jar
        }catch(IOException ioe)
        {
        	ioe.printStackTrace();
        }
        try
        {
            File file = new File("Data/moveSound.wav");
            moveSound = Applet.newAudioClip(file.toURI().toURL());

        }catch(IOException ioe)
        {
            ioe.printStackTrace();
        }
       
    }
    /**
     * Allows the user to get a save file from the computer
     * @return A save instance, containing save information
     */
    public static Save getSave()
    {
        fc.setDialogTitle("Choose file");
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
    	int val = fc.showOpenDialog(new JFrame()); // opens save menu
    	if(val == fc.APPROVE_OPTION)
    	{
    		File file = fc.getSelectedFile(); // the file chosen

            ArrayList<Position> moves = new ArrayList<>();
            String[] turn;
            String tmp;
            try
            {
                BufferedReader br = new BufferedReader(new FileReader(file));
                tmp = br.readLine();
                int gameType = 0;
                if(tmp.equals("1v1"))
                {
                	gameType = 1;
                }
                else if(tmp.equals("AI"))
                {
                	gameType = 2;
                }
                else
                {
                	return null;
                }
                tmp = br.readLine();
                while(tmp != null) // loops through all saves and stores them in array
                {
                	moves.add(new Position(tmp));
                    tmp = br.readLine();
                }
                return new Save(moves, gameType);
            }catch(IOException ioe)
            {
                ioe.printStackTrace();
            }
    	}
    	else
    	{
            return null;
    	}
        return null;
    }
    /**
     * Returns possibleMOves image.
     * @return The possible moves image
     */
    public static BufferedImage getPossibleMovesImage()
    {
        return possibleMovesImage;
    }
    /**
     * Returns an image based on a pieces type and color
     * @param pieceType	 The type of the piece
     * @param pieceColor The color of the piece
     * @return The image corresponding to the piece
     */
    public static BufferedImage getImage(PieceType pieceType, PieceColor pieceColor)
    {
        int num = pieceType.ordinal() + (PieceType.values().length * pieceColor.ordinal());
        return icons[num];
    }
    public static BufferedImage getTitleImage()
    {
    	return titleImage;
    }

    /**
     * Description: Returns audioClip that is played after a move
     * @return moveSound
     */
    public static AudioClip getMoveAudio()
    {
    	return moveSound;
    }

    /**Description: Given the moves and the gameType, the user will be able to choose
     *              Where to save their game.
     * @param moves
     * @param gameType
     */
    public static void saveGame(ArrayList<Position> moves, int gameType)
    {
        StringBuilder sb = new StringBuilder();
        switch(gameType)
        {
            case 1:
                sb.append("1v1" + System.lineSeparator());
                break;
            case 2:
                sb.append("AI" + System.lineSeparator());
                break;
        }
        for(int i = 0; i < moves.size(); i++)
        {
            sb.append(moves.get(i).toString() + System.lineSeparator());
        }
        fc.setDialogTitle("Save");
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int val = fc.showOpenDialog(new JFrame());
        if(val == JFileChooser.APPROVE_OPTION) //Create option to choose directory, make automatic file
        {
            File file = fc.getSelectedFile();
            try
            {
                BufferedWriter br = new BufferedWriter(new FileWriter(file));
                br.write(sb.toString());
                br.flush();
                br.close();

            }catch(IOException ioe)
            {
                ioe.printStackTrace();
            }
        }
    }

    /**
     * Returns the move sound audio
     * @return moveSound
     */
    public static AudioClip getMoveSound()
    {
        return moveSound;
    }
}
