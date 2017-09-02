package com.example.liran.minesweeper.Logic;

// The cell class
public class Cell {
    private int value;
    private boolean flagged;
    private boolean covered;
    private boolean removeFlag;
    public static final int MINE_VALUE = -1;

    //initialize cell, not flagged, covered(not revealed-'pressed")
    public Cell(int value) {
        this.value=value;
        this.flagged=false;
        this.covered=true;
        this.removeFlag=false;
    }

    public int getValue() {
        return value;
    }

    // increse the cell value by one
    public void increaseValue() {
        if(value!=MINE_VALUE)
            this.value++;
    }
    public void setMineValue(){
        this.value=MINE_VALUE;
    }

    public boolean isFlagged() {
        return flagged;
    }

    public boolean isCovered() {
        return covered;
    }

    public void setFlagged(boolean flagged) {
        this.flagged = flagged;
    }

    public void setCovered(boolean covered) {
        this.covered = covered;
    }

    public boolean isRemoveFlag() {
        return removeFlag;
    }

    public void setRemoveFlag(boolean removeFlag) {
        this.removeFlag = removeFlag;
    }
}
