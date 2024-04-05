package com.cloudwell.alarms;

import java.util.ArrayList;

public class AlarmsListManager {
    private static AlarmsListManager instance;
    private ArrayList<Alarm> alarmsList;

    private AlarmsListManager() {
        alarmsList = new ArrayList<>();
    }

    public static AlarmsListManager getInstance() {
        if (instance == null) {
            instance = new AlarmsListManager();
        }
        return instance;
    }

    public ArrayList<Alarm> getAlarms() {
        return alarmsList;
    }

    public void setAlarms(ArrayList<Alarm> alarmsList) {
        this.alarmsList = alarmsList;
    }
}
