package com.example.liran.minesweeper.UI;

import android.Manifest;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.liran.minesweeper.Logic.*;
import com.example.liran.minesweeper.Logic.LevelConst;
import com.example.liran.minesweeper.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.internal.IGoogleMapDelegate;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static android.R.attr.level;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;
import static com.example.liran.minesweeper.R.id.map;
import static com.example.liran.minesweeper.UI.GameActivity.gameManager;

//High score table activity
public class HighScoreActivity extends FragmentActivity {
    private ArrayList<HighScore> scores;
    private RadioGroup radioGroup;
    private int checkedButton;
    private TableLayout tl;
    private MapFragment mapFragment;
    private PlayerLocation currentLocation;
    private GoogleMap map;
//    private int check;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        currentLocation = new PlayerLocation(this);

        setContentView(R.layout.activity_high_score);
        if (isGoogleMapsInstalled()) {
            MapFragment mapFragment = (MapFragment) getFragmentManager()
                    .findFragmentById(R.id.map);

            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    setGoogleMap(googleMap);
                    radioGroup.check(R.id.easytable);
                    showPinsOnMap(LevelConst.LEVEL.EASY);
                }
            });
        }
         else {
        // Notify the user he should install GoogleMaps (after installing Google Play Services)
        FrameLayout mapsPlaceHolder = (FrameLayout) findViewById(R.id.mapPlaceHolder);
        TextView errorMessageTextView = new TextView(getApplicationContext());
        errorMessageTextView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER));
        errorMessageTextView.setText(R.string.missing_google_maps_error_message);
        errorMessageTextView.setTextColor(Color.RED);
        mapsPlaceHolder.addView(errorMessageTextView);
    }




        // load the score table from the shared preferences
        scores = HighScore.load(this);

        tl = (TableLayout) findViewById(R.id.scoretable);
        radioGroup = (RadioGroup) findViewById(R.id.group);
        //default radio button check easy table


        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                checkedButton = radioGroup.indexOfChild(findViewById(checkedId));
                switch (checkedButton) {
                    case 0:
                        tl.removeAllViews();
                        map.clear();
                        showTable(scores, LevelConst.LEVEL.EASY);
                        showPinsOnMap(LevelConst.LEVEL.EASY);
                        break;
                    case 1:
                        tl.removeAllViews();
                        map.clear();
                        showTable(scores, LevelConst.LEVEL.MEDIUM);
                        showPinsOnMap(LevelConst.LEVEL.MEDIUM);
                        break;
                    case 2:
                        tl.removeAllViews();
                        map.clear();
                        showTable(scores, LevelConst.LEVEL.HARD);
                        showPinsOnMap(LevelConst.LEVEL.HARD);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        currentLocation.removeUpdates();
    }

        private void showPinsOnMap(LevelConst.LEVEL level) {
            try {


                int rankVal = 1;
                LatLng current;
                for (HighScore e : scores) {
            /* Create a new row to be added. */
                    if (e.getLevel() == level) {
                        if (rankVal <= getResources().getInteger(R.integer.table_size)) {
                            if (e.getPlayerLocation() != null) {
                                current = new LatLng(e.getPlayerLocation().getLatitude(), e.getPlayerLocation().getLongitude());
                                map.addMarker(new MarkerOptions()
                                        .title("Rank: " + rankVal + " Time: " + e.getScore() + " Name: " + e.getPlayerName())
                                        .snippet(getStreetName(e.getPlayerLocation()))
                                        .position(current)).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.trophy));
                            }
                            rankVal++;

                        }
                    }

                }
            }catch (NullPointerException e){
                return;
            }
    }

    private void showTable(ArrayList<HighScore> scores, LevelConst.LEVEL level) {
        int rankVal = 1;
        Typeface face;

//        // view for headline
//        TableRow headlines = new TableRow(this);
//
//        //rank
//        ImageView rankHeadline = new ImageView(this);
//        rankHeadline.setImageResource(R.drawable.rank2);
//
//        //time
//        ImageView timeHeadline = new ImageView(this);
//        timeHeadline.setImageResource(R.drawable.time);
//
//        //name
//        ImageView nameHeadline = new ImageView(this);
//        nameHeadline.setImageResource(R.drawable.name);
//
//        //add the headlines to the view
//        headlines.addView(rankHeadline);
//        headlines.addView(timeHeadline);
//        headlines.addView(nameHeadline);
//        tl.addView(headlines, new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));

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






    public boolean isGoogleMapsInstalled() {
        try {
            ApplicationInfo info = getPackageManager().getApplicationInfo("com.google.android.apps.maps", 0);
            return info != null;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public void setGoogleMap(GoogleMap googleMap) {
        this.map = googleMap;
        boolean isAllowedToUseLocation = hasPermissionForLocationServices();
        if (isAllowedToUseLocation) {
            try {
                googleMap.setMyLocationEnabled(true);
                LatLng current = new LatLng(currentLocation.getCurrentLocation().getLatitude(), currentLocation.getCurrentLocation().getLongitude());
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(current, 15f));

            } catch (SecurityException exception) {
                Toast.makeText(this, "Error getting location", Toast.LENGTH_LONG).show();
            }
            catch (NullPointerException exception) {
                Toast.makeText(this, "Error getting location", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "Location is blocked in this app", Toast.LENGTH_LONG).show();
        }
    }

    public boolean hasPermissionForLocationServices() {
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            // Because the user's permissions started only from Android M and on...
            return true;
        }

        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // The user blocked the location services of THIS app
            return false;
        }

        return true;
    }

    private String getStreetName(Location location){
        Geocoder geoCoder = new Geocoder(this);

        List<Address> matches = null;
        try {
            matches = geoCoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            Address bestMatch = (matches.isEmpty() ? null : matches.get(0));
            return bestMatch.getAddressLine(0);
        } catch (IOException e) {
           return "Cant Find Street Name";
        }
        catch (NullPointerException e){
            return "Cant Find Street Name";
        }

    }

}