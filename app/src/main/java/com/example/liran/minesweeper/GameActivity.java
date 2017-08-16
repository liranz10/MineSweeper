package com.example.liran.minesweeper;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ToggleButton;

import static android.R.attr.value;
import static android.R.transition.move;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.V;

public class GameActivity extends AppCompatActivity {
    static GameManager gameManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

    }

    private GridLayout createNewGrid(int colsNum, int rowsNum) {
        ViewGroup.LayoutParams gridLayoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        GridLayout gridLayout = new GridLayout(this);
        gridLayout.setLayoutParams(gridLayoutParams);
        gridLayout.setOrientation(GridLayout.HORIZONTAL);
        gridLayout.setColumnCount(colsNum);
        gridLayout.setRowCount(rowsNum);
        gridLayout.setId(0);

        // Programmatically create the buttons layout
        for (int row = 0; row < rowsNum; row++) {
            for (int column = 0; column < colsNum; column++) {

                final MineSweeperButton btn = new MineSweeperButton(this,row,column);
                btn.setBackgroundResource(R.drawable.buttonshape);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       if (!gameManager.gameMove(btn.getRow(),btn.getCol(),getFlagStatus()))


                    }
                });

                gridLayout.addView(btn);
            }
        }

        return gridLayout;
    }
    private void showAllMines(){
        for (int row = 0; row < gameManager.getBoard().getRows(); row++) {
            for (int column = 0; column < gameManager.getBoard().getCols(); column++) {
                if (gameManager.getBoard().getCell(row,column).getValue()==Cell.MINE_VALUE)


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
         public void SetText(String value){
             this.setText(value);
         }
         public void setPressed(boolean flag,boolean mine,int value){
             if (flag)
                 this.setBackgroundResource(R.drawable.buttonflag);
             else if(mine)
                 this.setBackgroundResource(R.drawable.buttonmined);
             else{
                 this.setBackgroundResource(R.drawable.buttonpressed);
                 SetText(value+"");
             }
         }

         public int getRow() {
             return row;
         }

         public int getCol() {
             return col;
         }
     }
