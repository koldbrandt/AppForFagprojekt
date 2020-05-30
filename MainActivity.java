package com.example.fagprojektapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    Button NewKeyButton;
    TextView message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NewKeyButton = (Button)findViewById(R.id.NewKeyButton);
        NewKeyButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){


            }
        });


    }


}
