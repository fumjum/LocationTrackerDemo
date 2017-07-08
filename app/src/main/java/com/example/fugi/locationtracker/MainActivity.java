package com.example.fugi.locationtracker;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.Manifest;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private FusedLocationProviderClient mFusedLocationClient;
    ArrayList<Location> locationList = new ArrayList<>();
    Button btn;
    TextView tv;
    float distance = 0;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn = (Button) findViewById(R.id.calculate);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocation();
            }
        });

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    public void calculateDistance(Location last, Location current){
        String message="Current Distance Traveled: "+distance;
        float temp = last.distanceTo(current);

        distance += temp;
        tv = (TextView) findViewById(R.id.message);
        tv.setText(message);
    }

    public void getLocation(){
        tv = (TextView)findViewById(R.id.message);
        tv.setText("method called");

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else{
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            tv.setText("Success");
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                locationList.add(location);
                            }
                            if (locationList.size() > 1) {
                                calculateDistance(locationList.get(locationList.size() - 1), locationList.get(locationList.size() - 2));
                            }
                        }
                    });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            getLocation();
        }
    }
}
