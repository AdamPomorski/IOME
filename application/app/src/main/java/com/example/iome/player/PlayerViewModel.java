package com.example.iome.player;

import android.util.Log;
import android.view.View;

import androidx.lifecycle.ViewModel;


import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.client.ErrorCallback;

public class PlayerViewModel extends ViewModel {
    SpotifyAppRemote spotifyAppRemote;
    private final ErrorCallback errorCallback = this::logError;
    private static final String TAG = com.example.iome.player.PlayerViewModel.class.getSimpleName();


    public PlayerViewModel() {


    }

    public void setSpotifyAppRemote(SpotifyAppRemote spotifyAppRemote) {
        this.spotifyAppRemote = spotifyAppRemote;
    }

    public void onToggleShuffleButtonClicked(View view) {
        spotifyAppRemote
                .getPlayerApi()
                .toggleShuffle()
                .setResultCallback(
                        empty -> logMessage("toggle shuffle"))
                .setErrorCallback(errorCallback);
    }

    public void onToggleRepeatButtonClicked(View view) {
        spotifyAppRemote
                .getPlayerApi()
                .toggleRepeat()
                .setResultCallback(
                        empty -> logMessage("toggle repeat"))
                .setErrorCallback(errorCallback);
    }

    public void onSkipPreviousButtonClicked(View view) {
        spotifyAppRemote
                .getPlayerApi()
                .skipPrevious()
                .setResultCallback(
                        empty -> logMessage("skip previous"))
                .setErrorCallback(errorCallback);
    }

    public void onPlayPauseButtonClicked(View view) {
        spotifyAppRemote
                .getPlayerApi()
                .getPlayerState()
                .setResultCallback(
                        playerState -> {
                            if (playerState.isPaused) {
                                spotifyAppRemote
                                        .getPlayerApi()
                                        .resume()
                                        .setResultCallback(
                                                empty -> logMessage("play"))
                                        .setErrorCallback(errorCallback);
                            } else {
                                spotifyAppRemote
                                        .getPlayerApi()
                                        .pause()
                                        .setResultCallback(
                                                empty -> logMessage("pause"))
                                        .setErrorCallback(errorCallback);
                            }
                        });
    }

    public void onSkipNextButtonClicked(View view) {
        spotifyAppRemote
                .getPlayerApi()
                .skipNext()
                .setResultCallback(data -> logMessage("skip next"))
                .setErrorCallback(errorCallback);
    }

    public void onSeekBack(View view) {
        spotifyAppRemote
                .getPlayerApi()
                .seekToRelativePosition(-15000)
                .setResultCallback(data -> logMessage("seek back"))
                .setErrorCallback(errorCallback);
    }

    public void onSeekForward(View view) {
        spotifyAppRemote
                .getPlayerApi()
                .seekToRelativePosition(15000)
                .setResultCallback(data -> logMessage("seek fwd"))
                .setErrorCallback(errorCallback);
    }


    private void logError(Throwable throwable) {
        Log.e(TAG, "", throwable);
    }

    private void logMessage(String msg) {
        Log.e(TAG, msg);
    }
}


