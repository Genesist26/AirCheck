package com.example.aircheck;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.aircheck.directionhelpers.FetchURL;
import com.example.aircheck.directionhelpers.TaskLoadedCallback;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, TaskLoadedCallback {

    final static String myTAG = "myTAG";
    private GoogleMap mMap;
    private Button btnGetDirection;
    private MarkerOptions place1, place2;
    Polyline currentPolyline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        place1 = new MarkerOptions().position(new LatLng(13.75398, 100.50144)).title("Location 1");
        place2 = new MarkerOptions().position(new LatLng(14.0208, 100.5250)).title("Location 2");

        setupBotton();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        CameraPosition googlePlex = CameraPosition.builder()
                .target(new LatLng(13.75398,100.50144))
                .zoom(5)
                .bearing(0)
                .tilt(0)
                .build();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 5000, null);
    }

    private void setupBotton(){
        btnGetDirection = findViewById(R.id.btn_get_direction);

        btnGetDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = getUrl(place1.getPosition(), place2.getPosition(), "driving");
                new FetchURL(MapsActivity.this).execute(url, "driving");
                mMap.addMarker(place1);
                mMap.addMarker(place2);

                CameraPosition googlePlex = CameraPosition.builder()
                        .target(new LatLng(place2.getPosition().latitude, place2.getPosition().longitude))
                        .zoom(10)
                        .bearing(0)
                        .tilt(45)
                        .build();

                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 5000, null);
            }
        });
    }

    public String getUrl(LatLng origin, LatLng dest, String directionMode) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.google_maps_key);
        return url;
    }

    @Override
    public void onTaskDone(Object... values) {
        if( currentPolyline != null){
            currentPolyline.remove();
        }
        currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);
    }


}
