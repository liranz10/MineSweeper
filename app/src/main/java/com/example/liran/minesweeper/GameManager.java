package com.example.liran.minesweeper;


import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;
import static com.example.liran.minesweeper.LevelConst.LEVEL.EASY;

public class GameManager implements LevelConst{
    private MineField board;
    private Timer time;
    private int mineLeft;
    public GameManager(LEVEL level){
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

    }

    public boolean isWinning(){
        boolean isWon = true;
        for (int i=0; (i<board.getRows()) && isWon; i++){
            for (int j=0; (j<board.getCols()) && isWon; j++){
                if (board.getCell(i,j).isCovered() || !board.getCell(i,j).isFlagged()){
                    isWon = false;
                }
            }
        }
        if(mineLeft==0 && isWon)
            return true;
        else
            return false;
    }

    // return false if game over
    public boolean gameMove(int row, int col, boolean flag){
        if (flag) {
            board.flag(row, col);
            mineLeft--;
        }
        else{
            if (board.checkMine(row, col))
                return false;
            board.reveal(row, col);
            if (board.getCell(row, col).getValue()==0)
                board.revealNeighbours(row, col);

        }
        return true;
    }

    public MineField getBoard() {
        return board;
    }
}
