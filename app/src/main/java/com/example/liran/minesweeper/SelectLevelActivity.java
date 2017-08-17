package com.example.liran.minesweeper;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import static com.example.liran.minesweeper.R.layout.activity_game;

public class SelectLevelActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_level);

        final Intent game = new Intent(this, GameActivity.class);

        findViewById(R.id.easy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GameActivity.gameManager = new GameManager(LevelConst.LEVEL.EASY);
                startActivity(game);
            }});
        findViewById(R.id.medium).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GameActivity.gameManager = new GameManager(LevelConst.LEVEL.MEDIUM);
                startActivity(game);
            }});
        findViewById(R.id.hard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GameActivity.gameManager = new GameManager(LevelConst.LEVEL.HARD);
                startActivity(game);
            }});
    }
}