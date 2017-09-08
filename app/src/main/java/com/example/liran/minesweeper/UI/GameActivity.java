package com.example.liran.minesweeper.UI;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.liran.minesweeper.Logic.Cell;
import com.example.liran.minesweeper.Logic.GameManager;
import com.example.liran.minesweeper.Logic.LevelConst;
import com.example.liran.minesweeper.Logic.PlayerLocation;
import com.example.liran.minesweeper.R;

import java.util.Arrays;

import tyrantgit.explosionfield.ExplosionField;

import static android.R.transition.explode;
import static com.example.liran.minesweeper.R.id.grid;

//Game Activity class  - UI for the player (game board, time, mines number, flag on/off, new game button)
public class GameActivity extends AppCompatActivity implements SensorService.SensorServiceListener {
    static GameManager gameManager;
    private MineSweeperButton[] buttons;
    private TextView mineLeft;
    private TextView timeView;
    private Thread timerThread;
    private ImageButton smileButton;
    private float[] startValues;
    private PlayerLocation playerLocation;
    private GridLayout gameGrid;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            if (iBinder instanceof SensorService.SensorServiceBinder) {
                SensorService.SensorServiceBinder sensorServiceBinder = (SensorService.SensorServiceBinder) iBinder;
                sensorServiceBinder.setListener(GameActivity.this);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

        setContentView(R.layout.activity_game);

        //create new grid layout for the game grid
        setNewGrid(gameManager.getBoard().getRows(), gameManager.getBoard().getCols());

        //mine left view
        mineLeft = (TextView) findViewById(R.id.mineNum);
        mineLeft.setText(gameManager.getMineLeft() + "");

        //time view
        timeView = (TextView) findViewById(R.id.time);

        //new game button
        smileButton = (ImageButton) findViewById(R.id.smile);
        newGameButton();

    }

    @Override
    protected void onResume() {
        super.onResume();

        //checking if the game is not over on resume and starts timer
        if (!gameManager.isGameOver())
            tickOnTimerThreadForever();
    }

    @Override
    protected void onPause() {
        super.onPause();

        //stop timer
        timerThread.interrupt();
    }

