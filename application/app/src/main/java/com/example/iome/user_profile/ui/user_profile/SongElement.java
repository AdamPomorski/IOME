package com.example.iome.user_profile.ui.user_profile;

public class SongElement {
    private String songUri;
    private String type;
    private int numberOfTimesPlayed;
    private float currentAverage;
    private float currentSum;

    public SongElement() {
    }

    public SongElement(String songUri, String type, int numberOfTimesPlayed,float currentSum, float currentAverage) {
        this.songUri = songUri;
        this.type = type;
        this.numberOfTimesPlayed = numberOfTimesPlayed;
        this.currentSum = currentSum;
        this.currentAverage = currentAverage;
    }

    public String getSongUri() {
        return songUri;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getNumberOfTimesPlayed() {
        return numberOfTimesPlayed;
    }

    public void setNumberOfTimesPlayed(int numberOfTimesPlayed) {
        this.numberOfTimesPlayed = numberOfTimesPlayed;
    }

    public float getCurrentAverage() {
        return currentAverage;
    }

    public void setCurrentAverage(float currentAverage) {
        this.currentAverage = currentAverage;
    }

    public float getCurrentSum() {
        return currentSum;
    }

    public void setCurrentSum(float currentSum) {
        this.currentSum = currentSum;
    }
}

