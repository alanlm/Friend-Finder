package edu.csulb.android.friendfinder;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;

public class MapsAppBar extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // reference to default action bar and setting custom layout
        ActionBar mapsBar = getSupportActionBar();
        mapsBar.setDisplayShowHomeEnabled(false);
        mapsBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        // reference to custom bar layout
        View customBarView = getLayoutInflater().inflate(R.layout.custom_app_bar, null);
        mapsBar.setCustomView(customBarView);
        mapsBar.setDisplayShowCustomEnabled(true);

        // reference to ImageButton
        ImageButton friendsListIcon = (ImageButton) customBarView.findViewById(R.id.bar_friends_button);
        // handle friends list click event
        friendsListIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("FRIENDS LIST ICON", "You clicked on Friends List Image Button");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
