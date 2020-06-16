package com.example.androidapp;

import android.os.AsyncTask;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class MessageSender extends AsyncTask<String, String, String> {

    Socket s;
    PrintWriter pw;

    @Override
    protected String doInBackground(String... params) {

        String message = params[0];
        String IP = params[1];
        String port = params[2];

        try {
            s = new Socket(IP,Integer.parseInt(port));
            pw = new PrintWriter(s.getOutputStream());
            pw.write(message);
            pw.flush();
            pw.close();
            s.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
