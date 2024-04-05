package com.cloudwell.alarms;

public interface WebSocketObserver {
    void onMessageReceived(AlarmResponse alarmResponse);
}

