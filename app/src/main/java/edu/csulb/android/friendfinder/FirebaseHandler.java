package edu.csulb.android.friendfinder;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by krist on 4/25/2017.
 */

public class FirebaseHandler {
    List<String> friendsList = new ArrayList();
    Map<String,LatLng> friendsLocation = new HashMap<>();

    public void getUsernames(){
        FirebaseDatabase.getInstance().getReference().child("users")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            User user = snapshot.getValue(User.class);
                            Log.d("CHECK-NAMES",user.username);
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d("DATA-ERROR", "error: " + databaseError.getCode() );
                    }
                });
    }

    public List<String> readFriends(String uid){
        FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("friends")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot snap: dataSnapshot.getChildren()) {
                            friendsList.add(String.valueOf(snap.getValue()));
                            Log.d("FRIENDS-READ", String.valueOf(snap.getValue()));
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d("DATA-ERROR", "error: " + databaseError.getCode() );
                    }
                });
        return friendsList;
    }

    public Map<String,LatLng> getFriendLocationMap(final List<String> friends){
        FirebaseDatabase.getInstance().getReference().child("users")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final List<String> friendList = friends;
                        for(String friendName: friendList) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                User user = snapshot.getValue(User.class);
                                if (friendName.equals(user.username)){
                                    Log.d("FOUND","Found friend: "+friendName);
                                    friendsLocation.put(friendName,new LatLng(user.latitude,user.longitutde));
                                }
                            }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d("DATA-ERROR", "error: " + databaseError.getCode() );
                    }
                });
        return friendsLocation;
    }
}
