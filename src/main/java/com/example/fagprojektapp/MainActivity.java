package com.example.fagprojektapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {
    Button NewKeyButton;
    TextView message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        message = (TextView)findViewById(R.id.BitRateText);
        NewKeyButton = (Button)findViewById(R.id.NewKeyButton);
        Thread myThread = new Thread(new MyReadThread());
        myThread.start();

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
        String mes;

        @Override
        public void run() {
            try {
                s = new Socket("192.168.0.11",8080);
                isr = new InputStreamReader(s.getInputStream());
                br = new BufferedReader(isr);
                while(true){
                    mes = br.readLine();

                    h.post(new Runnable() {
                        @Override
                        public void run() {
                            message.setText(mes);
                        }
                    });
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



