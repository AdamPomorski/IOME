package com.cloudwell.alarms;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.cloudwell.R;
import com.cloudwell.property.PropertiesRecViewAdapter;

import java.util.ArrayList;
import java.util.Objects;

public class AlarmsListActivity extends AppCompatActivity {

    private ArrayList<Alarm> alarmsList = new ArrayList<>();
    RecyclerView recyclerView;
    private ArrayList<String[]> alarmsMessagesList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_list);
        recyclerView = findViewById(R.id.alarmsRecycleView);

        Toolbar toolbar = findViewById(R.id.toolbarAlarmsList);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        AlarmService alarmService = AlarmService.getInstance();
        alarmsMessagesList = alarmService.alarmsMessagesList;
//        AlarmsListManager alarmsListManager = AlarmsListManager.getInstance();
//        alarmsList.add(new Alarm(1,AlarmTypes.MAX_HUM,"INFORMACJA"));
//        alarmsListManager.setAlarms(alarmsList);
        //alarmsList = alarmsListManager.getAlarms();

        AlarmsRecViewAdapter adapter = new AlarmsRecViewAdapter(this);
        adapter.setPropertiesList(alarmsMessagesList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}