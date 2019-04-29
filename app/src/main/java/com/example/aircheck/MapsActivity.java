package com.example.aircheck;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aircheck.directionhelpers.FetchURL;
import com.example.aircheck.directionhelpers.TaskLoadedCallback;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, TaskLoadedCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    final static String myTAG = "myTAG";
    final static LatLng default_location = new LatLng(13.75398, 100.50144); //Your LatLong
    private GoogleMap mMap;
    private Button btnGetDirection;
    private MarkerOptions place1, place2;
    private Polyline currentPolyline;
    private Marker mMarkderOri = null;
    private Marker mMarkderDest = null;
    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;
    private LocationRequest mLocationRequest;
    private com.google.android.gms.location.LocationListener listener;
    private long UPDATE_INTERVAL = 2 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */
    private TextView tvOri, tvDest;
    private EditText etOri, etDest;

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
        checkPermission();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(default_location, 5));  //move camera to location

    }

    private void setupBotton() {
        btnGetDirection = findViewById(R.id.btn_get_direction);
        etOri = findViewById(R.id.et_ori);
        etDest = findViewById(R.id.et_dest);

        btnGetDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                String url = getUrl(place1.getPosition(), place2.getPosition(), "driving");
//                new FetchURL(MapsActivity.this).execute(url, "driving");
//                mMap.addMarker(place1);
//                mMap.addMarker(place2);
//
//                CameraPosition googlePlex = CameraPosition.builder()
//                        .target(new LatLng(place2.getPosition().latitude, place2.getPosition().longitude))
//                        .zoom(10)
//                        .bearing(0)
//                        .tilt(45)
//                        .build();
//
//                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 5000, null);

                Geocoder geocoder = new Geocoder(MapsActivity.this);
                List<Address> addresses;
                try {
                    String strDest = etDest.getText().toString();
                    if (strDest.length() > 0) {
                        addresses = geocoder.getFromLocationName(strDest, 1);
                        if (addresses.size() > 0) {
                            double latitude = addresses.get(0).getLatitude();
                            double longitude = addresses.get(0).getLongitude();

                            Log.i(myTAG, "geocoding:\t=" + strDest + "(" + latitude + ", " + longitude + ")");

                            if (mMarkderDest != null) {
                                mMarkderDest.remove();
                            }

                            place2 = new MarkerOptions().position(new LatLng(latitude, longitude)).title("Destination");
                            mMarkderOri = mMap.addMarker(place1);
                            mMarkderDest = mMap.addMarker(place2);

                            String url = getUrl(place1.getPosition(), place2.getPosition(), "driving");
                            new FetchURL(MapsActivity.this).execute(url, "driving");

                            CameraPosition googlePlex = CameraPosition.builder()
                                    .target(new LatLng(place2.getPosition().latitude, place2.getPosition().longitude))
                                    .zoom(10)
                                    .bearing(0)
                                    .tilt(0)
                                    .build();

                            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 5000, null);


                        }
                    } else {
                        Toast.makeText(MapsActivity.this, "Enter destination address", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    Log.i(myTAG, "IOException");
                }


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
        if (currentPolyline != null) {
            currentPolyline.remove();
        }
        currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);
    }

    private void checkPermission() {
        if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        startLocationUpdates();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            checkPermission();
        }
        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLocation == null) {
            startLocationUpdates();
        }
        if (mLocation != null) {

            // mLatitudeTextView.setText(String.valueOf(mLocation.getLatitude()));
            //mLongitudeTextView.setText(String.valueOf(mLocation.getLongitude()));
        } else {
            Toast.makeText(this, "Location not Detected Please check GPS!!!", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(myTAG, "Connection Suspended");
        mGoogleApiClient.connect();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(myTAG, "Connection failed. Error: " + connectionResult.getErrorCode());

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    protected void startLocationUpdates() {
        // Create the location request
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);

        // Request location updates
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            checkPermission();
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                mLocationRequest, this);
        Log.d(myTAG, "reque --->>>>");
    }


    @Override
    public void onLocationChanged(Location location) {

        Double lat = location.getLatitude();
        Double lon = location.getLongitude();

        String msg = "Updated Location: " +
                Double.toString(lat) + "," +
                Double.toString(lon);

        if (mMarkderOri != null) {
            mMarkderOri.remove();
        }

        place1 = new MarkerOptions().position(new LatLng(lat, lon)).title("Current");
        mMarkderOri = mMap.addMarker(place1);

//        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        Log.i(myTAG, "MapsActivity: onLocationChanged(): \t" + msg);


    }
}
