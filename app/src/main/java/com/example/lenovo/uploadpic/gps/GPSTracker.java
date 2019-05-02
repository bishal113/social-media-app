package com.example.lenovo.uploadpic.gps;


import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

public class GPSTracker implements com.google.android.gms.location.LocationListener {

    @Override
    public void onLocationChanged(Location location) {

    }
}
