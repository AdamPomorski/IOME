package com.example.iome.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ScoreDao {
    @Insert
    void insert(Score score);

    @Update
    void update(Score score);
    @Delete
    void delete(Score score);
    @Query("SELECT * FROM score_table WHERE type = :type AND timestamp >= :start_timestamp AND timestamp <= :end_timestamp")
    List<Score> getScores(String type, long start_timestamp, long end_timestamp);

    @Query("SELECT * FROM score_table WHERE type = :type")
    List<Score> getScoresByType(String type);

    @Query("SELECT * FROM score_table WHERE song_uri IN (SELECT song_uri FROM score_table WHERE type = :type GROUP BY song_uri ORDER BY MAX(timestamp) DESC LIMIT :limit)")
    List<Score> getLastSongsScores(String type, int limit);
}



