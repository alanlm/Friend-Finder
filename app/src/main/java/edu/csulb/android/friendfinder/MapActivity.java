package edu.csulb.android.friendfinder;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private GoogleMap gMap;
    private GoogleApiClient googleApiClient;
    private Marker myLocationMarker;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;

    private static final int MY_LOCATION_PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // reference to map fragment
        MapFragment mapFrag = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFrag.getMapAsync(this); // sets the callback on the fragment

        checkAppPermission(Manifest.permission.ACCESS_FINE_LOCATION,
                MY_LOCATION_PERMISSION_REQUEST_CODE);

        // reference to drawer layout and create drawer toggle
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, null,
                R.string.drawer_open, R.string.drawer_close);

        // custom hambuger icon
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        drawerToggle.setDrawerIndicatorEnabled(false);
        drawerToggle.setHomeAsUpIndicator(R.drawable.icon_friends);

        //==============================================================
        // SAMPLE FOR FRIENDS LIST IN THE DRAWER LAYOUT
        //==============================================================
        // making friends list and adding friends
        List<Map<String, String>> friendsList = new ArrayList<>();
        friendsList.add(addFriend("friend", "Alan"));
        friendsList.add(addFriend("friend", "Kristian"));
        friendsList.add(addFriend("friend", "Oscar"));
        friendsList.add(addFriend("friend", "Yosef"));

        // reference list view and set adapter
        ListView friends_listview = (ListView) findViewById(R.id.friends_listview);
        friends_listview.setAdapter(new SimpleAdapter(this, friendsList,
                android.R.layout.simple_list_item_1,
                new String[] {"friend"}, new int[] {android.R.id.text1}));
        friends_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView rowView = (TextView) view;
                Log.d("FRIENDS LIST LISTENER", "You clicked on " + rowView.getText());
            }
        });
    }

    private HashMap<String, String> addFriend(String key, String name) {
        HashMap<String, String> friend = new HashMap<>();
        friend.put(key, name);
        return friend;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;

        // initialize Google Play Services
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                gMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            gMap.setMyLocationEnabled(true);
        }
        Log.d("ON MAP READY", "googleApiClient status: " + googleApiClient.isConnected());

        // initializing Google Map UI settings
        UiSettings mapSettings = gMap.getUiSettings();
        mapSettings.setZoomControlsEnabled(true);
        mapSettings.setCompassEnabled(true);
    }

    protected synchronized void buildGoogleApiClient() {
         googleApiClient = new GoogleApiClient.Builder(this) // used to configure client
                .addConnectionCallbacks(this) // callbacks when client connected or disconnected
                .addOnConnectionFailedListener(this) // failed attempt of connect client to service
                .addApi(LocationServices.API) // adds LocationServices API endpoint from Google Play Services
                .build();
        googleApiClient.connect(); // client must be connected before executing any operation

        Log.d("BUILD GOOGLE API CLIENT", "googleApiClient connecting: " + googleApiClient.isConnecting());
    }

    public boolean checkAppPermission(String permissionString, int requestCode) {
        if(ContextCompat.checkSelfPermission(this, permissionString)
                != PackageManager.PERMISSION_GRANTED) {
            Log.d("CHECK APP PERMISSION", "checkSelfPermission returned PERMISSION_DENIED");

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissionString)) {
                Log.d("CHECK APP PERMISSION", "shouldShowRequestPermissionRationale returns true");

                // Show an explanation to the user *asynchronously* --
                // don't block this thread waiting for the user's response!
                // After the user sees the explanation, try again to request the permission

                // Prompt the user once explanation has been shown (dialog box)
                ActivityCompat.requestPermissions(this,
                        new String[]{permissionString}, requestCode);
            } else {
                Log.d("CHECK APP PERMISSION", "shouldShowRequestPermissionRationale returns false");

                // No explanation needed, we can request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{permissionString}, requestCode);
            }
            return false;
        } else {
            Log.d("CHECK APP PERMISSION", "checkSelfPermission returned PERMISSION_GRANTED");

            return true;
        }
        // checkSelfPermission method:
        // returns PERMISSION_GRANTED -- proceed with operation
        // returns PERMISSION_DENIED -- has to explicitly ask user for permission

        // shouldShowRequestPermissionRationale method:
        // returns true -- if app has requested this permission previously and user denied
        // returns false -- if user has chosen "Don't ask again" option when previously asked
    }

    // Will be called when user responds to permission requests
    // This method finds out whether the permission was granted
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode) {
            case MY_LOCATION_PERMISSION_REQUEST_CODE:
                // if request is cancelled, the results arrays are empty
                if(grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    if(ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        if(googleApiClient == null)
                            buildGoogleApiClient();
                        gMap.setMyLocationEnabled(true);
                    } else {
                        // permission denied, Disable the functionality
                        Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT);
                    }
                    return;
                }
                break;

            // TODO: Result of other permissions if needed
        } // end of switch
    }

    // Will be called whenever there is change in location of device
    @Override
    public void onLocationChanged(Location location) {
        // in case app needs to manipulate my last location,
        // otherwise not needed (for right now) -- uncomment to use
//        Location myLastLocation = location;

        if(myLocationMarker != null)
            myLocationMarker.remove();

        // place my current location marker
        LatLng latlong = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions myMarkerOptions = new MarkerOptions();
        myMarkerOptions.position(latlong);
        myMarkerOptions.title("You are here!");
        myMarkerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
        myLocationMarker = gMap.addMarker(myMarkerOptions);

        Log.d("ON LOCATION CHANGED", "My location: " + latlong.toString());

        // moving the map's camera
        gMap.moveCamera(CameraUpdateFactory.newLatLng(latlong));
        gMap.animateCamera(CameraUpdateFactory.zoomTo(11));

        // stop location updates
        if(googleApiClient != null)
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
    }

    // Will be called whenever device is connected and disconnected
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        // locationRequest used to get quality of service for location updates
        // from FusedLocationProviderAPI using requestLocationUpdates
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi
                    .requestLocationUpdates(googleApiClient, locationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    // Will be called whenever there is a failed attempt to connect the client to the service
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("ON CONNECTION FAILED","Error code: " + connectionResult.getErrorCode()
                + ", Error Message: " + connectionResult.getErrorMessage());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                Log.d("OPTIONS ITEM SELECTED", "You clicked the custom hamburger icon");
                if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawers();
                    drawerToggle.setHomeAsUpIndicator(R.drawable.icon_friends);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                    drawerToggle.setHomeAsUpIndicator(R.drawable.icon_map);
                }
                break;
            case R.id.options_map_settings:
                Log.d("OPTIONS ITEM SELECTED", "You clicked Map Settings");
                break;
            case R.id.options_account_settings:
                Log.d("OPTIONS ITEM SELECTED", "You clicked Account Settings");
                break;
            case R.id.options_logout:
                Log.d("OPTIONS ITEM SELECTED", "You clicked Logout");
                break;
        }
        return true;
    }
}
