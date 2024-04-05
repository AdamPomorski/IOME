package com.example.iome.database;

import android.app.Application;
import android.os.AsyncTask;

import java.util.List;

public class ScoreRepository {
    private ScoreDao scoreDao;
    private OnScoresLoadedListener listener;
    private ProjectDatabase database;


    public ScoreRepository(Application application){
        database = ProjectDatabase.getInstance(application.getApplicationContext());
        scoreDao = database.scoreDao();


    }

    public void setScoreDao(ScoreDao scoreDao) {
        this.scoreDao = scoreDao;
    }

    public void setDatabase(ProjectDatabase database) {
        this.database = database;
    }

    public void setOnScoresLoadedListener(OnScoresLoadedListener listener) {
        this.listener = listener;
    }

    public void insert(Score score){
        new InsertScoreAsyncTask(scoreDao).execute(score);
    }

    public void update(Score score){
        new UpdateScoreAsyncTask(scoreDao).execute(score);
    }
    public void delete(Score score){
        new DeleteScoreAsyncTask(scoreDao).execute(score);
    }
    public void getScores(String type, long start_timestamp, long end_timestamp, OnScoresLoadedListener listener){
        new GetScoresAsyncTask(scoreDao, listener).execute(type, start_timestamp, end_timestamp);
    }
    public void getScoresByType(String type, OnScoresLoadedListener listener){
        new GetScoresByTypeAsyncTask(scoreDao, listener).execute(type);
    }
    public void getLastSongsScores(String type,int limit, OnScoresLoadedListener listener){
        new GetLastSongsScoresAsyncTask(scoreDao,limit, listener).execute(type);
    }




    private static class InsertScoreAsyncTask extends AsyncTask<Score,Void,Void>{
        private ScoreDao scoreDao;

        private InsertScoreAsyncTask(ScoreDao scoreDao) {
            this.scoreDao = scoreDao;
        }

        @Override
        protected Void doInBackground(Score... scores) {
            scoreDao.insert(scores[0]);
            return null;
        }
    }

    private static class UpdateScoreAsyncTask extends AsyncTask<Score,Void,Void>{
        private ScoreDao scoreDao;

        private UpdateScoreAsyncTask(ScoreDao scoreDao) {
            this.scoreDao = scoreDao;
        }

        @Override
        protected Void doInBackground(Score... scores) {
            scoreDao.update(scores[0]);
            return null;
        }
    }
    private static class DeleteScoreAsyncTask extends AsyncTask<Score,Void,Void>{
        private ScoreDao scoreDao;

        private DeleteScoreAsyncTask(ScoreDao scoreDao) {
            this.scoreDao = scoreDao;
        }

        @Override
        protected Void doInBackground(Score... scores) {
            scoreDao.delete(scores[0]);
            return null;
        }
    }

    public static class GetScoresAsyncTask extends AsyncTask<Object, Void, List<Score>> {
        private ScoreDao scoreDao;
        private OnScoresLoadedListener listener;

        public GetScoresAsyncTask(ScoreDao scoreDao, OnScoresLoadedListener listener) {
            this.scoreDao = scoreDao;
            this.listener = listener;
        }

        @Override
        public List<Score> doInBackground(Object... params) {
            String type = (String) params[0];
            long startTimestamp = (long) params[1];
            long endTimestamp = (long) params[2];


            return scoreDao.getScores(type, startTimestamp, endTimestamp);
        }

        @Override
        protected void onPostExecute(List<Score> scores) {

            listener.onScoresLoaded(scores);
        }
    }

    private static class GetScoresByTypeAsyncTask extends AsyncTask<Object, Void, List<Score>> {
        private ScoreDao scoreDao;
        private OnScoresLoadedListener listener;

        GetScoresByTypeAsyncTask(ScoreDao scoreDao, OnScoresLoadedListener listener) {
            this.scoreDao = scoreDao;
            this.listener = listener;
        }

        @Override
        protected List<Score> doInBackground(Object... params) {
            String type = (String) params[0];


            return scoreDao.getScoresByType(type);
        }

        @Override
        protected void onPostExecute(List<Score> scores) {

            listener.onScoresLoadedByType(scores);
        }
    }
    private static class GetLastSongsScoresAsyncTask extends AsyncTask<Object, Void, List<Score>> {
        private ScoreDao scoreDao;
        private OnScoresLoadedListener listener;
        private int limit;

        GetLastSongsScoresAsyncTask(ScoreDao scoreDao,int limit, OnScoresLoadedListener listener) {
            this.scoreDao = scoreDao;
            this.listener = listener;
            this.limit = limit;

        }

        @Override
        protected List<Score> doInBackground(Object... params) {
            String type = (String) params[0];


            return scoreDao.getLastSongsScores(type, limit);
        }

        @Override
        protected void onPostExecute(List<Score> scores) {

            listener.onLastSongsLoaded(scores);
        }
    }


    public interface OnScoresLoadedListener {

        default void onScoresLoaded(List<Score> scores){};
        default void onScoresLoadedByType(List<Score> scores){};
        default void onLastSongsLoaded(List<Score> scores){};

    }



}
