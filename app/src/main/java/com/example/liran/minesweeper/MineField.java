package com.example.liran.minesweeper;

public class MineField {

    private Cell[][] gameGrid;
    private int rows;
    private int cols;
    private int mineNum;

    public MineField(int rows, int cols, int mineNum) {
        this.rows = rows;
        this.cols = cols;
        this.mineNum = mineNum;
        this.gameGrid = new Cell[rows][cols];
        initGameGrid();
        generateMines();
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public int getMineNum() {
        return mineNum;
    }
    public void reveal(int row, int col)
    {
        gameGrid[row][col].setCovered(false);
    }
    public void flag(int row, int col)
    {
        gameGrid[row][col].setFlagged(true);
    }
    public boolean checkMine(int row, int col)
    {
        if (gameGrid[row][col].getValue()== Cell.MINE_VALUE)
            return true;
        else
            return false;
    }

    private void initGameGrid(){
        for (int i=0 ; i < rows ; i++)
            for (int j=0 ; j < cols ; j++)
                gameGrid[i][j]=new Cell(0);
    }

    private void generateMines()
    {
        int randomRow;
        int randomCol;
        int i=0;
        while (i < mineNum) {
            randomRow = 0 + (int) (Math.random() * rows);
            randomCol = 0 + (int) (Math.random() * cols);
             if (gameGrid[randomRow][randomCol].getValue()!=Cell.MINE_VALUE) {
                 gameGrid[randomRow][randomCol] = new Cell(Cell.MINE_VALUE);
                 i++;
             }
        }
    }

    private void adjustNeighboursValues(int row, int col){
        int i=1, j=1;
        if (row == rows)
            i=-1;
        if (col == cols)
            j=-1;

        gameGrid[row+i][col].increaseValue();
        gameGrid[row][col+j].increaseValue();
        gameGrid[row+i][col+j].increaseValue();

        if(row == 0 || row==rows){
            if (col != 0 && col!=cols){
                gameGrid[row][col-j].increaseValue();
                gameGrid[row+i][col-j].increaseValue();
            }

        }
        else  {
            gameGrid[row-i][col].increaseValue();
            gameGrid[row-i][col+j].increaseValue();
            if(col != 0 && col !=cols){
                gameGrid[row-i][col-j].increaseValue();
                gameGrid[row][col-j].increaseValue();
                gameGrid[row+i][col-j].increaseValue();
            }
        }
    }

    public void revealNeighbours(int row, int col){
        int i=1, j=1;
        if (row == rows)
            i=-1;
        if (col == cols)
            j=-1;

        reveal(row+i, col);
        if (gameGrid[row+i][col].getValue() == 0)
            revealNeighbours(row+i, col);
        reveal(row, col+j);
        if (gameGrid[row][col+j].getValue() == 0)
            revealNeighbours(row, col+j);
        reveal(row+i, col+j);
        if (gameGrid[row+i][col+j].getValue() == 0)
            revealNeighbours(row+i, col+j);

        if(row == 0 || row==rows){
            if (col != 0 && col!=cols){
                reveal(row, col-j);
                if (gameGrid[row][col-j].getValue() == 0)
                    revealNeighbours(row, col-j);
                reveal(row+i, col-j);
                if (gameGrid[row+i][col-j].getValue() == 0)
                    revealNeighbours(row+i, col-j);
            }

        }
        else  {
            reveal(row-i, col);
            if (gameGrid[row-i][col].getValue() == 0)
                revealNeighbours(row-i, col);
            reveal(row-i, col+j);
            if (gameGrid[row-i][col+j].getValue() == 0)
                revealNeighbours(row-i, col+j);
            if(col != 0 && col !=cols){
                reveal(row-i, col-j);
                if (gameGrid[row-i][col-j].getValue() == 0)
                    revealNeighbours(row-i, col-j);
                reveal(row, col-j);
                if (gameGrid[row][col-j].getValue() == 0)
                    revealNeighbours(row, col-j);
                reveal(row+i, col-j);
                if (gameGrid[row+i][col-j].getValue() == 0)
                    revealNeighbours(row+i, col-j);
            }
        }
    }

    public Cell getCell(int row, int col){
        return gameGrid[row][col];
    }

}
