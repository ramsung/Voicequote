package in.beyonity.rk.voicequote;

import android.content.Intent;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    public void prof(View v) {
        Log.i("clicks","You clicked Profile");
        Intent i=new Intent(
                MainActivity.this,
                profile.class);
        startActivity(i);
    }

    public void menu(View v) {

        Intent ai=new Intent(
                MainActivity.this,
                create_quote.class);
        startActivity(ai);
    }
}
