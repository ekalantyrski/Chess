package com.Eric;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class Network implements Runnable{

    final String address = "localhost";
    BufferedReader br;
    public Network()
    {
        try
        {
            Socket socket = new Socket(address, 24377);
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }
        catch(IOException ioe)
        {
            ioe.printStackTrace();
        }

    }

    public void run()
    {
        try
        {
            System.out.println(br.readLine());
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
    }

}
