package com.example.liran.minesweeper.Logic;
import android.content.Context;

//Game Manager Class, refers to all SINGLE game logic
public class GameManager implements LevelConst{
    private MineField board;
    private Timer time;
    private HighScore highScore;
    private int mineLeft;
    private boolean firstMove;
    private LEVEL level;
    private boolean isGameOver;

    //initialize game grid by level
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

    //the game level enum
    public LEVEL getLevel() {
        return level;
    }

    //checks if the single game is in winning state - all not mined cells were revealed
    public boolean isWinning(){
        boolean isWon = true;
        for (int i=0; (i<board.getRows()) && isWon; i++){
            for (int j=0; (j<board.getCols()) && isWon; j++){
                if (board.getCell(i,j).isCovered() && !board.getCell(i,j).isFlagged()){
                    isWon = false;
                }
            }
        }
        if((mineLeft==0 && isWon) || checkAllRevealed()) {
            isGameOver = true;
            return true;
        }
        else
            return false;
    }

    //Check if all the non mined is revealed
    private boolean checkAllRevealed(){
        for (int i=0; i<board.getRows(); i++){
            for (int j=0; j<board.getCols(); j++){
                if (!(board.checkMine(i,j)) && (board.getCell(i,j).isCovered())){
                    return false;
                }
            }
        }
        return true;
    }

    // return false if game over
    public boolean gameMove(int row, int col, boolean flag){
        if (firstMove) {
            //init timer - turn on ticks
            time.setTimerOn(true);

            //turn off the first game move flag
            this.firstMove=false;
        }
        //set a flag on the chosen cell
        if (flag) {
            if(!board.getCell(row, col).isFlagged() && board.getCell(row, col).isCovered()) {
                board.flag(row, col, true);
                mineLeft--;
            }
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
    public void addMineToGame(){
        if(board.getMineNum()< board.getRows()*board.getCols()) {
            mineLeft = board.getMineNum();
            board.addMine();
        }
        else {
           isGameOver=true;
        }
    }

    public void updateBoard(){
        board.update();
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

    public HighScore getHighScore() {
        return highScore;
    }

    //sets the game high score record - player name, ticks from the timer, and the level
    public void setHighScore(Context context) {
        this.highScore= new HighScore(time.getTicks(),level,context);
    }


}
