package com.example.liran.minesweeper;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import static android.content.Context.MODE_PRIVATE;

class HighScore {
    private int highScoreCounter = 0;
    private String playerName;
    private int score;
    private LevelConst.LEVEL level;

    public HighScore(String playerName, int score, LevelConst.LEVEL level,Context context) {
        this.playerName = playerName;
        this.score = score;
        this.level=level;
        this.highScoreCounter = load(context).size();
        save(context);
    }

    private void save(Context context){
        String jsonString = new Gson().toJson(this);
        SharedPreferences.Editor editor = context.getSharedPreferences("HighScoreTable", MODE_PRIVATE).edit();
        editor.putString(HighScore.class.getSimpleName() + level + highScoreCounter, jsonString).apply();
    }

    static ArrayList<HighScore> load(Context context) {

        ArrayList<HighScore> table= new ArrayList<>();
        SharedPreferences sp = context.getSharedPreferences("HighScoreTable", MODE_PRIVATE);
//        to delete from share preferences
//        SharedPreferences.Editor editor = context.getSharedPreferences("HighScoreTable", MODE_PRIVATE).edit();
//        delete one row:
//        sp.edit().remove(HighScore.class.getSimpleName() + LevelConst.LEVEL.EASY+"1").apply();
//        delete all:
//        sp.edit().clear().apply();

        Map<String,?>  scores = sp.getAll();
        for (Map.Entry<String, ?> entry : scores.entrySet()){
            String json = entry.getValue().toString();
            table.add(new Gson().fromJson(json,HighScore.class));
        }
        Collections.sort(table, HighScore.getCompByScore());

        return table;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getScore() {
        return score;
    }

    public LevelConst.LEVEL getLevel() {
        return level;
    }


    public static Comparator<HighScore> getCompByScore(){
        Comparator comp = new Comparator<HighScore>() {
            @Override
            public int compare(HighScore h1, HighScore h2) {
                return  h1.getScore()-h2.getScore();
            }
        };
        return comp;
    }
}
