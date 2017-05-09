package edu.csulb.android.friendfinder;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Map;

public class SelectorActivity extends BaseActivity {
    private String friendName = "";
    private String userID;
    private String username;
    private List<String> friends;
    private EditText input;
    private boolean friendIsValid = false;
    boolean hasShown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selector);

        userID = getIntent().getStringExtra("uid");
        username = getIntent().getStringExtra("username");


        TextView textView = (TextView) findViewById(R.id.text_view);
        textView.setText("Signed in as " + username);
    }

    public void onClick(View view){
        switch (view.getId()) {
            case R.id.friend_add_button:
                alertDialog();
            break;

            case R.id.find_friend_button:
                Intent intent = new Intent(SelectorActivity.this, MapActivity.class);
                intent.putExtra("uid", userID);
                intent.putExtra("username", username);
                startActivity(intent);
                break;
        }
    }

    public void alertDialog(){
        input = new EditText(SelectorActivity.this);
        AlertDialog alertDialog = new AlertDialog.Builder(SelectorActivity.this).create();
        alertDialog.setTitle("Add Friend");
        alertDialog.setMessage("Enter Username");
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);
        alertDialog.setIcon(R.mipmap.friend_icon);

        DialogInterface.OnClickListener dialogClick = new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        addFriend();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
                dialog.dismiss();
            }
        };

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Add Friend", dialogClick);
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", dialogClick);
        alertDialog.show();
    }

    public void addFriend(){
        friendName = input.getText().toString();
        if (friendName.equals("")) {
            Toast.makeText(getApplicationContext(),
                    "Enter a Valid Name", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseDatabase.getInstance().getReference().child("users")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            User user = snapshot.getValue(User.class);
                            Log.d("VALID-TEST", user.username);
                            if (friendName.equals(user.username)) {
                                friendIsValid = true;
                                Log.d("VALID-TEST", user.username + " exists");
                                return;
                            }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d("DATA-ERROR", "error: " + databaseError.getCode() );
                    }
                });

        // get friends as list
        final FirebaseHandler fbHandler = new FirebaseHandler();
        showProgressDialog();
        friends = fbHandler.readFriends(userID);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!friendIsValid){
                    Toast.makeText(getApplicationContext(),friendName + " Doesn't Exist",Toast.LENGTH_SHORT).show();
                    hideProgressDialog();
                    return;
                }
                // add new friend to list
                if(!friends.contains(friendName)){
                    friends.add(friendName);
                    FirebaseDatabase.getInstance().getReference()
                            .child("users").child(userID).child("friends").setValue(friends);
                    Toast.makeText(getApplicationContext(),friendName + " Was Added",Toast.LENGTH_SHORT).show();

                    username = getIntent().getStringExtra("username");
                    Log.d("WHY", "Username: " + username);
                    // add yourself to friend's friendlist
                    final Map friendInfo =fbHandler.getFriendsFriendList(username,friendName);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Map.Entry < String, List < String >> entry =
                                    (Map.Entry<String, List<String>>) friendInfo.entrySet().iterator().next();
                            FirebaseDatabase.getInstance().getReference()
                                    .child("users").child(entry.getKey()).child("friends").setValue(entry.getValue());
                            Log.d("WHY","Friend uid: "+entry.getKey());
                            for(String s: entry.getValue()) {
                                Log.d("WHY", "Friend's friends: " + s);
                            }
                        }
                    },750);

                }
                else{
                    Toast.makeText(getApplicationContext(),friendName +
                            " Already Exists or Could Not Be Added",Toast.LENGTH_SHORT).show();
                }
                hideProgressDialog();
            }
        },500);

        // reset valid friend boolean
        friendIsValid = false;
    }

    public void alertDialog(final List<String> list, Context context){
        final EditText etInput = new EditText(context);
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("Add Friend");
        alertDialog.setMessage("Enter Username");
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        etInput.setLayoutParams(lp);
        alertDialog.setView(etInput);
        alertDialog.setIcon(R.mipmap.friend_icon);

        DialogInterface.OnClickListener dialogClick = new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        addFriend(list, etInput);
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
                dialog.dismiss();
            }
        };

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Add Friend", dialogClick);
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", dialogClick);
        alertDialog.show();
    }

    public void addFriend(final List<String> list, EditText editText){
        friendName = editText.getText().toString();
        if (friendName.equals("")) {
            Toast.makeText(getApplicationContext(),
                    "Enter a Valid Name", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseDatabase.getInstance().getReference().child("users")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            User user = snapshot.getValue(User.class);
                            Log.d("VALID-TEST", user.username);
                            if (friendName.equals(user.username)) {
                                friendIsValid = true;
                                Log.d("VALID-TEST", user.username + " exists");
                                return;
                            }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d("DATA-ERROR", "error: " + databaseError.getCode() );
                    }
                });

        // get friends as list
        final FirebaseHandler fbHandler = new FirebaseHandler();
        showProgressDialog();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!friendIsValid){
                    Toast.makeText(getApplicationContext(),friendName + " Doesn't Exist",Toast.LENGTH_SHORT).show();
                    hideProgressDialog();
                    return;
                }
                // add new friend to list
                if(!list.contains(friendName)){
                    list.add(friendName);
                    FirebaseDatabase.getInstance().getReference()
                            .child("users").child(userID).child("friends").setValue(list);
                    Toast.makeText(getApplicationContext(),friendName + " Was Added",Toast.LENGTH_SHORT).show();

                    username = getIntent().getStringExtra("username");
                    Log.d("WHY", "Username: " + username);
                    // add yourself to friend's friendlist
                    final Map friendInfo =fbHandler.getFriendsFriendList(username,friendName);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Map.Entry < String, List < String >> entry =
                                    (Map.Entry<String, List<String>>) friendInfo.entrySet().iterator().next();
                            FirebaseDatabase.getInstance().getReference()
                                    .child("users").child(entry.getKey()).child("friends").setValue(entry.getValue());
                            Log.d("WHY","Friend uid: "+entry.getKey());
                            for(String s: entry.getValue()) {
                                Log.d("WHY", "Friend's friends: " + s);
                            }
                        }
                    },750);

                }
                else{
                    Toast.makeText(getApplicationContext(),friendName +
                            " Already Exists or Could Not Be Added",Toast.LENGTH_SHORT).show();
                }
                hideProgressDialog();
            }
        },500);

        // reset valid friend boolean
        friendIsValid = false;
    }
}
