package com.cloudwell.alarms;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.cloudwell.R;

import java.util.ArrayList;

public class AlarmsRecViewAdapter extends RecyclerView.Adapter<AlarmsRecViewAdapter.ViewHolder> {
    private Context context;
    private ArrayList<String[]> alarmsMessagesList = new ArrayList<>();

    public AlarmsRecViewAdapter(Context context) {this.context = context;}

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.alarms_list_layout, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;

    }



    @Override
    public void onBindViewHolder(@NonNull AlarmsRecViewAdapter.ViewHolder holder, int position) {
        holder.title.setText(alarmsMessagesList.get(position)[0]);
        holder.content.setText(alarmsMessagesList.get(position)[1]);
        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alarmsMessagesList.remove(holder.getBindingAdapterPosition());
                notifyDataSetChanged();
            }
        });



    }

    @Override
    public int getItemCount() {
        return alarmsMessagesList.size();
    }
    public void setPropertiesList(ArrayList<String[]> alarmsList){
        this.alarmsMessagesList = alarmsList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView title, content;
        ImageView deleteBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            content = itemView.findViewById(R.id.alarmMessage);
            title = itemView.findViewById(R.id.alarmTitle);
            deleteBtn = itemView.findViewById(R.id.deleteAlarmBtn);
        }

    }
}

