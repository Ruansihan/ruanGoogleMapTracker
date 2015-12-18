package com.ruansihan.ruangooglemaptracker;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    public GoogleMap googleMap;
    double latitude;
    double longitude;
    private double pLati, plongi, dLati, dlongi;
    LatLng PRE,DES;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            drawMarker(intent);
            drawMap(intent);

            /**
             * Bundle bundle = intent.getExtras();

             if (bundle != null) {

             latitude = bundle.getDouble("lati");

             longitude = bundle.getDouble("longi");
             drawmap(latitude, longitude);
             }
             */

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Intent i = new Intent(MapsActivity.this, LocationServiceManager.class);
        startService(i);

        Intent intent = new Intent("SystemState");
        sendBroadcast(intent);

        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());

        // Showing status
        if (status != ConnectionResult.SUCCESS) { // Google Play Services are
            // not available
            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this,
                    requestCode);
            dialog.show();

        } else { // Google Play Services are available

            // Getting reference to the SupportMapFragment of activity_main.xml
            SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);

            // Getting GoogleMap object from the fragment
            googleMap = fm.getMap();

            // Enabling MyLocation Layer of Google Map
            googleMap.setMyLocationEnabled(true);
            // Getting LocationManager object from System Service

            // LOCATION_SERVICE
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            // Creating a criteria object to retrieve provider
            Criteria criteria = new Criteria();

            // Getting the name of the best provider
            String provider = locationManager.getBestProvider(criteria, true);

            // Getting Current Location
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            Location location = locationManager.getLastKnownLocation(provider);
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(18));

        }
    }


    @SuppressLint("ShowToast")
    private void drawMarker(Intent i) {

        double lat;
        double lng;
        try {
            lat = i.getDoubleExtra("lat", 0);
            lng = i.getDoubleExtra("lng", 0);

            if (lat != 0 && lng != 0) {
                LatLng ll = new LatLng(lat, lng);
                googleMap.addMarker(new MarkerOptions().position(ll));
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(ll));
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            return;
        }
    }

    public void drawMap(Intent intent){

        double lat;
        double lng;

        lat = intent.getDoubleExtra("lat", 0);
        lng = intent.getDoubleExtra("lng", 0);

        if(PRE==null){
            PRE = new LatLng(lat, lng);
        }else{
            DES = new LatLng(lat, lng);
            PolylineOptions polylineOptions = new PolylineOptions().add(PRE).add(DES).width(5).color(Color.BLUE);
            googleMap.addPolyline(polylineOptions);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(DES,18));
            PRE = DES;
        }

/**
 *  String str = "lat: "+lat+" lng: "+lng+"/n";

 if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
 File path = Environment.getExternalStorageDirectory();
 try {
 File targetFile = new File(path.getCanonicalPath()+"/iodoc.txt");

 RandomAccessFile randomAccessFile = new RandomAccessFile(targetFile,"rw");

 randomAccessFile.seek(targetFile.length());

 randomAccessFile.write(str.getBytes());

 randomAccessFile.close();
 } catch (IOException e) {
 e.printStackTrace();
 }
 }
 */

    }


    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, new IntentFilter(LocationServiceManager.TAG));

        //registerReceiver(broadcastReceiver, new IntentFilter("com.ruansihan.ruangooglemaptracker"));
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }

}
