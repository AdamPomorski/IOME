package com.example.iome.user_profile.ui.user_profile;

import android.app.Application;
import android.os.Handler;
import android.os.HandlerThread;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.iome.services.SpotifyService;
import com.example.iome.database.Score;
import com.example.iome.database.ScoreRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class TopSongsViewModel extends AndroidViewModel implements ScoreRepository.OnScoresLoadedListener {

    private ScoreRepository repository;
    private List<Score> meditationScores, attentionScores;
    private List<SongElement> meditationSongs, attentionSongs;
    private HandlerThread handlerThread;
    private Handler viewModelHandler;
    private CountDownLatch latch;
    private onSongsListsLoadedListener listener;

    private SpotifyService spotifyService;

    public TopSongsViewModel(@NonNull Application application) {
        super(application);
        repository = new ScoreRepository(application);
        repository.setOnScoresLoadedListener(this);
        handlerThread = new HandlerThread("TopSongsViewModelThread");
        handlerThread.start();
        viewModelHandler = new Handler(handlerThread.getLooper());
        spotifyService = SpotifyService.getInstance();


    }

    public void setListener(onSongsListsLoadedListener listener) {
        this.listener = listener;
    }

    @Override
    public void onScoresLoadedByType(List<Score> scores) {
        if(scores.size() == 0){
            return;
        }
        if (scores.get(0).getType().equals("meditation")) {
            meditationScores = scores;
        } else {
            attentionScores = scores;
        }


            latch.countDown();


    }

    public void getSongsLists() {

        viewModelHandler.post(() -> {

            latch = new CountDownLatch(1);

            repository.getScoresByType("meditation", this);
            repository.getScoresByType("attention", this);

            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if(meditationScores !=null) {
                meditationSongs = spotifyService.createTopSongsList(meditationScores, "meditation");
            }else {
                meditationSongs = new ArrayList<>();
            }

            if(attentionScores !=null) {
                attentionSongs = spotifyService.createTopSongsList(attentionScores, "attention");
            }else {
                attentionSongs = new ArrayList<>();
            }


            listener.onSongsListsLoaded(meditationSongs, attentionSongs);


        });
    }




    public interface onSongsListsLoadedListener {
        void onSongsListsLoaded(List<SongElement> meditationScores, List<SongElement> attentionScores);
    }


}
