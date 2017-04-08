package edu.csulb.android.friendfinder;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class SelectorActivity extends AppCompatActivity {
    private String friendName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selector);
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
                                // friend to friendlist of user in database
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
