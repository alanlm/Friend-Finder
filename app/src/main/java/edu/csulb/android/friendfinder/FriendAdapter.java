package edu.csulb.android.friendfinder;

import android.app.Activity;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class FriendAdapter extends ArrayAdapter {

    private DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
    private final List friendsList;
    private String mPhoneNumber = "123-4568";

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

        Log.d("Getting Phone number" , "get");
        // getting friends phone number from firebase database
        mRef.child(friendsList.get(position).toString()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                Log.d("Getting Phone number" , "friends numnber:" + value);
                System.out.print("user phone number" + value);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        

        // call friend intent
        callFriend_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent phoneIntent = new Intent(Intent.ACTION_DIAL);
                phoneIntent.setData(Uri.parse("tel:" + mPhoneNumber)); // get friends number
                Log.d("CALLING INTENT", "Friend's name: " + friends_name.getText());
                try {
                    phoneIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    v.getContext().startActivity(phoneIntent);
                }
                catch(final SecurityException ex) {

                }
            }
        });

        // message friend intent
        messageFriend_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent messageIntent = new Intent(Intent.ACTION_VIEW);
                messageIntent.setData(Uri.fromParts("sms", mPhoneNumber, null));

                try {
                    messageIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    v.getContext().startActivity(messageIntent);
                }
                catch (final SecurityException ex) {

                }

            }
        });

        return row;
    }

}
