package com.example.redbus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 123;
    private static final String TAG = "MainActivity";
    private static FirebaseUser user;
    public String uid;
    private SignInButton SignIn;
    private Button SignOut;
    private LinearLayout llProfileLayout;
    private TextView nameTextView, emailTextView;

    protected void dispUserDetails(){
        if (user != null) {
            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();
            ImageView ProfilePic = (ImageView) findViewById(R.id.ProfilePic);
            nameTextView = (TextView) findViewById(R.id.Name);
            nameTextView.setText(name);
            emailTextView = (TextView) findViewById(R.id.Email);
            emailTextView.setText(email);
            Glide.with(getApplicationContext()).load(photoUrl.toString())
                    .thumbnail(0.5f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(ProfilePic);
            // The user's ID, unique to the Firebase project.
            uid = user.getUid();
            user currentUser = new user(name, email, photoUrl.toString());
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("user");
            ValueEventListener Listener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(!dataSnapshot.exists()){
                        mDatabase.child(uid).setValue(currentUser);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Getting Post failed, log a message
                    Log.w(TAG, "User fetch:onCancelled", databaseError.toException());
                }
            };
            mDatabase.child(uid).addValueEventListener(Listener);
            updateUI(true);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                user = FirebaseAuth.getInstance().getCurrentUser();
                dispUserDetails();
                // ...
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
                    if(response != null){
                        try{
                            response.getError().getErrorCode();
                        } catch(Exception e){
                            Log.e(TAG, "Signin error", e);
                        }
                    }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        SignIn = (SignInButton) findViewById(R.id.SignIn);
        SignOut = findViewById(R.id.SignOut);
        llProfileLayout = (LinearLayout) findViewById(R.id.llProfile);
        SignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
        SignOut.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Create and launch sign-in intent
                signOut();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        dispUserDetails();
    }

    protected void signIn(){
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.GoogleBuilder().build());
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
    }

    protected void signOut() {
        // [START auth_fui_signout]
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });
        updateUI(false);
        // [END auth_fui_signout]
    }

    private void updateUI(boolean isSignedIn) {
        if (isSignedIn) {
            SignIn.setVisibility(View.GONE);
            SignOut.setVisibility(View.VISIBLE);
            llProfileLayout.setVisibility(View.VISIBLE);
        } else {
            SignIn.setVisibility(View.VISIBLE);
            SignOut.setVisibility(View.GONE);
            llProfileLayout.setVisibility(View.GONE);
        }
    }
    /*called on click of book button*/
    public void BookAcitivity(View view){
        Intent intent = new Intent(this, BookTicketActivity.class);
        intent.putExtra("userId", uid);
        startActivity(intent);
    }
}