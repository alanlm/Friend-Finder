package edu.csulb.android.friendfinder;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseCredentials;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Random;

public class LoginActivity extends AppCompatActivity {
    private EditText usernameField;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String mCustomToken;
    private DatabaseReference mDatabase;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FileInputStream serviceAccount =
                null;
        try {
            serviceAccount = new FileInputStream("res/raw/friend-finder-6afd1-firebase-adminsdk-5pk23-2412c6936.json");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.d("JSON", "File not found");
        }

        FirebaseOptions options = new FirebaseOptions.Builder().setCredential(FirebaseCredentials.fromCertificate(serviceAccount))
                .setDatabaseUrl("https://friend-finder-6afd1.firebaseio.com")
                .build();

        FirebaseApp.initializeApp(options);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        TextView textView = (TextView) findViewById(R.id.name_view);

        // check if name is cached
        username = readFromFile();

        // if cached then show username and store in database
        if(username != null){
            textView.setText(username);
            mDatabase.child("users").child("uid").setValue(username);
        }

        // generate custom token
        FirebaseAuth.getInstance().createCustomToken(username)
                .addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String customToken) {
                        // Send token back to client
                        mCustomToken = customToken;
                    }
                });

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
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void startSignIn() {
        // Initiate sign in with custom token
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
    }

    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.login_button) {
            usernameField = (EditText) findViewById(R.id.username_login);
            if(username == null && usernameField.getText().length() != 0) {
                username = usernameField.getText().toString();
                writeToFile(username); // cache username
            }
            mDatabase.child("users").child("uid").setValue(username);
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
