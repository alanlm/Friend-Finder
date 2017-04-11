package edu.csulb.android.friendfinder;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;

public class MapsAppBar extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // reference to default action bar and setting custom layout
        ActionBar mapsBar = getSupportActionBar();
        mapsBar.setDisplayShowHomeEnabled(false);
        mapsBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        mapsBar.setCustomView(getLayoutInflater().inflate(R.layout.custom_app_bar, null));
        mapsBar.setDisplayShowCustomEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
