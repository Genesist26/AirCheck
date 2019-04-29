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
import android.view.inputmethod.InputMethodManager;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.ui.IconGenerator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
    private ArrayList<Marker> markerList = new ArrayList<>();

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

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {

                Double lat = point.latitude;
                Double lng = point.longitude;

                if (mMarkderDest != null) {
                    mMarkderDest.remove();
                }

                place2 = new MarkerOptions().position(point).title("Destination");
                mMarkderDest = mMap.addMarker(place2);

                Geocoder geocoder;
                List<Address> addresses;
                geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());

                try {
                    addresses = geocoder.getFromLocation(lat, lng, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                    String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    String city = addresses.get(0).getLocality();
                    String state = addresses.get(0).getAdminArea();
                    String country = addresses.get(0).getCountryName();
                    String postalCode = addresses.get(0).getPostalCode();
                    String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL

                    Log.i(myTAG, "address: " + address);
                    etDest.setText(address);

                } catch (IOException e) {
                    e.printStackTrace();
                    Log.i(myTAG, "Can't get address by latlon");
                }


            }
        });

    }

    private void setupBotton() {
        btnGetDirection = findViewById(R.id.btn_get_direction);
        etOri = findViewById(R.id.et_ori);
        etDest = findViewById(R.id.et_dest);

        btnGetDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Geocoder geocoder = new Geocoder(MapsActivity.this);
                List<Address> addresses;
                try {
                    String strDest = etDest.getText().toString();
                    if (strDest.length() > 0) {

                        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        if (imm.isAcceptingText()) {
                            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                        }

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
                            FetchURL mFetchURL = new FetchURL(MapsActivity.this);
                            mFetchURL.execute(url, "driving");


                            Log.i(myTAG, "getUrl:\t" + url);

                            CameraPosition googlePlex = CameraPosition.builder()
                                    .target(new LatLng(place2.getPosition().latitude, place2.getPosition().longitude))
                                    .zoom(10)
                                    .bearing(0)
                                    .tilt(0)
                                    .build();

                            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 5000, null);


//                            try {
//                                Object x = mFetchURL.getPmPoint();
//                                Log.i(myTAG, "success mFetchURL.getPmPoint()");
//
//                            }catch (Exception e){
//                                Log.i(myTAG, "Exceiton mFetchURL.getPmPoint(): "+e.toString());
//
//                            }

                            // icon
//                            IconGenerator iconFactory = new IconGenerator(MapsActivity.this);
//                            MarkerOptions markerOptions = new MarkerOptions().
//                                    icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon("80"))).
//                                    position(new LatLng(-33.8696, 151.2094)).
//                                    anchor(iconFactory.getAnchorU(), iconFactory.getAnchorV());
//
//                            mMap.addMarker(markerOptions);

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
        markPmPoint((ArrayList<LatLng>) values[1]);
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

    public void markPmPoint(ArrayList<LatLng> list) {
        Log.i(myTAG, "markPmPoint() list size = " + list.size());
        MarkerOptions mMarkerOption;

        // icon
        IconGenerator iconFactory = new IconGenerator(MapsActivity.this);
//
        if(! markerList.isEmpty()){
            for (int i = 0; i < markerList.size(); i++) {
                Marker mMarker = markerList.get(i);
                mMarker.remove();
            }
        }

        for (int i = 0; i < list.size(); i++) {
            LatLng mLatlng = list.get(i);
            double lat = mLatlng.latitude;
            double lng = mLatlng.longitude;
            Log.i(myTAG, "mLatlng[" + i + "] = (" + lat + ", " + lng + ")");

            mMarkerOption = new MarkerOptions().
                    icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon("80"))).
                    position(new LatLng(lat, lng)).
                    anchor(iconFactory.getAnchorU(), iconFactory.getAnchorV());

            markerList.add(mMap.addMarker(mMarkerOption));

        }

    }
}
