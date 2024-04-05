package com.example.iome.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "score_table")
public class Score {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private final String type;
    private final float value;
    private final long timestamp;
    private final String song_uri;

    public Score(String type, float value, long timestamp, String song_uri) {
        this.type = type;
        this.value = value;
        this.timestamp = timestamp;
        this.song_uri = song_uri;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public float getValue() {
        return value;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getSong_uri() {
        return song_uri;
    }
}

