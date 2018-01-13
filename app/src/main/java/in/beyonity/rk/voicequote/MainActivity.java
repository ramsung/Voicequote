package in.beyonity.rk.voicequote;

import android.content.Intent;

import android.support.v4.view.ViewPager;
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

import in.beyonity.rk.voicequote.adapters.mainPage;
import in.beyonity.rk.voicequote.fragments.create;
import in.beyonity.rk.voicequote.fragments.feed;
import in.beyonity.rk.voicequote.fragments.profile;

public class MainActivity extends AppCompatActivity {

    ViewPager vg;
    create c;
    feed f;
    profile p;
    mainPage adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        vg = (ViewPager) findViewById(R.id.vg);
        adapter = new mainPage(getSupportFragmentManager());
        c = new create();
        f = new feed();
        p = new profile();
        adapter.addFragment(f);
        adapter.addFragment(c);
        adapter.addFragment(p);
        vg.setAdapter(adapter);


    }


    public void prof(View v) {
        Log.i("clicks","You clicked Profile");
        vg.setCurrentItem(2);
    }

    public void create(View v) {

        vg.setCurrentItem(1);
    }

    public void feedClick(View v){
        vg.setCurrentItem(0);
    }
}
