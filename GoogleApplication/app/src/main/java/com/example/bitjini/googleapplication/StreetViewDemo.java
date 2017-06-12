package com.example.bitjini.googleapplication;

/**
 * Created by bitjini on 2/2/16.
 */

import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.StreetViewPanorama.OnStreetViewPanoramaCameraChangeListener;
import com.google.android.gms.maps.StreetViewPanorama.OnStreetViewPanoramaChangeListener;
import com.google.android.gms.maps.StreetViewPanorama.OnStreetViewPanoramaClickListener;
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.StreetViewPanoramaCamera;
import com.google.android.gms.maps.model.StreetViewPanoramaLocation;
import com.google.android.gms.maps.model.StreetViewPanoramaOrientation;

import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This shows how to listen to some {@link StreetViewPanorama} events.
 */
public class StreetViewDemo extends FragmentActivity
        implements OnStreetViewPanoramaChangeListener, OnStreetViewPanoramaCameraChangeListener,
        OnStreetViewPanoramaClickListener {
//    LatLng(15.81521655, 74.48775772);
    // George St, Sydney
    private static final LatLng SYDNEY = new LatLng(37.765927, -122.449972);

    private StreetViewPanorama mStreetViewPanorama;

    private TextView mPanoChangeTimesTextView;
    private TextView mPanoCameraChangeTextView;
    private TextView mPanoClickTextView;

    private int mPanoChangeTimes = 0;
    private int mPanoCameraChangeTimes = 0;
    private int mPanoClickTimes = 0;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mPanoChangeTimesTextView = (TextView) findViewById(R.id.location);
        mPanoCameraChangeTextView = (TextView) findViewById(R.id.change_camera);
        mPanoClickTextView = (TextView) findViewById(R.id.click_pano);

        final SupportStreetViewPanoramaFragment streetViewPanoramaFragment =
                (SupportStreetViewPanoramaFragment)
                        getSupportFragmentManager().findFragmentById(R.id.map);
        Handler h = new Handler();
        h.postDelayed(new Runnable(){

            @Override
            public void run() {
                mStreetViewPanorama.setPosition(SYDNEY);
                StreetViewPanoramaLocation svpl = mStreetViewPanorama.getLocation();


                if(svpl == null){
                    Toast.makeText(StreetViewDemo.this, "Unable to show Street View at this location", Toast.LENGTH_SHORT).show();
                }
            }

        }, 1000);
        streetViewPanoramaFragment.getStreetViewPanoramaAsync(
                new OnStreetViewPanoramaReadyCallback() {
                    @Override
                    public void onStreetViewPanoramaReady(StreetViewPanorama panorama) {
                        mStreetViewPanorama = panorama;
                        mStreetViewPanorama.setOnStreetViewPanoramaChangeListener(
                                StreetViewDemo.this);
                        mStreetViewPanorama.setOnStreetViewPanoramaCameraChangeListener(
                                StreetViewDemo.this);
                        mStreetViewPanorama.setOnStreetViewPanoramaClickListener(
                                StreetViewDemo.this);


                        // Only set the panorama to SYDNEY on startup (when no panoramas have been
                        // loaded which is when the savedInstanceState is null).
                        if (savedInstanceState == null) {

                            mStreetViewPanorama.setPosition(SYDNEY);
                        }
                    }
                });
    }

    @Override
    public void onStreetViewPanoramaChange(StreetViewPanoramaLocation location) {
           if (location != null && location.links != null) {
                    // location is present
               mPanoChangeTimesTextView.setText("Times panorama changed=" + ++mPanoChangeTimes);
                } else {
                    // location not available
                }
            }

//        if (location != null) {
//            mPanoChangeTimesTextView.setText("Times panorama changed=" + ++mPanoChangeTimes);
//        }
//    }

    @Override
    public void onStreetViewPanoramaCameraChange(StreetViewPanoramaCamera camera) {
        mPanoCameraChangeTextView.setText("Times camera changed=" + ++mPanoCameraChangeTimes);
    }

    @Override
    public void onStreetViewPanoramaClick(StreetViewPanoramaOrientation orientation) {
        Point point = mStreetViewPanorama.orientationToPoint(orientation);
        if (point != null) {
            mPanoClickTimes++;
            mPanoClickTextView.setText(
                    "Times clicked=" + mPanoClickTimes);
            mStreetViewPanorama.animateTo(
                    new StreetViewPanoramaCamera.Builder()
                            .orientation(orientation)
                            .zoom(mStreetViewPanorama.getPanoramaCamera().zoom)
                            .build(), 1000);
        }
    }
}