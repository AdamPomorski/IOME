package com.example.iome.user_profile.ui.user_profile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.iome.R;
import com.example.iome.services.SpotifyService;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TopSongsFragment extends Fragment implements TopSongsViewModel.onSongsListsLoadedListener{

    private static final String TAG = com.example.iome.user_profile.ui.user_profile.TopSongsFragment.class.getSimpleName();
    private TopSongsViewModel mViewModel;
    private List<SongElement> meditationSongs, attentionSongs;
    private List<SongInformation> medSongInformationList, attSongInformationList;
    private SpotifyService spotifyService;
    private RecyclerView attentionRecyclerView, meditationRecyclerView;





    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(TopSongsViewModel.class);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_top_songs, container, false);
        mViewModel.setListener(this);
        mViewModel.getSongsLists();
        medSongInformationList = new ArrayList<>();
        attSongInformationList = new ArrayList<>();
        spotifyService = SpotifyService.getInstance();
        Toolbar toolbar = root.findViewById(R.id.toolbar_top_songs);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);

        attentionRecyclerView = root.findViewById(R.id.attentionRecycleView);
        meditationRecyclerView = root.findViewById(R.id.meditationRecycleView);


        return root;
    }

    @Override
    public void onSongsListsLoaded(List<SongElement> mScores, List<SongElement> aScores) {
        mScores.sort(Comparator.comparingDouble(SongElement::getCurrentAverage).reversed());
        aScores.sort(Comparator.comparingDouble(SongElement::getCurrentAverage).reversed());
        mScores = getTop10Songs(mScores);
        aScores = getTop10Songs(aScores);
        this.meditationSongs = mScores;
        this.attentionSongs = aScores;


        new RequestTrackDetailsTask().execute();









    }
    private List<SongElement> getTop10Songs(List<SongElement> songElements){

        for(int i = songElements.size()-1; i > 9; i--){
            songElements.remove(i);
        }
        return songElements;
    }


    private class RequestTrackDetailsTask extends AsyncTask<Void, Void, List<SongInformation>> {

        @Override
        protected List<SongInformation> doInBackground(Void... voids) {
            List<SongInformation> songInformationList = new ArrayList<>();

            String apiAttUrl = "https://api.spotify.com/v1/tracks?ids=";
            String apiMedUrl = apiAttUrl;


            for (SongElement songElement : attentionSongs) {
                apiAttUrl = apiAttUrl + "," + getTrackId(songElement.getSongUri());
            }


            for (SongElement songElement : meditationSongs) {
                apiMedUrl = apiMedUrl + "," + getTrackId(songElement.getSongUri());
            }

            try {

                URL urlAtt = new URL(apiAttUrl);
                URL urlMed = new URL(apiMedUrl);
                HttpURLConnection urlConnectionAtt = (HttpURLConnection) urlAtt.openConnection();
                HttpURLConnection urlConnectionMed = (HttpURLConnection) urlMed.openConnection();


                urlConnectionAtt.setRequestProperty("Authorization", "Bearer " + spotifyService.getAccessToken());
                urlConnectionMed.setRequestProperty("Authorization", "Bearer " + spotifyService.getAccessToken());


                String attResponse = readResponse(urlConnectionAtt);
                String medResponse = readResponse(urlConnectionMed);


                attSongInformationList = processResponses(attResponse);
                medSongInformationList = processResponses(medResponse);

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
                Log.d(TAG, "Error: " + responseCode);
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
                            List<String> artistNames = parseArtistNames(trackObject.toString());
                            String albumImageUrl = trackObject.getJSONObject("album")
                                    .getJSONArray("images")
                                    .getJSONObject(0)
                                    .getString("url");



                            songInformationList.add(new SongInformation(songName, artistNames, albumImageUrl));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            return songInformationList;
        }

        @Override
        protected void onPostExecute(List<SongInformation> songInformationList) {


            TopSongsRecViewAdapter meditationAdapter = new TopSongsRecViewAdapter(requireContext());
        TopSongsRecViewAdapter attentionAdapter = new TopSongsRecViewAdapter(requireContext());


        meditationAdapter.setSongInformationList(medSongInformationList);
        attentionAdapter.setSongInformationList(attSongInformationList);
        meditationAdapter.setSongElementList(meditationSongs);
        attentionAdapter.setSongElementList(attentionSongs);
        attentionAdapter.setColor(R.color.purple_500);


        LinearLayoutManager meditationLayoutManager = new LinearLayoutManager(requireContext());
        LinearLayoutManager attentionLayoutManager = new LinearLayoutManager(requireContext());

        meditationRecyclerView.setLayoutManager(meditationLayoutManager);
        attentionRecyclerView.setLayoutManager(attentionLayoutManager);


        meditationRecyclerView.setAdapter(meditationAdapter);
        attentionRecyclerView.setAdapter(attentionAdapter);



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