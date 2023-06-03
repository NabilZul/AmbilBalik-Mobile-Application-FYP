package com.example.ambilbalik;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class TabAdapter extends FragmentPagerAdapter {
    int totaltabs;
    private Context tabContext;

    public TabAdapter(Context context, FragmentManager fragmentManager, int totaltabs){
        super(fragmentManager);
        tabContext = context;
        this.totaltabs=totaltabs;
    }


    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                LostFragment lostFragment = new LostFragment();
                return lostFragment;
            case 1:
                FoundFragment foundFragment = new FoundFragment();
                return foundFragment;
            default:
                return null;
        }
    }
    @Override
    public int getCount() {
        return totaltabs;
    }
}
