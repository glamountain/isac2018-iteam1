package com.isacmapsample.app.isacmapsample;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.isacmapsample.app.isacmapsample.model.Satellite;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {

    LinearLayout mLLSatellites;
    final int REQUEST_PERMISSION = 1000;
    LocationManager locationManager;
    GoogleMap mMap;
    Marker mCurrentLocation;
    List<Marker> mSatelliteMarkers;
    List<Polyline> mLines;
    List<Satellite> mSatellites;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.mapFragment);

        mapFragment.getMapAsync(this);


        if (PermissionChecker.checkSelfPermission(
                MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "You are not allowed.", Toast.LENGTH_SHORT).show();
            requestLocationPermission();
        } else {

    //            LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
    //
    //            Iterable<GpsSatellite> satellites = null;
    //            try {
    //                satellites = lm.getGpsStatus(null).getSatellites();
    //
    //            } catch (NullPointerException e) {
    //                return;
    //            }
    //
    //            int cnt = 0;
    //            for( GpsSatellite sat : satellites ) {
    //                sat.getPrn();
    //                sat.getAzimuth();
    //                sat.getElevation();
    //                sat.getSnr();
    //                cnt++;
    //            }
    //            Toast.makeText(getApplicationContext(), String.valueOf(cnt), Toast.LENGTH_SHORT).show();

        }
        mLLSatellites = findViewById(R.id.llSatellites);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap= googleMap;
        locationStart();

    }

    private void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSION);

        } else {
            Toast toast = Toast.makeText(this,
                    "You need to allow to run this app.", Toast.LENGTH_SHORT);
            toast.show();

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,},
                    REQUEST_PERMISSION);

        }
    }

    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[]permissions, @NonNull int[] grantResults) {
        if (requestCode == 1000) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationStart();

            } else {
                Toast toast = Toast.makeText(this,
                        "This app can not work.", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    private void locationStart(){

        if (mMap == null) return;

        locationManager =
                (LocationManager) getSystemService(LOCATION_SERVICE);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
            mMap.setMyLocationEnabled(true);

            return;
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                1000, 50, this);
        mMap.setMyLocationEnabled(true);

        updateLocation(50.422, 22.084);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        switch (status) {
            case LocationProvider.AVAILABLE:
                Log.d("debug", "LocationProvider.AVAILABLE");
                break;
            case LocationProvider.OUT_OF_SERVICE:
                Log.d("debug", "LocationProvider.OUT_OF_SERVICE");
                break;
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                Log.d("debug", "LocationProvider.TEMPORARILY_UNAVAILABLE");
                break;
        }
    }

    @Override
    public void onLocationChanged(final Location location) {
        updateLocation(location.getLatitude(), location.getLongitude());
    }


    private void updateLocation(final Double latitude, final Double longitude) {

        TextView txtLatitude = findViewById(R.id.txtLatitude);
        String strLatitude = "Latitude:"+latitude;
        txtLatitude.setText(strLatitude);

        TextView txtLongitude = findViewById(R.id.txtLongitude);
        String strLongitude = "Longitude:"+longitude;
        txtLongitude.setText(strLongitude);

        LatLng position = new LatLng(latitude, longitude);
        MarkerOptions markerOptions = new MarkerOptions();

        if (mCurrentLocation == null) {
            markerOptions.position(position);
            markerOptions.title("I'm here");
            mCurrentLocation = mMap.addMarker(markerOptions);
        } else {
            mCurrentLocation.setPosition(position);
        }
        refleshSatelliteView(latitude, longitude);
    }

    private void refleshSatelliteView(final Double latitude, final Double longitude) {
        loadSatellites();

        if (mSatelliteMarkers == null) {
            mSatelliteMarkers = new ArrayList<>();
        } else {
            for (Marker m: mSatelliteMarkers) { m.remove(); }
        }
        for (Satellite s: mSatellites) {
            mSatelliteMarkers.add(mMap.addMarker(new MarkerOptions()
                    .position(s.getPosition())
                    .title(s.getName())));
        }

        if (mLines == null) {
            mLines = new ArrayList<>();
        } else {
            for (Polyline l: mLines) {l.remove();}
        }
        for (Satellite s: mSatellites) {
            mLines.add(mMap.addPolyline(new PolylineOptions()
                    .add(new LatLng(latitude, longitude), s.getPosition())
                    .width(5)
                    .color(Color.RED)));
        }

        if(mLLSatellites.getChildCount() > 0) mLLSatellites.removeAllViews();
        LinearLayout.LayoutParams linearlayoutlayoutparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LayoutInflater inflater = getLayoutInflater();

        for (Satellite s: mSatellites) {
            LinearLayout cdSatellite = (LinearLayout)  inflater.inflate(R.layout.card_sattellite_detail, null);
            TextView txtName = (TextView) cdSatellite.findViewById(R.id.txtName);
            txtName.setText(s.getName());
            TextView txtSatelliteLatitude = (TextView) cdSatellite.findViewById(R.id.txtSatelliteLatitude);
            txtSatelliteLatitude.setText(String.valueOf(s.getPosition().latitude));
            TextView txtSatelliteLongitude = (TextView) cdSatellite.findViewById(R.id.txtSatelliteLongitude);
            txtSatelliteLongitude.setText(String.valueOf(s.getPosition().longitude));
            mLLSatellites.addView(cdSatellite, linearlayoutlayoutparams);
            Toast.makeText(getApplicationContext(), "You are not allowed.", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadSatellites() {
        if (mSatellites == null) {
            mSatellites = new ArrayList<>();
        } else {
            mSatellites.clear();
        }
        mSatellites.add(new Satellite(new LatLng(20, 20), "satellite1"));
        mSatellites.add(new Satellite(new LatLng(30, 30), "satellite2"));
    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
