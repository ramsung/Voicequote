package in.beyonity.rk.voicequote;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import in.beyonity.rk.voicequote.adapters.mainPage;
import in.beyonity.rk.voicequote.databinding.ActivityMainBinding;
import in.beyonity.rk.voicequote.fragments.create;
import in.beyonity.rk.voicequote.fragments.feed;
import in.beyonity.rk.voicequote.fragments.profile;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding bind;
    ViewPager vg;
    create c;
    feed f;
    profile p;
    mainPage adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        bind = DataBindingUtil.setContentView(this,R.layout.activity_main);
        bind.bnve.enableAnimation(true);
        bind.bnve.enableShiftingMode(false);
        bind.bnve.enableItemShiftingMode(true);


        adapter = new mainPage(getSupportFragmentManager());
        c = new create();
        f = new feed();
        p = new profile();
        adapter.addFragment(c);
        adapter.addFragment(f);
        adapter.addFragment(p);
        bind.vg.setAdapter(adapter);

        bind.vg.setOffscreenPageLimit(3);
        bind.bnve.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.feed){
                    bind.vg.setCurrentItem(1);

                }else if(item.getItemId() == R.id.create){
                    bind.vg.setCurrentItem(0);

                }else if(item.getItemId() == R.id.profile){
                    bind.vg.setCurrentItem(2);

                }
                return true;
            }
        });

        bind.vg.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position == 0){
                    bind.bnve.setCurrentItem(0);
                }else if(position == 1){
                    bind.bnve.setCurrentItem(1);
                }else if(position == 2){
                    bind.bnve.setCurrentItem(2);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


}
