package com.example.liran.minesweeper;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

public class HighScore {
    static int highScoreCounter = 0;
    private String playerName;
    private int score;
    private LevelConst.LEVEL level;


    public HighScore(String playerName, int score, LevelConst.LEVEL level,Context context) {
        this.playerName = playerName;
        this.score = score;
        this.level=level;
        highScoreCounter++;
        save(context);
    }

    private void save(Context context){
        String jsonString = new Gson().toJson(this);
        SharedPreferences.Editor editor = context.getSharedPreferences("HighScoreTable", MODE_PRIVATE).edit();
        editor.putString(highScoreCounter+"", jsonString).apply();
    }

    static ArrayList<HighScore> load(Context context) {

        ArrayList<HighScore> table= new ArrayList<>();
        SharedPreferences sp = context.getSharedPreferences("HighScoreTable", MODE_PRIVATE);
//        to delete from share preferences
//        SharedPreferences.Editor editor = context.getSharedPreferences("HighScoreTable", MODE_PRIVATE).edit();
//        sp.edit().remove(highScoreCounter+"").apply();
        Map<String,?>  scores = sp.getAll();
        for (Map.Entry<String, ?> entry : scores.entrySet()){
            String json = entry.getValue().toString();
            table.add(new Gson().fromJson(json,HighScore.class));
        }
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
}
