package com.example.iome.services;

import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.example.iome.database.Score;
import com.example.iome.database.ScoreRepository;
import com.example.iome.headband_connection.ConnectionCallback;
import com.example.iome.utility.VolumeControlUtility;
import com.example.iome.utility.MyDataManager;
import com.neurosky.connection.DataType.MindDataType;
import com.neurosky.connection.TgStreamHandler;
import com.neurosky.connection.TgStreamReader;


public class HeadbandService extends Service {

    private static HeadbandService instance;
    private static final int MSG_UPDATE_BAD_PACKET = 1001;

    private static final String TAG = ConnectionCallback.class.getSimpleName();
    private boolean isReadFilter = false;
    private TgStreamReader tgStreamReader;

    private int currentState = 0;
    private String address = null;
    private BluetoothAdapter bluetoothAdapter;

    private ConnectionCallback callback;
    private TgStreamHandler tgStreamHandler;
    private int badPacketCount = 0;
    private MutableLiveData<String> connectionStateMsg = new MutableLiveData<>();

    private VolumeControlUtility volumeControlUtility;
    private Context context;



    private MyDataManager myDataManager;
    private HandlerThread handlerThread;
    private Handler serviceHandler;
    private Handler messageHandler;

    private float meditationAvg = 0, attentionAvg = 0;

    private ScoreRepository repository;


    public static HeadbandService getInstance() {
        if (instance == null) {
            instance = new HeadbandService();
        }
        return instance;
    }

    public MyDataManager getDataManager() {
        return myDataManager;
    }


    public HeadbandService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return START_STICKY;
    }

    @Override
    @SuppressLint("HandlerLeak")
    public void onCreate() {
        super.onCreate();

        repository = new ScoreRepository(getApplication());
        handlerThread = new HandlerThread("HeadbandServiceThread");
        handlerThread.start();
        serviceHandler = new Handler(handlerThread.getLooper());
        myDataManager = MyDataManager.getInstance();
        messageHandler = new Handler(handlerThread.getLooper()) {

            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1234:
                        tgStreamReader.MWM15_getFilterType();
                        isReadFilter = true;
                        Log.d(TAG, "MWM15_getFilterType ");

                        break;
                    case 1235:
                        tgStreamReader.MWM15_setFilterType(MindDataType.FilterType.FILTER_60HZ);
                        Log.d(TAG, "MWM15_setFilter  60HZ");
                        messageHandler.sendEmptyMessageDelayed(1237, 1000);
                        break;
                    case 1236:
                        tgStreamReader.MWM15_setFilterType(MindDataType.FilterType.FILTER_50HZ);
                        Log.d(TAG, "MWM15_SetFilter 50HZ ");
                        messageHandler.sendEmptyMessageDelayed(1237, 1000);
                        break;

                    case 1237:
                        tgStreamReader.MWM15_getFilterType();
                        Log.d(TAG, "MWM15_getFilterType ");

                        break;

                    case MindDataType.CODE_FILTER_TYPE:
                        Log.d(TAG, "CODE_FILTER_TYPE: " + msg.arg1 + "  isReadFilter: " + isReadFilter);
                        if (isReadFilter) {
                            isReadFilter = false;
                            if (msg.arg1 == MindDataType.FilterType.FILTER_50HZ.getValue()) {
                                messageHandler.sendEmptyMessageDelayed(1235, 1000);
                            } else if (msg.arg1 == MindDataType.FilterType.FILTER_60HZ.getValue()) {
                                messageHandler.sendEmptyMessageDelayed(1236, 1000);
                            } else {
                                Log.e(TAG, "Error filter type");
                            }
                        }

                        break;

                    case MindDataType.CODE_MEDITATION:
                        Log.d(TAG, "CODE_MEDITATION " + msg.arg1);
                        runOnUiThread(() -> {
                            myDataManager.setMeditationLiveData(msg.arg1);
                        });
                        if (myDataManager.getDataType() != null && myDataManager.getDataType().equals("meditation")) {
                            meditationAvg = volumeControlUtility.processAndCheckVolume(msg.arg1, myDataManager.getVolumeControlMode());
                        }
                        if (meditationAvg != 0 && meditationAvg > 0 && myDataManager.getDataType() != null && myDataManager.getDataType().equals("meditation") && myDataManager.isPlaying()) {
                            repository.insert(new Score(myDataManager.getDataType(), meditationAvg, System.currentTimeMillis(), myDataManager.getSongUri()));
                        }
                        break;
                    case MindDataType.CODE_ATTENTION:
                        Log.d(TAG, "CODE_ATTENTION " + msg.arg1);
                        runOnUiThread(() -> {
                            myDataManager.setAttentionLiveData(msg.arg1);

                        });
                        if (myDataManager.getDataType() != null && myDataManager.getDataType().equals("attention")) {
                            attentionAvg = volumeControlUtility.processAndCheckVolume(msg.arg1, myDataManager.getVolumeControlMode());
                        }
                        if (attentionAvg != 0 && attentionAvg > 0 && myDataManager.getDataType() != null && myDataManager.getDataType().equals("attention") && myDataManager.isPlaying()) {
                            repository.insert(new Score(myDataManager.getDataType(), attentionAvg, System.currentTimeMillis(), myDataManager.getSongUri()));
                        }


                        break;

                    case MindDataType.CODE_POOR_SIGNAL:
                        int poorSignal = msg.arg1;
                        Log.d(TAG, "Poor signal:" + poorSignal);

                        break;
                    case MSG_UPDATE_BAD_PACKET:
                        Log.d(TAG, "Update bad packet:" + msg.arg1);
                        break;
                    default:
                        break;
                }
                super.handleMessage(msg);
            }

        };
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        callback = new ConnectionCallback(messageHandler, myDataManager);
        tgStreamHandler = callback.getCallback();
        context = getApplicationContext();
        volumeControlUtility = new VolumeControlUtility(context);


        instance = this;
    }


    public void disconnectFromDevice() {
        serviceHandler.post(() -> {
            if (tgStreamReader != null) {
                tgStreamReader.stop();
                tgStreamReader.close();
            }
        });
    }


    public void connectToDevice(String address) {
        serviceHandler.post(() -> {
            if (address != null) {
                BluetoothDevice bd = bluetoothAdapter.getRemoteDevice(address);
                createStreamReader(bd);
                tgStreamReader.connectAndStart();
            }
        });
    }


    private TgStreamReader createStreamReader(BluetoothDevice bd) {
        if (tgStreamReader == null) {
            tgStreamReader = new TgStreamReader(bd, tgStreamHandler);
            tgStreamReader.startLog();
        } else {
            tgStreamReader.changeBluetoothDevice(bd);
            tgStreamReader.setTgStreamHandler(tgStreamHandler);
        }
        return tgStreamReader;

    }


    public void runOnUiThread(Runnable action) {
        new Handler(Looper.getMainLooper()).post(action);
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        disconnectFromDevice();
        serviceHandler.post(() -> {
            handlerThread.quit();
        });
    }
}
