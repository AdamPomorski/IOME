package com.example.iome.user_profile.ui.user_profile;

import java.util.List;

public class SongInformation {
    private String songName;
    private List<String> artists;
    private String pictureUri;
    private String type;
    private float averageScore;
    private String songUri;

    public SongInformation(String songName, List<String> artists, String pictureUri) {
        this.songName = songName;
        this.artists = artists;
        this.pictureUri = pictureUri;
    }


    public SongInformation(String songName, List<String> artists, String pictureUri, String songUri) {
        this.songName = songName;
        this.artists = artists;
        this.pictureUri = pictureUri;
        this.songUri = songUri;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public String getSongName() {
        return songName;
    }


    public List<String> getArtists() {
        return artists;
    }


    public String getPictureUri() {
        return pictureUri;
    }


    public String getSongUri() {
        return songUri;
    }


}
