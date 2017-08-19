package com.example.liran.minesweeper;


import android.content.Context;

public class GameManager implements LevelConst{

    private MineField board;
    private Timer time;
    private HighScore highScore;
    private int mineLeft;
    private boolean firstMove;
    private LEVEL level;
    private boolean isGameOver;
    public GameManager(LEVEL level){
        this.level = level;
        switch (level) {
            case EASY:
                this.board = new MineField(EASY_ROWS, EASY_COLS, EASY_MINES);
                break;
            case MEDIUM:
                this.board = new MineField(MEDIUM_ROWS, MEDIUM_COLS, MEDIUM_MINES);
                break;
            case HARD:
                this.board = new MineField(HARD_ROWS, HARD_COLS, HARD_MINES);
                break;
        }
        this.mineLeft=board.getMineNum();
        this.time=new Timer();
        this.firstMove=true;
        this.isGameOver = false;
    }

    public LEVEL getLevel() {
        return level;
    }

    public boolean isWinning(){
        boolean isWon = true;
        for (int i=0; (i<board.getRows()) && isWon; i++){
            for (int j=0; (j<board.getCols()) && isWon; j++){
                if (board.getCell(i,j).isCovered() && !board.getCell(i,j).isFlagged()){
                    isWon = false;
                }
            }
        }
        if(mineLeft==0 && isWon) {
            isGameOver = true;
            return true;
        }
        else
            return false;
    }

    // return false if game over
    public boolean gameMove(int row, int col, boolean flag){
        if (firstMove) {
            time.setTimerOn(true);
            this.firstMove=false;
        }
        if (flag) {
            board.flag(row, col,true);
            mineLeft--;
        }
        else{
                if (board.getCell(row, col).isFlagged()) {
                    board.getCell(row, col).setRemoveFlag(true);
                    board.flag(row, col, false);
                    mineLeft++;
                    return true;
            }
            if (board.checkMine(row, col)){
                isGameOver =true;
                return false;
            }

            board.reveal(row, col);
            if (board.getCell(row, col).getValue()==0) {
                board.revealNeighbours(row, col);
            }

        }
        return true;
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    public MineField getBoard() {
        return board;
    }

    public int getMineLeft() {
        return mineLeft;
    }

    public Timer getTime() {
        return time;
    }

    public void setHighScore(String playerName, Context context) {
        this.highScore= new HighScore(playerName,time.getTicks(),level,context);
}
}
