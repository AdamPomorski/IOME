package com.example.iome.headband_connection;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.iome.utility.MyDataManager;
import com.neurosky.connection.ConnectionStates;
import com.neurosky.connection.TgStreamHandler;

public class ConnectionCallback {
    private TgStreamHandler callback;
    private static final String TAG = ConnectionCallback.class.getSimpleName();
    private static final int MSG_UPDATE_BAD_PACKET = 1001;
    private static final int MSG_UPDATE_STATE = 1002;

    private String connectionMsg;
    private MyDataManager myDataManager;




    public ConnectionCallback(Handler messageHandler, MyDataManager myDataManager) {

        this.myDataManager = myDataManager;
        callback = new TgStreamHandler() {

            @Override
            public void onStatesChanged(int connectionStates) {

                Log.d(TAG, "connectionStates change to: " + connectionStates);

                connectionMsg = null;
                switch (connectionStates) {
                    case ConnectionStates.STATE_CONNECTED:

                        connectionMsg = "Connected to Myndband";
                        break;
                    case ConnectionStates.STATE_WORKING:
                        messageHandler.sendEmptyMessageDelayed(1234, 5000);
                        break;
                    case ConnectionStates.STATE_GET_DATA_TIME_OUT:
                        connectionMsg = "Myband connection timeout";
                        break;
                    case ConnectionStates.STATE_COMPLETE:
                        break;
                    case ConnectionStates.STATE_STOPPED:
                        connectionMsg = "Myband connection stopped";
                        break;
                    case ConnectionStates.STATE_DISCONNECTED:
                        connectionMsg = "Disconnected from Myndband";
                        break;
                    case ConnectionStates.STATE_ERROR:
                        connectionMsg = "Connection error, Please try again!";
                        break;
                    case ConnectionStates.STATE_FAILED:
                        connectionMsg = "Connection failed, Please try again!";
                        break;

                }

                if(connectionMsg != null) {
                    Log.d(TAG, connectionMsg);
                    myDataManager.setHeadbandConnectionStateLiveData(connectionMsg);

                }

                Message msg = messageHandler.obtainMessage();
                msg.what = MSG_UPDATE_STATE;
                msg.arg1 = connectionStates;
                messageHandler.sendMessage(msg);


            }

            @Override
            public void onRecordFail(int a) {

                Log.e(TAG, "onRecordFail: " + a);

            }

            private int badPacketCount = 0;

            @Override
            public void onChecksumFail(byte[] payload, int length, int checksum) {


                badPacketCount++;
                Message msg = messageHandler.obtainMessage();
                msg.what = MSG_UPDATE_BAD_PACKET;
                msg.arg1 = badPacketCount;
                messageHandler.sendMessage(msg);

            }

            @Override
            public void onDataReceived(int datatype, int data, Object obj) {

                Message msg = messageHandler.obtainMessage();
                msg.what = datatype;
                msg.arg1 = data;
                msg.obj = obj;
                messageHandler.sendMessage(msg);
            }
        };
    }


    public TgStreamHandler getCallback() {
        return callback;
    }

}
