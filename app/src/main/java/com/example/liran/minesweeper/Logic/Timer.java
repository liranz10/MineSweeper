package com.example.liran.minesweeper.Logic;

//Game Time Class
public class Timer{
    private int ticks;
    private boolean isTimerOn;

    //init new game timer
    public Timer() {
        this.ticks = 0;
        isTimerOn=false;
    }

    //increase game ticks
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
