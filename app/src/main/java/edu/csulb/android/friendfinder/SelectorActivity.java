package edu.csulb.android.friendfinder;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SelectorActivity extends AppCompatActivity {
    private String friendName = "";

    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private FirebaseUser fbUser;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selector);

        // set up our firebase user for db access
        mAuth = FirebaseAuth.getInstance();
        fbUser = mAuth.getCurrentUser();

        // check against data
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
                    }
                });
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

                                // fetch user from db
                                User user = new User(mDatabase.child("users").child(fbUser.getUid()).child("username").getKey());
                                // no more than 5 friends for now
                                if (user.friendsList.size() < 5) {
                                    // get friends as list
                                    // add new friend to list
                                    user.addFriend(friendName);

                                    //System.out.println(user.friendsList.get(0));
                                    //System.out.println(mDatabase.child("users").child(fbUser.getUid()).child("username").getKey());

                                    // write to db
                                    mDatabase.child("users").child(fbUser.getUid()).child("username").setValue("Friend", friendName);
                                }
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
                startActivity(intent);
                break;
        }
    }
}
