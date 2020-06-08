package com.example.androidapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
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
        SignalText.setText(0);
        BitErrorText = (TextView)findViewById(R.id.BitErrorText);
        BitErrorText.setText(0);
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

                    String code = mes.substring(0,8);
                    final String value = mes.substring(8);
                    switch(code){
                        case "00000001" :
                            h.post(new Runnable() {
                                @Override
                                public void run() {
                                    SignalText.setText(value);
                                }
                            });
                            break;
                        case "00000010" :
                            h.post(new Runnable() {
                                @Override
                                public void run() {
                                    BitErrorText.setText(value);
                                }
                            });
                            break;
                        case "00000011" :
                            h.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "Encryptio Key Changed", Toast.LENGTH_SHORT).show();
                                }
                            });
                            break;

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



