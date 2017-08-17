package com.example.liran.minesweeper;

/**
 * Created by Liran on 17/08/2017.
 */

public class NeighboursThread implements Runnable {
    private MineField board;
    private int row;
    private int col;

    public NeighboursThread(MineField board, int row, int col) {
        this.board = board;
        this.row = row;
        this.col = col;
    }


    @Override
    public void run() {
        board.revealNeighbours(row, col);
    }
}
