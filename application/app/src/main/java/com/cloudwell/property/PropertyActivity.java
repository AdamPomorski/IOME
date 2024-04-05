package com.cloudwell.property;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.audiofx.Equalizer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;


import com.cloudwell.R;

import org.json.JSONException;
import org.json.JSONObject;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;
import org.videolan.libvlc.util.VLCVideoLayout;

import com.cloudwell.settings.SettingsActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;
import java.util.Random;

import okhttp3.OkHttpClient;
import okhttp3.Request;


public class PropertyActivity extends AppCompatActivity {
    private int propertyId;
    //ProgressDialog pd;

    //String rtspUri = "rtsp://10.141.6.9:8080/";
    //ImageView playerView;
    //ExoPlayer exoPlayer;
    //private Socket socket;
    // private InputStream inputStream;
    //private Bitmap currentFrame;

    //org.videolan.libvlc.MediaPlayer mediaPlayer;
    //LibVLC libVlc;
    //VLCVideoLayout videoView;
    //String url = "rtsp://192.168.43.15:8080/";
    private TextView temperatureTextView;
    private Handler handlerSensors, handlerPhoto, handlerBoxes;
    private Runnable updateSensorsRunnable, updatePhotoRunnable, updateBoxesRunnable;
    private ImageView imageView;
    private SwitchCompat switchBoxes;
    private boolean initialSet;
    private static final int UPDATE_INTERVAL = 3000;
    private static final String URLTEMP = "http://10.141.10.136:5000/temperature";
    private static final String URLBOXES = "http://10.141.10.136:5000/boxes";
    //private static final String URLPHOTO = "http://10.141.10.136:5000/image";
    private static final String URLPHOTO = "http://142.250.186.196/images/branding/googlelogo/2x/googlelogo_color_160x56dp.png";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property);
//        playerView = findViewById(R.id.playerView);
//        videoView = findViewById(R.id.videoView);
        imageView = findViewById(R.id.imageView);
        switchBoxes = findViewById(R.id.switchBoxes);
        initialSet = true;
//        if (getSupportActionBar() != null) {
//            getSupportActionBar().hide();
//        }

        Toolbar toolbar = findViewById(R.id.toolbarProperty);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        propertyId = getIntent().getIntExtra("propertyId", 0);

        System.out.println(propertyId);

        //libVlc = new LibVLC(this);
        //videoView = findViewById(R.id.videoView);
        //mediaPlayer = new MediaPlayer(libVlc);


        temperatureTextView = findViewById(R.id.temperatureText);
        handlerSensors = new Handler();
        handlerPhoto = new Handler();
        handlerBoxes = new Handler();
        updateBoxesRunnable = new Runnable() {
            @Override
            public void run() {
                updateBoxes();
                handlerBoxes.postDelayed(this, UPDATE_INTERVAL);
            }
        };
        updateSensorsRunnable = new Runnable() {
            @Override
            public void run() {
                updateSensorValues();
                handlerSensors.postDelayed(this, UPDATE_INTERVAL);
            }
        };
        updatePhotoRunnable = new Runnable() {
            @Override
            public void run() {
                updatePhoto();
                handlerPhoto.postDelayed(this, UPDATE_INTERVAL);
            }
        };
        switchBoxes.setChecked(false);
        switchBoxes.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                File file = new File(getFilesDir(), "image_boxes.jpg");
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                imageView.setImageBitmap(bitmap);
            } else {
                File file = new File(getFilesDir(), "image.jpg");
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                imageView.setImageBitmap(bitmap);

            }
        });


//        Media media = new Media(libVLC, Uri.parse(url));
//        //media.addOption("--aout=opensles");
//        //media.addOption("--audio-time-stretch");
//        media.addOption("-vvv"); // verbosity
//        org.videolan.libvlc.MediaPlayer mediaPlayer = new org.videolan.libvlc.MediaPlayer(libVLC);
//        mediaPlayer.setMedia(media);
//        mediaPlayer.getVLCVout().setVideoSurface(videoView.getHolder().getSurface(), videoView.getHolder());
//        mediaPlayer.getVLCVout().setWindowSize(videoView.getWidth(), videoView.getHeight());
//        mediaPlayer.getVLCVout().attachViews();
//        mediaPlayer.play();

