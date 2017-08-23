package com.example.liran.minesweeper;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.support.annotation.IdRes;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import static com.example.liran.minesweeper.R.drawable.rank;

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
        scores = HighScore.load(this);
        tl = (TableLayout) findViewById(R.id.scoretable);
        radioGroup = (RadioGroup) findViewById(R.id.group);
        //default radio button check easy table
        radioGroup.check(R.id.easytable);
        showTable(scores, LevelConst.LEVEL.EASY);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                checkedButton = radioGroup.indexOfChild(findViewById(checkedId));
                switch (checkedButton) {
                    case 0:
                        tl.removeAllViews();
                        showTable(scores, LevelConst.LEVEL.EASY);
                        break;
                    case 1:
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


    private void showTable(ArrayList<HighScore> scores, LevelConst.LEVEL level) {
        int rankVal = 1;
        Typeface face;

        TableRow headlines = new TableRow(this);
        ImageView rankHeadline = new ImageView(this);
        rankHeadline.setImageResource(R.drawable.rank2);
        ImageView timeHeadline = new ImageView(this);
        timeHeadline.setImageResource(R.drawable.time);
        ImageView nameHeadline = new ImageView(this);
        nameHeadline.setImageResource(R.drawable.name);
        headlines.addView(rankHeadline);
        headlines.addView(timeHeadline);
        headlines.addView(nameHeadline);
        tl.addView(headlines, new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
        /* Create a new row to be added. */

        for (HighScore e : scores) {
            if (e.getLevel() == level) {
                if (rankVal <= 10) {
                    TableRow tr = new TableRow(this);
                    TextView rank = new TextView(this);
                    TextView time = new TextView(this);
                    TextView name = new TextView(this);
                    face = Typeface.createFromAsset(this.getAssets(), getString(R.string.number_font));

                    rank.setText((rankVal++) + "");
                    name.setText(e.getPlayerName());
                    time.setText(e.getScore() + "");

                    rank.setTypeface(face);
                    time.setTypeface(face);
                    name.setTypeface(face);

                    rank.setGravity(Gravity.CENTER);
                    time.setGravity(Gravity.CENTER);
                    name.setGravity(Gravity.CENTER);

                    rank.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
                    time.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
                    name.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);

                    rank.setTextSize(20);
                    time.setTextSize(20);
                    name.setTextSize(20);



                    tr.addView(rank);
                    tr.addView(time);
                    tr.addView(name);

                    tl.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
                }
            }
        }


    }
}
