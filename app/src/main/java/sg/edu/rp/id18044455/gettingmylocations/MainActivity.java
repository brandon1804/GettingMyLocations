package sg.edu.rp.id18044455.gettingmylocations;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.File;
import java.io.FileWriter;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;

public class MainActivity extends AppCompatActivity {

    TextView tvLat, tvLong;
    private GoogleMap map;
    Button btnGetLocationUpdate, btnRemoveLocationUpdate, btnCheckRecords;
    LocationRequest mLocationRequest;
    LocationCallback mLocationCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        tvLat = findViewById(R.id.tvLat);
        tvLong = findViewById(R.id.tvLong);

        btnGetLocationUpdate = findViewById(R.id.btnGetLocationUpdate);
        btnRemoveLocationUpdate = findViewById(R.id.btnRemoveLocationUpdate);
        btnCheckRecords = findViewById(R.id.btnCheckRecords);

        FragmentManager fm = getSupportFragmentManager();
        SupportMapFragment mapFragment = (SupportMapFragment)
                fm.findFragmentById(R.id.map);

        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);

        mapFragment.getMapAsync(new OnMapReadyCallback(){
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;

                UiSettings ui = map.getUiSettings();

                ui.setCompassEnabled(true);
                ui.setZoomControlsEnabled(true);


                if(checkPermission()){
                    map.setMyLocationEnabled(true);
                    Task<Location> task = client.getLastLocation();
                    task.addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            String msg;
                            if(location != null){
                                LatLng poi_CurrentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                                tvLat.setText("Latitude: " + location.getLatitude());
                                tvLong.setText("Longitude: " + location.getLongitude());
                                map.moveCamera(CameraUpdateFactory.newLatLngZoom(poi_CurrentLocation,
                                        15));
                                Marker marker = map.addMarker(new
                                        MarkerOptions()
                                        .position(poi_CurrentLocation)
                                        .title("Current Location")
                                        .snippet("Your current location")
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                            }//end of location validation
                        }
                    });


                }//end of permission true
                else{
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
                }


            }
        });


        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(30000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setSmallestDisplacement(500);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    Location data = locationResult.getLastLocation();
                    double lat = data.getLatitude();
                    double lng = data.getLongitude();
                    tvLat.setText("Latitude: " + lat);
                    tvLong.setText("Longitude: " + lng);

                    if (map != null) {
                        LatLng poi_update = new LatLng(lat, lng);
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(poi_update,
                                15));
                    }//end of map validation

                    if(checkReadWritePermission()) {
                        String folderLocation = Environment.getExternalStorageDirectory().getAbsolutePath() + "/PSFolder";
                        File folder = new File(folderLocation);
                        if (folder.exists() == false) {
                            boolean result = folder.mkdir();
                            if (result == true) {
                                Log.d("File Read/Write", "Folder created");
                            }
                        }
                        try {
                            folderLocation= Environment.getExternalStorageDirectory().getAbsolutePath() + "/PSFolder";
                            File targetFile = new File(folderLocation, "records.txt");
                            FileWriter writer = new FileWriter(targetFile, true);
                            writer.write(lat + ", " + lng + "\n");
                            writer.flush();
                            writer.close();
                        }
                        catch (Exception e) {
                            Toast.makeText(MainActivity.this, "Failed to write!", Toast.LENGTH_LONG).show();e.printStackTrace();
                        }
                    }
                    else{
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                    }

                }//end of location validation
            }

        };




        btnGetLocationUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkPermission()){
                    client.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
                }//end of permission true
                else{
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
                }
            }
        });


        btnRemoveLocationUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Task<Void> task = client.removeLocationUpdates(mLocationCallback);
                task.addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(MainActivity.this, "Location updates stopped", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        btnCheckRecords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RecordsActivity.class);
                startActivity(intent);
            }
        });



    }//end of onCreate

    private boolean checkPermission(){
        int permissionCheck_Coarse = ContextCompat.checkSelfPermission(
                MainActivity.this, ACCESS_COARSE_LOCATION);
        int permissionCheck_Fine = ContextCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);


        if (permissionCheck_Coarse == PermissionChecker.PERMISSION_GRANTED
                || permissionCheck_Fine == PermissionChecker.PERMISSION_GRANTED) {

            return true;
        } else {
            return false;
        }
    }//end of checkPermission


    private boolean checkReadWritePermission(){
        int permissionCheck_Write = ContextCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permissionCheck_Read = ContextCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (permissionCheck_Write == PermissionChecker.PERMISSION_GRANTED
                || permissionCheck_Read == PermissionChecker.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }//end of checkPermission


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 0: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(MainActivity.this, "Location access granted",
                            Toast.LENGTH_SHORT).show();
                    btnGetLocationUpdate.performClick();
                } else {
                    // permission denied... notify user
                    Toast.makeText(MainActivity.this, "Location access not granted",
                            Toast.LENGTH_SHORT).show();
                }
            }

        }
    }

}//end of class