//        pd = new ProgressDialog(this);
//        pd.setMessage("Buffering ...");
//        pd.setCancelable(true);

        //playVideo();
        //connectToServer();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.settingsOptions) {
            Intent intent = new Intent(this, SettingsActivity.class);
            intent.putExtra("propertyIdSettings", propertyId);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();

        //mediaPlayer.attachViews(videoView, null, false, false);

        //Media media = new Media(libVlc, Uri.parse(url));
        //media.setHWDecoderEnabled(true, false);
        //media.addOption(":network-caching=600");

        //mediaPlayer.setMedia(media);
        //media.release();
        //mediaPlayer.play();
        handlerSensors.postDelayed(updateSensorsRunnable, UPDATE_INTERVAL);
        handlerPhoto.postDelayed(updatePhotoRunnable, UPDATE_INTERVAL);
        handlerBoxes.postDelayed(updateBoxesRunnable, UPDATE_INTERVAL);
    }

    @Override
    protected void onStop() {
        super.onStop();

//         mediaPlayer.stop();
//         mediaPlayer.detachViews();
        handlerSensors.removeCallbacks(updateSensorsRunnable);
        handlerPhoto.removeCallbacks(updatePhotoRunnable);
        handlerBoxes.removeCallbacks(updateBoxesRunnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

//         mediaPlayer.release();
//         libVlc.release();
    }
    private void updateBoxes(){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(URLBOXES)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                //if (response.isSuccessful()) {
                    final String boxes = response.body().string();

                    PropertyActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Random random = new Random();
                            // Assuming you have an ImageView with the ID 'photoImageView'
                            //temperatureTextView.setText(boxes);
                            File file = new File(getFilesDir(), "image.jpg");
                            if (!file.exists()) {
                                return;
                            }
                            Bitmap originalBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                            Bitmap mutableBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
                            Canvas canvas = new Canvas(mutableBitmap);
                            Paint paint = new Paint();
                            paint.setColor(Color.RED);
                            paint.setStyle(Paint.Style.STROKE);
                            paint.setStrokeWidth(3);
                            canvas.drawRect(random.nextInt(100), random.nextInt(100), random.nextInt(201)+100, random.nextInt(201)+100, paint);
                            File file2 = new File(getFilesDir(), "image_boxes.jpg");
                            try {
                                FileOutputStream outStream = new FileOutputStream(file2);
                                mutableBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                                outStream.flush();
                                outStream.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    });
                //}
            }
        });
    }

    private void updatePhoto() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(URLPHOTO)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                if (response.isSuccessful()) {
                    InputStream inputStream = null;
                    try {
                        inputStream = response.body().byteStream();
                        final Bitmap photoBitmap = BitmapFactory.decodeStream(inputStream);

                        PropertyActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // Assuming you have an ImageView with the ID 'photoImageView'
                                if(initialSet){
                                    imageView.setImageBitmap(photoBitmap);
                                    initialSet = false;
                                }
                                File file = new File(getFilesDir(), "image.jpg");  // Example file name and location
                                try (OutputStream outputStream = new FileOutputStream(file)) {
                                    photoBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                                    outputStream.flush();
                                    outputStream.close();
                                } catch (IOException e) {
                                    e.printStackTrace();

                                }
                            }

                        });
                    } finally {
                        if (inputStream != null) {
                            inputStream.close();
                        }
                    }
                }
            }
        });
    }
    private void updateSensorValues() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(URLTEMP)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                if (response.isSuccessful()) {
                    String myResponse = response.body().string();

                    try {
                        JSONObject jsonObject = new JSONObject(myResponse);
                        String temperature = jsonObject.get("temperature").toString();
                        //String humidity = jsonObject.get("humidity").toString();
                        PropertyActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String myFullResponse = "Temperature: " + temperature;
                                temperatureTextView.setText(myFullResponse);
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }
}


//    private void connectToServer() {
//        Thread thread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    String serverIP = "10.141.6.5"; // Podaj adres IP serwera (Raspberry Pi)
//                    int serverPort = 5000; // Port TCP na którym jest strumieniowany obraz
//
//                    socket = new Socket(serverIP, serverPort);
//                    inputStream = socket.getInputStream();
//
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            startStreaming();
//                        }
//                    });
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//        thread.start();
//    }
//
//    private void startStreaming() {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    while (true) {
//                        byte[] buffer = new byte[1024];
//                        int bytesRead = inputStream.read(buffer);
//                        if (bytesRead == -1) {
//                            break;
//                        }
//                        try {
//                            currentFrame = BitmapFactory.decodeByteArray(buffer, 0, bytesRead);
//                            if (currentFrame == null) {
//                                Log.e("ImageDecode", "Failed to decode image.");
//                            }
//                        } catch (Exception e) {
//                            Log.e("ImageDecode", "Error decoding image: " + e.getMessage());
//                        }
//
////                        currentFrame = BitmapFactory.decodeByteArray(buffer, 0, bytesRead);
//
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                if (currentFrame != null) {
//                                    playerView.setImageBitmap(currentFrame);
//                                }
//                            }
//                        });
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
//    }

//    private void playVideo(){
//        try {
//            MediaSource mediaSource = new RtspMediaSource.Factory().createMediaSource(MediaItem.fromUri(rtspUri));
//            exoPlayer = new ExoPlayer.Builder(getApplicationContext()).build();
//            playerView.setPlayer(exoPlayer);
//            exoPlayer.setMediaItem(MediaItem.fromUri(rtspUri));
//            exoPlayer.prepare();
//            exoPlayer.play();
////            getWindow().setFormat(PixelFormat.TRANSLUCENT);
////            MediaController mediaController = new MediaController(this);
////            mediaController.setAnchorView(videoView);
////            Uri videoUri = Uri.parse(videoUrl);
////            videoView.setMediaController(mediaController);
////            videoView.setVideoURI(videoUri);
////            videoView.requestFocus();
////            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
////                @Override
////                public void onPrepared(MediaPlayer mp) {
////                    pd.dismiss();
////                    videoView.start();
////                }
////            });
//
//        }catch (Exception e){
//            pd.dismiss();
//            Log.e("VIDEO","Error: "+e.getMessage());
//            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
//
//        }
//    }


