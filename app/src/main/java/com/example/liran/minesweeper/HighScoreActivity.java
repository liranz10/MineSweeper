package com.example.liran.minesweeper;

import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import static android.R.id.list;
import static com.example.liran.minesweeper.HighScore.load;

public class HighScoreActivity extends AppCompatActivity {
    private LevelConst.LEVEL level;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_score);
        ArrayList<HighScore> scores =HighScore.load(this);
        RadioGroup radioGroup = (RadioGroup)findViewById(R.id.radioGroup);
        showTable(scores);

    }

    private void showTable(ArrayList<HighScore> scores){
        int rankVal=1;
        Typeface face;

        TableLayout tl = (TableLayout) findViewById(R.id.scoretable);
        /* Create a new row to be added. */

        RadioButton radioEasy = (RadioButton)findViewById(R.id.easytable);
        RadioButton radioMedium = (RadioButton)findViewById(R.id.mediumtable);
        RadioButton radioHard = (RadioButton)findViewById(R.id.hardtable);
        radioEasy.setChecked(true);
        level = LevelConst.LEVEL.EASY;
        radioEasy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                level = LevelConst.LEVEL.EASY;
            }
        });
        radioMedium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                level = LevelConst.LEVEL.MEDIUM;
            }
        });
        radioHard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                level = LevelConst.LEVEL.HARD;
            }
        });
//        if (radioEasy.isChecked())
//            level = LevelConst.LEVEL.EASY;
//        else if (radioMedium.isChecked())
//            level = LevelConst.LEVEL.MEDIUM;
//        else if(radioHard.isChecked())
//            level = LevelConst.LEVEL.HARD;
        for (HighScore e : scores)
        {
            //if (e.getLevel() == level) {
            TableRow tr = new TableRow(this);
            TextView rank = new TextView(this);
            TextView time = new TextView(this);
            TextView name = new TextView(this);
            face = Typeface.createFromAsset(this.getAssets(), "fonts/ComingSoon.ttf");
            //rank.setFontFeatureSettings("casual");
            rank.setTypeface(face);
            time.setTypeface(face);
            name.setTypeface(face);

            rank.setPadding(0, 0, 320, 0);
            time.setPadding(0, 0, 250, 0);
            rank.setText((rankVal++) + "");
            name.setText(e.getPlayerName());
            time.setText(e.getScore() + "");

            tr.addView(rank);
            tr.addView(time);
            tr.addView(name);

            tl.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
            // }
        }
    }
}
