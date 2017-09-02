package com.example.liran.minesweeper.UI;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;

public class SensorService extends Service implements SensorEventListener {

    interface SensorServiceListener {
        void onSensorChanged(float[] values);
    }

    private SensorManager sensorManager;
    private Sensor accelerometerSensor;
    private SensorServiceListener sensorServiceListener;

    @Override
    public void onCreate() {
        super.onCreate();

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    @Override
    public IBinder onBind(Intent intent) {
        sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_UI);

        return new SensorServiceBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // Important to free redundant resources
        setSensorServiceListener(null);
        sensorManager.unregisterListener(this);

        return super.onUnbind(intent);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorServiceListener != null) {
            sensorServiceListener.onSensorChanged(sensorEvent.values);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    public void setSensorServiceListener(SensorServiceListener sensorServiceListener) {
        this.sensorServiceListener = sensorServiceListener;
    }



    class SensorServiceBinder extends Binder {
        void setListener(SensorServiceListener listener) {
            setSensorServiceListener(listener);
        }
    }
}