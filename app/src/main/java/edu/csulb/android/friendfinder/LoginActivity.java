package edu.csulb.android.friendfinder;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class LoginActivity extends AppCompatActivity {
    private EditText usernameField;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private String mCustomToken;
    private TokenBroadcastReceiver mTokenReceiver;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        String username = "";
        username = readFromFile();

        if(username.equals("")) {
            usernameField = (EditText) findViewById(R.id.username_login);
            username = usernameField.getText().toString();
            writeToFile(username);
        }



        mDatabase = FirebaseDatabase.getInstance().getReference();
        // Create token receiver
        mTokenReceiver = new TokenBroadcastReceiver() {
            @Override
            public void onNewToken(String token) {
                setCustomToken(token);
                // add user to database
                User user = new User(token);
                mDatabase.child("users").child(token).setValue(user);
            }
        };

        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]

        // [START auth_state_listener]
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                } else {
                    // User is signed out
                }
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        // [START_EXCLUDE]
        registerReceiver(mTokenReceiver, TokenBroadcastReceiver.getFilter());
        // [END_EXCLUDE]
    }
    // [END on_start_add_listener]

    // [START on_stop_remove_listener]
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
        // [START_EXCLUDE]
        unregisterReceiver(mTokenReceiver);
        // [END_EXCLUDE]
    }
    // [END on_stop_remove_listener]

    private void startSignIn() {
        // Initiate sign in with custom token
        // [START sign_in_custom]
        mAuth.signInWithCustomToken(mCustomToken)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        // [END sign_in_custom]
    }


    private void setCustomToken(String token) {
        mCustomToken = token;

        // Enable/disable sign-in button and show the token
        findViewById(R.id.login_button).setEnabled((mCustomToken != null));
    }

    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.login_button) {
            startSignIn();
        }
    }

    public String readFromFile() {

        String name = "";

        try {
            InputStream inputStream = openFileInput("data.txt");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                name = bufferedReader.readLine();
            }

        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return name;
    }

    public void writeToFile(String data) {
        try {
            FileOutputStream fou = openFileOutput("data.txt", Context.MODE_PRIVATE);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fou);
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }
}
