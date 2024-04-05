package com.example.iome.player;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import com.example.iome.R;
import com.example.iome.services.HeadbandService;
import com.example.iome.services.TimerService;
import com.example.iome.utility.MyDataManager;
import com.example.iome.services.SpotifyService;
import com.example.iome.database.ScoreRepository;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.client.ErrorCallback;
import com.spotify.protocol.types.Image;
import com.spotify.protocol.types.PlaybackSpeed;
import com.spotify.protocol.types.PlayerContext;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Repeat;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/*
 * Copyright (c) 2018 Spotify AB
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

public class PlayerActivity extends FragmentActivity implements SpotifyService.SpotifyCallback {


    private static final String TAG = com.example.iome.player.PlayerActivity.class.getSimpleName();


    private static final int REQUEST_CODE = 1337;

    private static SpotifyAppRemote spotifyAppRemote;

    private PlayerViewModel mPlayerViewModel;

    private SpotifyService mSpotifyService;

    AuthorizationRequest request;

    private boolean meditationMode = false;
    private boolean attentionMode = false;

    private HeadbandService mHeadbandService;
    private MyDataManager myDataManager;

    private String song_uri;

    Gson gson = new GsonBuilder().setPrettyPrinting().create();

    Button connectAuthorizeButton;
    Button subscribeToPlayerContextButton;
    Button mPlayerContextButton;
    Button mSubscribeToPlayerStateButton;
    Button mPlayerStateButton;
    ImageView mCoverArtImageView;
    AppCompatTextView mImageLabel;
    AppCompatTextView mImageScaleTypeLabel;
    AppCompatImageButton mToggleShuffleButton;
    AppCompatImageButton mPlayPauseButton;
    AppCompatImageButton mToggleRepeatButton;
    AppCompatSeekBar mSeekBar;
    TextView modeTextView, modeValueTextView;

    List<View> mViews;
    com.example.iome.player.PlayerActivity.TrackProgressBar mTrackProgressBar;

    private final ErrorCallback mErrorCallback = this::logError;

    private Dialog timerDialog, volumeDialog;
    private Button startTimerButton, resetTimerButton, volumeButton;
    private TextView timeTextview, hoursMinutesTextView, minutesSecondsTextView;
    private EditText hoursEditText, minutesEditText, secondsEditText;
    private SwitchCompat volumeSwitch;

    private String hoursStr, minutesStr, secondsStr;
    private int hours, minutes, seconds;
    private ImageView timerImageView, timerWorkingIcon;
    private CardView spotifyCardView;

    private boolean isTimerRunning = false;


    private ScoreRepository repository;




    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        meditationMode = getIntent().getBooleanExtra("meditationMode", false);
        attentionMode = getIntent().getBooleanExtra("attentionMode", false);
        mPlayerViewModel = new ViewModelProvider(this).get(PlayerViewModel.class);
        repository = new ScoreRepository(getApplication());


        mSpotifyService = SpotifyService.getInstance();
        mSpotifyService.setCallback(this);
        mHeadbandService = HeadbandService.getInstance();
        connectAuthorizeButton = findViewById(R.id.connect_authorize_button);
        mPlayerContextButton = findViewById(R.id.current_context_label);
        subscribeToPlayerContextButton = findViewById(R.id.subscribe_to_player_context_button);
        mCoverArtImageView = findViewById(R.id.image);
        mImageLabel = findViewById(R.id.image_label);
        mImageScaleTypeLabel = findViewById(R.id.image_scale_type_label);
        mPlayerStateButton = findViewById(R.id.current_track_label);
        mSubscribeToPlayerStateButton = findViewById(R.id.subscribe_to_player_state_button);
        mToggleRepeatButton = findViewById(R.id.toggle_repeat_button);
        mToggleShuffleButton = findViewById(R.id.toggle_shuffle_button);
        mPlayPauseButton = findViewById(R.id.play_pause_button);
        volumeButton = findViewById(R.id.volume_button);

        modeTextView = findViewById(R.id.mode_text);
        modeValueTextView = findViewById(R.id.mode_value_text);

        timerImageView = findViewById(R.id.timer_image);
        timerWorkingIcon = findViewById(R.id.timer_working_icon);
        spotifyCardView = findViewById(R.id.cardView);


        mSeekBar = findViewById(R.id.seek_to);
        mSeekBar.setEnabled(false);
        mSeekBar.getProgressDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        mSeekBar.getIndeterminateDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);

        mTrackProgressBar = new com.example.iome.player.PlayerActivity.TrackProgressBar(mSeekBar);

        mViews =
                Arrays.asList(
                        findViewById(R.id.disconnect_button),
                        subscribeToPlayerContextButton,
                        mSubscribeToPlayerStateButton,
                        mImageLabel,
                        mImageScaleTypeLabel,
                        mPlayPauseButton,
                        findViewById(R.id.seek_forward_button),
                        findViewById(R.id.seek_back_button),
                        findViewById(R.id.skip_prev_button),
                        findViewById(R.id.skip_next_button),
                        mToggleRepeatButton,
                        mToggleShuffleButton,
                        mSeekBar);

        SpotifyAppRemote.setDebugMode(true);

        if (meditationMode && !attentionMode) {
            modeTextView.setText("Meditation");
        } else if (!meditationMode && attentionMode) {
            modeTextView.setText("Attention");
        }
        registerReceiver(timerUpdateReceiver, new IntentFilter(TimerService.TIMER_UPDATE_ACTION));
        timerImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buildTimerDialog();

            }
        });
        volumeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buildVolumeDialog();
            }
        });
        spotifyCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String spotifyPackageName = "com.spotify.music";
                Intent intent = getPackageManager().getLaunchIntentForPackage(spotifyPackageName);
                if (intent != null) {
                    startActivity(intent);
                } else {

                    Intent playStoreIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + spotifyPackageName));
                    startActivity(playStoreIntent);
                }
            }
        });

        onDisconnected();


    }

    @Override
    protected void onStart() {
        super.onStart();
        mSpotifyService = SpotifyService.getInstance();
        mHeadbandService = HeadbandService.getInstance();


        request = mSpotifyService.buildRequestSpotify();
        AuthorizationClient.openLoginActivity(this, REQUEST_CODE, request);

        myDataManager = mHeadbandService.getDataManager();

        if (meditationMode && !attentionMode) {
            myDataManager.getMeditationLiveData().observe(this, meditationValue -> {
                if (meditationValue != null && meditationValue != 0) {
                    modeValueTextView.setText(meditationValue.toString());
                }
                timerImageView.setVisibility(View.VISIBLE);
            });

        } else if (!meditationMode && attentionMode) {

            myDataManager.getAttentionLiveData().observe(this, attentionValue -> {
                if (attentionValue != null && attentionValue != 0 && attentionValue > 0) {
                    modeValueTextView.setText(attentionValue.toString());

                }

            });


        }


    }


    private void handlePlayerContext(PlayerContext playerContext) {
        if (playerContext != null) {
            mPlayerContextButton.setText(
                    String.format(Locale.US, "%s\n%s", playerContext.title, playerContext.subtitle));
            mPlayerContextButton.setTag(playerContext);
        }

    }

    private void handlePlayerState(PlayerState playerState) {
        Drawable drawable =
                ResourcesCompat.getDrawable(
                        getResources(), R.drawable.mediaservice_shuffle, getTheme());
        if (!playerState.playbackOptions.isShuffling) {
            mToggleShuffleButton.setImageDrawable(drawable);
            DrawableCompat.setTint(mToggleShuffleButton.getDrawable(), Color.WHITE);
        } else {
            mToggleShuffleButton.setImageDrawable(drawable);
            DrawableCompat.setTint(
                    mToggleShuffleButton.getDrawable(),
                    getResources().getColor(R.color.cat_medium_green));
        }

        if (playerState.playbackOptions.repeatMode == Repeat.ALL) {
            mToggleRepeatButton.setImageResource(R.drawable.mediaservice_repeat_all);
            DrawableCompat.setTint(
                    mToggleRepeatButton.getDrawable(),
                    getResources().getColor(R.color.cat_medium_green));
        } else if (playerState.playbackOptions.repeatMode == Repeat.ONE) {
            mToggleRepeatButton.setImageResource(R.drawable.mediaservice_repeat_one);
            DrawableCompat.setTint(
                    mToggleRepeatButton.getDrawable(),
                    getResources().getColor(R.color.cat_medium_green));
        } else {
            mToggleRepeatButton.setImageResource(R.drawable.mediaservice_repeat_off);
            DrawableCompat.setTint(mToggleRepeatButton.getDrawable(), Color.WHITE);
        }
        if (playerState.track != null) {
            mPlayerStateButton.setText(
                    String.format(
                            Locale.US, "%s\n%s", playerState.track.name, playerState.track.artist.name));
            mPlayerStateButton.setTag(playerState);
            song_uri = playerState.track.uri;

        }

        if (playerState.playbackSpeed > 0) {
            mTrackProgressBar.unpause();
        } else {
            mTrackProgressBar.pause();
        }


        if (playerState.isPaused) {
            mPlayPauseButton.setImageResource(R.drawable.btn_play);
        } else {
            mPlayPauseButton.setImageResource(R.drawable.btn_pause);
        }


        if (playerState.track != null) {

            spotifyAppRemote
                    .getImagesApi()
                    .getImage(playerState.track.imageUri, Image.Dimension.LARGE)
                    .setResultCallback(
                            bitmap -> {
                                mCoverArtImageView.setImageBitmap(bitmap);
                                mImageLabel.setText(
                                        String.format(
                                                Locale.ENGLISH, "%d x %d", bitmap.getWidth(), bitmap.getHeight()));
                            });
            // Invalidate seekbar length and position
            mSeekBar.setMax((int) playerState.track.duration);
            mTrackProgressBar.setDuration(playerState.track.duration);
            mTrackProgressBar.update(playerState.playbackPosition);
        }


        mSeekBar.setEnabled(true);


    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        spotifyAppRemote
                .getPlayerApi()
                .getPlayerState()
                .setResultCallback(
                        playerState -> {
                            spotifyAppRemote
                                    .getPlayerApi()
                                    .pause()
                                    .setResultCallback(
                                            empty -> logMessage(getString(R.string.command_feedback, "pause")))
                                    .setErrorCallback(mErrorCallback);

                        });

    }


    @Override
    protected void onStop() {
        super.onStop();
        SpotifyAppRemote.disconnect(spotifyAppRemote);
        //myDataManager.setPlaying(false);
        onDisconnected();
        dismissTimerDialog();
    }

    private void onConnected() {
        for (View input : mViews) {
            input.setEnabled(true);
        }

        connectAuthorizeButton.setEnabled(false);
        connectAuthorizeButton.setText(R.string.connected);

        onSubscribedToPlayerStateButtonClicked(null);
        onSubscribedToPlayerContextButtonClicked(null);

        myDataManager.getPlayerContextLiveData().observe(this, this::handlePlayerContext);

        myDataManager.getPlayerStateLiveData().observe(this, this::handlePlayerState);
    }

    private void onConnecting() {

        connectAuthorizeButton.setEnabled(false);
        connectAuthorizeButton.setText(R.string.connecting);
    }

    private void onDisconnected() {
        for (View view : mViews) {
            view.setEnabled(false);
        }

        connectAuthorizeButton.setEnabled(true);
        connectAuthorizeButton.setText(R.string.connect);
        mCoverArtImageView.setImageResource(R.drawable.widget_placeholder);
        mPlayerContextButton.setText(R.string.title_player_context);
        mPlayerStateButton.setText(R.string.title_current_track);
        mToggleRepeatButton.clearColorFilter();
        mToggleRepeatButton.setImageResource(R.drawable.btn_repeat);
        mToggleShuffleButton.clearColorFilter();
        mToggleShuffleButton.setImageResource(R.drawable.btn_shuffle);
        mPlayerContextButton.setVisibility(View.INVISIBLE);
        subscribeToPlayerContextButton.setVisibility(View.VISIBLE);
        mPlayerStateButton.setVisibility(View.INVISIBLE);
        mSubscribeToPlayerStateButton.setVisibility(View.VISIBLE);
    }

    public void onConnectAndAuthorizedClicked(View view) {
        onConnecting();
        request = mSpotifyService.buildRequestSpotify();
        AuthorizationClient.openLoginActivity(this, REQUEST_CODE, request);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == REQUEST_CODE) {
            AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, intent);
            mSpotifyService.setAccessToken(response.getAccessToken());

            switch (response.getType()) {

                case TOKEN:
                    mSpotifyService.connect(true, this);
                    break;

                case ERROR:
                    break;

                default:

            }
        }
    }


    public void onDisconnectClicked(View v) {
        SpotifyAppRemote.disconnect(spotifyAppRemote);
        onDisconnected();
    }

    public void showCurrentPlayerContext(View view) {
        if (view.getTag() != null) {
            showDialog("PlayerContext", gson.toJson(view.getTag()));
        }
    }

    public void showCurrentPlayerState(View view) {
        if (view.getTag() != null) {
            showDialog("PlayerState", gson.toJson(view.getTag()));
        }
    }

    public void onToggleShuffleButtonClicked(View view) {
       mPlayerViewModel.onToggleShuffleButtonClicked(view);
    }

    public void onToggleRepeatButtonClicked(View view) {
        mPlayerViewModel.onToggleRepeatButtonClicked(view);
    }

    public void onSkipPreviousButtonClicked(View view) {
        mPlayerViewModel.onSkipPreviousButtonClicked(view);
    }

    public void onPlayPauseButtonClicked(View view) {
        mPlayerViewModel.onPlayPauseButtonClicked(view);
    }

    public void onSkipNextButtonClicked(View view) {
        mPlayerViewModel.onSkipNextButtonClicked(view);
    }

    public void onSeekBack(View view) {
        mPlayerViewModel.onSeekBack(view);
    }

    public void onSeekForward(View view) {
        mPlayerViewModel.onSeekForward(view);
    }



    public void onSubscribedToPlayerContextButtonClicked(View view) {


        mPlayerContextButton.setVisibility(View.VISIBLE);
        subscribeToPlayerContextButton.setVisibility(View.INVISIBLE);

    }

    public void onSubscribedToPlayerStateButtonClicked(View view) {

        mPlayerStateButton.setVisibility(View.VISIBLE);
        mSubscribeToPlayerStateButton.setVisibility(View.INVISIBLE);

    }

    private void logError(Throwable throwable) {
        Toast.makeText(this, R.string.err_generic_toast, Toast.LENGTH_SHORT).show();
        Log.e(TAG, "", throwable);
    }

    private void logMessage(String msg) {
        Log.d(TAG, msg);
    }

    private void showDialog(String title, String message) {
        new AlertDialog.Builder(this).setTitle(title).setMessage(message).create().show();
    }

    @Override
    public void onSpotifyConnected(SpotifyAppRemote spotifyAppRemote) {
        PlayerActivity.spotifyAppRemote = spotifyAppRemote;
        mPlayerViewModel.setSpotifyAppRemote(spotifyAppRemote);
        onConnected();
    }


    private class TrackProgressBar {

        private static final int LOOP_DURATION = 500;
        private final SeekBar mSeekBar;
        private final Handler mHandler;

        private final SeekBar.OnSeekBarChangeListener mSeekBarChangeListener =
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        spotifyAppRemote
                                .getPlayerApi()
                                .seekTo(seekBar.getProgress())
                                .setErrorCallback(mErrorCallback);
                    }
                };

        private final Runnable mSeekRunnable =
                new Runnable() {
                    @Override
                    public void run() {
                        int progress = mSeekBar.getProgress();
                        mSeekBar.setProgress(progress + LOOP_DURATION);
                        mHandler.postDelayed(mSeekRunnable, LOOP_DURATION);
                    }
                };

        private TrackProgressBar(SeekBar seekBar) {
            mSeekBar = seekBar;
            mSeekBar.setOnSeekBarChangeListener(mSeekBarChangeListener);
            mHandler = new Handler();
        }

        private void setDuration(long duration) {
            mSeekBar.setMax((int) duration);
        }

        private void update(long progress) {
            mSeekBar.setProgress((int) progress);
        }

        private void pause() {
            mHandler.removeCallbacks(mSeekRunnable);
        }

        private void unpause() {
            mHandler.removeCallbacks(mSeekRunnable);
            mHandler.postDelayed(mSeekRunnable, LOOP_DURATION);
        }
    }

    private BroadcastReceiver timerUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long remainingTimeMillis = intent.getLongExtra(TimerService.TIMER_UPDATE_EXTRA, 0);
            isTimerRunning = intent.getBooleanExtra(TimerService.TIMER_UPDATE_EXTRA2, false);
            updateTimerUI(remainingTimeMillis);
            if (!isTimerRunning) {
                stopTimerService();
                spotifyAppRemote
                        .getPlayerApi()
                        .getPlayerState()
                        .setResultCallback(
                                playerState -> {
                                    spotifyAppRemote
                                            .getPlayerApi()
                                            .pause()
                                            .setResultCallback(
                                                    empty -> logMessage(getString(R.string.command_feedback, "pause")))
                                            .setErrorCallback(mErrorCallback);

                                });
                finish();
            }
        }
    };

    private void updateTimerUI(long remainingTimeMillis) {
        int hours = (int) (remainingTimeMillis / 3600000);
        int minutes = (int) (remainingTimeMillis % 3600000 / 60000);
        int seconds = (int) (remainingTimeMillis % 3600000 % 60000 / 1000);
        String hoursStr = String.valueOf(hours);
        String minutesStr = String.valueOf(minutes);
        String secondsStr = String.valueOf(seconds);
        if (hours < 10) {
            hoursStr = "0" + hoursStr;
        }
        if (minutes < 10) {
            minutesStr = "0" + minutesStr;
        }
        if (seconds < 10) {
            secondsStr = "0" + secondsStr;
        }
        timeTextview.setText(hoursStr + ":" + minutesStr + ":" + secondsStr);

    }

    private void startTimerService(long durationInMillis) {
        Intent serviceIntent = new Intent(this, TimerService.class);
        serviceIntent.putExtra("duration", durationInMillis);
        startService(serviceIntent);
        timerWorkingIcon.setVisibility(View.VISIBLE);

    }

    private void stopTimerService() {
        Intent serviceIntent = new Intent(this, TimerService.class);
        stopService(serviceIntent);
        timerWorkingIcon.setVisibility(View.INVISIBLE);
    }

    private void buildTimerDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.dialog_timer, null);
        timerDialog = new Dialog(this, R.style.dialog1);
        timerDialog.setContentView(view);
        startTimerButton = view.findViewById(R.id.startButton);
        resetTimerButton = view.findViewById(R.id.resetButton);
        timeTextview = view.findViewById(R.id.timeTextView);
        hoursEditText = view.findViewById(R.id.hoursEditText);
        minutesEditText = view.findViewById(R.id.minutesEditText);
        secondsEditText = view.findViewById(R.id.secondsEditText);
        hoursMinutesTextView = view.findViewById(R.id.hoursMinutesSeparatorTextView);
        minutesSecondsTextView = view.findViewById(R.id.minutesSecondsSeparatorTextView);
        if (isTimerRunning) {
            hoursEditText.setVisibility(View.INVISIBLE);
            minutesEditText.setVisibility(View.INVISIBLE);
            secondsEditText.setVisibility(View.INVISIBLE);
            hoursMinutesTextView.setVisibility(View.INVISIBLE);
            minutesSecondsTextView.setVisibility(View.INVISIBLE);
            timeTextview.setVisibility(View.VISIBLE);
            timerWorkingIcon.setVisibility(View.VISIBLE);
        }

        resetTimerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeTextview.setText("00:00:00");
                hoursEditText.setVisibility(View.VISIBLE);
                minutesEditText.setVisibility(View.VISIBLE);
                secondsEditText.setVisibility(View.VISIBLE);
                hoursMinutesTextView.setVisibility(View.VISIBLE);
                minutesSecondsTextView.setVisibility(View.VISIBLE);
                timeTextview.setVisibility(View.INVISIBLE);
                timerWorkingIcon.setVisibility(View.INVISIBLE);
                stopTimerService();


            }
        });

        startTimerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hoursEditText.setVisibility(View.INVISIBLE);
                minutesEditText.setVisibility(View.INVISIBLE);
                secondsEditText.setVisibility(View.INVISIBLE);
                hoursMinutesTextView.setVisibility(View.INVISIBLE);
                minutesSecondsTextView.setVisibility(View.INVISIBLE);
                timeTextview.setVisibility(View.VISIBLE);
                hoursStr = hoursEditText.getText().toString();
                minutesStr = minutesEditText.getText().toString();
                secondsStr = secondsEditText.getText().toString();
                hours = Integer.parseInt(hoursStr);
                minutes = Integer.parseInt(minutesStr);
                seconds = Integer.parseInt(secondsStr);
                int totalMilis = (hours * 3600 + minutes * 60 + seconds) * 1000;
                startTimerService(totalMilis);


            }
        });
        timerDialog.show();

    }

    void buildVolumeDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.dialog_volume, null);
        volumeDialog = new Dialog(this, R.style.dialog1);
        volumeDialog.setContentView(view);
        volumeSwitch = view.findViewById(R.id.volumeSwitch);
        if (myDataManager.getVolumeControlMode()) {
            volumeSwitch.setChecked(true);
        } else {
            volumeSwitch.setChecked(false);
        }
        volumeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                myDataManager.setVolumeControlMode(isChecked);
            }
        });

        volumeDialog.show();
    }

    private void dismissTimerDialog() {
        if (timerDialog != null && timerDialog.isShowing()) {
            timerDialog.dismiss();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        dismissTimerDialog();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(timerUpdateReceiver);
        dismissTimerDialog();

    }
}

