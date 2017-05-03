package edu.csulb.android.friendfinder;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.database.FirebaseDatabase;
import java.util.List;
import java.util.Map;

public class SelectorActivity extends BaseActivity {
    private String friendName = "";
    private String userID;
    private String username;
    private List<String> friends;
    private EditText input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selector);

        userID = getIntent().getStringExtra("uid");
        username = getIntent().getStringExtra("username");
        input = new EditText(SelectorActivity.this);

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
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(SelectorActivity.this);
        alertDialog.setTitle("Add Friend");
        alertDialog.setMessage("Enter Username");
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);
        alertDialog.setIcon(R.mipmap.friend_icon);

        alertDialog.setPositiveButton("ADD",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        addFriend();
                    }
                });

        alertDialog.setNegativeButton("CANCEL",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }

    public void addFriend(){
        friendName = input.getText().toString();
        if (friendName.equals("")) {
            Toast.makeText(getApplicationContext(),
                    "Enter a Valid Name", Toast.LENGTH_SHORT).show();
            return;
        }

        // get friends as list
        final FirebaseHandler fbHandler = new FirebaseHandler();
        showProgressDialog();
        friends = fbHandler.readFriends(userID);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // add new friend to list
                if(!friends.contains(friendName)){
                    friends.add(friendName);
                    FirebaseDatabase.getInstance().getReference()
                            .child("users").child(userID).child("friends").setValue(friends);
                    Toast.makeText(getApplicationContext(),friendName + " Was Added",Toast.LENGTH_SHORT).show();

                    username = getIntent().getStringExtra("username");
                    Log.d("WHY", "Usernmae: " + username);
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
                    },500);

                }
                else{
                    Toast.makeText(getApplicationContext(),friendName +
                            " Already Exists or Could Not Be Added",Toast.LENGTH_SHORT).show();
                }
                hideProgressDialog();
            }
        },500);
    }
}
