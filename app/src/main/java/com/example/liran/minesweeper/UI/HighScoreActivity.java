package com.example.liran.minesweeper.UI;

import android.Manifest;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.liran.minesweeper.Logic.*;
import com.example.liran.minesweeper.Logic.LevelConst;
import com.example.liran.minesweeper.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;
import static com.example.liran.minesweeper.UI.GameActivity.gameManager;

//High score table activity
public class HighScoreActivity extends FragmentActivity implements OnMapReadyCallback {
    private ArrayList<HighScore> scores;
    private RadioGroup radioGroup;
    private int checkedButton;
    private TableLayout tl;
    private MapFragment mapFragment;
    private PlayerLocation currentLocation;
    private LocationManager locationManager;
    private GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        setContentView(R.layout.activity_high_score);

        currentLocation = new PlayerLocation();
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, currentLocation);


        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        // load the score table from the shared preferences
        scores = HighScore.load(this);

        tl = (TableLayout) findViewById(R.id.scoretable);
        radioGroup = (RadioGroup) findViewById(R.id.group);

        //default radio button check easy table
        radioGroup.check(R.id.easytable);
        showTable(scores, LevelConst.LEVEL.EASY);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                checkedButton = radioGroup.indexOfChild(findViewById(checkedId));
                switch (checkedButton) {
                    case 0:
                        tl.removeAllViews();
                        showTable(scores, LevelConst.LEVEL.EASY);
                        shoePinsOnMap(LevelConst.LEVEL.EASY);
                        break;
                    case 1:
                        tl.removeAllViews();
                        showTable(scores, LevelConst.LEVEL.MEDIUM);
                        shoePinsOnMap(LevelConst.LEVEL.MEDIUM);

                        break;
                    case 2:
                        tl.removeAllViews();
                        showTable(scores, LevelConst.LEVEL.HARD);
                        shoePinsOnMap(LevelConst.LEVEL.HARD);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void shoePinsOnMap(LevelConst.LEVEL level) {
        int rankVal = 1;
        LatLng current;
        for (HighScore e : scores) {
            /* Create a new row to be added. */
            if (e.getLevel() == level) {
                if (rankVal <= getResources().getInteger(R.integer.table_size)) {
                    current = new LatLng(e.getPlayerLocation().getCurrentLocation().getLatitude(), e.getPlayerLocation().getCurrentLocation().getLongitude());
                    map.addMarker(new MarkerOptions()
                            .title(rankVal+"")
                            .snippet(e.getPlayerLocation().getCurrentLocation().toString())
                            .position(current));
                    rankVal++;
                }
            }

        }
    }

    private void showTable(ArrayList<HighScore> scores, LevelConst.LEVEL level) {
        int rankVal = 1;
        Typeface face;

        // view for headline
        TableRow headlines = new TableRow(this);

        //rank
        ImageView rankHeadline = new ImageView(this);
        rankHeadline.setImageResource(R.drawable.rank2);

        //time
        ImageView timeHeadline = new ImageView(this);
        timeHeadline.setImageResource(R.drawable.time);

        //name
        ImageView nameHeadline = new ImageView(this);
        nameHeadline.setImageResource(R.drawable.name);

        //add the headlines to the view
        headlines.addView(rankHeadline);
        headlines.addView(timeHeadline);
        headlines.addView(nameHeadline);
        tl.addView(headlines, new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));

        //create the table
        for (HighScore e : scores) {
            /* Create a new row to be added. */
            if (e.getLevel() == level) {
                if (rankVal <= getResources().getInteger(R.integer.table_size)) {
                    TableRow tr = new TableRow(this);
                    TextView rank = new TextView(this);
                    TextView time = new TextView(this);
                    TextView name = new TextView(this);

                    // set row text
                    rank.setText((rankVal++) + "");
                    name.setText(e.getPlayerName());
                    time.setText(e.getScore() + "");

                    //change font
                    face = Typeface.createFromAsset(this.getAssets(), getString(R.string.number_font));
                    rank.setTypeface(face);
                    time.setTypeface(face);
                    name.setTypeface(face);

                    rank.setGravity(Gravity.CENTER);
                    time.setGravity(Gravity.CENTER);
                    name.setGravity(Gravity.CENTER);

                    rank.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
                    time.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
                    name.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);

                    rank.setTextSize(getResources().getInteger(R.integer.table_text_size));
                    time.setTextSize(getResources().getInteger(R.integer.table_text_size));
                    name.setTextSize(getResources().getInteger(R.integer.table_text_size));

                    // add row to view
                    tr.addView(rank);
                    tr.addView(time);
                    tr.addView(name);
                    tl.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
                }
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        map.setMyLocationEnabled(true);
        if (currentLocation.getCurrentLocation() != null) {
            permissionCheck = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION);
            LatLng current = new LatLng(currentLocation.getCurrentLocation().getLatitude(), currentLocation.getCurrentLocation().getLongitude());
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(current, 13));
        }


    }
}