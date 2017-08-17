package com.example.liran.minesweeper;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageButton;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

import static android.R.attr.value;
import static android.R.transition.move;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.V;

public class GameActivity extends AppCompatActivity {
    static GameManager gameManager;
    private GridLayout gameGrid;
    private MineSweeperButton[] buttons;
    //remove
    private final String TAG = "sapir";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        createNewGrid(gameManager.getBoard().getRows(),gameManager.getBoard().getCols());



    }

    private GridLayout createNewGrid(final int colsNum, int rowsNum) {
        buttons = new  MineSweeperButton[rowsNum*colsNum];
       ViewGroup.LayoutParams gridLayoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        GridLayout gridLayout = (GridLayout)findViewById(R.id.grid);
        gridLayout.setColumnCount(colsNum);
        gridLayout.setRowCount(rowsNum);

        // Programmatically create the buttons layout
        for (int row = 0; row < rowsNum; row++) {
            for (int column = 0; column < colsNum; column++) {

                final MineSweeperButton btn = new MineSweeperButton(this,row,column);
                if (rowsNum>5) {
                    btn.setLayoutParams(new LinearLayout.LayoutParams(100, 100));
                    btn.setBackgroundResource(R.drawable.buttonshapesmall);
                }
                else{
                    btn.setLayoutParams(new LinearLayout.LayoutParams(170, 170));
                    btn.setBackgroundResource(R.drawable.buttonshape);
                }

                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        boolean isMine = !gameManager.gameMove(btn.getRow(),btn.getCol(),getFlagStatus());
                        boolean isFlagged = gameManager.getBoard().getCell(btn.getRow(),btn.getCol()).isFlagged();
                        int cellValue = gameManager.getBoard().getCell(btn.getRow(),btn.getCol()).getValue();
                        btn.setPressed(isFlagged,isMine,cellValue,colsNum);
                        if(isMine)
                            showAllMines(colsNum);
                        showAllRevealed(colsNum);
                    }
                });
                buttons[row + column * colsNum] = btn;
                gridLayout.addView(btn);
            }
        }

        return gridLayout;
    }
    private void showAllMines(int colsNum){
        for (int row = 0; row < gameManager.getBoard().getRows(); row++) {
            for (int column = 0; column < gameManager.getBoard().getCols(); column++) {
                if (gameManager.getBoard().getCell(row, column).getValue() == Cell.MINE_VALUE) {
                    buttons[row + column * colsNum].setPressed(false,true,Cell.MINE_VALUE,colsNum);
                }
            }
        }
    }

    private void showAllRevealed(int colsNum){
        for (int row = 0; row < gameManager.getBoard().getRows(); row++) {
            for (int column = 0; column < gameManager.getBoard().getCols(); column++) {
                if (!gameManager.getBoard().getCell(row, column).isCovered()) {
                    buttons[row + column * colsNum].setPressed(false,false,gameManager.getBoard().getCell(row, column).getValue(),colsNum);
                }
            }
        }
    }
    private boolean getFlagStatus(){
        ToggleButton flagStatus = (ToggleButton)findViewById(R.id.toggleButton);
        return flagStatus.isChecked();
    }



    }

     class MineSweeperButton extends AppCompatButton{
         private int row;
         private int col;
         public MineSweeperButton(Context context,int row,int col) {
             super(context);
             this.row=row;
             this.col=col;
         }
         public void settext(int value){
             switch (value){
                 case 1:
                     this.setTextColor(Color.BLUE);
                     break;
                 case 2:
                     this.setTextColor(Color.GREEN);
                     break;
                 case 3:
                     this.setTextColor(Color.RED);
                     break;
                 case 4:
                     this.setTextColor(Color.CYAN);
                     break;
                 case 5:
                     this.setTextColor(Color.MAGENTA);
                     break;
                 case 6:
                     this.setTextColor(Color.BLACK);
                     break;
                 case 7:
                     this.setTextColor(Color.DKGRAY);
                     break;
                 case 8:
                     this.setTextColor(Color.WHITE);
                     break;
             }
            if (value != 0)
             this.setText(value+"");
         }
         public void setPressed(boolean flag,boolean mine,int value,int cols){
             if (flag) {
                 if (cols > 5)
                     this.setBackgroundResource(R.drawable.buttonshapesmallflag);
                 else
                     this.setBackgroundResource(R.drawable.buttonflag);
             }
             else if(mine) {
                 if(cols>5)
                 this.setBackgroundResource(R.drawable.buttonshapesmallmined);
                 else
                     this.setBackgroundResource(R.drawable.buttonmined);
             }
             else{
                 if(cols>5)
                     this.setBackgroundResource(R.drawable.buttonshapesmallpressed);
                 else
                     this.setBackgroundResource(R.drawable.buttonpressed);

                 settext(value);
             }
         }

         public int getRow() {
             return row;
         }

         public int getCol() {
             return col;
         }
     }
