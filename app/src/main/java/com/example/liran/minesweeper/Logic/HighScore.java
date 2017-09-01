package com.example.liran.minesweeper.Logic;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import static android.content.Context.MODE_PRIVATE;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;

// High score class (create/save/load high scores)
public class HighScore {
    private int highScoreCounter = 0;
    private String playerName;
    private int score;
    private LevelConst.LEVEL level;
    private Location playerLocation;

    public HighScore(int score, LevelConst.LEVEL level,Context context) {

        this.score = score;
        this.level=level;
        this.highScoreCounter = load(context).size();

    }

    // save the high score to shared preferences by GSON
    public void save(Context context){
        String jsonStringScore = new Gson().toJson(this);
        SharedPreferences.Editor editorTable = context.getSharedPreferences("HighScoreTable", MODE_PRIVATE).edit();
        editorTable.putString(HighScore.class.getSimpleName() + level + highScoreCounter, jsonStringScore).apply();
        int counter = loadCounter(context);
        if(counter<10) {
            counter++;
            SharedPreferences.Editor editorCounters = context.getSharedPreferences("HighScoreCounters", MODE_PRIVATE).edit();
            editorCounters.putInt(this.getLevel() + "", counter).apply();
        }
    }

    private int loadCounter(Context context) {
        SharedPreferences sp = context.getSharedPreferences("HighScoreCounters", MODE_PRIVATE);
        SharedPreferences.Editor editor = context.getSharedPreferences("HighScoreCounters", MODE_PRIVATE).edit();
        return sp.getInt(this.getLevel().toString(),0);
    }


    // load the high scores from shared preferences by GSON
   public  static ArrayList<HighScore> load(Context context) {
        ArrayList<HighScore> table= new ArrayList<>();
        SharedPreferences sp = context.getSharedPreferences("HighScoreTable", MODE_PRIVATE);
        Map<String,?>  scores = sp.getAll();

        //get the high scores
        for (Map.Entry<String, ?> entry : scores.entrySet()){
            String json = entry.getValue().toString();
            table.add(new Gson().fromJson(json,HighScore.class));
        }
        // sort the high score table by comparator
        Collections.sort(table, HighScore.getCompByScore());

        return table;
    }

    // return true if the score should be inserted to the high score table
    public boolean checkHighScore(Context context){
        ArrayList<HighScore> table = load(context);
        if (table.size()==0 || loadCounter(context)<10)
            return true;
        for (int i= table.size()-1; i >= 0; i--) {
            HighScore temp = table.get(i);
            if(temp.getLevel() == this.getLevel()){
                if(this.getScore() < temp.getScore()){
                    //delete the old score
                    SharedPreferences sp = context.getSharedPreferences("HighScoreTable", MODE_PRIVATE);
                    SharedPreferences.Editor editor = context.getSharedPreferences("HighScoreTable", MODE_PRIVATE).edit();
                    sp.edit().remove(HighScore.class.getSimpleName() + temp.getLevel()+ temp.getHighScoreCounter()).apply();
                    setHighScoreCounter(temp.getHighScoreCounter());
                    return true;
                }
            }
        }
        return false;
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

    public int getHighScoreCounter() {
        return highScoreCounter;
    }

    public void setHighScoreCounter(int highScoreCounter) {
        this.highScoreCounter = highScoreCounter;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public Location getPlayerLocation() {
        return playerLocation;
    }

    public void setPlayerLocation(Location playerLocation) {
        this.playerLocation = playerLocation;
    }

    // comparator to sort high score table (by score)
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
