package com.cloudwell.settings;

import com.cloudwell.alarms.Alarm;

import java.util.ArrayList;

public interface SettingsObserver {
    void onConfirmButtonPressed(ArrayList<Alarm> alarmsList);
}