    @Override
    protected void onStart() {
        super.onStart();
        playerLocation=new PlayerLocation(this);
        bindService(new Intent(this ,SensorService.class), serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(serviceConnection);
        playerLocation.removeUpdates();
    }

    private void setNewGrid(final int colsNum, int rowsNum) {
        buttons = new MineSweeperButton[rowsNum * colsNum];
        ViewGroup.LayoutParams gridLayoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
         gameGrid = (GridLayout) findViewById(grid);

        //set new grid params
        gameGrid.removeAllViews();
        gameGrid.setColumnCount(colsNum);
        gameGrid.setRowCount(rowsNum);
        // Programmatically create the buttons layout
        for (int row = 0; row < rowsNum; row++) {
            for (int column = 0; column < colsNum; column++) {
                final MineSweeperButton btn = new MineSweeperButton(this, row, column);
                //small buttons for easy and medium level
                if (rowsNum > LevelConst.HARD_ROWS) {
                    btn.setLayoutParams(new LinearLayout.LayoutParams(getResources().getInteger(R.integer.button_small_size), getResources().getInteger(R.integer.button_small_size)));
                    btn.setBackgroundResource(R.drawable.buttonshapesmall);
                } else {
                    btn.setLayoutParams(new LinearLayout.LayoutParams(getResources().getInteger(R.integer.button_big_size), getResources().getInteger(R.integer.button_big_size)));
                    btn.setBackgroundResource(R.drawable.buttonshape);
                }

                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // gets all status changes resolved by the game move
                        boolean isMine = !gameManager.gameMove(btn.getRow(), btn.getCol(), getFlagStatus());
                        boolean isFlagged = gameManager.getBoard().getCell(btn.getRow(), btn.getCol()).isFlagged();
                        int cellValue = gameManager.getBoard().getCell(btn.getRow(), btn.getCol()).getValue();
                        boolean removeFlag = gameManager.getBoard().getCell(btn.getRow(), btn.getCol()).isRemoveFlag();

                        //view the changes on the game button
                        btn.setPressed(isFlagged, isMine, removeFlag, cellValue, colsNum);
                        if (isMine) {
                            showAllMines(colsNum);
                            disableButtons(colsNum);

                            //stop timer
                            timerThread.interrupt();

                            //sets new game button to game over image
                            smileButton.setBackgroundResource(R.drawable.gameover);
                        } else if (gameManager.isWinning()) {
                            //stop timer
                            timerThread.interrupt();

                            //sets new game button to game win image
                            smileButton.setBackgroundResource(R.drawable.win);
                            disableButtons(colsNum);




                            gameManager.setHighScore(GameActivity.this);
                            gameManager.getHighScore().setPlayerLocation(playerLocation.getCurrentLocation());

                            if (gameManager.getHighScore().checkHighScore(GameActivity.this)) {
                                //enter name
                                winDialog();
                            }

                        }
                        else if (gameManager.isAllBoardIsMined()){
                            showAllMines(colsNum);
                            disableButtons(colsNum);
                        }
                        //show all revealed cells resulted from the click
                        showAllRevealed(colsNum);

                        //update mine left view
                        mineLeft.setText(gameManager.getMineLeft() + "");
                    }
                });
                buttons[row + column * colsNum] = btn;
                gameGrid.addView(btn);
                gameGrid.showContextMenu();
            }
        }
    }

    //view all mines to player if player pressed mined cell
    private void showAllMines(int colsNum) {
        for (int row = 0; row < gameManager.getBoard().getRows(); row++) {
            for (int column = 0; column < gameManager.getBoard().getCols(); column++) {
                if (gameManager.getBoard().checkMine(row, column)) {
                    buttons[row + column * colsNum].setPressed(false, true, false, Cell.MINE_VALUE, colsNum);
                }
            }
        }

            ExplosionField explosionField = ExplosionField.attach2Window(this);
            for (int i =0 ; i < buttons.length ; i++)
                    explosionField.explode(buttons[i]);

    }



    //view all revealed cells resulted from the last game move
    private void showAllRevealed(int colsNum) {
        for (int row = 0; row < gameManager.getBoard().getRows(); row++) {
            for (int column = 0; column < gameManager.getBoard().getCols(); column++) {
                if (!gameManager.getBoard().getCell(row, column).isCovered()) {
                    buttons[row + column * colsNum].setPressed(false, false, false, gameManager.getBoard().getCell(row, column).getValue(), colsNum);
                }
            }
        }
    }

    //get toggle button status - flag on/off
    private boolean getFlagStatus() {
        ToggleButton flagStatus = (ToggleButton) findViewById(R.id.toggleButton);
        return flagStatus.isChecked();
    }

    //disable al game grid buttons
    private void disableButtons(int colsNum) {
        for (int row = 0; row < gameManager.getBoard().getRows(); row++) {
            for (int column = 0; column < gameManager.getBoard().getCols(); column++)
                buttons[row + column * colsNum].setClickable(false);
        }
    }

    //start new game button on click
    private void newGameButton() {
        smileButton.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //gets current game level
                LevelConst.LEVEL level = gameManager.getLevel();

                // starts new single game logic
                gameManager = new GameManager(level);

                //restart grid

                setNewGrid(gameManager.getBoard().getCols(),gameManager.getBoard().getRows());

                //restart mine left view
                mineLeft.setText(gameManager.getMineLeft() + "");

                //stop and start new timer
                timerThread.interrupt();
                tickOnTimerThreadForever();

                //set new game button back to default
                smileButton.setBackgroundResource(R.drawable.happy);
            }
        }));
    }

    // Create new timer in background thread
    private void tickOnTimerThreadForever() {
        timerThread = new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // increase ticks
                                gameManager.getTime().tick();
                                timeView.setText(gameManager.getTime().getTicks() + "");
                            }
                        });
                        // delay for 1 second
                        Thread.sleep(getResources().getInteger(R.integer.one_second));
                    }
                } catch (InterruptedException e) {
                }
            }
        };
        timerThread.start();
    }

    //presents a win dialog to the player - enter name
    private void winDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("You Won!");
        final Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(GameActivity.this, R.anim.hyperspace_jump);

        // Set up the input
        final EditText input = new EditText(this);
        input.setHint(getString(R.string.win_dialog_hint));

        // Specify the type of input expected;
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton(getString(R.string.win_dialog_positive_button), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                gameManager.getHighScore().setPlayerName(input.getText().toString());
                gameManager.getHighScore().save(GameActivity.this);
                for (int i=0 ; i < buttons.length ; i++)
                    buttons[i].startAnimation(hyperspaceJumpAnimation);

            }
        });
        builder.setNegativeButton(getString(R.string.win_dialog_negative_button), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                for (int i=0 ; i < buttons.length ; i++)
                    buttons[i].startAnimation(hyperspaceJumpAnimation);
            }
        });

        builder.show();

    }

    @Override
    public void onSensorChanged(float[] values) {
        if(startValues==null) {
            startValues= new float[3];
            startValues[0] = values[0];
            startValues[1] = values[1];
            startValues[2] = values[2];
        }
        if (gameManager.isWinning()){
            return;
        }

        float x=Math.abs(values[0]);
        float y=Math.abs(values[1]);
        float z=Math.abs(values[2]);
        float sx=Math.abs(startValues[0]);
        float sy=Math.abs(startValues[1]);
        float sz=Math.abs(startValues[2]);
        if(x > sx+8 || y > sy+8 || z > sz+8) // check const accuracy
        {

            gameManager.addMineToGame();
            mineLeft.setText(gameManager.getMineLeft()+"");
            gameManager.updateBoard();
            updateGrid();

            if(gameManager.isAllBoardIsMined()) {
                showAllMines(gameManager.getBoard().getCols());
                disableButtons(gameManager.getBoard().getCols());
                //stop timer
                timerThread.interrupt();
                //sets new game button to game over image
                smileButton.setBackgroundResource(R.drawable.gameover);
            }
        }
        else
            mineLeft.setText(gameManager.getMineLeft()+"");
    }


