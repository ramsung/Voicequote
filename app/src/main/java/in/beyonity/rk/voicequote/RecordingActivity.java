package in.beyonity.rk.voicequote;
import java.io.File;
import java.io.IOException;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnErrorListener;
import android.media.MediaRecorder.OnInfoListener;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import in.beyonity.rk.voicequote.utils.VisualizerView;


public class RecordingActivity extends AppCompatActivity{
    public static final String DIRECTORY_NAME_TEMP = "AudioTemp";
    private Handler customHandler = new Handler();
    ImageView imageView;
    TextView recordtxt;
    VisualizerView visualizerView;
    private long startHTime = 0L;
    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;
    private MediaRecorder recorder = null;

    File audioDirTemp;
    private boolean isRecording = false;


    private Handler handler; // Handler for updating the visualizer
    // private boolean recording; // are we currently recording?

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording_acitivity);
        ActivityCompat.requestPermissions(RecordingActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO},1);

        visualizerView = (VisualizerView) findViewById(R.id.visualizer);
        imageView = (ImageView) findViewById(R.id.RecordButton);
        recordtxt = (TextView) findViewById(R.id.recordtxt);

        imageView.setOnClickListener(recordClick);

        audioDirTemp = new File(Environment.getExternalStorageDirectory(),
                DIRECTORY_NAME_TEMP);
        if (audioDirTemp.exists()) {
            deleteFilesInDir(audioDirTemp);
        } else {
            audioDirTemp.mkdirs();
        }

        // create the Handler for visualizer update
        handler = new Handler();
        handler.post(updateVisualizer);
    }

    OnClickListener recordClick = new OnClickListener() {

        @Override
        public void onClick(View v) {

            if (!isRecording) {
                // isRecording = true;
                recordtxt.setText("00:00");
                imageView.setImageResource(R.drawable.ic_mic_red_24dp);
                recorder = new MediaRecorder();

                recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                recorder.setOutputFile(audioDirTemp + "/audio_file"
                        + ".mp3");
                Log.d("file", "onClick: "+audioDirTemp.getAbsolutePath());

                OnErrorListener errorListener = null;
                recorder.setOnErrorListener(errorListener);
                OnInfoListener infoListener = null;
                recorder.setOnInfoListener(infoListener);

                try {
                    recorder.prepare();
                    recorder.start();
                    isRecording = true; // we are currently recording
                    startHTime = SystemClock.uptimeMillis();
                   customHandler.post(updateTimerThread);
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }


            } else {

                imageView.setImageResource(R.drawable.ic_mic_black_24dp);

                releaseRecorder();
            }

        }
    };

    private void releaseRecorder() {
        if (recorder != null) {
            isRecording = false; // stop recording
            timeSwapBuff += timeInMilliseconds;
            customHandler.removeCallbacks(updateTimerThread);
            visualizerView.clear();
            recorder.stop();
            recorder.reset();
            recorder.release();
            recorder = null;
        }
    }

    public static boolean deleteFilesInDir(File path) {

        if( path.exists() ) {
            File[] files = path.listFiles();
            if (files == null) {
                return true;
            }
            for(int i=0; i<files.length; i++) {

                if(files[i].isDirectory()) {

                }
                else {
                    files[i].delete();
                }
            }
        }
        return true;
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        handler.removeCallbacks(updateVisualizer);
        releaseRecorder();
    }

    // updates the visualizer every 50 milliseconds
    Runnable updateVisualizer = new Runnable() {
        @Override
        public void run() {
            if (isRecording) // if we are already recording
            {

                // get the current amplitude
                int x = recorder.getMaxAmplitude();
                Log.d("Amplitude", "run: "+x);
                visualizerView.addAmplitude(x); // update the VisualizeView
                visualizerView.invalidate(); // refresh the VisualizerView
                // update in 40 milliseconds

            }else {
                int x = 250;
                Log.d("Amplitude", "run: "+x);
                visualizerView.addAmplitude(x); // update the VisualizeView
                visualizerView.invalidate(); // refresh the VisualizerView
            }

            handler.postDelayed(this, 40);
        }
    };
    private Runnable updateTimerThread = new Runnable() {

        public void run() {

            timeInMilliseconds = SystemClock.uptimeMillis() - startHTime;

            updatedTime = timeSwapBuff + timeInMilliseconds;

            int secs = (int) (updatedTime / 1000);
            int mins = secs / 60;
            secs = secs % 60;
            if (recordtxt != null)
                recordtxt.setText("" + String.format("%02d", mins) + ":"
                        + String.format("%02d", secs));
            customHandler.postDelayed(this, 0);
        }

    };
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

}