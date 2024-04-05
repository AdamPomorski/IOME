
package com.example.iome.services;
import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;

public class TimerService extends Service {

    private CountDownTimer countDownTimer;
    private boolean isTimerRunning = false;

    public static final String TIMER_UPDATE_ACTION = "com.example.iome.timer.update";
    public static final String TIMER_UPDATE_EXTRA = "remainingTimeMillis";
    public static final String TIMER_UPDATE_EXTRA2 = "isTimerRunning";



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            long durationInMillis = intent.getLongExtra("duration", 0);
            startTimer(durationInMillis);
        }
        return START_NOT_STICKY;
    }

    private void startTimer(long durationInMillis) {
        isTimerRunning = true;
        countDownTimer = new CountDownTimer(durationInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                sendUpdateBroadcast(millisUntilFinished, isTimerRunning);
            }

            @Override
            public void onFinish() {

                isTimerRunning = false;
                sendUpdateBroadcast(0,isTimerRunning);
            }
        };

        countDownTimer.start();
    }

    private void sendUpdateBroadcast(long remainingTimeMillis, boolean isTimerRunningLocal) {
        Intent intent = new Intent(TIMER_UPDATE_ACTION);
        intent.putExtra(TIMER_UPDATE_EXTRA, remainingTimeMillis);
        intent.putExtra(TIMER_UPDATE_EXTRA2, isTimerRunningLocal);
        sendBroadcast(intent);
    }

    @Override
    public void onDestroy() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
