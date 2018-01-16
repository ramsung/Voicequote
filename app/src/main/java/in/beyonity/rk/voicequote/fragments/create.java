package in.beyonity.rk.voicequote.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;

import in.beyonity.rk.voicequote.R;
import in.beyonity.rk.voicequote.RecordingActivity;
import in.beyonity.rk.voicequote.utils.VisualizerView;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link create.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link create#newInstance} factory method to
 * create an instance of this fragment.
 */
public class create extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static final String DIRECTORY_NAME_TEMP = "AudioTemp";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    ImageButton voice;
    TextView quotetext;
    int maxChar =  2000;
    View v;


    //recording
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


    private OnFragmentInteractionListener mListener;

    public create() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment create.
     */
    // TODO: Rename and change types and number of parameters
    public static create newInstance(String param1, String param2) {
        create fragment = new create();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       v = inflater.inflate(R.layout.fragment_create,container,false);
       voice = (ImageButton) v.findViewById(R.id.voice);
       voice.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO},1);
               final LinearLayout mainLayout = (LinearLayout)
                       v.findViewById(R.id.main_layout);
               final PopupWindow popupWindow;
               // inflate the layout of the text_popup window
               LayoutInflater inflater = (LayoutInflater)
                       getActivity().getSystemService(LAYOUT_INFLATER_SERVICE);
               final View popupView = inflater.inflate(R.layout.activity_recording_acitivity, null);
               boolean focusable = true; // lets taps outside the text_popup also dismiss it
               int width = LinearLayout.LayoutParams.WRAP_CONTENT;
               int height = LinearLayout.LayoutParams.WRAP_CONTENT;

               popupWindow = new PopupWindow(popupView, width, height, focusable);

               // show the text_popup window
               popupWindow.showAtLocation(mainLayout, Gravity.CENTER, 0, 0);
               visualizerView = (VisualizerView) popupView.findViewById(R.id.visualizer);
               imageView = (ImageView) popupView.findViewById(R.id.RecordButton);
               recordtxt = (TextView) popupView.findViewById(R.id.recordtxt);

               imageView.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
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

                           MediaRecorder.OnErrorListener errorListener = null;
                           recorder.setOnErrorListener(errorListener);
                           MediaRecorder.OnInfoListener infoListener = null;
                           recorder.setOnInfoListener(infoListener);

                           try {
                               recorder.prepare();
                               recorder.start();
                               isRecording = true; // we are currently recording
                               startHTime = SystemClock.uptimeMillis();
                           } catch (IllegalStateException e) {
                               e.printStackTrace();
                           } catch (IOException e) {
                               e.printStackTrace();
                           }
                           handler.post(updateVisualizer);

                       } else {

                           imageView.setImageResource(R.drawable.ic_mic_black_24dp);

                           releaseRecorder();
                       }

                   }


               });

               audioDirTemp = new File(Environment.getExternalStorageDirectory(),
                       DIRECTORY_NAME_TEMP);
               if (audioDirTemp.exists()) {
                   deleteFilesInDir(audioDirTemp);
               } else {
                   audioDirTemp.mkdirs();
               }

               // create the Handler for visualizer update
               handler = new Handler();


           }
       });
        quotetext = (TextView) v.findViewById(R.id.quotetext);

        quotetext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final LinearLayout mainLayout = (LinearLayout)
                        v.findViewById(R.id.main_layout);
                final PopupWindow popupWindow;
                // inflate the layout of the text_popup window
                LayoutInflater inflater = (LayoutInflater)
                        getActivity().getSystemService(LAYOUT_INFLATER_SERVICE);
                final View popupView = inflater.inflate(R.layout.text_popup, null);

                String text = quotetext.getText().toString();
                final EditText quote = (EditText) popupView.findViewById(R.id.quote);

                Button setbutton = (Button) popupView.findViewById(R.id.setbutton);
                Button clearButton = (Button) popupView.findViewById(R.id.clearbutton);
                Button back = (Button) popupView.findViewById(R.id.backbutton);
                final TextView count = (TextView) popupView.findViewById(R.id.lettercount);
                if(text.length()>0){
                    int letterCount = text.length();
                    int remaning = maxChar-letterCount;
                    count.setText(String.valueOf(remaning));
                    quote.setText("");
                    quote.append(text);
                }



                // create the text_popup window
                int width = LinearLayout.LayoutParams.WRAP_CONTENT;
                int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                boolean focusable = true; // lets taps outside the text_popup also dismiss it
                popupWindow = new PopupWindow(popupView, width, height, focusable);

                // show the text_popup window
                popupWindow.showAtLocation(mainLayout, Gravity.CENTER, 0, 0);

                // dismiss the text_popup window when touched
                /*popupView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        popupWindow.dismiss();
                        return true;
                    }
                });*/

                setbutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String newText = quote.getText().toString();

                        quotetext.setText(newText);


                        popupWindow.dismiss();
                    }
                });
                clearButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        quote.setText("");
                    }
                });
                back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        popupWindow.dismiss();
                    }
                });

                quote.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        int letterCount = charSequence.length();
                        int remaning = maxChar-letterCount;
                        count.setText(String.valueOf(remaning));

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
            }
        });
        return v;

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
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
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
    private void releaseRecorder() {
        if (recorder != null) {
            isRecording = false; // stop recording
            timeSwapBuff += timeInMilliseconds;
            handler.removeCallbacks(updateVisualizer);
            visualizerView.clear();
            recorder.stop();
            recorder.reset();
            recorder.release();
            recorder = null;
        }
    }

    Runnable updateVisualizer = new Runnable() {
        @Override
        public void run() {
            if (isRecording) // if we are already recording
            {

                // get the current amplitude
                int x = recorder.getMaxAmplitude();
                visualizerView.addAmplitude(x); // update the VisualizeView
                visualizerView.invalidate(); // refresh the VisualizerView
                timeInMilliseconds = SystemClock.uptimeMillis() - startHTime;

                updatedTime = timeSwapBuff + timeInMilliseconds;

                int secs = (int) (updatedTime / 1000);
                int mins = secs / 60;
                secs = secs % 60;
                if(secs <= 30){
                    if (recordtxt != null)
                        recordtxt.setText("" + String.format("%02d", mins) + ":"
                                + String.format("%02d", secs));

                }else {
                    imageView.setImageResource(R.drawable.ic_mic_black_24dp);

                    releaseRecorder();
                }


                handler.postDelayed(this, 0);
            }
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
