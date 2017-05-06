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
import android.os.Handler;


import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.List;


public class FriendAdapter extends ArrayAdapter {

    private DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
    private final List friendsList;
    private String mPhoneNumber;
    private String userID;

    public FriendAdapter(Context context, int resource, List friendsList) {
        super(context, resource, friendsList);
        this.friendsList = friendsList;
    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
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
            public void onClick(final View v) {
                Log.d("Getting Phone number" , "get" + friendsList.get(position).toString() + " number");
                final Query query = mRef.child("users")
                        .orderByChild("username")
                        .equalTo(friendsList.get(position).toString());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            User user = snapshot.getValue(User.class);
                            if (friendsList.get(position).toString().equals(user.username)) {
                                mPhoneNumber = user.phonenumber.toString();
                                Log.d("Friends phone number", " user: " + user.phonenumber);
                                Log.d("Friends phone number", " mPhone: " + mPhoneNumber);
                            }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // This intent goes in the post delay run method
                        Log.d("CALLING INTENT", "phone number: " + mPhoneNumber);
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
                },500);
            }
        });

        // message friend intent
        messageFriend_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final Query query = mRef.child("users")
                        .orderByChild("username")
                        .equalTo(friendsList.get(position).toString());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            User user = snapshot.getValue(User.class);
                            if (friendsList.get(position).toString().equals(user.username)) {
                                mPhoneNumber = user.phonenumber.toString();
                                Log.d("Friends phone number", " user: " + user.phonenumber);
                                Log.d("Friends phone number", " mPhone: " + mPhoneNumber);
                            }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent messageIntent = new Intent(Intent.ACTION_VIEW);
                        messageIntent.setData(Uri.fromParts("sms", mPhoneNumber, null));
                        try {
                            messageIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            v.getContext().startActivity(messageIntent);
                        }
                        catch (final SecurityException ex) {

                        }
                    }
                }, 500);


            }
        });

        return row;
    }

}
