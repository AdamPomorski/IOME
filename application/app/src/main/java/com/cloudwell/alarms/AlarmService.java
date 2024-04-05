package com.cloudwell.alarms;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;


import com.cloudwell.R;
import com.cloudwell.settings.SettingsObserver;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class AlarmService extends Service implements WebSocketObserver, SettingsObserver {
    private String CHANNEL_ID = "ServiceID";
    private int notificationId = 0;
    private static final String TAG = AlarmService.class.getSimpleName();

    PendingIntent pendingIntent;
    AlarmsWebSocket alarmsWebSocket;
    private static AlarmService instance;
    public ArrayList<String[]> alarmsMessagesList = new ArrayList<>();


    public static AlarmService getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        alarmsWebSocket = AlarmsWebSocket.getInstance();
        alarmsWebSocket.setContext(getApplicationContext());
        alarmsWebSocket.run();
        createNotificationChannel();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        intent = new Intent(getApplicationContext(), AlarmsListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE);


        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onMessageReceived(AlarmResponse alarmResponse) {
        if (!alarmResponse.getAlarm_type().equals("unknown")) {
            String[] message = createMessage(alarmResponse);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_baseline_notification_important_12)
                    .setContentTitle(message[0])
                    .setContentText(message[1])
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setContentIntent(createPendingIntent())
                    .setAutoCancel(true);
            notificationManager.notify(notificationId, builder.build());
            notificationId++;
            alarmsMessagesList.add(message);
        } else {
            Log.e(TAG, "Unknown message has been received");
        }


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        instance = null;
    }

    private String[] createMessage(AlarmResponse alarmResponse) {
        String[] response = new String[2];
        if (alarmResponse.getAlarm_type().contains("max_temp")) {
            response[0] = "TOO HIGH TEMPERATURE!";
            response[1] = "In property " + alarmResponse.getId() + " the temperature is " + alarmResponse.getCurrent_val() + " which is above the limit of " + alarmResponse.getAlarm_val();
        } else if (alarmResponse.getAlarm_type().contains("min_temp")) {
            response[0] = "TOO LOW TEMPERATURE!";
            response[1] = "In property " + alarmResponse.getId() + " the temperature is " + alarmResponse.getCurrent_val() + " which is below the limit of " + alarmResponse.getAlarm_val();

        } else if (alarmResponse.getAlarm_type().contains("max_hum")) {
            response[0] = "TOO HIGH HUMIDITY!";
            response[1] = "In property " + alarmResponse.getId() + " the humidity is " + alarmResponse.getCurrent_val() + "% which is above the limit of " + alarmResponse.getAlarm_val() + "%";

        } else if (alarmResponse.getAlarm_type().contains("min_hum")) {
            response[0] = "TOO LOW HUMIDITY!";
            response[1] = "In property " + alarmResponse.getId() + " the humidity is " + alarmResponse.getCurrent_val() + " which is below the limit of " + alarmResponse.getAlarm_val() + "%";
        } else if (alarmResponse.getAlarm_type().contains("intruder")) {
            response[0] = "INTRUDER DETECTED!";
            response[1] = "In property " + alarmResponse.getId() + " a intruder has been detected.";
        }
        return response;

    }

    @Override
    public void onConfirmButtonPressed(ArrayList<Alarm> alarmsList) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("property_id", alarmsList.get(0).getId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        for (Alarm alarm : alarmsList
        ) {

            switch (alarm.getT()) {
                case MAX_TEMP:
                    try {
                        jsonObject.put("max_temp", alarm.getValue());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case MIN_TEMP:
                    try {
                        jsonObject.put("min_temp", alarm.getValue());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case MAX_HUM:
                    try {
                        jsonObject.put("max_hum", alarm.getValue());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case MIN_HUM:
                    try {
                        jsonObject.put("min_hum", alarm.getValue());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case INTRUDER:
                    try {
                        jsonObject.put("intruder", alarm.getValue());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
        alarmsWebSocket = AlarmsWebSocket.getInstance();
        alarmsWebSocket.sendData(jsonObject.toString());

    }
    private PendingIntent createPendingIntent() {
        Intent intent = new Intent(this, AlarmsListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
    }
}

