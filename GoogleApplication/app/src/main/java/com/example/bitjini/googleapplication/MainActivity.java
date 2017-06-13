package com.example.bitjini.googleapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by bitjini on 19/1/17.
 */

public class MainActivity extends Activity {
    Button startBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        intiViews();
        initListeners();
    }



    private void intiViews() {
        startBtn=(Button) findViewById(R.id.startBtn);
    }
    private void initListeners() {
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,DistanceTimeActivity.class);
                startActivity(intent);
            }
        });
    }
}
