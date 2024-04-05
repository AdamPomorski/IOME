package com.example.iome.user_profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.iome.R;
import com.example.iome.services.SpotifyService;
import com.example.iome.user_profile.ui.user_profile.UserProfileFragment;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

public class UserProfileActivity extends AppCompatActivity {
    private AuthorizationRequest request;
    private SpotifyService spotifyService;
    private static final int REQUEST_CODE = 1337;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, UserProfileFragment.newInstance())
                    .commitNow();
        }

        spotifyService = SpotifyService.getInstance();
        request = spotifyService.buildRequestSpotify();
        AuthorizationClient.openLoginActivity(this, REQUEST_CODE, request);
    }
        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
            super.onActivityResult(requestCode, resultCode, intent);


            if (requestCode == REQUEST_CODE) {
                AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, intent);
                spotifyService.setAccessToken(response.getAccessToken());



                switch (response.getType()) {

                    case TOKEN:
                        spotifyService.connect(true, this);
                        break;

                    case ERROR:

                        break;

                    default:

                }
            }
        }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_user_profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.about:
                LayoutInflater inflater = LayoutInflater.from(this);
                View view = inflater.inflate(R.layout.dialog_about, null);
                dialog = new Dialog(this, R.style.dialog1);
                dialog.setContentView(view);
                dialog.show();
                break;

        }

        return true;
    }



    }
