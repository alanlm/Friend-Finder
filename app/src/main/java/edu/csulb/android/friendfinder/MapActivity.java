package edu.csulb.android.friendfinder;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;

public class MapActivity extends Activity implements OnMapReadyCallback {

    private MapFragment mapFrag;
    private GoogleMap gMap;
    private UiSettings mapSettings;

    private static final int MY_LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final int LOCATION_LAYER_PERMISSION_REQUEST_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // reference to map fragment
        mapFrag = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFrag.getMapAsync(this); // sets the callback on the fragment

        // ------------------- REQUESTS LOCATION PERMISSION ----------------------------------------
        requestUserPermission(Manifest.permission.ACCESS_FINE_LOCATION, MY_LOCATION_PERMISSION_REQUEST_CODE);
        requestUserPermission(Manifest.permission.ACCESS_FINE_LOCATION, LOCATION_LAYER_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        /* EXAMPLE of adding marker on map
        * googleMap.addMarker(new MarkerOptions()
        *           .position(new LatLng(0, 0))
        *           .title("Marker"));
        */

        gMap = googleMap;

        // initializing Google Map UI settings
        mapSettings = gMap.getUiSettings();
        mapSettings.setZoomControlsEnabled(true);
        mapSettings.setCompassEnabled(true);

        // ---------------- CHECKS IF LOCATION PERMISSION IS GRANTED -------------------------------
        gMap.setMyLocationEnabled(checkPermission(Manifest.permission.ACCESS_FINE_LOCATION));
        mapSettings.setMyLocationButtonEnabled(checkPermission(Manifest.permission.ACCESS_FINE_LOCATION));
    }

    public boolean checkPermission(String permission) {
        // Method returns PackageManager.PERMISSION_GRANTED if app has permission
        // Method returns PERMISSION_DENIED if permission denied
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                permission);
        if(permissionCheck == PackageManager.PERMISSION_GRANTED) {
            Log.d("checkPermission", "permission = " + permission
                    + ", permissionCheck = " + permissionCheck
                    + ", PERMISSION_GRANTED = " + PackageManager.PERMISSION_GRANTED);
            return true;
        }
        else
            return false;
    }

    public void requestUserPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode) {
            case MY_LOCATION_PERMISSION_REQUEST_CODE:
                // if request is cancelled, the results array is empty
                if(grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    gMap.setMyLocationEnabled(checkPermission(permissions[0]));
                else
                    gMap.setMyLocationEnabled(false);
                return;

            case LOCATION_LAYER_PERMISSION_REQUEST_CODE:
                if(grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    mapSettings.setMyLocationButtonEnabled(checkPermission(permissions[0]));
                else
                    mapSettings.setMyLocationButtonEnabled(false);
                return;

            // TODO: include other cases for permissions
        }
    }
}
