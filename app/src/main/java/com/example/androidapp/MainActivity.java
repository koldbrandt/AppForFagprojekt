package com.example.androidapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {
    Button NewKeyButton;
    TextView SignalText;
    TextView BitErrorText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SignalText = (TextView)findViewById(R.id.SignalText);
        SignalText.setText("0");
        BitErrorText = (TextView)findViewById(R.id.BitErrorText);
        BitErrorText.setText("0");

        NewKeyButton = (Button)findViewById(R.id.NewKeyButton);

        // Første tråd til det ene board
        Thread myThread = new Thread(new MyReadThread("192.168.0.11",8080));
        myThread.start();

        // Anden tråd til det andet board. Brug ny port.
        Thread mySecondThread = new Thread(new MyReadThread("192.168.0.11",8181));
        mySecondThread.start();

        NewKeyButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                requestNewKey(v);
            }
        });
    }

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
                while(true){
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
    }


    public void requestNewKey(View v){
        MessageSender ms = new MessageSender();
        ms.execute("WeWouldLikeANewKeySirPleaseAndThankYou");
    }
}



