//package com.example.bitjini.googleapplication;
//
///**
// * Created by bitjini on 2/2/16.
// */
//
//import android.graphics.Point;
//import android.os.Bundle;
//import android.support.v4.app.FragmentActivity;
//import android.widget.TextView;
//
//import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
//import com.google.android.gms.maps.StreetViewPanorama;
//import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;
//import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.maps.model.Marker;
//import com.google.android.gms.maps.model.MarkerOptions;
//import com.google.android.gms.maps.model.StreetViewPanoramaCamera;
//import com.google.android.gms.maps.model.StreetViewPanoramaLocation;
//import com.google.android.gms.maps.model.StreetViewPanoramaOrientation;
//
///**
// * This shows how to listen to some {@link com.google.android.gms.maps.StreetViewPanorama} events.
// */
//public class Demo extends FragmentActivity
//        implements GoogleMap.OnMarkerDragListener, StreetViewPanorama.OnStreetViewPanoramaChangeListener {
//
//    private StreetViewPanorama svp;
//    private GoogleMap mMap;
//    private Marker marker;
//
//    // George St, Sydney
//    private static final LatLng SYDNEY = new LatLng(-33.87365, 151.20689);
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_maps);
//
//        setUpStreetViewPanoramaIfNeeded(savedInstanceState);
//    }
//
//    private void setUpStreetViewPanoramaIfNeeded(Bundle savedInstanceState) {
//        if (svp == null) {
//            svp = ((SupportStreetViewPanoramaFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getStreetViewPanorama();
//            setUpMap();
//            if (svp != null) {
//                if (savedInstanceState == null) {
//                    svp.setPosition(SYDNEY);
//                }
//                svp.setOnStreetViewPanoramaChangeListener(this);
//            }
//        }
//    }
//
//    @Override
//    public void onStreetViewPanoramaChange(StreetViewPanoramaLocation location) {
//        if (location != null) {
//            marker.setPosition(location.position);
//        }
//    }
//
//    private void setUpMap() {
//        mMap.setOnMarkerDragListener(this);
//        // Creates a draggable marker. Long press to drag.
//        marker = mMap.addMarker(new MarkerOptions()
//                .position(SYDNEY)
//                .draggable(true));
//    }
//
//    @Override
//    public void onMarkerDragStart(Marker marker) {
//    }
//
//    @Override
//    public void onMarkerDragEnd(Marker marker) {
//        svp.setPosition(marker.getPosition(), 150);
//    }
//
//    @Override
//    public void onMarkerDrag(Marker marker) {
//    }
//}