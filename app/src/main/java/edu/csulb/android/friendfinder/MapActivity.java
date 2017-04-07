package edu.csulb.android.friendfinder;

import android.app.Activity;
import android.os.Bundle;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;

public class MapActivity extends Activity implements OnMapReadyCallback {

    MapFragment mapFrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

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
    }
}
