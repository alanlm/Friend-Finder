package edu.csulb.android.friendfinder;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;

public class MapActivity extends Activity implements OnMapReadyCallback {

    private MapFragment mapFrag;
    private GoogleMap gMap;
    private UiSettings mapSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // reference to map fragment
        mapFrag = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFrag.getMapAsync(this); // sets the callback on the fragment
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
    }

}
