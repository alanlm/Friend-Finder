package edu.csulb.android.friendfinder;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.auth.api.Auth;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class LoginActivity extends BaseActivity implements GoogleApiClient.OnConnectionFailedListener {
    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "Auth";
    private EditText usernameField;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;
    private String username;
    private String phoneNumber;
    private EditText phoneNumberField;

    private boolean bIsSignedIn = false;
    private boolean userIsValid = false;


    private GoogleApiClient mGoogleApiClient;
    private FirebaseUser fbUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        mDatabase = FirebaseDatabase.getInstance().getReference();

        signIn();
      
        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]

        // [START auth_state_listener]
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                fbUser = firebaseAuth.getCurrentUser();
                if (fbUser != null) {
                    // User is signed in
                    bIsSignedIn = true;
                    Log.d("SIGNIN","signed in as " + fbUser.getUid());

                    mDatabase.child("users").child(fbUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);
                            try {
                                username = user.username;
                            } catch (NullPointerException e) {
                                Log.d("SIGNIN", "Username is Null!");
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    Log.d("SIGNIN", "AUTO LOGIN USERNAME: " + username);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(username != null){
                                Intent intent = new Intent(LoginActivity.this, SelectorActivity.class);
                                intent.putExtra("uid",fbUser.getUid());
                                intent.putExtra("username",username);
                                startActivity(intent);
                            }
                        }
                    },500);
                }
                else {
                    // User is signed out
                    Log.d("SIGNIN","signed out");
                }
            }
        };
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            }
            else {
                // Google Sign In failed, update UI appropriately
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        showProgressDialog();
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        hideProgressDialog();
                    }
                });
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

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signOut() {
        // Firebase sign out
        mAuth.signOut();

        // Google sign out
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        // update UI to indicate sign out
                    }
                });
    }

    /// for first time users
    // TODO add user phone number to database
    // TODO validate phone number, cant login if the phone number is not active
    // first time user
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.login_button) {
            usernameField = (EditText) findViewById(R.id.username_login);
            phoneNumberField = (EditText) findViewById(R.id.phonenumber_login);

            if(usernameField.getText().length() == 0){
                Toast.makeText(this, "Please enter a username", Toast.LENGTH_SHORT).show();
                return;
            }
            if (phoneNumberField.getText().length() <= 0 && phoneNumberField.getText().length() >= 12) {// no phone number is entered
                Toast.makeText(this, "Please enter a phone number", Toast.LENGTH_SHORT).show();
                return;
            }

            username = usernameField.getText().toString();

            if (bIsSignedIn == true) {

                User user = new User(username);
                mDatabase.child("users").child(fbUser.getUid()).setValue(user);

                // Debugging
                Log.d("SIGNIN", "Username is: " + username);
                Log.d("SIGNIN", "UsernameField is: " + usernameField.getText().toString());

                // check if username has been entered and matches whats on the database

                phoneNumberField = (EditText) findViewById(R.id.phonenumber_login);
                if (usernameField.getText().toString().length() != 0) {
                    username = usernameField.getText().toString();
                }
                if (phoneNumberField.getText().length() <= 0) // no phone number is entered
                    Toast.makeText(this, "Please enter a phone number", Toast.LENGTH_SHORT).show();
                if (phoneNumberField.getText().toString().length() != 0) { // field is not empty, TODO check valid phone number
                    // phoneNumber = phoneNumberField.getText().toString();
                    System.out.println("Phone number text field" + phoneNumberField.getText().toString());
                    phoneNumber = PhoneNumberUtils.formatNumber
                            (phoneNumberField.getText().toString(), Locale.getDefault().getCountry());
                    Log.d("PhoneNumber", " : " + phoneNumber);
                }
                mDatabase.child("users").child(fbUser.getUid()).setValue(user);
                mDatabase.child("users").child(fbUser.getUid()).child("phone-number").setValue(phoneNumber); // adding phone number to database

                Intent intent = new Intent(this, SelectorActivity.class);
                intent.putExtra("uid", fbUser.getUid());
                intent.putExtra("username", username);
                startActivity(intent);
                //========
                mDatabase.child("users")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            userIsValid = true;
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                User user = snapshot.getValue(User.class);
                                Log.d("VALID-TEST", user.username);
                                if (username.equals(user.username)) {
                                    userIsValid = false;
                                    Log.d("VALID-TEST", user.username + "exists");
                                    return;
                                }
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.d("DATA-ERROR", "error: " + databaseError.getCode() );
                        }
                    });

                showProgressDialog();
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                  @Override
                  public void run() {
                    hideProgressDialog();
                    if(!userIsValid){
                        Toast.makeText(LoginActivity.this, "Username already exists", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    User user = new User(username);
                    mDatabase.child("users").child(fbUser.getUid()).setValue(user);

                   if(usernameField.getText().toString().length() != 0) {
                     username = usernameField.getText().toString();
                   }
          
                  phoneNumber = PhoneNumberUtils.formatNumber
                          (phoneNumberField.getText().toString(), Locale.getDefault().getCountry());
                  Log.d("PhoneNumber", " : " + phoneNumber);
                  mDatabase.child("users").child(fbUser.getUid()).setValue(user);
                  mDatabase.child("users").child(fbUser.getUid()).child("phonenumber").setValue(phoneNumber); // adding phone number to database
                  Intent intent = new Intent(LoginActivity.this,SelectorActivity.class);
                          intent.putExtra("uid",fbUser.getUid());
                          intent.putExtra("username",username);
                          startActivity(intent);
                      }
                   }, 500);
                } else {
                Toast.makeText(this, "Please sign in with your google account", Toast.LENGTH_SHORT).show();
                signIn();
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }
}
