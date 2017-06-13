package com.example.bitjini.googleapplication;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class LocationActivity extends FragmentActivity implements   OnMapReadyCallback {

    ArrayList<LatLng> arrLocation=new ArrayList<>();
    ArrayList<LatLng> arrPausedLocation=new ArrayList<>();

    Location StartLocation,DestinationLocation;
    Polyline line; //added
    TextView tvDistance,tvAvgSpeed;
    Intent intent;
    double speed=0.0;
    double distanceCovered=0.0;
    double lat_new,lon_new ,lat_curr ,lon_curr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dist_time_layout);
        // Getting Google Play availability status
        intiViews();
         intent=getIntent();
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());

        // Add a thin red line from current location to New Location.
        Bundle b = getIntent().getExtras();
         lat_new = b.getDouble("lat_new");
         lon_new = b.getDouble("lon_new");
         lat_curr = b.getDouble("lat_curr");
         lon_curr= b.getDouble("lon_curr");
        arrLocation=b.getParcelableArrayList("arrayOfLocation");
        arrPausedLocation=b.getParcelableArrayList("arrPausedLocation");
        distanceCovered=b.getDouble("distanceCovered");
        speed=b.getDouble("speed");
        // Showing status
        if (status != ConnectionResult.SUCCESS) { // Google Play Services are not available

            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
            dialog.show();

        } else { // Google Play Services are available

            // Getting GoogleMap object from the fragment
            SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map1);

            // Getting Map for the SupportMapFragment
            fm.getMapAsync(this);


        }
    }

    private void intiViews() {
        tvDistance=(TextView)findViewById(R.id.distanceCovered);
        tvAvgSpeed=(TextView) findViewById(R.id.avg_speed);
    }

//    @Override
//    public void onLocationChanged(Location location) {
//
//         tvLocation = (TextView) findViewById(R.id.tv_distance_time);
//
//        double lat = location.getLatitude();
//        double lng = location.getLongitude();
//        LatLng latLng = new LatLng(lat, lng);
//        Toast.makeText(LocationActivity.this,location.getLatitude()+" "+location.getLongitude(),Toast.LENGTH_SHORT).show();
//        startingLocation=location;
//        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//        googleMap.animateCamera(CameraUpdateFactory.zoomTo(14));
//        googleMap.addMarker(new MarkerOptions()
//                .position(new LatLng(lat, lng))
//                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
//                .title("Here"));
//        DistanceClass distanceClass=new DistanceClass();
//        double distance=distanceClass.updateDistance(startingLocation);
//
//        tvLocation.setText("Distance Travelled: " + distance + " metres");
//    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        //DO WHATEVER YOU WANT WITH GOOGLEMAP
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
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
        googleMap.setMyLocationEnabled(true);
        googleMap.setTrafficEnabled(true);
        googleMap.setIndoorEnabled(true);
        googleMap.setBuildingsEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);


        // Enabling MyLocation Layer of Google Map
        googleMap.setMyLocationEnabled(true);

        Log.e(" lat_cur",""+lat_curr);
        Log.e(" lon_curr",""+lon_curr);

        Log.e(" lat_new",""+lat_new);
        Log.e(" lon_new",""+lon_new);
        Log.e(" distance",""+distanceCovered);
        Log.e(" speed",""+speed);


            addPolyLine(googleMap);
            addMarker(googleMap);
            showValuesInTextView();



    }



    private void addPolyLine(GoogleMap googleMap) {

        PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
        for (int z = 0; z < arrLocation.size(); z++) {
            Log.e(" arrayOfLocation",""+arrLocation.get(z));
            LatLng point = arrLocation.get(z);
            options.add(point);
            // move camera to zoom on map
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(arrLocation.get(z)));
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(14));
        }
        line = googleMap.addPolyline(options);

    }
    private void addMarker(GoogleMap googleMap) {

        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(lat_curr, lon_curr))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .title("start"));

        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(lat_new, lon_new))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                .title("destination"));
        for (int i=0;i<arrPausedLocation.size();i++)
        {
            LatLng point = arrPausedLocation.get(i);
            googleMap.addMarker(new MarkerOptions()
                    .position(point)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                    .title("destination"));
        }

    }
    private void showValuesInTextView() {

            tvDistance.setText("Distance Covered = " + new DecimalFormat("##.###").format(distanceCovered/1000) + " kms");
            tvAvgSpeed.setText("Avg Speed = " + new DecimalFormat("##.###").format(speed) + "Km/hr");

    }
}
