package com.example.liran.minesweeper;


public class Cell {

    private int value;
    private boolean flagged;
    private boolean covered;
    private boolean removeFlag;
    public static final int MINE_VALUE = -1;

    public Cell(int value)
    {
        this.value=value;
        this.flagged=false;
        this.covered=true;
        this.removeFlag=false;
    }

    public int getValue() {return value;
    }

    public void increaseValue() {
        if(value!=MINE_VALUE)
            this.value++;
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
