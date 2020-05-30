package com.example.fagprojektapp;

import java.net.Socket;
import java.io.*;
import java.net.UnknownHostException;
import java.util.*;

public class AndroidClient {

    public static MainActivity MA;

    public AndroidClient(MainActivity ma){
        MA = ma;
    }

    public static boolean sending = false;
    public static BufferedReader reader;
    public static PrintWriter pw;
    public static Socket mySocket;

    public static void startClient() throws IOException {
        mySocket = null;

        //Connect to server
        try {
            mySocket = new Socket("127.0.0.0",8080);
        } catch (UnknownHostException uhEx) {
            System.out.println("Could not find host");
        } catch (IOException e) {
            System.out.println("No internet connetion");
        }

        reader = new BufferedReader(new InputStreamReader(mySocket.getInputStream()));
        pw = new PrintWriter(mySocket.getOutputStream());

        RecvMessage();

    }

    public static void endClient() throws IOException {
        mySocket.close();
    }

    public static void RecvMessage() throws IOException {
        String textLine;
        while(!sending){
            //takes input form server
            textLine = reader.readLine();
            MA.message.setText(textLine);
        }
    }

    public static void SendMessage(String msg) throws IOException {
        sending = true;

        pw.print(msg + "\r\n");
        pw.flush();

        sending = false;
        RecvMessage();
    }
}
