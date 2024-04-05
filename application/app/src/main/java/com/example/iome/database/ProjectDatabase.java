package com.example.iome.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.RoomDatabase;



@Database(entities = {Score.class}, version = 3)
public abstract class ProjectDatabase extends RoomDatabase {

    private static ProjectDatabase instance;

    public abstract ScoreDao scoreDao();


    public static synchronized ProjectDatabase getInstance(Context context){
        if(instance == null){
            instance = androidx.room.Room.databaseBuilder(context.getApplicationContext(),
                    ProjectDatabase.class, "project_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }




}
