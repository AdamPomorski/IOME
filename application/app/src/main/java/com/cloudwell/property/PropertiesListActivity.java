package com.cloudwell.property;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.cloudwell.R;
import com.cloudwell.alarms.AlarmService;

import java.util.ArrayList;
import java.util.Objects;


public class PropertiesListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ArrayList<Property> properties = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property_list);
        recyclerView = findViewById(R.id.propertiesRecycleView);

        Toolbar toolbar = findViewById(R.id.toolbarPropertiesList);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        PropertiesListManager propertiesListManager = PropertiesListManager.getInstance();
        properties.add(new Property(1,"Warszawska 12"));
        propertiesListManager.setProperties(properties);
        properties = propertiesListManager.getProperties();

        PropertiesRecViewAdapter adapter = new PropertiesRecViewAdapter(this);
        adapter.setPropertiesList(properties);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


    }
}