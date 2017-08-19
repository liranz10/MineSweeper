package com.example.liran.minesweeper;



public class Timer{
    private int ticks;
    private boolean isTimerOn;

    public Timer() {
        this.ticks = 0;
        isTimerOn=false;
    }

    public void tick() {
        if(isTimerOn)
            ticks++;
        else
            return;

    }

    public int getTicks() {
        return ticks;
    }

    public void setTimerOn(boolean timerOn) {
        isTimerOn = timerOn;
    }
}
