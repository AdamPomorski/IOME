package com.cloudwell;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.cloudwell.alarms.Alarm;
import com.cloudwell.alarms.AlarmService;
import com.cloudwell.alarms.AlarmTypes;
import com.cloudwell.alarms.AlarmsListActivity;
import com.cloudwell.alarms.AlarmsListManager;
import com.cloudwell.property.PropertiesListActivity;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {
    ArrayList<Alarm> alarms = new ArrayList<>();
    CardView propertyBtn, alarmsBtn;
    private static boolean isServiceRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        propertyBtn = findViewById(R.id.property_home);
        alarmsBtn = findViewById(R.id.alarms_home);
        if(!isServiceRunning) {
            Intent serviceIntent = new Intent(this, AlarmService.class);
            startService(serviceIntent);
            isServiceRunning = true;
        }
        AlarmsListManager alarmsListManager = AlarmsListManager.getInstance();
        alarms = alarmsListManager.getAlarms();
        //saveInitialAlarmValues();
        loadAlarmValues();
        alarmsListManager.setAlarms(alarms);

        propertyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, PropertiesListActivity.class);
                startActivity(intent);
            }
        });
        alarmsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, AlarmsListActivity.class);
                startActivity(intent);
            }
        });


    }
    private void saveInitialAlarmValues(){

        FileOutputStream fos = null;

            try {
                fos = getApplicationContext().openFileOutput("alarms.txt", MODE_PRIVATE);
                String toFileString ="1 35 3 80 5 on";
                fos.write(toFileString.getBytes());
                fos.write("\n".getBytes());
                toFileString = "2 39 7 90 10 off";
                fos.write(toFileString.getBytes());
                Log.i("Save", "Saved " + getApplicationContext().getFilesDir());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }



    private void loadAlarmValues() {

        FileInputStream fis = null;
            try {
                fis = getApplicationContext().openFileInput("alarms.txt");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            InputStreamReader inputStreamReader =
                    new InputStreamReader(fis, StandardCharsets.UTF_8);
            try (BufferedReader reader = new BufferedReader(inputStreamReader)) {
                String line = reader.readLine();
                while (line != null) {
                    try {
                        String[] valuesArray = line.split(" ",10);
                        alarms.add(new Alarm(Integer.parseInt(valuesArray[0]), AlarmTypes.MAX_TEMP,valuesArray[1]));
                        alarms.add(new Alarm(Integer.parseInt(valuesArray[0]), AlarmTypes.MIN_TEMP,valuesArray[2]));
                        alarms.add(new Alarm(Integer.parseInt(valuesArray[0]), AlarmTypes.MAX_HUM,valuesArray[3]));
                        alarms.add(new Alarm(Integer.parseInt(valuesArray[0]), AlarmTypes.MIN_HUM,valuesArray[4]));
                        alarms.add(new Alarm(Integer.parseInt(valuesArray[0]), AlarmTypes.INTRUDER,valuesArray[5]));
                        line = reader.readLine();

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            Log.i("Load", "Loaded content");

    }




    }
