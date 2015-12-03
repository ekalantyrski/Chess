package com.Eric;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by Eric on 11/5/2015.
 * Hi there
 */
public class Game {

    private Screen screen;
    private Board board;
    private Position clickPosition;

    MouseAdapter mouseAdapter = new MouseAdapter()
    {
        public void mouseReleased(MouseEvent e) {
            board.calculate(calculateSquareCoordinate(e.getX(), e.getY()));
            screen.setElementArray(board.getElementArray());
            screen.update();
        };
    };


    public Game()
    {
        createWindow();
        DAL.load();
        board = new Board(true);
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
        JMenuItem menuItem = new JMenuItem();

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
