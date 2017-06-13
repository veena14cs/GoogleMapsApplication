package com.example.bitjini.googleapplication;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.model.LatLng;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by bitjini on 19/1/17.
 */

public class DistanceTimeActivity extends Activity implements View.OnClickListener//,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback<Status>
    {

    private static final float MINIMUM_DISTANCE_CHANGE_FOR_UPDATES = 1.0f; // in Meters
    private static final long MINIMUM_TIME_BETWEEN_UPDATES = 30000;

    Button stopBtn, resumeBtn;
    TextView textViewTotalTimer,textViewWorkOutTimer,txtViewDistInKms,textViewAvgPace;

    int time=0;

    private long startTime = 0L;
    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;

    double speed=0.0;
    double  distanceCovered=0.0;
    double distance=0.0;

    private Handler customWorkoutHandler = new Handler();
    private Handler customTimerHandler = new Handler();
    double lat_new =0,lon_new=0;
    double lat_curr=0 ,lon_curr=0;

    ArrayList<LatLng> arrLocation=new ArrayList<>();
        ArrayList<Location> arrListLocation=new ArrayList<>();
    ArrayList<LatLng> arrPausedLocation=new ArrayList<>();
    Location oldLocation;
    private LocationListener locListener = new MyLocationListener();
    LocationManager locationManager;
    LocationRequest mLocationRequest;

    boolean isPause=true,isResume=false;


    private TextView mDetectedActivityTextView;

    String message="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.distance_time_layout);

        intiViews();
        startTime = SystemClock.uptimeMillis(); // take the current system time

        startTimer();
        startWorkoutTimer();

        initLocationManager();// detect user current location

        showCurrentLocation();

        initListeners();
    }

    private void startTimer() {
        customTimerHandler.postDelayed(updateTimerThread, 0);// start timer
    }
    private void startWorkoutTimer() {
            customWorkoutHandler.postDelayed(updateWorkoutTimerThread, 0);// start timer
    }

    private void intiViews() {
        stopBtn=(Button) findViewById(R.id.stopBtn);
        resumeBtn=(Button) findViewById(R.id.resumeBtn);

        textViewWorkOutTimer=(TextView)findViewById(R.id.workoutTimer);
        textViewTotalTimer=(TextView)findViewById(R.id.Totaltimer);
        txtViewDistInKms=(TextView)findViewById(R.id.distance_km);
        textViewAvgPace=(TextView)findViewById(R.id.avgPace);

        mDetectedActivityTextView=(TextView)findViewById(R.id.userActivity);

    }
    private void initLocationManager() {
        // Getting LocationManager object from System Service LOCATION_SERVICE
        // Create the LocationRequest object
           mLocationRequest = LocationRequest.create();
        // Use high accuracy
        mLocationRequest.setPriority(
                LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                MINIMUM_TIME_BETWEEN_UPDATES,
                MINIMUM_DISTANCE_CHANGE_FOR_UPDATES, locListener
        );
    }
    private void initListeners() {
        stopBtn.setOnClickListener(this);
        resumeBtn.setOnClickListener(this);
    }
    protected void showCurrentLocation() {

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        // Getting LocationManager object from System Service LOCATION_SERVICE
         locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);


        // Creating a criteria object to retrieve provider
        Criteria criteria = new Criteria();

        // Getting the name of the best provider
        String provider = locationManager.getBestProvider(criteria, true);

        // Getting Current Location
        Location location = locationManager.getLastKnownLocation(provider);
        oldLocation=location;
        if (location != null) {

            lat_curr=location.getLatitude();
            lon_curr=location.getLongitude();

            addLocationPointInArray(lat_curr,lon_curr);// add current location point to an array

            message = String.format(
                    "Current Location \n Longitude: %1$s \n Latitude: %2$s",
                    location.getLongitude(), location.getLatitude()
            );
            showToaster(message);
            arrListLocation.add(location);

        } else {
            message= "null location";
            showToaster(message);
            showCurrentLocation();
        }

    }


