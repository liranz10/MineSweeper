package com.example.liran.minesweeper.Logic;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

public class PlayerLocation implements LocationListener {

    private Location currentLocation;

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