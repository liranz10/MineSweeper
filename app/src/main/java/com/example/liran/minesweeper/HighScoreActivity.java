package com.example.liran.minesweeper;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
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
    private ArrayList<HighScore> scores;
    private RadioGroup radioGroup;
    private int checkedButton;
    private TableLayout tl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        setContentView(R.layout.activity_high_score);
        scores =HighScore.load(this);
        tl = (TableLayout) findViewById(R.id.scoretable);
        radioGroup = (RadioGroup)findViewById(R.id.group);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                checkedButton = radioGroup.indexOfChild(findViewById(checkedId));
                switch (checkedButton){
                    case 0:
                        tl.removeAllViews();
                        showTable(scores, LevelConst.LEVEL.EASY);
                        break;
                    case  1:
                        tl.removeAllViews();
                        showTable(scores, LevelConst.LEVEL.MEDIUM);
                        break;
                    case 2:
                        tl.removeAllViews();
                        showTable(scores, LevelConst.LEVEL.HARD);
                        break;
                    default:
                        break;
                }
            }
        });

//        showTable(scores);

    }


    private void showTable(ArrayList<HighScore> scores, LevelConst.LEVEL level){
        int rankVal=1;
        Typeface face;


        /* Create a new row to be added. */

        for (HighScore e : scores)
        {
            if (e.getLevel() == level) {
                if (rankVal <= 10) {
                    TableRow tr = new TableRow(this);
                    TextView rank = new TextView(this);
                    TextView time = new TextView(this);
                    TextView name = new TextView(this);
                    face = Typeface.createFromAsset(this.getAssets(), "fonts/ComingSoon.ttf");

                    rank.setTypeface(face);
                    time.setTypeface(face);
                    name.setTypeface(face);

//                tr.setGravity(Gravity.CENTER_HORIZONTAL);
//                rank.setPadding(0, 0, 280, 0);
//                time.setPadding(0, 0, 100, 0);
//                name.setPadding(100,0,0,0);
//                rank.setGravity(Gravity.LEFT);
//                time.setGravity(Gravity.CENTER_HORIZONTAL);
//                name.setGravity(Gravity.RIGHT);


                    rank.setText((rankVal++) + "");
                    name.setText(e.getPlayerName());
                    time.setText(e.getScore() + "");

                    rank.setTextSize(20);
                    time.setTextSize(20);
                    name.setTextSize(20);

//                rank.setGravity(Gravity.CENTER);

                    tr.addView(rank);
                    tr.addView(time);
                    tr.addView(name);

                    tl.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
                }
            }
        }
        tl.setColumnStretchable(0,true);
        tl.setColumnStretchable(1,true);
        tl.setColumnStretchable(2,true);

    }
}
