package com.example.liran.minesweeper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageButton;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import static android.R.attr.value;
import static android.R.id.message;
import static android.R.transition.move;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.V;

public class GameActivity extends AppCompatActivity {
    static GameManager gameManager;
    private GridLayout gameGrid;
    private MineSweeperButton[] buttons;
    private TextView mineLeft;
    private TextView timeView;
    private Thread timerThread;
    private ImageButton smileButton;
    //remove
    private final String TAG = "sapir";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        createNewGrid(gameManager.getBoard().getRows(),gameManager.getBoard().getCols());
        mineLeft = (TextView)findViewById(R.id.mineNum);
        mineLeft.setText(gameManager.getMineLeft()+"");
        timeView=(TextView) findViewById(R.id.time);
        smileButton = (ImageButton)findViewById(R.id.smile);
        newGameButton();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!gameManager.isGameOver())
            tickOnAnonymousThreadForever();

    }

    @Override
    protected void onPause() {
        super.onPause();
        timerThread.interrupt();
    }


    private GridLayout createNewGrid(final int colsNum, int rowsNum) {
        buttons = new  MineSweeperButton[rowsNum*colsNum];
       ViewGroup.LayoutParams gridLayoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        GridLayout gridLayout = (GridLayout)findViewById(R.id.grid);
        gridLayout.removeAllViews();
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
                        boolean removeFlag = gameManager.getBoard().getCell(btn.getRow(),btn.getCol()).isRemoveFlag();
                        btn.setPressed(isFlagged,isMine,removeFlag,cellValue,colsNum);
                        if(isMine) {
                            showAllMines(colsNum);
                            disableButtons(colsNum);
                            timerThread.interrupt();
                            smileButton.setBackgroundResource(R.drawable.gameover);
                        }
                        else if (gameManager.isWinning()){
                            timerThread.interrupt();
                            smileButton.setBackgroundResource(R.drawable.win);
                            winDialog();
                        }
                        showAllRevealed(colsNum);
                        mineLeft.setText(gameManager.getMineLeft()+"");
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
                    buttons[row + column * colsNum].setPressed(false,true,false,Cell.MINE_VALUE,colsNum);
                }
            }
        }
    }

    private void showAllRevealed(int colsNum){
        for (int row = 0; row < gameManager.getBoard().getRows(); row++) {
            for (int column = 0; column < gameManager.getBoard().getCols(); column++) {
                if (!gameManager.getBoard().getCell(row, column).isCovered()) {
                    buttons[row + column * colsNum].setPressed(false,false,false,gameManager.getBoard().getCell(row, column).getValue(),colsNum);
                }
            }
        }
    }
    private boolean getFlagStatus(){
        ToggleButton flagStatus = (ToggleButton)findViewById(R.id.toggleButton);
        return flagStatus.isChecked();
    }

    private void disableButtons(int colsNum){
        for (int row = 0; row < gameManager.getBoard().getRows(); row++) {
            for (int column = 0; column < gameManager.getBoard().getCols(); column++)
                    buttons[row + column * colsNum].setClickable(false);
            }
    }

    private void newGameButton(){

        smileButton.setOnClickListener((new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                LevelConst.LEVEL level = gameManager.getLevel();
                gameManager = new GameManager(level);
                createNewGrid(gameManager.getBoard().getRows(),gameManager.getBoard().getCols());
                mineLeft.setText(gameManager.getMineLeft()+"");
                timerThread.interrupt();
                tickOnAnonymousThreadForever();
                smileButton.setBackgroundResource(R.drawable.happy);
            }
        }));

    }

    private void tickOnAnonymousThreadForever() {
        timerThread = new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                gameManager.getTime().tick();
                                timeView.setText(gameManager.getTime().getTicks() + "");
                            }
                        });
                        Thread.sleep(1000);
                    }
                } catch (InterruptedException e) {
                }
            }
        };
        timerThread.start();
    }

    private void winDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("You Won!");

        // Set up the input
        final EditText input = new EditText(this);
        input.setHint("Enter Name");
        // Specify the type of input expected;
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                gameManager.setHighScore(input.getText().toString(),GameActivity.this);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();

    }




}

     class MineSweeperButton extends AppCompatButton{
         private int row;
         private int col;
         private Typeface face;
         public MineSweeperButton(Context context,int row,int col) {
             super(context);
             this.row=row;
             this.col=col;
             face = Typeface.createFromAsset(context.getAssets(), "fonts/ComingSoon.ttf");
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
             this.setTypeface(face);

         }
         public void setPressed(boolean flag,boolean mine,boolean removeFlag,int value,int cols){
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
             else if(removeFlag){
                 if(cols>5)
                     this.setBackgroundResource(R.drawable.buttonshapesmall);
                 else
                     this.setBackgroundResource(R.drawable.buttonshape);
             }
             else{
                 if(cols>5)
                     this.setBackgroundResource(R.drawable.buttonshapesmallpressed);
                 else
                     this.setBackgroundResource(R.drawable.buttonpressed);

                 if (cols>5)
                     this.setTextSize(14);
                 else
                     this.setTextSize(24);
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
