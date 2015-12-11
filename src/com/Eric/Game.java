package com.Eric;

import javax.swing.*;
import java.awt.*;
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

    MouseAdapter mouseAdapter = new MouseAdapter()
    {
        public void mouseReleased(MouseEvent e) {
            board.calculate(calculateSquareCoordinate(e.getX(), e.getY()));
            elementArray = board.getElementArray();
            screen.setElementArray(elementArray);
            screen.update();
            if(board.getCheckStatus() == 2)
            {
            	System.out.println("checkmate");
            }
            else if(board.getCheckStatus() == 1)
            {
                System.out.println("stalemate");
            }
        };
    };


    public Game()
    {
        createWindow();
        DAL.load();
        board = new Board(true);
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
        JMenuItem menuItem = new JMenuItem("Hello");

        menu.add(menuItem);

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
}
