package com.example.iome.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.util.Log;

import com.example.iome.database.Score;
import com.example.iome.database.ScoreRepository;
import com.example.iome.user_profile.ui.user_profile.SongElement;
import com.example.iome.utility.MyDataManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.types.PlayerContext;
import com.spotify.protocol.types.PlayerState;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class SpotifyService extends Service implements ScoreRepository.OnScoresLoadedListener {
    public static final String TAG = "SpotifyService";
    private SpotifyAppRemote mSpotifyAppRemote;
    private final String CLIENT_ID = "286c54f3c9ae463f8aea76bb4b3c5cd2";
    private final String REDIRECT_URI = "wavecontrol://callback";
    private static SpotifyService instance;
    private SpotifyCallback callback;

    private HandlerThread serviceThread;
    private Handler serviceHandler;
    private MyDataManager dataManager;

    private String accessToken;

    Subscription<PlayerState> playerStateSubscription;
    Subscription<PlayerContext> playerContextSubscription;

    private String currentTrackUri;
    private String previousTrackUri;

    ScoreRepository repository;
    private List<SongElement> songElements;
    private List<SongElement> firebaseElement;


    public static SpotifyService getInstance() {
        if (instance == null) {
            instance = new SpotifyService();
        }
        return instance;
    }

    public SpotifyService() {
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return accessToken;
    }



    public void setCallback(SpotifyCallback callback) {
        this.callback = callback;
    }



    @Override
    public void onCreate() {
        super.onCreate();
        serviceThread = new HandlerThread("SpotifyServiceThread");
        serviceThread.start();
        serviceHandler = new Handler(serviceThread.getLooper());
        dataManager = MyDataManager.getInstance();
        repository = new ScoreRepository(getApplication());
        repository.setOnScoresLoadedListener(this);
        songElements = new ArrayList<>();
        firebaseElement = new ArrayList<>();


        instance = this;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SpotifyAppRemote.disconnect(mSpotifyAppRemote);
        serviceThread.quit();
        instance = null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    public void setmSpotifyAppRemote(SpotifyAppRemote mSpotifyAppRemote) {
        this.mSpotifyAppRemote = mSpotifyAppRemote;
    }

    public AuthorizationRequest buildRequestSpotify() {
        SpotifyAppRemote.disconnect(mSpotifyAppRemote);

        AuthorizationRequest.Builder builder =
                new AuthorizationRequest.Builder(CLIENT_ID, AuthorizationResponse.Type.TOKEN, REDIRECT_URI);

        builder.setScopes(new String[]{"streaming", "user-modify-playback-state", "user-top-read"});
        AuthorizationRequest request = builder.build();

        return request;
    }

    public void disconnectFromSpotify() {
        SpotifyAppRemote.disconnect(mSpotifyAppRemote);
    }

    public void connect(boolean showAuthView, Context context) {
        SpotifyAppRemote.disconnect(mSpotifyAppRemote);

        SpotifyAppRemote.connect(
                context,
                new ConnectionParams.Builder(CLIENT_ID)
                        .setRedirectUri(REDIRECT_URI)
                        .showAuthView(showAuthView)
                        .build(),
                new Connector.ConnectionListener() {
                    @Override
                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        mSpotifyAppRemote = spotifyAppRemote;
                        instance.setmSpotifyAppRemote(spotifyAppRemote);
                        if (callback != null) {
                            callback.onSpotifyConnected(mSpotifyAppRemote);
                        }
                        dataManager.setSpotifyConnectionStateLiveData("Connected to Spotify");
                        subscribeToPlayerContext();
                        subscribeToPlayerState();

                    }

                    @Override
                    public void onFailure(Throwable error) {
                        if (dataManager != null) {
                            dataManager.setSpotifyConnectionStateLiveData("Connection to Spotify failed. Please try again.");
                        }

                    }
                });

    }

    private final Subscription.EventCallback<PlayerContext> playerContextEventCallback =
            new Subscription.EventCallback<PlayerContext>() {
                @Override
                public void onEvent(PlayerContext playerContext) {
                    dataManager.setPlayerContextLiveData(playerContext);

                }
            };


    private class MyPlayerStateEventCallback implements Subscription.EventCallback<PlayerState> {
        private ScoreRepository.OnScoresLoadedListener listener;

        public void setListener(ScoreRepository.OnScoresLoadedListener listener) {
            this.listener = listener;
        }

        @Override
        public void onEvent(PlayerState playerState) {
            dataManager.setPlayerStateLiveData(playerState);
            if (playerState.track != null) {

                currentTrackUri = playerState.track.uri;

                if (!currentTrackUri.equals(previousTrackUri)) {
                    dataManager.setSongUri(currentTrackUri);
                    previousTrackUri = currentTrackUri;
                    repository.getLastSongsScores(dataManager.getDataType(), 3, listener);
                    //   repository.getLastSongScore(currentTrackUri,dataManager.getDataType(),listener);
                }
            }
            if (playerState.isPaused) {
                dataManager.setPlaying(false);
            } else {
                dataManager.setPlaying(true);
            }


        }


    }


    public void subscribeToPlayerContext() {
        if (playerContextSubscription != null && !playerContextSubscription.isCanceled()) {
            playerContextSubscription.cancel();
            playerContextSubscription = null;
        }


        playerContextSubscription =
                (Subscription<PlayerContext>)
                        mSpotifyAppRemote
                                .getPlayerApi()
                                .subscribeToPlayerContext()
                                .setEventCallback(playerContextEventCallback)
                                .setErrorCallback(
                                        throwable -> {
                                            Log.e(TAG, "", throwable);
                                        });
    }

    public void subscribeToPlayerState() {

        if (playerStateSubscription != null && !playerStateSubscription.isCanceled()) {
            playerStateSubscription.cancel();
            playerStateSubscription = null;
        }

        MyPlayerStateEventCallback playerStateEventCallback = new MyPlayerStateEventCallback();


        playerStateSubscription =
                (Subscription<PlayerState>)
                        mSpotifyAppRemote
                                .getPlayerApi()
                                .subscribeToPlayerState()
                                .setEventCallback(playerStateEventCallback)
                                .setLifecycleCallback(
                                        new Subscription.LifecycleCallback() {
                                            @Override
                                            public void onStart() {
                                                Log.d(TAG, "onStart");
                                            }

                                            @Override
                                            public void onStop() {
                                                Log.d(TAG, "onStop");
                                            }
                                        })
                                .setErrorCallback(
                                        throwable -> {
                                            Log.e(TAG, "", throwable);
                                        });
        playerStateEventCallback.setListener(this);

    }

    public List<SongElement> createTopSongsList(List<Score> list, String type) {

        List<SongElement> songElements = new ArrayList<>();
        for (Score score : list) {
            if (score.getSong_uri() == null) {
                continue;
            }

            SongElement existingSongElement = findSongElementByName(songElements, score.getSong_uri());

            if (existingSongElement == null) {
                songElements.add(new SongElement(score.getSong_uri(), type, 1, score.getValue(), score.getValue()));
            } else {
                existingSongElement.setNumberOfTimesPlayed(existingSongElement.getNumberOfTimesPlayed() + 1);
                existingSongElement.setCurrentSum(existingSongElement.getCurrentSum() + score.getValue());
                existingSongElement.setCurrentAverage(existingSongElement.getCurrentSum() / existingSongElement.getNumberOfTimesPlayed());
            }

        }


        return songElements;

    }

    private SongElement findSongElementByName(List<SongElement> songElements, String songName) {
        for (SongElement songElement : songElements) {
            if (songElement.getSongUri().equals(songName)) {
                return songElement;
            }
        }
        return null;
    }

    private boolean areCurrentAveragesAscending(List<SongElement> songElements) {
        if (songElements == null || songElements.isEmpty()) {
            // Handle the case when the list is null or empty
            return false;
        }

        float previousAverage = songElements.get(0).getCurrentAverage();

        for (int i = 1; i < songElements.size(); i++) {
            float currentAverage = songElements.get(i).getCurrentAverage();
            if (currentAverage < previousAverage) {

                return false;
            }
            previousAverage = currentAverage;
        }


        return true;
    }

    private class RequestTopTracks extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            String topSongUri = null;

            String urlString = "https://api.spotify.com/v1/me/top/tracks?time_range=short_term&limit=10";


            try {

                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();


                urlConnection.setRequestProperty("Authorization", "Bearer " + accessToken);


                String response = readResponse(urlConnection);
                topSongUri = processResponses(response);


            } catch (IOException e) {
                e.printStackTrace();
            }

            return topSongUri;
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
                Log.e(TAG, "Error in response: " + responseCode);
            }

            return responseString;
        }

        private String processResponses(String response) {
            String songUri = null;
            Random random = new Random();
            int randomIndex = 0;
            if (response != null) {
                try {
                    JSONObject responseObject = new JSONObject(response);
                    JSONArray itemsArray = responseObject.getJSONArray("items");
                    randomIndex = random.nextInt(itemsArray.length());
                    songUri = itemsArray.getJSONObject(randomIndex).getString("id");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return songUri;
        }


        @Override
        protected void onPostExecute(String topSongUri) {
            super.onPostExecute(topSongUri);
            new AddTrackToPlaybackQueue().execute(topSongUri);

        }


    }


    private class AddSongElementToFirebase extends AsyncTask<SongElement, Void, Void> {

        private DatabaseReference databaseReference;

        @Override
        protected Void doInBackground(SongElement... songElements) {
            SongElement songElement = songElements[0];
            databaseReference = FirebaseDatabase.getInstance().getReference();
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            String entryId = databaseReference.push().getKey();
            databaseReference.child("user_scores").child(userId).child(songElement.getType()).child(entryId).setValue(songElement);
            return null;
        }
    }


    private class AddTrackToPlaybackQueue extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... songUri) {
            String topSongUri = songUri[0];

            String urlString = "https://api.spotify.com/v1/me/player/queue?uri=spotify:track:" + topSongUri;


            try {

                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();



                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Authorization", "Bearer " + accessToken);


                int responseCode = urlConnection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_NO_CONTENT) {
                    Log.d(TAG, "Song added to queue");
                } else {
                    Log.e(TAG, "Error in response: " + responseCode);
                }


            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

    }


    public interface SpotifyCallback {
        void onSpotifyConnected(SpotifyAppRemote spotifyAppRemote);
    }

    @Override
    public void onLastSongsLoaded(List<Score> scores) {
        songElements = createTopSongsList(scores, dataManager.getDataType());
        if (!songElements.isEmpty() && songElements.get(songElements.size() - 1) != null) {
            new AddSongElementToFirebase().execute(songElements.get(songElements.size() - 1));
        }
        boolean isAscending = areCurrentAveragesAscending(songElements);
        if (isAscending && songElements.size() > 0) {
            new RequestTopTracks().execute();
        }

    }


}
