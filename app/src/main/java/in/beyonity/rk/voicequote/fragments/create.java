package in.beyonity.rk.voicequote.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import in.beyonity.rk.voicequote.DynamicSquareLayout;
import in.beyonity.rk.voicequote.MainActivity;
import in.beyonity.rk.voicequote.R;
import in.beyonity.rk.voicequote.outputimage;
import in.beyonity.rk.voicequote.utils.ColorPicker;
import in.beyonity.rk.voicequote.utils.VisualizerView;

import static android.content.ContentValues.TAG;
import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static android.view.Gravity.CENTER;

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
    ImageButton play;
    ImageButton delete;
    ImageButton colorPicker;
        ImageView batman;
        SeekBar fontSize;

    ImageButton justify,alignleft,alignright,aligncenter,strike;

    ImageView share;
    SeekBar seekBar;
    DynamicSquareLayout mainview;
    TextView quotetext;
    TextView maintext;
    boolean paused = false,resumed = false,started = false;
    int maxChar =  2000;
    View v;
    MediaPlayer player;
    float dX, dY;
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

    private int mPickedColor = Color.WHITE;
    private Handler handler;
    private Handler seekbarHandler = new Handler();

    // Handler for updating the visualizer
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
        ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO},1);
       v = inflater.inflate(R.layout.fragment_create,container,false);
        audioDirTemp = new File(Environment.getExternalStorageDirectory(),
                DIRECTORY_NAME_TEMP);
        if (audioDirTemp.exists()) {
            //deleteFilesInDir(audioDirTemp);
        } else {
            audioDirTemp.mkdirs();
        }
        fontSize =(SeekBar) v.findViewById(R.id.fontSize);
        fontSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(i>0){
                    int size = i * 6;
                    maintext.setTextSize((float)size);

                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //alignment
        ImageButton.OnClickListener alignment = new ImageButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(view.getId() == R.id.justify){

                }else if(view.getId() == R.id.alignCenter){
                    maintext.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    String value = maintext.getText().toString();
                    maintext.setText(value);
                    maintext.invalidate();
                    maintext.setPaintFlags(maintext.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                }else if(view.getId() ==  R.id.alignleft){
                    maintext.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
                    String value = maintext.getText().toString();
                    maintext.setText(value);
                }else if(view.getId() == R.id.alignright){
                    maintext.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
                    String value = maintext.getText().toString();
                    maintext.setText(value);
                }else if(view.getId() == R.id.strike){
                    if((maintext.getPaintFlags() & Paint.STRIKE_THRU_TEXT_FLAG) == Paint.STRIKE_THRU_TEXT_FLAG) {
                        maintext.setPaintFlags(maintext.getPaintFlags() ^ Paint.STRIKE_THRU_TEXT_FLAG);
                    }else {
                        maintext.setPaintFlags(maintext.getPaintFlags() ^ Paint.STRIKE_THRU_TEXT_FLAG);
                    }
                }
            }
        };
       justify = (ImageButton) v.findViewById(R.id.justify);
       alignleft = (ImageButton) v.findViewById(R.id.alignleft);
       aligncenter= (ImageButton) v.findViewById(R.id.alignCenter);
       alignright= (ImageButton) v.findViewById(R.id.alignright);
       strike = (ImageButton) v.findViewById(R.id.strike);

       justify.setOnClickListener(alignment);
       alignright.setOnClickListener(alignment);
       alignleft.setOnClickListener(alignment);
       aligncenter.setOnClickListener(alignment);
       strike.setOnClickListener(alignment);





        colorPicker = (ImageButton) v.findViewById(R.id.colorpicker);
        colorPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GridView gv = (GridView) ColorPicker.getColorPicker(getActivity());

                // Initialize a new AlertDialog.Builder object
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                // Set the alert dialog content to GridView (color picker)
                builder.setView(gv);
                final LinearLayout rl = (LinearLayout) v.findViewById(R.id.main_layout);
                // Initialize a new AlertDialog object
                final AlertDialog dialog = builder.create();

                // Show the color picker window
                dialog.show();

                // Set the color picker dialog size
                dialog.getWindow().setLayout(
                        getScreenSize().x,
                        getScreenSize().y/2);
                dialog.getWindow().setGravity(CENTER);

                // Set an item click listener for GridView widget
                gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        // Get the pickedColor from AdapterView
                        mPickedColor = (int) parent.getItemAtPosition(position);

                        // Set the layout background color as picked color
                        maintext.setTextColor(mPickedColor);

                        // close the color picker
                        dialog.dismiss();
                    }
                });
            }


        });
       voice = (ImageButton) v.findViewById(R.id.voice);
       mainview = (DynamicSquareLayout) v.findViewById(R.id.dynamicview);
       batman = (ImageView) v.findViewById(R.id.batman);
       share = (ImageView) v.findViewById(R.id.share);
       share.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               getImage();
           }
       });
       batman.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               mainview.setBackgroundResource(R.drawable.batman);
           }
       });
        maintext = (TextView) v.findViewById(R.id.maintext);
        maintext.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:

                        dX = view.getX() - event.getRawX();
                        dY = view.getY() - event.getRawY();
                        Log.d(TAG, "onTouch: x = "+view.getX()+"y = "+view.getY()+" event x = "+event.getRawX()+ " event y = "+event.getRawY());
                        Log.d(TAG, "onTouch: dx  ="+dX+" dy = "+dY);
                        break;

                    case MotionEvent.ACTION_MOVE:

                        view.animate()
                                .x(event.getRawX() + dX)
                                .y(event.getRawY() + dY)
                                .setDuration(0)
                                .start();
                        break;
                    default:
                        return false;
                }
                return true;
            }
        });
        handler = new Handler();
       voice.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               audioDirTemp = new File(Environment.getExternalStorageDirectory(),
                       DIRECTORY_NAME_TEMP);
               if (audioDirTemp.exists()) {
                   //deleteFilesInDir(audioDirTemp);
               } else {
                   audioDirTemp.mkdirs();
               }
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
               popupWindow.setOutsideTouchable(false);
               // show the text_popup window
               popupWindow.showAtLocation(mainLayout, CENTER, 0, 0);
               visualizerView = (VisualizerView) popupView.findViewById(R.id.visualizer);
               imageView = (ImageView) popupView.findViewById(R.id.RecordButton);
               recordtxt = (TextView) popupView.findViewById(R.id.recordtxt);


               play = (ImageButton) popupView.findViewById(R.id.playbutton);

               delete = (ImageButton) popupView.findViewById(R.id.deletebutton);

               seekBar = (SeekBar) popupView.findViewById(R.id.seekBar);


               play.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                       Log.d(TAG, "onClick: inside play");
                       if(isRecording){
                           Toast.makeText(getContext(), "Stop the recording", Toast.LENGTH_SHORT).show();
                           return;
                       }
                       File file = new File(audioDirTemp.getPath()+"/"+"audio_file.mp3");
                       if(file.exists()){
                            if(player!= null){
                                Log.d(TAG, "onClick: inside not null");
                                if(player.isPlaying()){
                                    Log.d(TAG, "onClick: inside isplayer");
                                    player.pause();
                                    paused = true;
                                    play.setImageResource(R.drawable.play);
                                }else if(paused){
                                    player.start();
                                    paused = false;
                                    play.setImageResource(R.drawable.ic_pause_black_24dp);
                                }else {
                                    Log.d(TAG, "onClick: inside not playing");

                                    play();

                                    try {
                                        player.setDataSource(file.getPath());
                                        player.prepareAsync();

                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }else {
                                Log.d(TAG, "onClick: inside player null");
                                play();
                                try {
                                    player.setDataSource(file.getPath());
                                    player.prepareAsync();

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                       }else {
                           Log.d(TAG, "onClick: file not exist");
                       }
                   }
               });

               delete.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                       File file = new File(audioDirTemp.getPath()+"/"+"audio_file.mp3");
                       if(player!=null) {
                           if (player.isPlaying()||paused) {
                               Toast.makeText(getContext(), "please stop the player beforing deleting the file", Toast.LENGTH_SHORT).show();
                               return;
                           }
                       }
                       if(isRecording){
                           Toast.makeText(getContext(), "please stop the recording beforing deleting the file", Toast.LENGTH_SHORT).show();
                           return;
                       }
                           if (file.exists()) {
                               file.delete();
                               Toast.makeText(getContext(), "Deleted Successfully", Toast.LENGTH_SHORT).show();
                               recordtxt.setText("00:00");
                           } else {
                               Toast.makeText(getContext(), "nothing to delete", Toast.LENGTH_SHORT).show();
                           }

                       }
               });
               imageView.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                       if (!isRecording) {
                           if(player!=null){
                               if(player.isPlaying()||paused){
                                   Toast.makeText(getContext(), "Stop the player before recording", Toast.LENGTH_SHORT).show();
                                   return;
                               }
                           }
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



               // create the Handler for visualizer update
               popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                   @Override
                   public void onDismiss() {
                       Log.d(TAG, "onDismiss: inside on dismiss");
                       if(isRecording){
                           releaseRecorder();
                           Log.d(TAG, "onDismiss: inside on record dismiss");
                           if(player != null){
                               if(player.isPlaying()||paused){
                                   Log.d(TAG, "onDismiss: inside on paused");
                                   player.stop();
                                   player.reset();
                                   paused = false;

                               }
                               seekBar.setProgress(0);

                           }



                           handler.removeCallbacks(updateVisualizer);
                           visualizerView.clear();

                       }
                       releaseRecorder();
                       if(player != null){
                           if(player.isPlaying()||paused){
                               Log.d(TAG, "onDismiss: inside not recording");
                               player.stop();
                               player.reset();
                               paused = false;

                           }
                           seekBar.setProgress(0);

                       }


                   }
               });


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
                popupWindow.showAtLocation(mainLayout, CENTER, 0, 0);


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
                        maintext.setText(newText);


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

    public void play(){
        player = new MediaPlayer();
        player.reset();
        paused = false;
        player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                player.start();
                seekBar.setMax(player.getDuration());
                seekbarHandler.post(UpdateSongTime);
                play.setImageResource(R.drawable.ic_pause_black_24dp);
            }
        });

        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                Log.d(TAG, "onCompletion: inside on complete");
                seekBar.setProgress(0);

                seekbarHandler.removeCallbacks(UpdateSongTime);
                play.setImageResource(R.drawable.play);
            }
        });

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

                updatedTime = timeInMilliseconds;

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

    private Runnable UpdateSongTime = new Runnable() {
        public void run() {
           int startTime = player.getCurrentPosition();
            recordtxt.setText(String.format("%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                    TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                    toMinutes((long) startTime)))
            );
            seekBar.setProgress((int)startTime);
            seekbarHandler.postDelayed(this, 100);
        }
    };

    public void getImage(){
        mainview.setDrawingCacheEnabled(true);
        mainview.buildDrawingCache(true);
        Bitmap b = Bitmap.createBitmap(mainview.getDrawingCache());
        mainview.setDrawingCacheEnabled(false);

        SaveImage(b);
    }

    private void SaveImage(Bitmap finalBitmap) {



        File myDir = new File(audioDirTemp.getPath());
        myDir.mkdirs();
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String fname = "Image-"+ n +".jpg";
        File file = new File (myDir, fname);
        if (file.exists ()) file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Point getScreenSize(){
        WindowManager wm = (WindowManager)getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        //Display dimensions in pixels
        display.getSize(size);
        return size;
    }

    // Custom method to get status bar height in pixels
    public int getStatusBarHeight() {
        int height = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            height = getResources().getDimensionPixelSize(resourceId);
        }
        return height;
    }
}
