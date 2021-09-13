package com.example.twitterlogin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.TwitterAuthProvider;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

public class TwitterLogin extends AppCompatActivity {

    FirebaseAuth fauth;
    TwitterLoginButton twittebtn;
    TwitterConfig twitterConfig;
    TwitterAuthConfig twitterAuthConfig;
    AuthCredential authCredential;
    TextView tvtwitname,tvtwitemail;
    ImageView ivtwitprofile;
    Button signoutbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_twitter_login);

       tvtwitname= findViewById(R.id.twitname);
       tvtwitemail=findViewById(R.id.twitemail);
       ivtwitprofile=findViewById(R.id.twitterprofile);
       signoutbtn=findViewById(R.id.twittersignout);

        fauth = FirebaseAuth.getInstance();
        twittebtn = findViewById(R.id.twitterbtn1);

        twitterAuthConfig = new TwitterAuthConfig(getString(R.string.API_Key),getString(R.string.Api_secreat));
         twitterConfig = new TwitterConfig.Builder(this)
                .twitterAuthConfig(twitterAuthConfig)
                .build();
        Twitter.initialize(twitterConfig);

        twittebtn.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                Toast.makeText(TwitterLogin.this, "on Success", Toast.LENGTH_SHORT).show();
                getDataofUser(result.data);
            }

            @Override
            public void failure(TwitterException exception) {

                Toast.makeText(TwitterLogin.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        signoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signout();
                tvtwitname.setText("");
                tvtwitemail.setText("");
                ivtwitprofile.setImageResource(R.drawable.person);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        twittebtn.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void getDataofUser(TwitterSession session) {
         authCredential = TwitterAuthProvider.getCredential(session.getAuthToken().token, session.getAuthToken().secret);

        fauth.signInWithCredential(authCredential)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(@NonNull AuthResult authResult) {
                        FirebaseUser user=fauth.getCurrentUser();

                        String personName = user.getDisplayName();
                        String personEmail = user.getEmail();
                        Uri personPhoto = user.getPhotoUrl();

                        Toast.makeText(TwitterLogin.this, authResult.getUser().getEmail().toString(), Toast.LENGTH_SHORT).show();

                        tvtwitname.setText(personName);
                        tvtwitemail.setText(personEmail);
                        Glide.with(TwitterLogin.this).load(String.valueOf(personPhoto)).into(ivtwitprofile);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(TwitterLogin.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
    public void signout(){
        FirebaseAuth.getInstance().signOut();
    }
}