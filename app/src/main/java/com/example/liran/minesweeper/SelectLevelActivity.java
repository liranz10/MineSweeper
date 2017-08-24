package com.example.liran.minesweeper;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

//Select Level Activity - UI for the player to pick his desired difficulty
public class SelectLevelActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        setContentView(R.layout.activity_select_level);

        final Intent game = new Intent(this, GameActivity.class);

        //initialize new single game(game manager - logic) by the selected difficulty  and starts the game activity
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