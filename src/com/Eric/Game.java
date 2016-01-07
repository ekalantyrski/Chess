package com.Eric;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

/**
 * Created by Eric on 11/5/2015.
 * Hi there
 */
public class Game {

    private Screen screen;
    private Board board;
    private Position clickPosition;
    private boolean whiteTurn;
    private boolean flipBoard;
    private ArrayList<Element> elementArray = null;
    private Save save;
    private JMenuItem menuSaveItem, menuLoadItem;

    MouseAdapter mouseAdapter = new MouseAdapter()
    {
        public void mouseReleased(MouseEvent e) {
            if(board.getPawnPromotion() == true)
            {
                PieceType type = getPawnPromotionPiece(e.getX(), e.getY());
                if(type != null)
                {
                    board.setPiece(type);
                    screen.setDrawPawnPromotion(false);
                    elementArray = board.getElementArray();
                    screen.setElementArray(elementArray);
                    screen.update();
                }
            }else {
                board.calculate(calculateSquareCoordinate(e.getX(), e.getY()));
                elementArray = board.getElementArray();
                screen.setElementArray(elementArray);
                screen.update();
                if (board.getCheckStatus() == 2) {
                    System.out.println("checkmate");
                } else if (board.getCheckStatus() == 1) {
                    System.out.println("stalemate");
                }

                if(board.getPawnPromotion() == true)
                {
                    int amountOfMoves = save.getSaveSize();
                    if(amountOfMoves % 2 == 0)
                    {
                        screen.setDrawPawnPromotion(true, PieceColor.BLACK);
                    }
                    else
                    {
                        screen.setDrawPawnPromotion(true, PieceColor.WHITE);
                    }
                }
            }

        }
    };
    
    ActionListener actionListener = new ActionListener()
    {
    	public void actionPerformed(ActionEvent e)
    	{
    		if(e.getSource() == menuSaveItem)
    		{
    			save.save();
    		}
    		else if(e.getSource() == menuLoadItem)
    		{
                Save newSave = Save.createNewSave();
                if(newSave != null)
                {
                    board.setSave(newSave);
                }
    		}
    		
    	}
    };


    public Game()
    {
        createWindow();
        DAL.load();
        save = new Save();
        board = new Board(true, save);
        whiteTurn = true;
        flipBoard = false;
        board.setFlipBoard(flipBoard);
        screen.setElementArray(board.getElementArray());
        screen.update();
        screen.addMouseListener(mouseAdapter);
    }

    private void createWindow() {
        JFrame frame = new JFrame("Chess"); // creates window object
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); //exits if X is clicked
        frame.setSize(720, 720);   //sets size of the window was 1280, 720
        frame.setLocationRelativeTo(null); //centers the window
        frame.setResizable(false);
        screen = new Screen();
        screen.setPreferredSize(new Dimension(720, 720));
        frame.add(screen);
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("File");
        menuBar.add(menu);
        menuSaveItem = new JMenuItem("Save");
        menuLoadItem = new JMenuItem("Load");
        
        menuSaveItem.addActionListener(actionListener);
        menuLoadItem.addActionListener(actionListener);

        menu.add(menuSaveItem);
        menu.add(menuLoadItem);
        
        //menu.addSeparator();
        
        
        frame.setJMenuBar(menuBar);
        frame.pack();
        frame.setVisible(true);
    }

    private Position calculateSquareCoordinate(int mouseX, int mouseY)
    {
        int row = (mouseY - screen.topInset) / screen.squareSize;
        int column = (mouseX - screen.leftInset) / screen.squareSize;
        Position position = new Position(row, column);
        return position;
    }

    private PieceType getPawnPromotionPiece(int x, int y)
    {
        if(y > screen.pawnPromotionTopInset + 97 || y < screen.pawnPromotionTopInset + 12)
            return null;
        else if(x > screen.pawnPromotionLeftInset)
        {
            int num = (x - screen.pawnPromotionLeftInset)/(screen.squareSize + 5);
            switch(num)
            {
                case 0:
                    return PieceType.QUEEN;
                case 1:
                    return PieceType.KNIGHT;
                case 2:
                    return PieceType.ROOK;
                case 3:
                    return PieceType.BISHOP;
                default:
                    return null;
            }
        }
        return null;
    }
}
