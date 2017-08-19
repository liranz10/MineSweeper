package com.example.liran.minesweeper;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import static android.R.id.list;
import static com.example.liran.minesweeper.HighScore.load;

public class HighScoreActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_score);
        ArrayList<HighScore> scores =HighScore.load(this);
        showTable(scores);

    }

    private void showTable( ArrayList<HighScore> scores){
        TableLayout tl = (TableLayout) findViewById(R.id.scoretable);
        /* Create a new row to be added. */
        TableRow tr = new TableRow(this);

        TextView rank = new TextView(this);
        TextView time = new TextView(this);
        TextView name = new TextView(this);
        TableLayout.LayoutParams tableRowParams=
                new TableLayout.LayoutParams
                        (TableLayout.LayoutParams.WRAP_CONTENT,TableLayout.LayoutParams.WRAP_CONTENT);

        int leftMargin=0;
        int topMargin=2;
        int rightMargin=10;
        int bottomMargin=2;

        tableRowParams.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);

        tr.setLayoutParams(tableRowParams);

        for (HighScore e : scores)
        {
            rank.setText("1");
            name.setText(e.getPlayerName());
            time.setText(e.getScore()+"");

            tr.addView(rank);
            tr.addView(time);
            tr.addView(name);

            tl.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT , TableLayout.LayoutParams.WRAP_CONTENT));
        }
    }
}
