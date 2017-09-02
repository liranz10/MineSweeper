package com.example.liran.minesweeper.Logic;

// Mine field class - the game board
public class MineField {
    private Cell[][] gameGrid;
    private int rows;
    private int cols;
    private int mineNum;

    //Constructor - initialize the grid values and the mines positions
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

    //reveal the cell
    public void reveal(int row, int col)
    {
        gameGrid[row][col].setCovered(false);
    }

    //flag the cell
    public void flag(int row, int col,boolean on_off)
    {
        gameGrid[row][col].setFlagged(on_off);
    }

    //checks if the cell is mined
    public boolean checkMine(int row, int col) {
        if (gameGrid[row][col].getValue()== Cell.MINE_VALUE)
            return true;
        else
            return false;
    }

    //initialize game grid , cell values to zero
    private void initGameGrid(){
        for (int i=0 ; i < rows ; i++)
            for (int j=0 ; j < cols ; j++)
                gameGrid[i][j]=new Cell(0);
    }

    //randomize the mines positions
    private void generateMines() {
        int randomRow;
        int randomCol;
        int i=0;
        while (i < mineNum) {
            randomRow =  (int) (Math.random() * rows);
            randomCol =  (int) (Math.random() * cols);
             if (!checkMine(randomRow,randomCol)) {
                 gameGrid[randomRow][randomCol] = new Cell(Cell.MINE_VALUE);
                 adjustNeighboursValues(randomRow,randomCol);
                 i++;
             }
        }
    }

    public void addMine() {
        mineNum++;
        int randomRow;
        int randomCol;

            randomRow =  (int) (Math.random() * rows);
            randomCol =  (int) (Math.random() * cols);
            if (!checkMine(randomRow,randomCol)) {
                gameGrid[randomRow][randomCol].setMineValue();
                adjustNeighboursValues(randomRow,randomCol);

            }

    }


    //adjusting mined cell neighbours values
    private void adjustNeighboursValues(int row, int col){
        int i=1, j=1;

        //last row
        if (row == rows-1)
            i=-1;

        //last column
        if (col == cols-1)
            j=-1;

        //left,right,and bottom left neighbours
        gameGrid[row+i][col].increaseValue();
        gameGrid[row][col+j].increaseValue();
        gameGrid[row+i][col+j].increaseValue();

        //first or last row
        if(row == 0 || row==rows-1){
            if (col != 0 && col!=cols-1){
                gameGrid[row][col-j].increaseValue();
                gameGrid[row+i][col-j].increaseValue();
            }
        }

        //middle rows
        else  {
            gameGrid[row-i][col].increaseValue();
            gameGrid[row-i][col+j].increaseValue();

            //first or last column
            if(col != 0 && col !=cols-1){
                gameGrid[row-i][col-j].increaseValue();
                gameGrid[row][col-j].increaseValue();
                gameGrid[row+i][col-j].increaseValue();
            }
        }
    }

    //recursive function to reveal the neighbours of cell that was pressed
    public void revealNeighbours(int row, int col) {
        int minX = (row <= 0 ? 0 : row - 1);
        int minY = (col <= 0 ? 0 : col - 1);
        int maxX = (row >= rows - 1 ? rows : row + 2);
        int maxY = (col >= cols - 1 ? cols : col + 2);

        // loop over all surrounding cells
        for (int i = minX; i < maxX; i++) {
            for (int j = minY; j < maxY; j++) {
                //checking id the cell is not MINED, REVEALED already  or FLAGGED
                if (!checkMine(i,j) && gameGrid[i][j].isCovered() && !gameGrid[i][j].isFlagged()) {
                    reveal(i, j);
                    if (gameGrid[i][j].getValue() == 0) {
                        // call recursively
                        revealNeighbours(i, j);
                    }
                }
            }
        }
    }

    public Cell getCell(int row, int col){
        return gameGrid[row][col];
    }

    public void update(){
        for (int i=0 ; i < rows ; i++)
            for (int j=0 ; j < cols ; j++){
                gameGrid[i][j].setCovered(true);
                gameGrid[i][j].setFlagged(false);
                gameGrid[i][j].setRemoveFlag(false);
            }
    }
}
