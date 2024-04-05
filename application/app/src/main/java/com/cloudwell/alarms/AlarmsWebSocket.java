package com.cloudwell.alarms;


import android.content.Context;
import android.util.Log;

import com.cloudwell.settings.SettingsObserver;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public final class AlarmsWebSocket extends WebSocketListener {
    private static final String TAG = AlarmService.class.getSimpleName();
    private static WebSocketObserver webSocketObserver;
    private WebSocket webSocket;
    private CheckAlarmMessage checkAlarmMessage;
    private static AlarmsWebSocket instance;
    private Context context;

    public static AlarmsWebSocket getInstance() {


        synchronized (AlarmsWebSocket.class) {
            if (instance == null) {
                instance = new AlarmsWebSocket();
            }
        }

        return instance;
    }
    public void setContext(Context context) {
        this.context = context;
    }

    private AlarmsWebSocket() {
    }

//    private AlarmsWebSocket(WebSocketObserver webSocketObserver) {
//        this.webSocketObserver = webSocketObserver;
//        checkAlarmMessage = new CheckAlarmMessage();
//    }

    public void run() {
        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(0, TimeUnit.MILLISECONDS)
                .build();

        Request request = new Request.Builder()
//                .url("wss://socketsbay.com/wss/v2/1/demo/")
                .url("ws://10.141.10.136:8765")
                .build();
        client.newWebSocket(request, this);

        // Trigger shutdown of the dispatcher's executor so this process can exit cleanly.
        client.dispatcher().executorService().shutdown();
    }

    public void sendData(String data) {
        if (webSocket != null) {
            webSocket.send(data);

        } else {
            Log.e(TAG, "No webSocket found");
        }
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        this.webSocket = webSocket;
        Log.i(TAG, "WebSocket connection opened");
        webSocket.send("Hello");
        //webSocket.close(1000, "Goodbye, World!");
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        System.out.println("MESSAGE: " + text);
        if(!text.contains("Hello")) {
            checkAlarmMessage = new CheckAlarmMessage();
            AlarmResponse alarmResponse = checkAlarmMessage.checkMessage(text);

            webSocketObserver = AlarmService.getInstance();
            if (webSocketObserver != null) {
                webSocketObserver.onMessageReceived(alarmResponse);
            }
        }
    }

    @Override
    public void onMessage(WebSocket webSocket, ByteString bytes) {
        System.out.println("MESSAGE: " + bytes.hex());
    }

    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        webSocket.close(1000, null);
        System.out.println("CLOSE: " + code + " " + reason);
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        t.printStackTrace();
    }

//    public static void main(String... args) {
//        new AlarmsWebSocket().run();
//    }
}
