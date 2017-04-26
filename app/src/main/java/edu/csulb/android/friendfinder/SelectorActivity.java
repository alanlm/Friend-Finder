package edu.csulb.android.friendfinder;

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
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SelectorActivity extends BaseActivity {
    private String friendName = "";
    private String userID;
    private List<String> friends;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selector);

        userID = getIntent().getStringExtra("uid");

    }

    public void onClick(View view){
        switch (view.getId()) {
            case R.id.friend_add_button:
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(SelectorActivity.this);
                alertDialog.setTitle("Add Friend");
                alertDialog.setMessage("Enter Username");

                final EditText input = new EditText(SelectorActivity.this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                alertDialog.setView(input);
                alertDialog.setIcon(R.mipmap.friend_icon);

                alertDialog.setPositiveButton("ADD",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                friendName = input.getText().toString();
                                if (friendName.equals("")) {
                                    Toast.makeText(getApplicationContext(),
                                            "Enter a Valid Name", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                // get friends as list
                                FirebaseHandler fbHandler = new FirebaseHandler();
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
                                        }
                                        else{
                                            Toast.makeText(getApplicationContext(),friendName +
                                                    " Already Exists or Could Not Be Added",Toast.LENGTH_SHORT).show();
                                        }
                                        hideProgressDialog();
                                    }
                                },500);
                            }
                        });

                alertDialog.setNegativeButton("CANCEL",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                alertDialog.show();
            break;

            case R.id.find_friend_button:
                Intent intent = new Intent(SelectorActivity.this, MapActivity.class);
                intent.putExtra("uid", userID);
                startActivity(intent);
                break;
        }
    }
}
