package com.example.iome.user_profile.ui.user_profile;

import android.app.Application;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.HandlerThread;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;


import com.example.iome.services.SpotifyService;
import com.example.iome.database.Score;
import com.example.iome.database.ScoreRepository;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class ChartViewModel extends AndroidViewModel implements ScoreRepository.OnScoresLoadedListener {
    public ScoreRepository repository;
    private List<Score> scores;
    private CountDownLatch latch;
    private OnDataLoadedListener listener;
    private HandlerThread handlerThread;
    private Handler viewModelHandler;
    private Object[][] chartData;
    private SpotifyService spotifyService;
    private String localType;
    private List<SongInformation> songInformations;


    public ChartViewModel(@NonNull Application application) {
        super(application);
        repository = new ScoreRepository(application);
        repository.setOnScoresLoadedListener(this);
        handlerThread = new HandlerThread("ChartViewModelThread");
        handlerThread.start();
        viewModelHandler = new Handler(handlerThread.getLooper());
        spotifyService = SpotifyService.getInstance();
        songInformations = new ArrayList<>();


    }
    public void setOnDataLoadedListener(OnDataLoadedListener listener) {
        this.listener = listener;
    }



    public void getChartData(String type, long start_timestamp, long end_timestamp) {
        viewModelHandler.post(() -> {

            latch = new CountDownLatch(1);

            localType = type;


            repository.getScores(type, start_timestamp, end_timestamp, this);

            try {

                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            listener.onDataLoaded(chartData, songInformations);
        });
    }


    @Override
    public void onScoresLoaded(List<Score> scores) {
        List<SongElement> songElements = new ArrayList<>();
        this.scores = scores;
        chartData = new Object[scores.size()][3];
        for (Score score : scores) {
            chartData[scores.indexOf(score)][0] = score.getTimestamp();
            chartData[scores.indexOf(score)][1] = score.getValue();
            chartData[scores.indexOf(score)][2] = score.getSong_uri();

        }
        songElements = spotifyService.createTopSongsList(scores,localType);
        new RequestTrackDetailsTask().execute(songElements);


    }

    public interface OnDataLoadedListener {
        void onDataLoaded( Object[][] chartData, List<SongInformation> songInformations);
    }

    private class RequestTrackDetailsTask extends AsyncTask<List<SongElement>, Void, List<SongInformation>> {

        @Override
        protected List<SongInformation> doInBackground(List<SongElement>... list) {
            List<SongElement> songElements = list[0];
            List<SongInformation> songInformationList = new ArrayList<>();

            String apiUrl = "https://api.spotify.com/v1/tracks?ids=";


            // Build URLs for attentionSongs
            for (SongElement songElement : songElements) {
                apiUrl = apiUrl + "," + getTrackId(songElement.getSongUri());
            }



            try {

                URL url = new URL(apiUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setRequestProperty("Authorization", "Bearer " + spotifyService.getAccessToken());

                String response = readResponse(urlConnection);

                songInformationList = processResponses(response);


            } catch (IOException e) {
                e.printStackTrace();
            }

            return songInformationList;
        }

        private String readResponse(HttpURLConnection urlConnection) throws IOException {

            int responseCode = urlConnection.getResponseCode();
            String responseString = null;

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                reader.close();
                responseString = response.toString();

            } else {

            }

            return responseString;
        }

        private List<SongInformation> processResponses(String response) {
            List<SongInformation> songInformationList = new ArrayList<>();
            if (response != null) {
                try {
                    JSONObject responseObject = new JSONObject(response);
                    JSONArray tracksArray = responseObject.getJSONArray("tracks");

                    for (int i = 1; i < tracksArray.length(); i++) {
                        JSONObject trackObject = tracksArray.getJSONObject(i);
                        String songName = trackObject.getString("name");
                        String songUri = trackObject.getString("uri");
                        List<String> artistNames = parseArtistNames(trackObject.toString());
                        String albumImageUrl = trackObject.getJSONObject("album")
                                .getJSONArray("images")
                                .getJSONObject(0)
                                .getString("url");



                        songInformationList.add(new SongInformation(songName, artistNames, albumImageUrl,songUri));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return songInformationList;
        }

        @Override
        protected void onPostExecute(List<SongInformation> songInformationList) {

            songInformations = songInformationList;
            latch.countDown();



        }
        private  List<String> parseArtistNames(String response) {
            List<String> artistNames = new ArrayList<>();

            try {
                JSONObject jsonObject = new JSONObject(response);


                JSONArray artistsArray = jsonObject.getJSONArray("artists");

                for (int i = 0; i < artistsArray.length(); i++) {

                    String artistName = artistsArray.getJSONObject(i).getString("name");
                    artistNames.add(artistName);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return artistNames;
        }
        private  String getTrackId(String trackUri) {

            return trackUri.split(":")[2];
        }
    }

}