package in.beyonity.rk.voicequote;

import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView quotetext;
    int maxChar =  1000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        quotetext = (TextView) findViewById(R.id.quotetext);

        quotetext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final LinearLayout mainLayout = (LinearLayout)
                        findViewById(R.id.main_layout);
                final PopupWindow popupWindow;
                // inflate the layout of the popup window
                LayoutInflater inflater = (LayoutInflater)
                        getSystemService(LAYOUT_INFLATER_SERVICE);
                final View popupView = inflater.inflate(R.layout.popup, null);

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



                // create the popup window
                int width = LinearLayout.LayoutParams.WRAP_CONTENT;
                int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                boolean focusable = true; // lets taps outside the popup also dismiss it
                popupWindow = new PopupWindow(popupView, width, height, focusable);

                // show the popup window
                popupWindow.showAtLocation(mainLayout, Gravity.CENTER, 0, 0);

                // dismiss the popup window when touched
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
    }
}
