package edu.csulb.android.friendfinder;

import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MapsDrawer extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_drawer);

        // reference to drawer layout and create drawer toggle
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, null,
                R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                Log.d("onDrawerOpened", "Opening Drawer");
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                Log.d("onDrawerClosed", "Closing Drawer");
            }
        };

        // create drawer
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        // custom hambuger icon
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
//        drawerToggle.setDrawerIndicatorEnabled(false);
//        drawerToggle.setHomeAsUpIndicator(R.drawable.icon_friends);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // when the drawer toggle (Up button) is clicked, drawer opens
        if(drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch(item.getItemId()) {
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