private void updateGrid() {

    for (int i = 0; i < buttons.length; i++)
        if (gameManager.getBoard().getRows() > LevelConst.HARD_ROWS) {
            if (buttons[i].isFlag()) {
                buttons[i].setBackgroundResource(R.drawable.buttonshapesmallflag);
            } else {
                buttons[i].setBackgroundResource(R.drawable.buttonshapesmall);
                buttons[i].setText("");
            }
        }
        else {

            if (buttons[i].isFlag()) {
                buttons[i].setBackgroundResource(R.drawable.buttonflag);

            } else {
                buttons[i].setBackgroundResource(R.drawable.buttonshape);
                buttons[i].setText("");
            }
        }
}
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    //Mine Sweeper Button inner class
    class MineSweeperButton extends AppCompatButton {
        private int row;
        private int col;
        private Typeface face;
        private boolean flag;

        public MineSweeperButton(Context context, int row, int col) {
            super(context);
            this.row = row;
            this.col = col;
            this.flag=false;
            face = Typeface.createFromAsset(context.getAssets(), context.getString(R.string.number_font));
        }

        //set color by value
        public void setButtonText(int value) {
            switch (value) {
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
                this.setText(value + "");

            //set font style
            this.setTypeface(face);
        }

        //pressed button - changes the button view by the flags and params
        //changes button status according to the grid(level) size
        public void setPressed(boolean flag, boolean mine, boolean removeFlag, int value, int cols) {
            //flagged button
            if (flag) {
                this.flag=true;
                if (cols > LevelConst.HARD_ROWS)
                    this.setBackgroundResource(R.drawable.buttonshapesmallflag);
                else
                    this.setBackgroundResource(R.drawable.buttonflag);
            }

            //mined button
            else if (mine) {
                if (cols > LevelConst.HARD_ROWS)
                    this.setBackgroundResource(R.drawable.buttonshapesmallmined);
                else
                    this.setBackgroundResource(R.drawable.buttonmined);
            }

            //remove flag from button
            else if (removeFlag) {
                this.flag=false;
                if (cols > LevelConst.HARD_ROWS)
                    this.setBackgroundResource(R.drawable.buttonshapesmall);
                else
                    this.setBackgroundResource(R.drawable.buttonshape);
            }

            //reveal button
            else {
                if (cols > LevelConst.HARD_ROWS)
                    this.setBackgroundResource(R.drawable.buttonshapesmallpressed);
                else
                    this.setBackgroundResource(R.drawable.buttonpressed);

                if (cols > LevelConst.HARD_ROWS)
                    this.setTextSize(this.getContext().getResources().getInteger(R.integer.button_small_font_size));
                else
                    this.setTextSize(this.getContext().getResources().getInteger(R.integer.button_big_font_size));

                setButtonText(value);
            }
        }

        public int getRow() {
            return row;
        }

        public int getCol() {
            return col;
        }

        public boolean isFlag() {
            return flag;
        }
    }


}