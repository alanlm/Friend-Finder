package edu.csulb.android.friendfinder;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by krist on 4/25/2017.
 */

public class FirebaseHandler {
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private List<String> friendsList = new ArrayList();
    private Map<String,LatLng> friendsLocation = new HashMap<>();
    private Map<String,List<String>> friendInfo = new HashMap<>();
    private String uName;
    private boolean userIsValid = true;


    public String getUsername(String uid) {
        Log.d("Firebase initial", uid);
        mDatabase.child("users").child(uid).child("username")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d("Setting userName", String.valueOf(dataSnapshot.getValue()));
                        uName = String.valueOf(dataSnapshot.getValue());
                        System.out.println(uName);
                        Log.d("userName", String.valueOf(dataSnapshot.getValue()));
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d("DATA-ERROR", "error: " + databaseError.getCode() );
                    }
                });
        Log.d("Sending username", "username:" + uName);
        return uName;
    }

    public void getUsernames(){
        mDatabase.child("users")
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

    public boolean userIsValid(String username){
        final String localUsername = username;
        mDatabase.child("users")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            User user = snapshot.getValue(User.class);
                            if(localUsername.equals(user.username)){
                                userIsValid = false;
                            }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d("DATA-ERROR", "error: " + databaseError.getCode() );
                    }
                });
        return userIsValid;
    }

    public List<String> readFriends(String uid){
        mDatabase.child("users").child(uid).child("friends")
                .addListenerForSingleValueEvent(new ValueEventListener() {
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

    // get friend's friendlist to add yourself to a friend's list
    public Map<String,List<String>> getFriendsFriendList(final String username,final String friendName){
        final Query query = mDatabase.child("users")
                .orderByChild("username")
                .equalTo(friendName);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // get friend's uid
                DataSnapshot snap = dataSnapshot.getChildren().iterator().next();
                String uid = String.valueOf(snap.getKey());
                Log.d("AddFriend", String.valueOf(dataSnapshot.toString()));
                Log.d("AddFriend", uid);
                List<String> newList = new ArrayList<>();
                if(snap.child("friends").getValue() != null) {
                    newList.addAll((List<String>) snap.child("friends").getValue());
                }
                newList.add(username);
                friendInfo.put(uid,newList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return friendInfo;
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
                                    friendsLocation.put(friendName,new LatLng(user.latitude,user.longitude));
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
