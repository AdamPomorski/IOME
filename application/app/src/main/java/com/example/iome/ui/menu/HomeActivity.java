package com.example.iome.ui.menu;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.iome.services.HeadbandService;
import com.example.iome.utility.MyDataManager;
import com.example.iome.R;
import com.example.iome.services.SpotifyService;
import com.example.iome.player.PlayerActivity;
import com.example.iome.user_profile.UserProfileActivity;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

public class HomeActivity extends AppCompatActivity {


    private CardView spotifyCardView, headbandCardView;
    private TextView meditationText, attentionText, connectionStateHeadbandText, connectionStateSpotifyText;
    private String deviceAddress;

    private static final int REQUEST_CODE = 1337;


    private SpotifyService mSpotifyService;
    private boolean isServiceBound = false;
    private AuthorizationRequest request;

    private HeadbandService mHeadbandService;
    private MyDataManager myDataManager;
    private boolean isConnectedToSpotify = false;
    private boolean isConnectedToHeadband = false;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_test);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        Intent intent = getIntent();
        deviceAddress = intent.getStringExtra("address");

        mSpotifyService = SpotifyService.getInstance();
        mHeadbandService = HeadbandService.getInstance();





        spotifyCardView = findViewById(R.id.btn_connect_spotify);
        headbandCardView = findViewById(R.id.btn_connect_headband);
        meditationText = findViewById(R.id.btn_meditation);
        attentionText = findViewById(R.id.btn_attention);
        connectionStateHeadbandText = findViewById(R.id.connecion_state_headband);
        connectionStateSpotifyText = findViewById(R.id.conenction_state_spotify);
        isConnectedToSpotify = false;
        isConnectedToHeadband = false;




        myDataManager = MyDataManager.getInstance();


        Intent playerActivityIntent = new Intent(this, PlayerActivity.class);



        spotifyCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isConnectedToSpotify) {
                    request = mSpotifyService.buildRequestSpotify();
                    openSpotifyLoginActivity(request);
                } else {
                    mSpotifyService.disconnectFromSpotify();
                    isConnectedToSpotify = false;
                    connectionStateSpotifyText.setText("Connect");
                }
            }

        });
        headbandCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isConnectedToHeadband) {
                    mHeadbandService.connectToDevice(deviceAddress);
                } else {
                    mHeadbandService.disconnectFromDevice();
                }

            }
        });
        meditationText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isConnectedToSpotify) {
                    Toast.makeText(HomeActivity.this, "Connect to Spotify first", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!isConnectedToHeadband){
                    Toast.makeText(HomeActivity.this, "Connect to Headband first", Toast.LENGTH_SHORT).show();
                    return;
                }
                myDataManager.setDataType("meditation");
                playerActivityIntent.putExtra("meditationMode", true);
                playerActivityIntent.putExtra("attentionMode", false);
                startActivity(playerActivityIntent);

            }
        });
        attentionText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (!isConnectedToSpotify) {
                    Toast.makeText(HomeActivity.this, "Connect to Spotify first", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!isConnectedToHeadband){
                    Toast.makeText(HomeActivity.this, "Connect to Headband first", Toast.LENGTH_SHORT).show();
                    return;
                }
                myDataManager.setDataType("attention");
                playerActivityIntent.putExtra("attentionMode", true);
                playerActivityIntent.putExtra("meditationMode", false);
                startActivity(playerActivityIntent);

            }
        });

        myDataManager.getSpotifyConnectionStateLiveData().observe(this, connectionState -> {
            if (connectionState.equals("Connected to Spotify") && !isConnectedToSpotify) {
                connectionStateSpotifyText.setText("Connected");
                isConnectedToSpotify = true;
            } else {
                connectionStateSpotifyText.setText("Connect");
                isConnectedToSpotify = false;
            }
            Toast.makeText(this, connectionState, Toast.LENGTH_SHORT).show();
        });
        myDataManager.getHeadbandConnectionStateLiveData().observe(this, connectionState -> {
            if (connectionState.equals("Connected to Myndband") && !isConnectedToHeadband) {
                connectionStateHeadbandText.setText("Connected");
                isConnectedToHeadband = true;
            } else {
                connectionStateHeadbandText.setText("Connect");
                isConnectedToHeadband = false;
            }
            Toast.makeText(this, connectionState, Toast.LENGTH_SHORT).show();
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_search:
               openSpotifyApp();
                break;
            case R.id.action_user_profile:
                Intent userProfileIntent = new Intent(this, UserProfileActivity.class);
                startActivity(userProfileIntent);
                break;

        }

        return true;
    }

    private void openSpotifyLoginActivity(AuthorizationRequest request) {
        AuthorizationClient.openLoginActivity(this, REQUEST_CODE, request);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);


        if (requestCode == REQUEST_CODE) {
            AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, intent);

            switch (response.getType()) {

                case TOKEN:

                    mSpotifyService.connect(true, this);
                    break;

                case ERROR:

                    Toast.makeText(this, "Couldn't connect to Spotify. Please check your internet connection and try again", Toast.LENGTH_LONG).show();
                    break;


                default:

            }
        }
    }

    public void openSpotifyApp() {

        String spotifyPackageName = "com.spotify.music";
        Intent intent = getPackageManager().getLaunchIntentForPackage(spotifyPackageName);
        if (intent != null) {
            startActivity(intent);
        } else {

            Intent playStoreIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + spotifyPackageName));
            startActivity(playStoreIntent);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

}