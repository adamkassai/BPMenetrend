package com.kassaiweb.bpmenetrend.views;


import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.kassaiweb.bpmenetrend.R;
import com.kassaiweb.bpmenetrend.utils.Favourites;
import com.kassaiweb.bpmenetrend.utils.PagerAdapter;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PagerAdapter pager = new PagerAdapter(getSupportFragmentManager(), this);
        ViewPager mainViewPager = (ViewPager) findViewById(R.id.mainViewPager);
        mainViewPager.setAdapter(pager);

    }

    @Override protected void onResume() { super.onResume();
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String favourites = settings.getString("favourites", "");
        Favourites.getInstance().setFavouritesFromString(favourites);
    }

}
