package com.cloudwell.property;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.cloudwell.R;

import java.util.ArrayList;

public class PropertiesRecViewAdapter extends RecyclerView.Adapter<PropertiesRecViewAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Property> propertiesList = new ArrayList<>();

    public PropertiesRecViewAdapter(Context context) {this.context = context;}

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.properties_list_layout, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;

    }



    @Override
    public void onBindViewHolder(@NonNull PropertiesRecViewAdapter.ViewHolder holder, int position) {
        holder.name.setText("Property "+propertiesList.get(position).getId());
        holder.address.setText("Address: "+propertiesList.get(position).getAddress());


    }

    @Override
    public int getItemCount() {
        return propertiesList.size();
    }
    public void setPropertiesList(ArrayList<Property> propertiesList){
        this.propertiesList = propertiesList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView name, address;
        private CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.propertyText);
            address = itemView.findViewById(R.id.addressText);
            cardView = itemView.findViewById(R.id.parentView);
            cardView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Property property = propertiesList.get(position);

            // Start the next activity or perform any other desired action
            // For example:
            Context context = v.getContext();
            Intent intent = new Intent(context, PropertyActivity.class);
            // Pass any data you need to the next activity using intent.putExtra()
            intent.putExtra("propertyId", property.getId());
            context.startActivity(intent);

        }
    }
}
