package com.Eric;


import static com.Eric.PieceColor.*;
import static com.Eric.ActionType.*;

public class Action {
    PieceColor pieceColor;
    ActionType actionType;
    String[] movesInformation;
    String information;


    public Action(String input)
    {
        if(input != null)
        {
            pieceColor = (input.charAt(0) == '0') ? WHITE : BLACK;
            switch (input.charAt(1)) {
                case '0':
                    actionType = MOVE;
                    movesInformation = input.substring(2).split(" ");
                    information = null;
                    break;
                case '1':
                    actionType = CHAT;
                    information = input.substring(2);
                    movesInformation = null;
                    break;
                case '3':
                    actionType = EXIT;
                    information = null;
                    movesInformation = null;
                    break;
            }
        }
    }

    public Action(PieceColor pieceColor, ActionType actionType, String information)
    {
        this.pieceColor = pieceColor;
        this.actionType = actionType;
        this.information = information;
        this.movesInformation = null;
    }

    public Action(PieceColor pieceColor, ActionType actionType, Position oldPosition, Position newPosition)
    {
        this.pieceColor = pieceColor;
        this.actionType = actionType;
        this.movesInformation = new String[2];
        this.movesInformation[0] = oldPosition.toString();
        this.movesInformation[1] = newPosition.toString();
    }

    public ActionType getActionType()
    {
        return actionType;
    }

    public String[] getMovesInformation()
    {
        return movesInformation;
    }

    public String getInformation()
    {
        return information;
    }

    public PieceColor getPieceColor()
    {
        return pieceColor;
    }

    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        if(pieceColor == WHITE)
        {
            sb.append('0');
        }
        else
        {
            sb.append('1');
        }

        switch(actionType)
        {
            case MOVE:
                sb.append('0');
                sb.append(movesInformation[0]);
                sb.append(" ");
                sb.append(movesInformation[1]);
                break;
            case CHAT:
                sb.append('1');
                sb.append(information);
                break;
            case EXIT:
                break;

        }

        return sb.toString();

    }



}
