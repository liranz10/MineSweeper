package com.example.liran.minesweeper;

import android.util.Log;

import static android.content.ContentValues.TAG;

/**
 * Created by Liran on 16/08/2017.
 */

public class Timer {
    private int ticks;
    private static final int ONE_SECOND=1000;
    public Timer() {
        this.ticks = 0;
    }

    private void tickEndlessly() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    tick();
                    try {
                        Thread.sleep(ONE_SECOND);
                    } catch (InterruptedException exception) {
                        exception.printStackTrace();
                    }
                }
            }
        }).start();
    }
    private void tick() {
        ticks++;
    }
}
