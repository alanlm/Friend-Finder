package edu.csulb.android.friendfinder;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class FriendAdapter extends ArrayAdapter {

    private final List friendsList;

    public FriendAdapter(Context context, int resource, List friendsList) {
        super(context, resource, friendsList);
        this.friendsList = friendsList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.row, null);

        // setting friend name
        ImageView callFriend_view = (ImageView) row.findViewById(R.id.friends_call);
        ImageView messageFriend_view = (ImageView) row.findViewById(R.id.friends_message);

        final TextView friends_name = (TextView) row.findViewById(R.id.friends_name);
        friends_name.setText(friendsList.get(position).toString());

        

        // call friend intent
        callFriend_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent phoneIntent = new Intent(Intent.ACTION_DIAL);
                phoneIntent.setData(Uri.parse("tel:888-8888")); // get friends number
                Log.d("CALLING INTENT", "Friend's name: " + friends_name.getText());
                try {
                    getContext().startActivity(phoneIntent);
                }
                catch(final SecurityException ex) {

                }
            }
        });

        // message friend intent
        messageFriend_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return row;
    }

}
