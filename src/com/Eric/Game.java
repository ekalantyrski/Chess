package com.Eric;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import static com.Eric.PieceColor.*;

/**
 * Created by Eric on 11/5/2015.
 * Hi there
 */
public class Game {

    private Screen screen; // Screen object
    private Board board; //Board object
    private ArrayList<Element> elementArray = null; // elementArray
    private Save save; // Save instance
    private JMenuItem menuSaveItem, menuLoadItem, menuNewGame; // Menu items
    private int gameType; // 1 = 1v1, 2 = AI, 3 = Network
    private int boardStatus; // if above 0, game end
    private Network nt; // the network object
    private DrawData drawData;
    private AI ai;
    private boolean whiteTurn;
    
    MouseAdapter mouseAdapter = new MouseAdapter()
    {
        /**
         * Main method of the program.
         * Based on several gamestates, something different happens
         *
         * @param e the mouse event
         */
        public void mouseReleased(MouseEvent e) {
            if (board == null) {
                if(gameType == 2)
                {
                    whiteTurn = (getPlayerColor(e.getX(), e.getY()) == WHITE) ? true : false;
                    screen.setDrawPlayerColorScreen(false);
                    board = new Board(save, gameType, whiteTurn, drawData);
                    ai = new AI(board.getBoard(), !whiteTurn, save, board, !whiteTurn);
                    if(!whiteTurn)
                    {
                        board.setCanMove(false);
                        (new Thread(ai)).start();

                    }
                    else
                    {
                        board.setCanMove(true);

                    }
                    elementArray = board.getElementArray();
                    screen.setElementArray(elementArray);
                    screen.update();

                }
                else
                {
                    gameType = getGameType(e.getX(), e.getY());
                    save = new Save(gameType);
                    switch (gameType)
                    {
                        case 1: // 1v1
                            whiteTurn = true;
                            board = new Board(save, gameType, whiteTurn, drawData);
                            screen.createGameTypeScreen(false);
                            board.setCanMove(true);
                            break;
                        case 2: // AI
                            screen.setDrawPlayerColorScreen(true);
                            screen.createGameTypeScreen(false);
                            screen.update();
                            break;
                        case 3:// network
                            if(nt == null) {
                                nt = new Network();
                                (new Thread(nt)).start();
                                Action action;
                                do
                                {
                                    try
                                    {
                                        Thread.sleep(10);
                                    } catch (InterruptedException ie) {
                                    }

                                    action = nt.getAction();
                                } while (action == null);
                                whiteTurn = (action.getPieceColor() == WHITE) ? true : false;
                                board = new Board(save, gameType, whiteTurn, drawData);
                                screen.createGameTypeScreen(false);
                                if (whiteTurn) {
                                    board.setCanMove(true);
                                } else {
                                    board.setCanMove(false);
                                }
                            }
                            break;
                    }
                    if(gameType != 2)
                    {
                        elementArray = board.getElementArray();
                        screen.setElementArray(elementArray);
                        screen.update();
                    }
                }
                screen.setBoardStatus(0);
                boardStatus = 0;
            }
            else if(boardStatus > 0)
            {
                int choice = getBoardStatusChoice(e.getX(), e.getY());
                switch(choice)
                {
                    case 1:
                        screen.setBoardStatus(0);
                        boardStatus = 0;
                        createGameTypeScreen();
                        break;
                    case 2:
                        System.exit(0);
                }
            }
            else
            {
                if (board.getPawnPromotion() == true) { // if board promotion is true, first choose what pawn to promote
                    PieceType type = getPawnPromotionPiece(e.getX(), e.getY()); // chooses the piece to promote to
                    if (type != null) {
                        board.setPiece(type);
                        screen.setDrawPawnPromotion(false);
                        elementArray = board.getElementArray();
                        screen.setElementArray(elementArray);
                        screen.update();


                        if(gameType == 2 && board.isMoveCompleted())
                        {
                            try
                            {
                                Thread.sleep(50);
                            }catch (InterruptedException ie)
                            {}
                            (new Thread(ai)).start();
                            board.updateElementArray();
                            elementArray = board.getElementArray();
                            screen.setElementArray(elementArray);
                            screen.update();
                        }
                    }
                } else
                {
                    board.calculate(calculateSquareCoordinate(e.getX(), e.getY())); // the normal input, choosing pieces and where to move
                    if(board.isMoveCompleted()) {
                        board.updateElementArray();
                        checkBoardStatus();

                    }

                    if(gameType == 2 && board.isMoveCompleted() && board.getPawnPromotion() == false)
                    {
                        (new Thread(ai)).start();

                    }
                    if(gameType == 3 && board.isMoveCompleted())
                    {
                        Action action = board.getActionMade();
                        nt.sendAction(action);
                        checkBoardStatus();
                    }


                    if (board.getPawnPromotion() == true) { // draw pawn promotion
                        int amountOfMoves = save.getSaveSize();
                        PieceColor colorToDraw;
                        if (amountOfMoves % 2 == 0)
                        {
                            if(whiteTurn)
                            {
                               colorToDraw = BLACK;
                            }
                            else
                            {
                                colorToDraw = WHITE;
                            }
                            screen.setDrawPawnPromotion(true, colorToDraw);
                        }
                        else
                        {
                            if(whiteTurn)
                            {
                                colorToDraw = WHITE;
                            }
                            else
                            {
                                colorToDraw = BLACK;
                            }
                            screen.setDrawPawnPromotion(true, colorToDraw);
                        }
                    }
                }

            }
        }
    };

