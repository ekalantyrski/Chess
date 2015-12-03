package com.Eric;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class DAL {
    static BufferedImage[] icons;
    static BufferedImage possibleMovesImage;
    public static void load()
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
            }catch (IOException e)
            {
                e.printStackTrace();
            }

        }
        try
        {
            File file = new File("Data/CIRCLE_RED.png");
            possibleMovesImage = ImageIO.read(file);
        }catch(IOException e)
        {
            e.printStackTrace();
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
}
