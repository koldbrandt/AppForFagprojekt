package com.example.androidapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// BUG LIST
// - Can "connect" to a valid ip without connecting, leaving it in a stuck state.


public class MainActivity extends AppCompatActivity {
    Button NewKeyButton, ConnectButton;
    TextView SignalText, BitErrorText;
    EditText TransIP, RecvIP, TransPort, RecvPort;
    boolean checkConnect = false;
    String ip1, ip2;
    String port1, port2;
    MyReadThread Thread1;
    MyReadThread Thread2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SignalText = (TextView)findViewById(R.id.SignalText);
        SignalText.setText("0");
        BitErrorText = (TextView)findViewById(R.id.BitErrorText);
        BitErrorText.setText("0");

        TransIP = (EditText)findViewById(R.id.IPText);
        RecvIP = (EditText)findViewById(R.id.IPText2);

        TransPort = (EditText)findViewById(R.id.TransPort);
        RecvPort = (EditText)findViewById(R.id.RecvPort);

        ConnectButton = (Button)findViewById(R.id.ConnectButton);
        NewKeyButton = (Button)findViewById(R.id.NewKeyButton);

        NewKeyButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(checkConnect) {
                    sendMessage("NewKeyPlease", ip2, port2);
                } else {
                    Toast.makeText(getApplicationContext(), "Not connected yet",Toast.LENGTH_SHORT).show();
                }
            }
        });

        ConnectButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //Get text from the port and IP fields.
                ip1 = TransIP.getText().toString();
                port1 = TransPort.getText().toString();

                ip2 = RecvIP.getText().toString();
                port2 =RecvPort.getText().toString();
                if(!(isValidPort(port1) && isValidPort(port2))){ // Check if port is of the correct format
                    // Show toast widget if not
                    Toast.makeText(getApplicationContext(), "Incorrect port format",Toast.LENGTH_SHORT).show();
                } else if(!(isValidIPAddress(ip1) && isValidIPAddress(ip2))){ // Check if IP is of the correct format
                    // Show Toast widget if not
                    Toast.makeText(getApplicationContext(), "Incorrect IP format",Toast.LENGTH_SHORT).show();
                } else if(!checkConnect) { // If we haven't connected yet, we can connect.
                    // Første tråd til det ene board
                    Thread1 = new MyReadThread(ip1,Integer.parseInt(port1));
                    Thread myThread = new Thread(Thread1);

                    // Anden tråd til det andet board.
                    Thread2 = new MyReadThread(ip2,Integer.parseInt(port2));
                    Thread mySecondThread = new Thread(Thread2);

                    myThread.start();
                    mySecondThread.start();

                    checkConnect = true;
                } else {
                    // Already connected
                    Toast.makeText(getApplicationContext(), "Already Connected",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        endConnection();
    }

    @Override
    protected void onStop() {
        super.onStop();
        endConnection();
    }

    @Override
    public void finish() {
        super.finish();
        endConnection();
    }

    @Override
    protected void onPause() {
        super.onPause();
        endConnection();
    }

    public void endConnection(){
        if(checkConnect) {
            sendMessage("EXIT", ip1, port1);
            sendMessage("EXIT", ip2, port2);
            try {
                Thread1.closeConn();
                Thread2.closeConn();
                checkConnect = false;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //Function to validate the PORT
    public static boolean isValidPort(String port){
        int x = 0;
        try {
            x = Integer.parseInt(port);
        } catch (NumberFormatException e){
            return false;
        }

        if(x <= 0 || x > 65535){
            return false;
        }
        return true;
    }

    // Function to validate the IPs address.
    public static boolean isValidIPAddress(String ip) {
        // Regex for digit from 0 to 255.
        String zeroTo255
                = "(\\d{1,2}|(0|1)\\"
                + "d{2}|2[0-4]\\d|25[0-5])";

        String regex
                = zeroTo255 + "\\."
                + zeroTo255 + "\\."
                + zeroTo255 + "\\."
                + zeroTo255;

        Pattern p = Pattern.compile(regex);

        if (ip == null) {
            return false;
        }
        Matcher m = p.matcher(ip);

        return m.matches();
    }

    // Thread to receive parameters to read
    class MyReadThread implements Runnable
    {
        Socket s;
        InputStreamReader isr;
        BufferedReader br;
        Handler h = new Handler();
        String mes, code, value;
        String IP;
        int PORT;
        public MyReadThread(String IP, int PORT){
            this.IP = IP;
            this.PORT = PORT;
        }

        @Override
        public void run() {
            try {
                s = new Socket(IP,PORT);
                isr = new InputStreamReader(s.getInputStream());
                br = new BufferedReader(isr);
                while(checkConnect){
                    mes = br.readLine().trim();
                    //Log.i("MAIN",mes);

                    if(mes.length() >= 8) {
                        h.post(new Runnable() {
                            @Override
                            public void run() {
                                code = mes.substring(0, 8).trim();
                                value = mes.substring(8).trim();
                                if(code.equals("00000001")){
                                    SignalText.setText(value);
                                } else if(code.equals("00000010")){
                                    BitErrorText.setText(value);
                                } else if(code.equals("00000011")){
                                    Toast.makeText(getApplicationContext(), "Encryption Key Changed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void closeConn() throws IOException {
            br.close();
            isr.close();
            s.close();
        }
    }

    //Send messeage function, uses seperate thread in MessageSender
    public void sendMessage(String message, String IP, String PORT){
        MessageSender ms = new MessageSender();
        ms.execute(message, IP, PORT);
    }
}