//
        private void showToaster(String message) {
        Toast.makeText(DistanceTimeActivity.this,message,
                Toast.LENGTH_LONG).show();
    }


        private class MyLocationListener implements LocationListener {

        public void onLocationChanged(Location location) {
            if (location != null) {

                location.getAccuracy();
                Log.e("accuracy=",""+ location.getAccuracy());


                lat_new = location.getLatitude();
                lon_new =location.getLongitude();

               if(!arrListLocation.isEmpty() ){

                   double  dist =  arrListLocation.get(arrListLocation.size()-1).distanceTo(location);// get distance from old to new location
                   message = "entered pause dist="+dist;

                   showToaster(message);
                    if (dist < 5.0) {
                        message = "dist="+dist;
                        showToaster(message);
                            Handler handler=new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                AutoPauseTimer();// after 5 secs
                            }
                        },3000);

                        addPausedLocationPointInArray(lat_new, lon_new);// add each location point to an array
                        return;
                    }
                }

                if(!isPause) {
                    message = "resume called";
                    showToaster(message);
                    AutoResumeTimer();
                }

                // Check if array is not empty, and add distances.
                if (!arrListLocation.isEmpty()) {
                    distance=oldLocation.distanceTo(location);
                    distanceCovered = (distanceCovered + distance); // add distance
                    message = "distance covered " + distanceCovered + "speed= " + speed;
                    speed = (distanceCovered / time) * (18 / 5); // calculate avg speed
                    showToaster(message);
                    message = "distance  " + distance;
                    showToaster(message);
                }
                arrListLocation.add(location);
                addLocationPointInArray(lat_new,lon_new);// add each location point to an array
                oldLocation = location;

                txtViewDistInKms.setText(new DecimalFormat("##.###").format(distanceCovered/1000) + " Kms");
                textViewAvgPace.setText(new DecimalFormat("##.###").format(speed) + "Km/Hr");

                message = String.format(
                        "New Location \n Longitude: %1$s \n Latitude: %2$s",
                        lat_new, lon_new
                );
                showToaster(message);


            }

        }
        public void onStatusChanged(String s, int i, Bundle b) {
          message="Provider status changed";
            showToaster(message);
        }
        public void onProviderDisabled(String s) {
            message="Provider disabled by the user. GPS turned off";
            showToaster(message);
        }
        public void onProviderEnabled(String s) {
            message="Provider enabled by the user. GPS turned on";
            showToaster(message);
        }
    }


    private void addLocationPointInArray(double lat_new, double lon_new) {
        LatLng latLng = new LatLng( lat_new, lon_new);
        arrLocation.add(latLng);

    }
    private void addPausedLocationPointInArray(double lat_new, double lon_new) {
        LatLng latLng = new LatLng( lat_new, lon_new);
        arrPausedLocation.add(latLng);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.stopBtn :
//                timeSwapBuff += timeInMilliseconds;
                customWorkoutHandler.removeCallbacks(updateWorkoutTimerThread);// stop timer
                customTimerHandler.removeCallbacks(updateTimerThread);// stop timer
                resumeBtn.setText("Resume");
                isResume=true;
                isPause=false;
                createIntent();// go to locationActivity
                
                break;

            case R.id.resumeBtn:
                if(isPause)
                {
                    customTimerHandler.removeCallbacks(updateTimerThread);// pause OverAll timer
                    AutoPauseTimer();// pause workout timer

                }else if(isResume)
                {
                    customTimerHandler.postDelayed(updateTimerThread, 0); // resume OverAll timer
                    AutoResumeTimer(); //resume workout timer
                }
                break;
        }
    }

    private void createIntent() {
        Intent intent = new Intent(DistanceTimeActivity.this, LocationActivity.class);

        Bundle b = new Bundle();
        b.putDouble("lat_curr", lat_curr);
        b.putDouble("lon_curr", lon_curr);
        b.putDouble("lat_new", lat_new);
        b.putDouble("lon_new", lon_new);

        b.putDouble("distanceCovered", distanceCovered);
        b.putDouble("speed", speed);

        b.putParcelableArrayList("arrayOfLocation", arrLocation);
        b.putParcelableArrayList("arrPausedLocation", arrPausedLocation);
        intent.putExtras(b);
        startActivity(intent);
    }

    private Runnable updateWorkoutTimerThread = new Runnable() {

        public void run() {
            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
            updatedTime = timeSwapBuff + timeInMilliseconds;
            int secs = (int) (updatedTime / 1000);
            int mins = secs / 60;
            secs = secs % 60;
            time=secs;
            int milliseconds = (int) (updatedTime % 1000);
            textViewWorkOutTimer.setText("" + mins + ":" + String.format("%02d", secs) + ":"
                            + String.format("%03d", milliseconds));
            customWorkoutHandler.postDelayed(this, 0);
        }

    };
    private Runnable updateTimerThread = new Runnable() {

        public void run() {
            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
            updatedTime = timeSwapBuff + timeInMilliseconds;
            int secs = (int) (updatedTime / 1000);
            int mins = secs / 60;
            secs = secs % 60;
            int milliseconds = (int) (updatedTime % 1000);
            textViewTotalTimer.setText("" + mins + ":" + String.format("%02d", secs) + ":"
                    + String.format("%03d", milliseconds));
            customTimerHandler.postDelayed(this, 0);
        }
    };
        private void AutoResumeTimer() {
            message = "Activity Resumed";
            showToaster(message);
            customWorkoutHandler.postDelayed(updateWorkoutTimerThread, 0); // resume timer
            isPause=true;
            isResume=false;
            resumeBtn.setText("Pause");
            mDetectedActivityTextView.setText(message);
//            removeActivityUpdates();
        }

        private void AutoPauseTimer() {
            message = "Activity Paused";
            showToaster(message);
            customWorkoutHandler.removeCallbacks(updateWorkoutTimerThread);// pause timer
            isResume = true;
            isPause = false;
            resumeBtn.setText("Resume");
            mDetectedActivityTextView.setText(message);
        }
    }
