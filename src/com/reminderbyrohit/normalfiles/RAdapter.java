package com.reminderbyrohit.normalfiles;


import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.rohit.smartnotifier.*;


public class RAdapter extends FragmentPagerAdapter {
 

    public RAdapter(android.support.v4.app.FragmentManager fm) {
		super(fm);
		// TODO Auto-generated constructor stub
	}

    @Override
    public android.support.v4.app.Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new WRFragment();
            case 1:
                return new RFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
