package com.cloudwell.settings;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.os.FileUtils;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudwell.R;
import com.cloudwell.alarms.Alarm;
import com.cloudwell.alarms.AlarmService;
import com.cloudwell.alarms.AlarmTypes;
import com.cloudwell.alarms.AlarmsListManager;
import com.cloudwell.property.PropertiesListManager;
import com.cloudwell.property.Property;
import com.cloudwell.property.PropertyActivity;
import com.google.common.io.Files;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {
    TextView address;
    EditText maxTempEdt, minTempEdt, maxHumEdt, minHumEdt;
    SwitchCompat switchCompat;
    ArrayList<Alarm> allAlarmsList = new ArrayList<>();
    ArrayList<Alarm> correctAlarmsList = new ArrayList<>();
    ArrayList<Property> properties = new ArrayList<>();
    Button confirmButton;
    private SettingsObserver settingsObserver;

    int propertyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = findViewById(R.id.toolbarSettings);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        address = findViewById(R.id.addressTextSettings);
        maxTempEdt = findViewById(R.id.maxTempEdit);
        minTempEdt = findViewById(R.id.minTempEdit);
        maxHumEdt = findViewById(R.id.maxHumEdit);
        minHumEdt = findViewById(R.id.minHumEdit);
        switchCompat = findViewById(R.id.alarmSwitch);
        confirmButton = findViewById(R.id.confirmButtonSettings);

        propertyId = getIntent().getIntExtra("propertyIdSettings",0);

        PropertiesListManager propertiesListManager = PropertiesListManager.getInstance();
        properties = propertiesListManager.getProperties();

        for (Property p : properties
             ) {
            if(p.getId()==propertyId)
            {
                String addr = "Address: "+p.getAddress();
                address.setText(addr);
            }

        }

        AlarmsListManager alarmsListManager = AlarmsListManager.getInstance();
        allAlarmsList = alarmsListManager.getAlarms();

        correctAlarmsList = findCorrectAlarm(allAlarmsList,propertyId);

        for (Alarm a : correctAlarmsList
             ) {
            switch (a.getT()) {
                case MAX_TEMP:
                    maxTempEdt.setText(a.getValue());break;
                case MIN_TEMP:
                    minTempEdt.setText(a.getValue());break;
                case MAX_HUM:
                    maxHumEdt.setText(a.getValue());break;
                case MIN_HUM:
                    minHumEdt.setText(a.getValue());break;
                case INTRUDER:
                    if(a.getValue().equals("on")) {
                        switchCompat.setChecked(true);
                    }else{
                        switchCompat.setChecked(false);
                    }
            }

        }

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!TextUtils.isDigitsOnly(maxHumEdt.getText())) {
                    maxHumEdt.setError("Wrong input");
                    return;
                }
                if (!TextUtils.isDigitsOnly(minHumEdt.getText())) {
                    minHumEdt.setError("Wrong input");
                    return;
                }
                if (!TextUtils.isDigitsOnly(maxTempEdt.getText())) {
                    maxTempEdt.setError("Wrong input");
                    return;
                }
                if (!TextUtils.isDigitsOnly(minTempEdt.getText())) {
                    minTempEdt.setError("Wrong input");
                    return;
                }


                for (Alarm a: allAlarmsList
                     ) {
                    if(a.getId()==propertyId && a.getT()== AlarmTypes.MAX_TEMP){
                        a.setValue(maxTempEdt.getText().toString());
                    }else if(a.getId()==propertyId && a.getT()== AlarmTypes.MIN_TEMP){
                        a.setValue( minTempEdt.getText().toString());
                    }else if(a.getId()==propertyId && a.getT()== AlarmTypes.MAX_HUM){
                        a.setValue(maxHumEdt.getText().toString());
                    }else if(a.getId()==propertyId && a.getT()== AlarmTypes.MIN_HUM){
                        a.setValue(minHumEdt.getText().toString());
                    }else if(a.getId()==propertyId && a.getT()== AlarmTypes.INTRUDER){
                        String value = switchCompat.isChecked() ? "on" : "off";
                       a.setValue(value);
                    }

                }

                alarmsListManager.setAlarms(allAlarmsList);
                saveAlarmSettings(propertyId);
                settingsObserver = new SettingsObserver() {
                    @Override
                    public void onConfirmButtonPressed(ArrayList<Alarm> alarmsList) {
                        AlarmService.getInstance().onConfirmButtonPressed(correctAlarmsList);
                    }
                };
                settingsObserver.onConfirmButtonPressed(correctAlarmsList);
                Intent intent = new Intent(getApplicationContext(), PropertyActivity.class);
                intent.putExtra("propertyId", propertyId);
                startActivity(intent);
                Toast.makeText(getApplicationContext(), "Configuration successful", Toast.LENGTH_LONG).show();



            }
        });





    }
    private ArrayList<Alarm> findCorrectAlarm(ArrayList<Alarm> alarms, int id){
        ArrayList<Alarm> localList = new ArrayList<>();
        for (Alarm a: alarms
        ) {
            if(a.getId()==id){
                localList.add(a);
            }
        }

        return localList;

    }
    private void saveAlarmSettings(int id) {



        try {

            File file = new File(getFilesDir(), "alarms.txt");
            String chosenId = String.valueOf(id);
            List<String> lines = new ArrayList<>();
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
            reader.close();

            for (int i = 0; i < lines.size(); i++) {
                String currentLine = lines.get(i);
                if (currentLine.startsWith(chosenId)) {
                    String value = switchCompat.isChecked() ? "on" : "off";
                    lines.set(i, id + " " + maxTempEdt.getText().toString() + " " + minTempEdt.getText().toString() + " " + maxHumEdt.getText().toString() + " " +minHumEdt.getText().toString()+" "+ value);
                    break;
                }
            }

            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            for (String updatedLine : lines) {
                writer.write(updatedLine);
                writer.newLine();
            }
            writer.close();


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}