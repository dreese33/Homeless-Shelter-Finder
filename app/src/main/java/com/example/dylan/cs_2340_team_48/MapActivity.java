package com.example.jake.cs_2340_team_48;

import android.*;
import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ericreese on 4/3/18.
 */

/**
 * Displays the google maps
 */
public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private boolean permissionGranted = false;
    private static final int REQUEST_CODE = 1234;
    private GoogleMap mMap;
    private FusedLocationProviderClient client;
    private ArrayList<Double> latitudes = new ArrayList<>();
    private ArrayList<Double> longitudes = new ArrayList<>();
    private static ArrayList<String> currentKeys = new ArrayList<>();
    private static ArrayList<String> currentValues = new ArrayList<>();
    public static String name = "";
    public static String value = "";
    private int position;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map is ready", Toast.LENGTH_SHORT).show();
        mMap = googleMap;
        fillLatitudeAndLongitudeArrays();
        mMap.setOnInfoWindowClickListener(this);

        Log.d("Location", "Getting");

        if (permissionGranted) {
            getDeviceLocation();
        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        //Toast.makeText(this, "Info window clicked",
                //Toast.LENGTH_SHORT).show();
        for (int i = 0; i < currentKeys.size(); i++) {
            if (currentKeys.get(i).equals(marker.getTitle())) {
                this.position = i;
            }
        }

        name = currentKeys.get(position);
        value = currentValues.get(position);

        currentKeys.clear();
        currentValues.clear();
        Context context = MapActivity.this.getApplicationContext();
        Intent goToPopUpScreen = new Intent(context, Pop.class);
        startActivity(goToPopUpScreen);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity);
        getLocationPermission();
        MainActivity.activeMap = true;
        for (int i = 0; i < WelcomeActivity.keys.size(); i++) {
            if (currentKeys.contains(WelcomeActivity.keys.get(i))) {
                continue;
            }
            currentKeys.add(WelcomeActivity.keys.get(i));
            currentValues.add(WelcomeActivity.values.get(i));
        }
    }

    /**
     * Get permission for location
     */
    private void getLocationPermission() {
        Log.d("Permissions", "Getting");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            permissionGranted = true;
            initMap();
        } else {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionGranted = false;
        switch(requestCode) {
            case REQUEST_CODE:
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            permissionGranted = false;
                            return;
                        }
                    }
                    permissionGranted = true;
                    initMap();
                }
        }
    }

    /**
     * Initializes a map
     */
    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapActivity.this);
        Log.d("initializing map", "initializing");
    }

    /**
     * Obtains devices current location
     */
    private void getDeviceLocation() {
        client = LocationServices.getFusedLocationProviderClient(this);
        try {
            if (permissionGranted) {

                Task location = client.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Location currentLocation = (Location) task.getResult();
                            if (currentLocation != null) {
                                moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), 15f);
                            }
                        } else {
                            Toast.makeText(MapActivity.this, "Unable to get location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Security Error", e.getMessage());
        }
    }

    /**
     * Moves camera to latitude and longitude of location
     * @param latLng Latitude and longitude
     * @param zoom The camera's zoom
     */
    private void moveCamera(LatLng latLng, float zoom) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    /**
     * Puts all addresses in the form of latitudes and longitudes
     */
    private void fillLatitudeAndLongitudeArrays() {
        Geocoder geocoder = new Geocoder(this);
        int index = 0;
        Log.d("Size", Integer.toString(WelcomeActivity.locations.size()));
        boolean focused = false;
        for (String s: WelcomeActivity.locations) {
            try {
                if (WelcomeActivity.keys.get(index).equals("Name")) {
                    index++;
                }
                List<Address> addresses = geocoder.getFromLocationName(s, 1);
                if(addresses.size() > 0) {
                    double latitude = addresses.get(0).getLatitude();
                    double longitude = addresses.get(0).getLongitude();
                    latitudes.add(latitude);
                    longitudes.add(longitude);
                    MarkerOptions options = new MarkerOptions().position(new LatLng(latitude, longitude)).title(WelcomeActivity.keys.get(index));

                    mMap.addMarker(options);
                    if (!focused) {
                        moveCamera(new LatLng(latitude, longitude), 10f);
                        focused = true;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            index++;
        }
    }
}
