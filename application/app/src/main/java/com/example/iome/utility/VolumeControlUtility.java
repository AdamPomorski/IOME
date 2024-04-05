package com.example.iome.utility;


import android.content.Context;
import android.media.AudioManager;

public class VolumeControlUtility {
    private static final int WINDOW_SIZE = 5;
    private float[] values = new float[WINDOW_SIZE];
    private int currentIndex = 0;
    private float previousAverage = 0;
    private int badPackets = 0;
    private Context context;
    private int volumeJump = 1;
    private int maxVolume = 13;
    private int minVolume = 2;
    private float averageDifferential = 3.0f;
    private static float THRESHOLD = 70.0f;

    private AudioManager audioManager;


    public VolumeControlUtility(Context context) {
        this.context = context;

    }

    public float processAndCheckVolume(int sensorValue, boolean volumeControlMode) {
        if (sensorValue == 0) {
            badPackets++;
            return -1;
        }
        if (currentIndex <= WINDOW_SIZE) {

            values[currentIndex] = sensorValue;

            currentIndex = (currentIndex + 1) % WINDOW_SIZE;
        }
        if (currentIndex == WINDOW_SIZE - 1) {
            float currentAverage = calculateAverage();


                if (currentAverage - previousAverage > averageDifferential&&currentAverage<THRESHOLD) {
                    if(volumeControlMode) {
                        adjustVolume(true);
                    }
                    else {
                        adjustVolume(false);
                    }
                } else if (currentAverage - previousAverage < -averageDifferential&&currentAverage<THRESHOLD) {
                    if(volumeControlMode) {
                        adjustVolume(false);
                    }
                    else {
                        adjustVolume(true);
                    }
                }

            previousAverage = currentAverage;
            badPackets = 0;
            currentIndex = 0;
            return currentAverage;
        }
        return 0;
    }

    private float calculateAverage() {
        float sum = 0;
        int correctPackets = WINDOW_SIZE - badPackets;
        for (int i = 0; i < correctPackets; i++) {
            sum += values[i];
        }
        return sum / correctPackets;
    }

    private void adjustVolume(boolean turnUp){
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        int newVolume;
        if (turnUp) {
            newVolume = currentVolume + volumeJump;
        } else {
            newVolume = currentVolume - volumeJump;
        }

        if (newVolume > maxVolume) {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume, 0);
        } else if (newVolume < minVolume) {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, minVolume, 0);
        } else {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, newVolume, 0);
        }

    }
}

