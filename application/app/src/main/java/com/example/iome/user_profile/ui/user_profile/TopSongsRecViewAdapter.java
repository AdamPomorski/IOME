package com.example.iome.user_profile.ui.user_profile;



import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iome.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class TopSongsRecViewAdapter extends RecyclerView.Adapter<TopSongsRecViewAdapter.ViewHolder> {


    private Context context;
    private List<SongInformation> songInformationList = new ArrayList<>();
    private List<SongElement> songElementList = new ArrayList<>();
    private int color;
    private ColorStateList colorStateList;


    public TopSongsRecViewAdapter(Context context) {
        this.context = context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.top_songs_list_layout, parent, false);
        ViewHolder holder = new ViewHolder(view);
        color = 0;
        return holder;
    }



    @Override
    public void onBindViewHolder(@NonNull TopSongsRecViewAdapter.ViewHolder holder, final int position) {

        holder.songPostion.setText((position+1)+".");
        holder.songTitle.setText(songInformationList.get(position).getSongName());
        StringBuilder authors = new StringBuilder();
        for (int i = 0; i < songInformationList.get(position).getArtists().size(); i++) {
            authors.append(songInformationList.get(position).getArtists().get(i));
            if (i != songInformationList.get(position).getArtists().size() - 1) {
                authors.append(", ");
            }
        }
        holder.songAuthors.setText(authors.toString());
        holder.songScore.setText(String.format("%.1f", songElementList.get(position).getCurrentAverage()));
        Picasso.get().load(songInformationList.get(position).getPictureUri()).into(holder.songImage);





    }

    public void setSongInformationList(List<SongInformation> songInformationList) {
        this.songInformationList = songInformationList;
    }

    public void setSongElementList(List<SongElement> songElementList) {
        this.songElementList = songElementList;
    }

    public void setColor(int color) {
        this.color = color;
        colorStateList = ColorStateList.valueOf(color);
    }

    @Override
    public int getItemCount() {
        return songInformationList.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView songPostion, songTitle, songAuthors, songScore;
        private CardView parent, imageCard;
        private ImageView songImage;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            parent = itemView.findViewById(R.id.parent);
            songPostion = itemView.findViewById(R.id.songPosition);
            songTitle = itemView.findViewById(R.id.songTitle);
            songAuthors = itemView.findViewById(R.id.songAuthors);
            songScore = itemView.findViewById(R.id.songScore);
            songImage = itemView.findViewById(R.id.songImageView);
            imageCard = itemView.findViewById(R.id.songImageCardView);
            if(color != 0){
                imageCard.setBackgroundTintList(colorStateList);
                imageCard.setForegroundTintList(colorStateList);
            }

        }

    }

}


