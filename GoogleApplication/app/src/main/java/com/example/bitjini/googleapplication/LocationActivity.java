package com.example.bitjini.googleapplication;

import android.app.Dialog;
import android.content.Context;
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

import java.util.ArrayList;
import java.util.List;

public class LocationActivity extends FragmentActivity implements LocationListener, GoogleMap.OnMyLocationChangeListener, OnMapReadyCallback {

    GoogleMap googleMap;
    private boolean drawTrack = true;
    private Polyline route = null;
    private PolylineOptions routeOpts = null;
    private ArrayList<LatLng> points; //added
    Polyline line; //added
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dist_time_layout);
        // Getting Google Play availability status
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());
        points = new ArrayList<LatLng>(); //added
        // Showing status
        if (status != ConnectionResult.SUCCESS) { // Google Play Services are not available

            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
            dialog.show();

        } else { // Google Play Services are available

            // Getting reference to the SupportMapFragment of activity_main.xml
//            SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

            // Getting GoogleMap object from the fragment
            SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map1);

            // Getting Map for the SupportMapFragment
            fm.getMapAsync(this);

            // Enabling MyLocation Layer of Google Map
//            googleMap.setMyLocationEnabled(true);

//            // Getting LocationManager object from System Service LOCATION_SERVICE
//            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
//
//            // Creating a criteria object to retrieve provider
//            Criteria criteria = new Criteria();
//
//            // Getting the name of the best provider
//            String provider = locationManager.getBestProvider(criteria, true);
//
//            // Getting Current Location
//            Location location = locationManager.getLastKnownLocation(provider);
//
//            if (location != null) {
//                onLocationChanged(location);
//                startTracking();
//                System.out.println("Latitude:" + location.getLatitude());
//                System.out.println("Longitude:" + location.getLongitude());
//
//            }
//            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                // TODO: Consider calling
//                //    ActivityCompat#requestPermissions
//                // here to request the missing permissions, and then overriding
//                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                //                                          int[] grantResults)
//                // to handle the case where the user grants the permission. See the documentation
//                // for ActivityCompat#requestPermissions for more details.
//                return;
//            }
//            locationManager.requestLocationUpdates(provider, 20000, 0, this); //The first number here is the timespan in milliseconds in which we want want to receive the updates,
//            // the second one is the distance in meters that the user has to move before we get them.
//            stopTracking();
        }
    }

    private void startTracking(GoogleMap googleMap) {
        if (googleMap != null) {
            routeOpts = new PolylineOptions()
                    .color(Color.BLUE)
                    .width(5 /* TODO: respect density! */)
                    .geodesic(true);
            route = googleMap.addPolyline(routeOpts);
            route.setVisible(drawTrack);

            googleMap.setOnMyLocationChangeListener(this);
        }

    }

    private void stopTracking(GoogleMap googleMap) {
        if (googleMap != null)
            googleMap.setOnMyLocationChangeListener(null);

        if (route != null)
            route.remove();
        route = null;
        routeOpts = null;
    }

//


    public void onMyLocationChange(Location location) {
        if (routeOpts != null) {
            LatLng myLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            List<LatLng> points = route.getPoints();
            points.add(myLatLng);
            route.setPoints(points);
        }
    }

    @Override
    public void onLocationChanged(Location location) {

        TextView tvLocation = (TextView) findViewById(R.id.tv_distance_time);

        double lat = location.getLatitude();
        double lng = location.getLongitude();
        LatLng latLng = new LatLng(lat, lng);

        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(14));
        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(lat, lng))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .title("Here"));
//        // Getting latitude of the current location
//        double latitude = location.getLatitude();
//
//        // Getting longitude of the current location
//        double longitude = location.getLongitude();
//
//
//        // Creating a LatLng object for the current location
//        LatLng latLng = new LatLng(latitude, longitude);
//
//        curLoc = location;
//        Marker marker;
//        LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
//        marker = googleMap.addMarker(new MarkerOptions().position(loc));
//        if(googleMap != null){
//            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 16.0f));
//        }
//        Marker marker = googleMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude()))
//                .title("Hello Maps"));
//        marker.showInfoWindow();
        // Showing the current location in Google Map
//        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//
//        // Zoom in the Google Map
//        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        // Setting latitude and longitude in the TextView tv_location
        tvLocation.setText("Latitude:" + lat + ", Longitude:" + lng);

    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
    }


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

            // Getting LocationManager object from System Service LOCATION_SERVICE
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            // Creating a criteria object to retrieve provider
            Criteria criteria = new Criteria();

            // Getting the name of the best provider
            String provider = locationManager.getBestProvider(criteria, true);

            // Getting Current Location
            Location location = locationManager.getLastKnownLocation(provider);

            if (location != null) {
                onLocationChanged(location);
                startTracking(googleMap);
                Toast.makeText(LocationActivity.this,location.getLatitude()+" "+location.getLongitude(),Toast.LENGTH_SHORT).show();
                System.out.println("Latitude:" + location.getLatitude());
                System.out.println("Longitude:" + location.getLongitude());

            }
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
            locationManager.requestLocationUpdates(provider, 20000, 0, this); //The first number here is the timespan in milliseconds in which we want want to receive the updates,
//            // the second one is the distance in meters that the user has to move before we get them.
            stopTracking(googleMap);
    }
}