    private void checkBoardStatus() {
        board.checkStatus(WHITE);
        boardStatus = board.getCheckStatus();
        if(boardStatus > 0)
        {
            PieceColor pc = (save.getSaveSize() % 2 == 0) ? BLACK : WHITE;
            screen.setBoardStatus(boardStatus, pc);
        }else{
            board.checkStatus(BLACK);
            boardStatus = board.getCheckStatus();
            if(boardStatus > 0)
            {
                PieceColor pc = (save.getSaveSize() % 2 == 0) ? BLACK : WHITE;
                screen.setBoardStatus(boardStatus, pc);
            }
        }
    }

    ActionListener actionListener = new ActionListener()
    {
        /**
         * Does an action based on what person clicked in menu
         * @param e What was clicked
         */
    	public void actionPerformed(ActionEvent e)
    	{
    		if(e.getSource() == menuSaveItem && board == null)
    		{
    			save.saveGame(); // give option to save game
    		}
    		else if(e.getSource() == menuLoadItem) // give option to load game
    		{
                Save newSave = Save.createNewSave();
                if(newSave != null)
                {
                    screen.createGameTypeScreen(false);
                    int gameType = (newSave.getGameType() == 2) ? 2 : 1;
                	board = new Board(newSave, gameType, false,drawData);
                    elementArray = board.getElementArray();
                    screen.setElementArray(elementArray);
                    screen.update();
                }
    		}
    		else if(e.getSource() == menuNewGame) // create a new game
    		{
    			createGameTypeScreen();
                board = null;
                gameType = 0;
    		}
    		
    	}
    };

    /**
     * Constructor, creates all main game components
     */
    public Game()
    {
        createWindow();
        DAL dal = new DAL();
        screen.addMouseListener(mouseAdapter);
        createGameTypeScreen();
        Action action;
        //game update loop
        while(true) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ie) {

            }

            if (board != null) {

                if (ai != null && ai.moveFinished()) {
                    board.setCanMove(true);
                    ai.setMoveFinished(false);
                    checkBoardStatus();

                }
                //network update code
                if (nt != null) {
                    action = nt.getAction();
                    if (action != null) {
                        board.movePiece(action);
                        checkBoardStatus();
                    }

                }


                //board.updateElementArray();
                elementArray = board.getElementArray();
                screen.setElementArray(elementArray);
                screen.update();
            }
        }
    }



    /**
     * Creates the window and adds menu and display
     */
    private void createWindow() {
        drawData = new DrawData(new ArrayList<>());
        JFrame frame = new JFrame("Chess"); // creates window object
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); //exits if X is clicked
        frame.setSize(720, 720);   //sets size of the window was 1280, 720
        frame.setLocationRelativeTo(null); //centers the window
        frame.setResizable(false);
        screen = new Screen(drawData);
        screen.setPreferredSize(new Dimension(690,690));
        frame.add(screen);
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Game");
        menuBar.add(menu);
        menuNewGame = new JMenuItem("New Game");
        menuSaveItem = new JMenuItem("Save");
        menuLoadItem = new JMenuItem("Load");
        
        menuNewGame.addActionListener(actionListener);
        menuSaveItem.addActionListener(actionListener);
        menuLoadItem.addActionListener(actionListener);

        menu.add(menuNewGame);
        menu.addSeparator();
        menu.add(menuSaveItem);
        menu.add(menuLoadItem);
        
        //menu.addSeparator();
        
        
        frame.setJMenuBar(menuBar);
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * Calculates what coordinate was clicked (squares)
     * @param mouseX The x coordinate of mouse
     * @param mouseY The Y coordinate of mouse
     * @return Returns a position value
     */
    private Position calculateSquareCoordinate(int mouseX, int mouseY)
    {
        int row = (mouseY - screen.topInset) / screen.squareSize;
        int column = (mouseX - screen.leftInset) / screen.squareSize;
        Position position = new Position(row, column);
        return position;
    }

    /**
     * Calculate what piece to promote to
     * @param x The x coordinate of the mouse
     * @param y The y coordinate of the mouse
     * @return Returns the pieceType chosen
     */
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

    private PieceColor getPlayerColor(int x, int y)
    {
        boolean done = false;
        PieceColor pc = null;
        do
        {
            if (y > 345 && y < 500) {
                if (x > 105 && x < 260) {
                    done = true;
                    pc = BLACK;
                } else if (x > 370 && x < 585) {
                    done = true;
                    pc = WHITE;
                }
            }
        }while(!done);
        return pc;
    }

    /**
     * Sets variables to make the newGame screen
     */
    private void createGameTypeScreen()
    {
        board = null;
    	screen.createGameTypeScreen(true);
    	screen.update();
    }

    /**
     * Calculates what gameType was chosen
     * @param x The mouse x coordinate
     * @param y The mouse y coordinate
     * @return Returns int value of what gameType was chosen
     */
    private int getGameType(int x, int y)
    {
        if((x < 320 && x > 105) && (y > 345 && y < 460))
            return 1;
        if((x > 370 && x < 585) && (y > 345 && y < 460))
            return 2;
        if((x > 235 && x < 450) && (y > 520 && y < 675))
            return 3;
        else
            return 0;
    }

    /**
     * Gets the choice for when the boardstatus screen is popped up
     * @param x The x coordinte for mouse
     * @param y The y coordinate for mouse
     * @return The integer value of the status chosen
     */
    private int getBoardStatusChoice(int x, int y)
    {
        if(y > 475 || y < 350)
            return 0;
        else if(x < 140 || (x > 315 && x < 370) || x > 545)
            return 0;
        else if(x < 370)
        {
            return 1; // playagain
        }
        else
        {
            return 2; // exit
        }
    }
}
