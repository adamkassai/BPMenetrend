package com.kassaiweb.bpmenetrend.utils;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.kassaiweb.bpmenetrend.R;
import com.kassaiweb.bpmenetrend.views.FavouritesFragment;
import com.kassaiweb.bpmenetrend.views.SearchFragment;

public class PagerAdapter extends FragmentPagerAdapter {
    private Context context;

    public PagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment ret = null;

            switch (position) {
                case 0:
                    ret = new FavouritesFragment();
                    break;
                case 1:
                    ret = new SearchFragment();
                    break;
            }

        return ret;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title;

            switch (position) {
                case 0:
                    title = context.getString(R.string.favourites);
                    break;
                case 1:
                    title = context.getString(R.string.search);
                    break;
                default:
                    title = "";
            }

        return title;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
