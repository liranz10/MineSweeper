package com.example.liran.minesweeper.UI;
import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;
import com.example.liran.minesweeper.Logic.PlayerLocation;
import com.example.liran.minesweeper.Logic.Cell;
import com.example.liran.minesweeper.Logic.GameManager;
import com.example.liran.minesweeper.Logic.LevelConst;
import com.example.liran.minesweeper.R;

import static android.R.id.input;
import static com.example.liran.minesweeper.R.id.grid;

//Game Activity class  - UI for the player (game board, time, mines number, flag on/off, new game button)
public class GameActivity extends AppCompatActivity {
    static GameManager gameManager;
    private MineSweeperButton[] buttons;
    private TextView mineLeft;
    private TextView timeView;
    private Thread timerThread;
    private ImageButton smileButton;
    private LocationManager locationManager;

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
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);



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

    private void setNewGrid(final int colsNum, int rowsNum) {
        buttons = new MineSweeperButton[rowsNum * colsNum];
        ViewGroup.LayoutParams gridLayoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        GridLayout gameGrid = (GridLayout) findViewById(grid);

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
                            gameManager.getHighScore().setPlayerLocation();
                            int permissionCheck = ContextCompat.checkSelfPermission(GameActivity.this,
                                    Manifest.permission.ACCESS_FINE_LOCATION);
                            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, gameManager.getHighScore().getPlayerLocation());

                            if (gameManager.getHighScore().checkHighScore(GameActivity.this)) {
                                //enter name
                                winDialog();
                            }
                        }
                        //show all revealed cells resulted from the click
                        showAllRevealed(colsNum);

                        //update mine left view
                        mineLeft.setText(gameManager.getMineLeft() + "");
                    }
                });
                buttons[row + column * colsNum] = btn;
                gameGrid.addView(btn);
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
                setNewGrid(gameManager.getBoard().getRows(), gameManager.getBoard().getCols());

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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("You Won!");

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

            }
        });
        builder.setNegativeButton(getString(R.string.win_dialog_negative_button), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }


    //Mine Sweeper Button inner class
    class MineSweeperButton extends AppCompatButton {
        private int row;
        private int col;
        private Typeface face;

        public MineSweeperButton(Context context, int row, int col) {
            super(context);
            this.row = row;
            this.col = col;
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
    }

    class PlayerLocation implements LocationListener {

        private Location currentLocation ;


        @Override
        public void onLocationChanged(Location location) {
            currentLocation=location;
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }

        public Location getCurrentLocation() {
            return currentLocation;
        }
    }

}