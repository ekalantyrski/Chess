package com.Eric;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

public class Network implements Runnable{

    final String address = "127.0.0.1";
    BufferedReader in;
    private Queue<Action> actionQueue;
    private PrintWriter out;
    public Network()
    {
        try
        {
            Socket socket = new Socket(address, 24377);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        }
        catch(IOException ioe)
        {
            ioe.printStackTrace();
        }
            actionQueue = new LinkedList<>();
    }

    public void run()
    {
        String input;
        boolean running = true;
        try
        {
            while(running)
            {
                input = in.readLine();
                if(input != null)
                {
                    actionQueue.add(new Action(input));
                }
                else
                {
                    running = false;
                }
            }
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
    }


    public Action getAction()
    {
        return actionQueue.poll();
    }

    public void sendAction(Action action)
    {
        out.println(action.toString());

    }

}
