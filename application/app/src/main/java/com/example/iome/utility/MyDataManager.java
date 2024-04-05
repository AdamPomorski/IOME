package com.example.iome.utility;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.spotify.protocol.types.PlayerContext;
import com.spotify.protocol.types.PlayerState;

public class MyDataManager {
    private MutableLiveData<String> spotifyConnectionStateLiveData = new MutableLiveData<>();
    private MutableLiveData<String> headbandConnectionStateLiveData = new MutableLiveData<>();
    private MutableLiveData<Integer> meditationLiveData = new MutableLiveData<>();
    private MutableLiveData<Integer> attentionLiveData = new MutableLiveData<>();
    private MutableLiveData<Float> meditationAverageLiveData = new MutableLiveData<>();
    private MutableLiveData<Float> attentionAverageLiveData = new MutableLiveData<>();
    private MutableLiveData<PlayerState> playerStateLiveData = new MutableLiveData<>();
    private MutableLiveData<PlayerContext> playerContextLiveData = new MutableLiveData<>();
    private String songUri;
    private boolean isPlaying = false;
    private boolean volumeControlMode = true;
    private String dataType = null;
    private static MyDataManager instance;


    public static MyDataManager getInstance() {
        if (instance == null) {
            instance = new MyDataManager();
        }
        return instance;
    }

    public MyDataManager() {
        meditationLiveData.setValue(0);
        attentionLiveData.setValue(0);
        meditationAverageLiveData.setValue(0f);
        attentionAverageLiveData.setValue(0f);
    }

    public boolean getVolumeControlMode() {
        return volumeControlMode;
    }

    public void setVolumeControlMode(boolean volumeControlMode) {
        this.volumeControlMode = volumeControlMode;
    }

    public String getSongUri() {
        return songUri;
    }

    public void setSongUri(String songUri) {
        this.songUri = songUri;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public MutableLiveData<PlayerState> getPlayerStateLiveData() {
        return playerStateLiveData;
    }

    public void setPlayerStateLiveData(PlayerState playerState) {
        playerStateLiveData.setValue(playerState);
    }

    public MutableLiveData<PlayerContext> getPlayerContextLiveData() {
        return playerContextLiveData;
    }

    public void setPlayerContextLiveData(PlayerContext playerContext) {
        playerContextLiveData.setValue(playerContext);
    }


    public LiveData<String> getSpotifyConnectionStateLiveData() {
        return spotifyConnectionStateLiveData;
    }

    public void setSpotifyConnectionStateLiveData(String state) {
        spotifyConnectionStateLiveData.postValue(state);
    }

    public LiveData<String> getHeadbandConnectionStateLiveData() {
        return headbandConnectionStateLiveData;
    }

    public void setHeadbandConnectionStateLiveData(String state) {
        headbandConnectionStateLiveData.postValue(state);
    }

    public LiveData<Integer> getMeditationLiveData() {
        return meditationLiveData;
    }

    public void setMeditationLiveData(int meditationValue) {
        meditationLiveData.setValue(meditationValue);
    }
    public LiveData<Integer> getAttentionLiveData() {
        return attentionLiveData;
    }

    public void setAttentionLiveData(int attentionValue) {
        attentionLiveData.setValue(attentionValue);
    }

}
