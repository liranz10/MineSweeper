package com.example.liran.minesweeper.UI;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.liran.minesweeper.R;

// The main activity class (select play/ high score/ exit)
public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        setContentView(R.layout.activity_main);

        final Intent selectLevel = new Intent(this,SelectLevelActivity.class);

        final Intent highScore = new Intent(this,HighScoreActivity.class);

        // move to select level activity
        findViewById(R.id.play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(selectLevel);
            }
        });

        // move to select high score activity
        findViewById(R.id.high_score).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(highScore);
            }
        });

        // exit the app
        findViewById(R.id.exit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                System.exit(0);
            }
        });
    }
}
